package com.adelegue.reactivestreams;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.Environment;
import reactor.rx.Stream;
import reactor.rx.Streams;
import rx.Observable;
import rx.RxReactiveStreams;
import scala.runtime.BoxedUnit;

import java.util.Arrays;

public class Interrop {

    public static void main(String[] args) {
        //Init
        Environment.initialize();
        final ActorSystem system = ActorSystem.create("interrop");
        final Materializer mat = ActorMaterializer.create(system);

        //Rx
        Observable<Integer> from = Observable.from(Arrays.asList(1, 2, 3, 4, 5)).doOnEach(elt -> System.out.println("Rx : " + elt)).map(integ -> integ + 1);
        Publisher<Integer> integerPublisher = RxReactiveStreams.toPublisher(from);

        //Reactor
        Stream<String> stream = Streams.wrap(integerPublisher).map(String::valueOf).map(elt -> {
            System.out.println("Reactor : " + elt);
            return elt;
        });

        //Akka
        Source<Integer, BoxedUnit> source = Source.from(stream).map(Integer::valueOf).map(elt -> elt * 10).map(elt -> {
            System.out.println("Akka : " + elt);
            return elt;
        });
        Sink<Integer, Publisher<Integer>> publisher = Sink.publisher();

        Publisher<Integer> akkaPublisher = source.runWith(publisher, mat);

        akkaPublisher.subscribe(new Subscriber<Integer>() {

            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("New value : " + integer);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Oups : " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Finished");
            }
        });

    }

}
