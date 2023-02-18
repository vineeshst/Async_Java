package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class B_ReadingOneOfSeveralTasks {
    record Weather(String server, String weather) {
    }

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        Random random = new Random();
        List<Supplier<Weather>> weatherTasks = buildWeatherTasks(random);
        List<CompletableFuture<Weather>> futures = new ArrayList<>();
        for (Supplier<Weather> task:
                weatherTasks) {
            CompletableFuture<Weather> future = CompletableFuture.supplyAsync(task);
            futures.add(future);
        }

        CompletableFuture<Object> future = CompletableFuture.anyOf(futures.toArray(CompletableFuture[]::new));
        future.thenAccept(System.out::println).join();
    }

    private static List<Supplier<Weather>> buildWeatherTasks(Random random) {
        Supplier<Weather> fetchWeatherA =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Weather("Server A", "Sunny");
                };
        Supplier<Weather> fetchWeatherB =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Weather("Server B", "Mostly Sunny");
                };
        Supplier<Weather> fetchWeatherC =
                () -> {
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
