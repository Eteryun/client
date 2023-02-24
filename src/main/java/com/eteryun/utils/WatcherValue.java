package com.eteryun.utils;

import com.google.common.base.Supplier;

import java.util.function.BiFunction;

public class WatcherValue<T> {
    private final Supplier<T> supplier;
    private final BiFunction<T, T, Boolean> equals;
    private T value;
    private final Watcher<T> watcher;

    public WatcherValue(Supplier<T> initialValue, Watcher<T> watcher) {
        this.supplier = initialValue;
        this.watcher = watcher;
        this.value = supplier.get();
        this.equals = Object::equals;
    }

    public WatcherValue(Supplier<T> initialValue, Watcher<T> watcher, BiFunction<T, T, Boolean> equals) {
        this.supplier = initialValue;
        this.watcher = watcher;
        this.value = supplier.get();
        this.equals = equals;
    }

    public T get() {
        return supplier.get();
    }

    public void notifyWatcher() {
        watcher.onChange(supplier.get());
    }

    public void tick() {
        if (get() == null) return;
        if (!this.equals.apply(get(), value)) {
            value = get();
            notifyWatcher();
        }
    }

    public interface Watcher<T> {
        void onChange(T newValue);
    }
}
