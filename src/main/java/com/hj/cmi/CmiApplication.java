package com.hj.cmi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
@EnableAsync
public class CmiApplication {

    @Data
    @RequiredArgsConstructor
    static class MemberLogin {

        @NonNull
        private String radioCheck;
        @NonNull
        private String id;
        @NonNull
        private String pw;
        private String result;
        private String gd_name;
        private String name;
        private String post_name;
        private String type;

    }

    @Data
    @RequiredArgsConstructor
    static class MemberSearch {

        @NonNull
        private String search_keyword;
        @NonNull
        private String sch_grade;
        @NonNull
        private String sch_YN;
        @NonNull
        private String sch_sex;
        @NonNull
        private String sch_order;

    }

    @Data
    @AllArgsConstructor
    static class Member<T> {

        private T userEmail;
        private T userBye;
        private T userId;
        private T bookLocation;
        private T postName;
        private T lastYearUseVoc;
        private T thisYearUseVoc;
        private T userU2;
        private T annualVocation;
        private T monthlyVacation;
        private T userName;
        private T leftVoc;
        private T gdOrder;
        private T userGrade;
        private T userHphone;
        private T userCphone;

    }

    @Data
    @AllArgsConstructor
    static class Project{
        private String rptFlag;
        private String pos;
        private String dept;
        private String prjPost;
        private String prjStatus;
        private String name;
        private String prjClient;
        private String prjContent;
        private String regId;
        private String prjCode;
        private String userId;
        private String fromDt;
        private String toDt;
        private String endDt;
        private String userName;
        private String prjStatusName;
        private String color;
        private String result;
        private String user_name;
        private String list;

        public Project(){}

    }


    @Service
    public static class MyService {
        //
        final ObjectMapper mapper = new ObjectMapper();

        @Async
        public CompletableFuture<List<Member<String>>> list(Map<String, Object> req) {

            Stream<Map<String, Object>> stream = ((List<Map<String, Object>>) req.get("list"))
                    .stream();



            List<Member<String>> memberList = stream.map(x ->
                    new Member<String>(x.get("USER_EMAIL")+"", x.get("USER_BYE")+"", x.get("USER_ID")+"", x.get("BOOK_LOCATION")+"", x.get("POST_NAME")+""
                            , x.get("LAST_YEAR_USE_VOC")+"", x.get("THIS_YEAR_USE_VOC")+"", x.get("USER_U2")+"", x.get("ANNUAL_VACATION")+""
                            , x.get("MONTHLY_VACATION")+"", x.get("USER_NAME")+"", x.get("LEFT_VOC")+"", x.get("GD_ORDER")+"", x.get("USER_GRADE")+"", x.get("USER_HPHONE")+"", x.get("USER_CPHONE")+"")
            ).collect(Collectors.toList());

            return CompletableFuture.completedFuture(memberList);
        }

        @Async
        public CompletableFuture<List<Project>> listProject(Map<String, Object> req) {

            Stream<Map<String, Object>> stream = ((List<Map<String, Object>>) req.get("list")).stream();


            return CompletableFuture.completedFuture(stream.map(x ->
                            mapper.convertValue(x, Project.class)
                    ).collect(Collectors.toList())
                            .stream()
                            .sorted((cp1, cp2) -> cp1.getUserName().compareTo(cp2.getUserName()))
                            .collect(Collectors.toList())

            );
        }

