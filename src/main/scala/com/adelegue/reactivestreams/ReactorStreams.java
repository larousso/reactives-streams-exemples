package com.adelegue.reactivestreams;

import reactor.Environment;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.List;

public class ReactorStreams {

    public static void main(String[] args) {
        Environment.initialize();
        List<String> mots = Arrays.asList("un", "deux", "trois", "quatre");

        Streams.from(mots)
                .flatMap(mot -> Streams.from(mot.split("")))
                .reduce(0, (acc, i) -> acc + 1)
                .consume(res -> System.out.println("Somme : " + res));
    }

}
