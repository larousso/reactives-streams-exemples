package com.adelegue.reactivestreams;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.adelegue.reactivestreams.utils.Utils.print;

public class JavaStream {

    public static void main(String[] args) {
//        collections();
//        streams();
//        streamsBloquing();
        streamsAsync();
    }

    public static void collections() {
        List<String> minuscules = Arrays.asList("a", "b", "c", "d", "e");
        List<String> majuscules = new ArrayList<>();
        for (String elt : minuscules) {
            majuscules.add(elt.toUpperCase());
        }
        majuscules.forEach(System.out::println);

    }

    public static void mapFlatMap() {
        Stream<Integer> ints = Arrays.asList(1, 2, 3, 4).stream();
        Stream<String> strings = ints.map(i -> String.valueOf(i));

        Stream<String> phrases = Arrays.asList("abcd", "efg", "hijk", "lmnopq", "rst", "uvw", "xyz").stream();
        Stream<Stream<String>> letters = phrases.map(p -> Stream.of(p.split("")));

        Stream<String> letters2 = phrases.flatMap(p -> Stream.of(p.split("")));
    }

    public static void streams() {
        List<String> minuscule = Arrays.asList("a", "b", "c", "d", "e");
        List<String> majuscules = minuscule
                .stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        majuscules.forEach(System.out::println);

    }


    public static void streamsBloquing() {

        Stream.iterate(0, i -> i + 1)
                .map(String::valueOf)
                .map(id -> getFromApi(id))
                .map(String::toUpperCase)
                .forEach(System.out::println);
    }

    public static String getFromApi(String id){
        try {
            return asyncHttpClient.prepareGet("http://localhost:8080/api/" + id).execute().get().getResponseBody();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("Oups", e);
        }
    }

    public static void streamsAsync() {

        List<CompletableFuture<String>> results = IntStream.range(0, 20).boxed()
                .filter(i -> i % 2 == 0)
                .map(String::valueOf)
                .map(JavaStream::getFromAsyncApi)
                .map(value -> value.thenApply(String::toUpperCase))
                .collect(Collectors.toList());

        CompletableFuture<List<String>> futureList = sequence(results);
        futureList.thenAccept(list -> list.forEach(System.out::println));
    }

    public static void streamsAsync2() {

        IntStream.range(0, 20).boxed()
                .filter(i -> i % 2 == 0)
                .map(String::valueOf)
                .flatMap(id -> Stream.of(getFromAsyncApi(id)))
//                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(any -> futures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.<T>toList())
                );
    }

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public static CompletableFuture<String> getFromAsyncApi(String id){
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        asyncHttpClient.prepareGet("http://localhost:8080/api/" + id).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                completableFuture.complete(response.getResponseBody());
                return response;
            }

            @Override
            public void onThrowable(Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        });
        return completableFuture;
    }

    public static void subscribe() {
        Publisher<String> publisher = null;
        publisher.subscribe(new Subscriber<String>() {

            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext "+s);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Oups : "+throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("End");
            }
        });
    }


}
