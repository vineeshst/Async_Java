package org.example;

import java.util.concurrent.ExecutionException;

public class RunAll {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        A_RunSynchronousTask.run();
        B_RunExecutionTasks.run();
        C_RunAsyncTasks.run();
    }
}
