package org.phoenicis.javafx.collections;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ConcatenatedList<T> extends ObservableListBase<T> {
    private final ObservableList<? extends ObservableList<? extends T>> sources;
    private final Map<ObservableList<? extends T>, ListChangeListener<T>> listeners;
    private final List<T> backingList;

    private ConcatenatedList(ObservableList<? extends ObservableList<? extends T>> sources) {
        this.sources = sources;
        this.listeners = new IdentityHashMap<>();
        this.backingList = new ArrayList<>();

        bindSources();

        this.sources.addListener((ListChangeListener<ObservableList<? extends T>>) change -> {
            bindSources();
            refresh();
        });

        refresh();
    }

    public static <T> ConcatenatedList<T> create(ObservableList<? extends ObservableList<? extends T>> sources) {
        return new ConcatenatedList<>(sources);
    }

    @SafeVarargs
    public static <T> ConcatenatedList<T> create(ObservableList<? extends T>... sources) {
        return new ConcatenatedList<>(FXCollections.observableArrayList(sources));
    }

    @Override
    public T get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @SuppressWarnings("unchecked")
    private void bindSources() {
        listeners.forEach(ObservableList::removeListener);
        listeners.clear();

        for (ObservableList<? extends T> source : sources) {
            final ListChangeListener<T> listener = change -> refresh();
            ((ObservableList<T>) source).addListener(listener);
            listeners.put(source, listener);
        }
    }

    private void refresh() {
        final List<T> oldValues = new ArrayList<>(backingList);

        backingList.clear();
        for (ObservableList<? extends T> source : sources) {
            backingList.addAll(source);
        }

        beginChange();
        nextReplace(0, backingList.size(), oldValues);
        endChange();
    }
}
