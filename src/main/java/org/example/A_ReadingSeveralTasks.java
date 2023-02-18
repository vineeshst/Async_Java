package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class A_ReadingSeveralTasks {
    record Quotation(String name, int amount){
    }

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        Random random = new Random();

        List<Supplier<Quotation>> quotationTasks = buildQuotationTasks(random);
        List<CompletableFuture<Quotation>> quotationCFS = new ArrayList<>();

        for (Supplier<Quotation> task:
                quotationTasks ) {
            CompletableFuture<Quotation> future = CompletableFuture.supplyAsync(task);
            quotationCFS.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(quotationCFS.toArray(CompletableFuture[]::new));

        Quotation bestQuotation = allOf.thenApply(
                v ->
                        quotationCFS.stream()
                                .map(CompletableFuture::join)
                                .min(Comparator.comparing(Quotation::amount))
                                .orElseThrow()
                ).join();
        System.out.println("bestQuotation = " + bestQuotation);
    }

    private static List<Supplier<Quotation>> buildQuotationTasks(Random random) {
        Supplier<Quotation> fetchQuotationA =
                ()->{
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Quotation("Server A", random.nextInt(40, 60));
                };
        Supplier<Quotation> fetchQuotationB =
                ()->{
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Quotation("Server B", random.nextInt(30, 70));
                };
        Supplier<Quotation> fetchQuotationC =
                ()->{
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Quotation("Server C", random.nextInt(40, 80));
                };

        var quotationTasks = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        return quotationTasks;
    }

}
