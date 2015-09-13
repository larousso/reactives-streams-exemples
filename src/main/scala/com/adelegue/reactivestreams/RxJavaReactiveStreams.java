package com.adelegue.reactivestreams;

import com.ning.http.client.AsyncHttpClient;
import rx.Observable;
import rx.Subscriber;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.adelegue.reactivestreams.utils.Utils.print;

public class RxJavaReactiveStreams {

	public static void main(String[] args) throws InterruptedException{
		
		CountDownLatch countDownLatch = new CountDownLatch(1);

		List<Integer> ints = IntStream.range(1, 20).boxed().collect(Collectors.toList());

		Observable
			.from(ints)
			.map(i -> {
				print("step1", i);
				return i;
			})
			.filter(i -> i % 2 == 0)
			.map(String::valueOf)
			.subscribe(new Subscriber<String>() {

				@Override
				public void onStart() {
					request(1);
				}

				@Override
				public void onCompleted() {
					System.out.println("Completed");
					countDownLatch.countDown();
				}

				@Override
				public void onError(Throwable throwable) {
					System.out.println("Oups : " + throwable.getMessage());
				}

				@Override
				public void onNext(String s) {
					System.out.println("Next : " + s);
					//On requête l'éléments suivant :
                    request(1);
				}
			});
		
		countDownLatch.await();
    }
	
}
