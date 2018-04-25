package com.hj.cmi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {

    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101);



        for(int i = 0 ; i < 100; i++){
            es.submit(() -> {

                int idx = counter.addAndGet(1);

                barrier.await();

                log.info("Thread " + idx);

                StopWatch sw = new StopWatch();
                sw.start();

                String response =  rt.getForObject(url, String.class, idx);

                sw.stop();

                log.info("elapsed: {} {} {}", idx, + sw.getTotalTimeSeconds(), response);

                return null;
            });
        }

        barrier.await();

        StopWatch main = new StopWatch();
        main.start();
        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total : {}", main.getTotalTimeSeconds());
    }
}
