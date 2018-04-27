package com.hj.cmi.react;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxScEX {

    public static void main(String[] args) {

        try {
            Flux.interval(Duration.ofMillis(200))
                    .take(10)
                    .subscribe(s -> log.debug("onNext: {}", s));

            TimeUnit.SECONDS.sleep(10);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("exit");
    }
}
