package org.abhijitsarkar.ufo.service;

import reactor.core.publisher.Flux;

import java.util.Map;

import static java.util.Map.Entry.comparingByKey;

/**
 * @author Abhijit Sarkar
 */
public class PrettyPrinter {
    private static final String FORMAT = "| %-12s | %-8s |%n";

    public static void print(Map<String, Integer> map, String keyHeader, String valueHeader) {
        System.out.format("+--------------+----------+%n");
        System.out.format(FORMAT, keyHeader, valueHeader);
        System.out.format("+--------------+----------+%n");

        if (map != null) {
            Flux.fromIterable(map.entrySet())
                    .sort(comparingByKey())
                    .zipWith(Flux.range(1, Integer.MAX_VALUE))
                    .subscribe(t -> {
                        Map.Entry<String, Integer> e = t.getT1();
                        System.out.format(FORMAT, e.getKey(), e.getValue());
                    });
        }

        System.out.format("+--------------+----------+%n");
    }
}
