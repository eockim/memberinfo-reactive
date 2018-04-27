package com.hj.cmi.react;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class FutureEx {

    interface SuccessCalback{
        void onSuccess(String result);
    }

    interface ExceptionCallbak{
        void onError(Throwable t);
    }

    public static class CallbackFutreTask extends FutureTask<String>{

        SuccessCalback sc;
        ExceptionCallbak ec;

        public CallbackFutreTask(Callable<String> callable, SuccessCalback sc, ExceptionCallbak ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutreTask f =new CallbackFutreTask(() -> {
            Thread.sleep(2000);
            if(1 ==1) throw new RuntimeException("Async ERROR");
            log.info("Async");
            return "Hello";
        },
                System.out::println,
                e-> System.out.println("Error: " + e.getMessage()));


        es.execute(f);
        es.shutdown();
//        Future<String> f = es.submit(() ->{
//           Thread.sleep(2000);
//           log.info("Async");
//           return "Hello";
//        });

//        Future<String> f2 = es.submit(() ->{
//            Thread.sleep(2000);
//            log.info("Async2");
//            return "Hello2";
//        });
        System.out.println(f.isDone());
        Thread.sleep(2100);
        log.info("Exit");
        System.out.println(f.isDone());
        System.out.println(f.get());

       //System.out.println("exit");

    }
}
