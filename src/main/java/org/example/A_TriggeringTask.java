package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class A_TriggeringTask {
    record Quotation(String server, int amount){
    }
    public static void main(String[] args) throws InterruptedException {
        run();
    }

    public static void run() throws InterruptedException {
        Random random = new Random();
        Supplier<Quotation> fetchQuotationA =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                  System.out.println("A running in " + Thread.currentThread());
                    return new Quotation("Server A", random.nextInt(40, 60));
                };
        Supplier<Quotation> fetchQuotationB =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                  System.out.println("B running in " + Thread.currentThread());
                    return new Quotation("Server B", random.nextInt(30, 70));
                };
        Supplier<Quotation> fetchQuotationC =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                  System.out.println("C running in " + Thread.currentThread());
                    return new Quotation("Server C", random.nextInt(40, 80));
                };

        var quotationTasks = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        Instant begin = Instant.now();

        List<CompletableFuture> futures = new ArrayList<>();
        for (Supplier<Quotation> task:
             quotationTasks) {
            CompletableFuture<Quotation> future =
                    CompletableFuture.supplyAsync(task);
            futures.add(future);
        }

        Collection<Quotation> quotations = new ConcurrentLinkedDeque<>();
        List<CompletableFuture> voids = new ArrayList<>();
        for (CompletableFuture<Quotation> future:
             futures) {
            future.thenAccept(System.out::println);
            CompletableFuture<Void> accept =future.thenAccept(quotations::add);
            voids.add(accept);
        }

        for (CompletableFuture<Void> v:
             voids) {
            v.join();
        }

        Thread.sleep(500);

        Quotation bestQuotation = quotations.stream()
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("Best quotation [ASYNC] = " + bestQuotation +
                " (" + duration.toMillis() + "ms)");
    }
}
