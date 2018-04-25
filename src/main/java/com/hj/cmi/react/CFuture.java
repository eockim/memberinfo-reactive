package com.hj.cmi.react;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("run Async");
                    return 1;
                })
                .thenApply((s) -> {
                    log.info("thenrun {}", s);
                    return s + 1;
                })
                .thenAccept(s2 -> log.info("thenAccept {}", s2));

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }
}
