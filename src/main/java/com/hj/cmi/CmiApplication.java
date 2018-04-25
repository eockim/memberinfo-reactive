package com.hj.cmi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SpringBootApplication
@Slf4j
public class CmiApplication {


    static WebClient client = WebClient.create();

    @RestController
    public static class Controller{

        @GetMapping("/cmi")
        public Mono<String> cmi(int idx) {

            final String uri = "http://gw.dkitec.com:8080/intranet-api/member/list";

            LinkedMultiValueMap map = new LinkedMultiValueMap();

            map.add("sch_YN", "n");
            map.add("sch_grade", "");
            map.add("sch_order", "");
            map.add("sch_sex", "");
            map.add("search_keyword", "");

            LinkedMultiValueMap loginMap = new LinkedMultiValueMap();

            loginMap.add("radioCheck", "1");
            loginMap.add("id", "hj-kim");
            map.add("pw", "0000");

            Mono<String> body = WebClient.create("http://gw.dkitec.com:8080")
                    .post()
                    .uri(URI.create("http://gw.dkitec.com:8080/intranet-api/login"))
                    .body(BodyInserters.fromMultipartData(loginMap))
                    //.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.117 Safari/537.36Mozilla/5.0")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));

            return body;
        }

    }


        //@Autowired MyService myService;

//        Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();
//
//        @GetMapping("/emitter")
//        public ResponseBodyEmitter emitter() throws InterruptedException{
//
//            ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//
//            Executors.newSingleThreadExecutor().submit(() -> {
//                try{
//
//                    for(int i = 1 ; i <= 50; i++){
//                        emitter.send("<p>stream " + i + "</p>");
//                        Thread.sleep(100);
//                    }
//
//                }catch(Exception e){
//
//                }
//            });
//
//            return emitter;
//        }

        //RestTemplate rt = new RestTemplate();


    @Component
    public static class MyService {

        @Async(value = "threadPool")
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello");
            Thread.sleep(1000);

            return new AsyncResult<>("Hello");
        }
    }

    public static void main(String[] args) {

        SpringApplication.run(CmiApplication.class, args);

    }

//    @Autowired
//    MyService myService;

//    @Bean
//    ThreadPoolTaskExecutor threadPool(){
//
//        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
//        te.setCorePoolSize(10);
//        te.setMaxPoolSize(100);
//        te.setQueueCapacity(200);
//        te.setThreadNamePrefix("hjThread");
//        te.initialize();
//
//        return te;
//    }
//
//    @Bean
//    ApplicationRunner run(){
//        return args ->{
//            log.info("run()");
//            ListenableFuture<String> f = myService.hello();
//            f.addCallback(x -> System.out.println(x), e -> System.out.println(e.getMessage()));
//
//            log.info("exit : " + f.isDone());
//            log.info("result : " + f.get());
//        };
//    }
}
