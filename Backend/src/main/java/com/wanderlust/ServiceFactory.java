package com.wanderlust;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.collect.Lists;
import com.wanderlust.api.FrameworkError;
import com.wanderlust.api.ServiceException;
import com.wanderlust.services.Service;
import com.wanderlust.services.ServiceImplementation;


class ServiceSpecification{

	public ServiceSpecification(Class<?> serviceClazz,
			ServiceImplementation metadata) {
		super();
		this.serviceClazz = serviceClazz;
		this.metadata = metadata;
	}

	public Class<?> serviceClazz;
	ServiceImplementation metadata;

	@Override
	public String toString(){
		return String.format("name : %s,  class : %s, config : %s",
				metadata.name(), serviceClazz.getName(), metadata.configClass().getName());
	}
}


public class ServiceFactory {

	private static Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
	private static final String PLUGIN_SEARCH_SCOPE = "com.wanderlust";

	private Map<Class<?>, Object> serviceInstances = new LinkedHashMap<Class<?>, Object>();

	public <T> T createAndRegisterService(Class<T> serviceType, 
			Map<String, Object> serviceConfig, Object... params){
		T service = createService(serviceType, serviceConfig, params);
		registerService(serviceType, service);
		return service;
	}

	public <T> T createService(Class<T> serviceType, Map<String, Object> serviceConfig, Object... params){

		Map<String, Object> localConfig = new LinkedHashMap<String, Object>(serviceConfig);
		String serviceImpl = (String) localConfig.remove(ServiceManager.MODEL_KEY);

		if(serviceImpl == null){
			throw new ServiceException(FrameworkError.FAILED_TO_LOAD_SERVICE).
			set("serviceType", serviceType).set("missingField", ServiceManager.MODEL_KEY);	
		}

		T serviceInstance = null;

		try{
			ServiceSpecification spec = getServiceImplByName(serviceImpl, serviceType);
			Constructor<?> implCtor = null;

			Class<?>[] paramTypeArray = buildParamTypes(params, spec);

			try{
				implCtor = spec.serviceClazz.getConstructor(paramTypeArray);
			}
			catch(NoSuchMethodException nsmex)
			{

				implCtor = findCompatibleConstructor(spec.serviceClazz, paramTypeArray);

				// could not find anything compatible, rethrow
				if(implCtor == null)
					throw nsmex;
			}


			params = buildParams(params, spec, localConfig);

			serviceInstance = serviceType.cast(implCtor.newInstance(params));

		} catch (Exception e) {
			throw ServiceException.wrap(e, FrameworkError.FAILED_TO_LOAD_SERVICE).
			set("ServiceClass", serviceImpl);
		}

		return serviceInstance;	
	}


	private static Constructor<?> findCompatibleConstructor(
			Class<?> serviceClazz, Class<?>[] paramTypeArray) {
		for(Constructor<?> candidate : serviceClazz.getConstructors()){
			Class<?>[] candidateTypes = candidate.getParameterTypes();
			if(paramsAreCompatible(paramTypeArray, candidateTypes)){
				return candidate;
			}
		}
		return null;
	}

	private static Class<?>[] buildParamTypes(
			Object[] params, ServiceSpecification spec) {

		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		for(int i = 0; i < params.length; ++i)
			paramTypes.add(params[i].getClass());

		Class<?>[] dependencies = spec.metadata.dependencies();
		for(int i = 0; i < dependencies.length; ++i)
			paramTypes.add(dependencies[i]);

		Class<?> configClass = spec.metadata.configClass();
		if(configClass != Void.class){
			paramTypes.add(configClass);
		}

		return paramTypes.toArray(new Class<?>[paramTypes.size()]);
	}

	private Object[] buildParams(Object[] params,
			ServiceSpecification spec, Map<String, Object> serviceConfig) {

		List<Object> paramList = new ArrayList<Object>();
		for(int i = 0; i < params.length; ++i)
			paramList.add(params[i]);

		Class<?>[] dependencies = spec.metadata.dependencies();
		for(int i = 0; i < dependencies.length; ++i)
			paramList.add(getService(dependencies[i]));

		Class<?> configClass = spec.metadata.configClass();
		if(configClass != Void.class){
			Yaml yaml = new Yaml();           
			String configString = yaml.dump(serviceConfig);
			Object configObject = yaml.loadAs(configString, configClass);
			paramList.add(configObject);
		}

		return paramList.toArray();
	}

	public synchronized <T> T getService(Class<T> serviceType) {
		Object instance = this.serviceInstances.get(serviceType);
		if(instance == null)
			throw new ServiceException(FrameworkError.FAILED_TO_LOAD_DEPENDENCY).
			set("dependencyType", serviceType.getName());

		return serviceType.cast(instance);
	}

	private synchronized void registerService(Class<?> serviceType, Object serviceInstance){
		this.serviceInstances.put(serviceType, serviceInstance);
	}

	public synchronized List<Service> getServiceList() {
		List<Service> services = new ArrayList<Service>(this.serviceInstances.size());
		for(Object service : this.serviceInstances.values())
			services.add((Service)service);
		return Lists.reverse(services);
	}

	private static ServiceSpecification getServiceImplByName(String serviceImpl, Class<?> serviceType) {
		logger.debug("SEARCHING for {}:{} under {}", serviceType.getSimpleName(), 
				serviceImpl, PLUGIN_SEARCH_SCOPE);
		Reflections reflections = new Reflections(PLUGIN_SEARCH_SCOPE);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(ServiceImplementation.class);   
		for(Class<?> candidate : annotated){
			if(serviceType.isAssignableFrom(candidate)){
				ServiceImplementation spec = candidate.getAnnotation(ServiceImplementation.class);
				logger.trace("FOUND candidate {} : {}", serviceType.getSimpleName(), spec.name());
				if(spec.name().equals(serviceImpl)){
					ServiceSpecification match = new ServiceSpecification(candidate, spec);
					logger.debug("MATCHED service of type - {}", serviceType.getName());
					logger.debug("\t{}", match);
					return match;
				}
			}
		}
		throw new ServiceException(FrameworkError.FAILED_TO_LOAD_SERVICE).set("serviceName", serviceImpl);
	}

	private static boolean paramsAreCompatible(
			Class<?>[] requested, Class<?>[] offered){
		if(requested.length == offered.length){
			for(int i=0; i < requested.length; ++i){
				if(false == offered[i].isAssignableFrom(requested[i])){
					return false;
				}
			}
		} else {
			return false;
		}

		return true;
	}
}
