package org.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class D_ComposingTasks {
    record TravelPage(Quotation quotation, Weather weather){ }
    record Weather(String server, String weather){}
    record Quotation(String server, int amount){}

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        Random random = new Random();
        List<Supplier<Weather>> weatherTasks = buildWeatherTasks(random);
        List<Supplier<Quotation>> quotationTasks = buildQuotationTasks(random);

        CompletableFuture<Weather>[] weatherCFS = weatherTasks.stream()
                .map(CompletableFuture::supplyAsync)
                .toArray(CompletableFuture[]::new);

        CompletableFuture<Weather> weatherCF =
                CompletableFuture.anyOf(weatherCFS)
                        .thenApply(weather -> (Weather) weather);


        CompletableFuture<Quotation>[] quotationCFS = quotationTasks.stream()
                .map(CompletableFuture::supplyAsync)
                .toArray(CompletableFuture[]::new);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(quotationCFS);

        CompletableFuture<Quotation> bestQuotationCF = allOf.thenApply(
                v -> Arrays.stream(quotationCFS)
                        .map(CompletableFuture::join)
                        .min(Comparator.comparing(Quotation::amount))
                        .orElseThrow()
        );

        CompletableFuture<Void> done = bestQuotationCF.thenCompose(
                quotation -> weatherCF.thenApply(weather -> new TravelPage(quotation, weather))
                        .thenAccept(System.out::println));

        done.join();

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
                    return new Quotation("Server B", random.nextInt(40, 60));
                };
        Supplier<Quotation> fetchQuotationC =
                ()->{
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Quotation("Server B", random.nextInt(40, 60));
                };

        List<Supplier<Quotation>> quotationTasks = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        return quotationTasks;
    }

    private static List<Supplier<Weather>> buildWeatherTasks(Random random) {
        Supplier<Weather> fetchWeatherA =
                ()->{
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Weather("Server A", "Sunny");
                };
        Supplier<Weather> fetchWeatherB =
                ()->{
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Weather("Server B", "Mostly Sunny");
                };

        Supplier<Weather> fetchWeatherC =
                ()->{
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Weather("Server C", "Almost Sunny");
                };
       var weatherTasks = List.of(fetchWeatherA, fetchWeatherB, fetchWeatherC);
       return weatherTasks;
    }
}
