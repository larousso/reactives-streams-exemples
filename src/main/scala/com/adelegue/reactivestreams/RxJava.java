package com.adelegue.reactivestreams;

import static com.adelegue.reactivestreams.utils.Utils.print;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import rx.Observable;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class RxJava {

	private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public static void main(String[] args){

		List<Integer> ints = IntStream.range(0, 20).boxed().collect(Collectors.toList());

        Observable
			.from(ints)
			.map(i -> {print("step1", i); return i;})
			.filter(i -> i % 2 == 0)
			.map(String::valueOf)
			.concatMap(id -> Observable.from(getFromAsyncApi(id)))
			.map(i -> {print("step2", i); return i;})
			.subscribe(
				elt -> System.out.println(String.format("Next element %s", elt)),
				err -> System.out.println(String.format("Error %s", err)),
				() -> System.out.println("Completed")
			);
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
