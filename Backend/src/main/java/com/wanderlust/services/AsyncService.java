//package com.wanderlust.services;
//
//import com.wanderlust.async.AsyncTaskType;
//import com.wanderlust.async.AsyncWorker;
//import com.wanderlust.async.RecoverableAsyncTask;
//
//public interface AsyncService extends Service {
//
//    // Task management
//    public void submitTask(RecoverableAsyncTask task);
//    public void taskComplete(RecoverableAsyncTask task);
//    public void taskFailed(RecoverableAsyncTask task, Throwable cause);
//    public void taskRejected(RecoverableAsyncTask task);
//
//    // Service management
//    public void registerRecoveryService(AsyncTaskType taskType, AsyncWorker worker);
//     
//}
