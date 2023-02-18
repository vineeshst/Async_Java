package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class A_RunSynchronousTask {
    record Quotation(String server, int amount){
    }
    public static void main(String[] args) {
        run();
    }

    public static void run() {
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

        Instant begin = Instant.now();
        Quotation bestQuotation =
        quotationTasks.stream()
                .map(A_RunSynchronousTask::fetchQuotation)
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("Best quotation [SYNC ] = " + bestQuotation +
                " (" + duration.toMillis() + "ms)");

    }

    private static Quotation fetchQuotation(Callable<Quotation> task) {
        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}