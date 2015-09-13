package com.adelegue.reactivestreams;

import static com.adelegue.reactivestreams.utils.Utils.print;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class RxJavaParallelise {

	private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public static void main(String[] args) throws InterruptedException{
		
		CountDownLatch countDownLatch = new CountDownLatch(1);

		List<Integer> ints = IntStream.range(0, 20).boxed().collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Observable
			.from(ints)
			.map(i -> {
                print("step1", i);
                return i;
            })
			.filter(i -> i % 2 == 0)
			//Ici on parallélise
            .flatMap(i ->
                Observable.just(i).observeOn(Schedulers.io())
                .map(j -> {
                    print("step2", j);
                    return j;
                })
                .map(String::valueOf)
                .flatMap(id -> Observable.from(getFromAsyncApi(id)))
                .map(j -> {
                    print("step3", j);
                    return j;
                })
            )
			.subscribe(
                    elt -> System.out.println(String.format("Next element %s", elt)),
                    err -> System.out.println(String.format("Error %s", err)),
                    () -> {
                        System.out.println("Completed");
                        countDownLatch.countDown();
                    }
            );
		
		countDownLatch.await();
	}
	

	public static Future<String> getFromAsyncApi(String id){
		return asyncHttpClient.prepareGet("http://localhost:8080/api/"+id).execute(new AsyncCompletionHandler<String>() {
			@Override
		    public String onCompleted(Response response) throws Exception{
				String responseBody = response.getResponseBody();
				print("Handling response", responseBody);
		        return responseBody;
		    }
		});
	}
	
}
