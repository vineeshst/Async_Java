package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class B_RunExecutionTasks {
    record Quotation(String server, int amount){
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        run();
    }

    public static void run() throws ExecutionException, InterruptedException {
        Random random = new Random();

        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep( random.nextInt(80,120));
            return new Quotation("Server A", random.nextInt(40, 60));
        };
        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep( random.nextInt(80,120));
            return new Quotation("Server B", random.nextInt(30, 70));
        };
        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep( random.nextInt(80,120));
            return new Quotation("Server C", random.nextInt(40, 80));
        };

        var quotationTasks = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        var executror  = Executors.newFixedThreadPool(4)  ;
        Instant begin = Instant.now();

        List<Future> futures = new ArrayList<>();
        for (Callable<Quotation> task:
             quotationTasks) {
            futures.add(executror.submit(task));
        }
        List<Quotation> quotations = new ArrayList<>();
        for (Future<Quotation> future:
             futures) {
            quotations.add(future.get());
        }
        Quotation bestQuotation =
        quotations.stream()
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("Best quotation [SYNC ] = " + bestQuotation +
                " (" + duration.toMillis() + "ms)");

        executror.shutdown();

    }

    private static A_RunSynchronousTask.Quotation fetchQuotation(Callable<A_RunSynchronousTask.Quotation> task) {
        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
