package com.wanderlust.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceImplementation {    
	
    String name();
    
    Class<?> configClass() default Void.class;

    Class<?>[] dependencies() default {};
}
