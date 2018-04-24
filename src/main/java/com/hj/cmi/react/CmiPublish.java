package com.hj.cmi.react;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CmiPublish {

    public static void main(String[] args){

        Publisher<Integer> pub = iterPub(Stream.iterate(1, x -> x + 1).limit(10).collect(Collectors.toList()));
        //Publisher<List> mapPub = mapPub(pub, s -> Collections.singletonList(s));
        //Publisher<Integer> sumPub = sumPub(pub);
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b+",") .append(".."));
        reducePub.subscribe(logSub());

    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {

                pub.subscribe(new DelegateSub<T, R>(sub){

                    R result = init;

                    @Override
                    public void onNext(T integer) {
                        result = bf.apply(result, integer);
                    }

                    //
//                    @Override
//                    public void onNext(T integer) {
//                        result = bf.apply(result, integer);
//                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

    private static <T> Publisher<T> sumPub(Publisher<T> pub) {
        return new Publisher<T>() {
            @Override
            public void subscribe(Subscriber<? super T> sub) {
                pub.subscribe(new DelegateSub(sub){
                    int sum = 0;

                    @Override
                    public void onNext(Object integer) {
                        sub.onNext(integer);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(sum);
                        sub.onComplete();
                    }

                });
            }
        };
    }

    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
            return new Publisher<R>(){
                @Override
                public void subscribe(Subscriber<? super R> sub) {
                    pub.subscribe(new DelegateSub<T, R>(sub) {
                        @Override
                        public void onNext(T integer) {
                            sub.onNext(f.apply(integer));
                        }
                    });
                }
            };
    }
//,
    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
                log.debug("onSubscribe");
            }

            @Override
            public void onNext(T integer) {
                log.debug("onNext : {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError : {}", t);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(Iterable<Integer> iter) {
        return new Publisher<Integer>() {

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {

                    @Override
                    public void request(long n) {

                        try{
                            iter.forEach(s->sub.onNext(s));
                            sub.onComplete();
                        }catch(Throwable t){
                            sub.onError(t);
                        }

                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }

}
