package com.candy.sort;

import com.candy.model.Sweet;
import java.util.Comparator;

public enum SweetOrder implements Comparator<Sweet> {
    BY_NAME(byName()),
    BY_ENERGY(Comparator.<Sweet>comparingInt(Sweet::energy)
            .thenComparing(byName())),
    BY_PRODUCTION(Comparator
            .comparing(Sweet::production, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(byName()));

    private final Comparator<Sweet> delegate;

    SweetOrder(Comparator<Sweet> delegate) {
        this.delegate = delegate;
    }

    private static Comparator<Sweet> byName() {
        return Comparator.comparing(Sweet::name, String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public int compare(Sweet first, Sweet second) {
        return delegate.compare(first, second);
    }
}