        @Async
        public CompletableFuture<Map<String, String>> login(ClientResponse res) {

            Map<String, String> loginMap = new HashMap<String, String>();

            loginMap = res.bodyToMono(Map.class).block();

            try {

                loginMap.put("loginToken", res.cookies().getFirst("loginToken").getValue());
                loginMap.put("name", URLEncoder.encode(loginMap.get("name").toString(), "UTF-8"));
                loginMap.put("type", URLEncoder.encode(loginMap.get("type").toString(), "UTF-8"));
                loginMap.put("post_name", URLEncoder.encode(loginMap.get("post_name").toString(), "UTF-8"));
                loginMap.put("gd_name", URLEncoder.encode(loginMap.get("gd_name").toString(), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return CompletableFuture.completedFuture(loginMap);
        }

        @Async
        public CompletableFuture<List<Map<String, String>>> vacationSort(List<Member<String>> res,  String sort){

            AtomicInteger sortNum = new AtomicInteger(1);
            Optional.<String>ofNullable(sort)
                    .ifPresent(s -> sortNum.set(s.equals("desc") ? -1 : s.equals("asc") ? 1 : 1 ));

            return CompletableFuture.completedFuture( res.stream()
                    .sorted((c1, c2) -> Double.compare(Double.parseDouble(c1.getThisYearUseVoc()), Double.parseDouble(c2.getThisYearUseVoc())) * sortNum.intValue())
                    .map(x -> {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", x.getUserName());
                        map.put("userId", x.getUserId());
                        map.put("thisYearUseVoc", x.getThisYearUseVoc());
                        map.put("bookLocation", x.getBookLocation());
                        map.put("postName", x.getPostName());
                        return map;
                    })
                    .collect(Collectors.toList()));
        }
    }

    @RestController
    public static class Controller {

        WebClient client = WebClient.create();

        @Autowired
        private MyService myService;

        @GetMapping("/hello")
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

        @GetMapping("/cmi")
        public Mono<List<Member<String>>> cmi(String rt) {

            return getMemberMono(Optional.ofNullable(rt).isPresent() ? rt.toUpperCase().equals("Y") ? true : false : false  );
        }

        @GetMapping("/cmi/vacation")
        public Mono<List<Map<String, String>>> vacation(String sort, String rt){

            return getMemberMono(Optional.ofNullable(rt).isPresent() ? rt.toUpperCase().equals("Y") ? true : false : false  )
                    .flatMap(res -> Mono.fromCompletionStage(myService.vacationSort(res, sort)));
        }

        @GetMapping("/cmi/projects")
        public Mono<List<Project>> projects(){

            Mono<List<Project>> result = getLogin("hj-kim", "1234")
                    .flatMap(body -> client.get()
                            .uri(URI.create("http://gw.dkitec.com:8080/intranet-api/project/list?orderby=1"))
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .acceptCharset(Charset.forName("UTF-8"))
                            .cookie("SESSION_USER_ID", body.get("id"))
                            .cookie("SESSION_USER_NAME", body.get("name"))
                            .cookie("SESSION_CHECK_ID", body.get("type"))
                            .cookie("SESSION_POST_NAME", body.get("post_name"))
                            .cookie("SESSION_GD_NAME", body.get("gd_name"))
                            .cookie("loginToken", body.get("loginToken"))
                            .exchange()
                    )
                    .flatMap(c -> c.bodyToMono(HashMap.class))
                    .flatMap(c2 -> Mono.fromCompletionStage(myService.listProject(c2)));

            return result;
        }

        private Mono<Map<String, String>> getLogin(String userId, String pass){

            return client
                    .post()
                    .uri(URI.create("http://gw.dkitec.com:8080/intranet-api/login"))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromObject(new MemberLogin("1", userId, pass)))
                    .exchange()
                    .flatMap(r -> Mono.fromCompletionStage(myService.login(r)));

        }

        private Mono<List<Member<String>>> getMemberMono(boolean isRtm) {
            return getLogin("hj-kim", "1234")
                    .flatMap(body ->
                            client.post()
                                    .uri(URI.create("http://gw.dkitec.com:8080/intranet-api/member/list"))
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .acceptCharset(Charset.forName("UTF-8"))
                                    .cookie("SESSION_USER_ID", body.get("id"))
                                    .cookie("SESSION_USER_NAME", body.get("name"))
                                    .cookie("SESSION_CHECK_ID", body.get("type"))
                                    .cookie("SESSION_POST_NAME", body.get("post_name"))
                                    .cookie("SESSION_GD_NAME", body.get("gd_name"))
                                    .cookie("loginToken", body.get("loginToken"))
                                    .body(BodyInserters.fromObject(new MemberSearch("", "", isRtm ? "Y":"N", "", "")))
                                    .exchange()
                    )
                    .flatMap(c2 ->c2.bodyToMono(Map.class))
                    .flatMap(res2 ->Mono.fromCompletionStage(myService.list(res2)));
        }

    }


    public static void main(String[] args) {
        SpringApplication.run(CmiApplication.class, args);
    }
}
