package com.hj.cmi;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootApplication
@EnableAsync
public class CmiApplication {


    @Data
    @RequiredArgsConstructor
    static class Member{

        @NonNull private String radioCheck;
        @NonNull private String id;
        @NonNull private String pw;
        private String result;
        private String gd_name;
        private String name;
        private String post_name;
        private String type;

//        private String search_keyword ="";
//        private String sch_grade ="";
//        private String sch_YN = "N";
//        private String sch_sex = "";
//        private String sch_order= "";

    }

    @Data
    @RequiredArgsConstructor
    static class MemberSearch{

        @NonNull private String search_keyword;
        @NonNull private String sch_grade;
        @NonNull private String sch_YN;
        @NonNull private String sch_sex;
        @NonNull private String sch_order;

    }


    @Service
    public static class MyService {

        @Async
        public CompletableFuture<Object> list(Map<String, Object> req) {
            return CompletableFuture.completedFuture(req.get("list"));
        }
    }

    @RestController
    public static class Controller {

        WebClient client = WebClient.create();

        @Autowired
        private MyService myService;

        @RequestMapping("/hello")
        public Publisher<String> hello(String name) {

            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> s) {
                    s.onSubscribe(new Subscription() {
                        @Override
                        public void request(long n) {
                            s.onNext("Hello " + name);
                            s.onComplete();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            };
        }

        @RequestMapping("/cmi")
        public Mono<Map> cmi() {

//            String response = client
//                    .post()
//                    .uri(URI.create("http://gw.dkitec.com:8080/intranet-api/login"))
//                    .body(inserter3)
//                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();

            //System.out.println(response)
            
//            return WebClient.create().post().uri(URI.create("http://gw.dkitec.com:8080/intranet-api/login"))
//                    .body(inserter3).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                    .retrieve().bodyToMono(String.class);

            return client
                    .post()
                    .uri(URI.create("http://gw.dkitec.com:8080/intranet-api/login"))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromObject(new Member("1","hj-kim", "0000")))
                    .exchange()
                    .flatMap(c -> c.bodyToMono(Map.class))
                    .flatMap(body ->

                    {
                        Mono<ClientResponse> response = null;
                        try {

                            MultiValueMap<String, Object>  map = new LinkedMultiValueMap();

                            //map.deepCopy();
                            map.add("search_keyword", "".toString());
                            map.add("sch_grade", "".toString());
                            map.add("sch_YN", "N".toString());
                            map.add("sch_sex", "".toString());
                            map.add("sch_order", "".toString());

//                            BodyInserter<MultiValueMap<String, String>, ClientHttpRequest>
//                                    inserter = BodyInserters.fromFormData(map);

                            //eyJ0eXBlIjoiSldUIiwicmVnRGF0ZSI6MTUyNDU4MDYwNDIwNywiYWxnIjoiSFMyNTYifQ.eyJleHAiOjE1MjQ2NjcwMDQsIm5pY2tuYW1lIjoiaGota2ltIiwicm9sZSI6MX0.0ff8IlwNkg9QZ0B0o8GjxyO67T-S1UkB4ZGhgNsDfzk
                            log.debug("name {}", URLEncoder.encode(body.get("name").toString(), "UTF-8"));
                            log.debug("type {}", URLEncoder.encode(body.get("type").toString(), "UTF-8"));
                            log.debug("post_name {}", URLEncoder.encode(body.get("post_name").toString(), "UTF-8"));
                            log.debug("gd_name {}", URLEncoder.encode(body.get("gd_name").toString(), "UTF-8"));
                            log.debug("id {}", body.get("name").toString());
                            response = client.post()
                                    .uri(URI.create("http://gw.dkitec.com:8080/intranet-api/member/list") )
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .acceptCharset(Charset.forName("UTF-8"))
                                    .cookie("SESSION_USER_ID", "hj-kim")
                                    .cookie("SESSION_USER_NAME", URLEncoder.encode(body.get("name").toString(), "UTF-8"))
                                    .cookie("SESSION_CHECK_ID", URLEncoder.encode(body.get("type").toString(), "UTF-8"))
                                    .cookie("SESSION_POST_NAME", URLEncoder.encode(body.get("post_name").toString(), "UTF-8"))
                                    .cookie("SESSION_GD_NAME", URLEncoder.encode(body.get("gd_name").toString(), "UTF-8"))
                                    .cookie("loginToken", "eyJ0eXBlIjoiSldUIiwicmVnRGF0ZSI6MTUyNDU4MDYwNDIwNywiYWxnIjoiSFMyNTYifQ.eyJleHAiOjE1MjQ2NjcwMDQsIm5pY2tuYW1lIjoiaGota2ltIiwicm9sZSI6MX0.0ff8IlwNkg9QZ0B0o8GjxyO67T-S1UkB4ZGhgNsDfzk")
                                    .body(BodyInserters.fromObject(new MemberSearch("", "", "N", "", "")))
//                                    .body(BodyInserters.fromMultipartData("search_keyword", "")
//                                            .with("sch_grade","")
//                                            .with("sch_YN","N")
//                                            .with("sch_sex","")
//                                            .with("sch_order",""))
                                    .exchange();

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        return response;
                    })
                    .flatMap(c2 -> c2.bodyToMono(Map.class))
                    .flatMap(res2 -> Mono.fromCompletionStage(myService.list(res2)));

        }
    }


    public static void main(String[] args) {
        SpringApplication.run(CmiApplication.class, args);
    }
}
