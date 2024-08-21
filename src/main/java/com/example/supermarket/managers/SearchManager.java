package com.example.supermarket.managers;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchManager<T> {
    private List<T> items;

    public SearchManager(List<T> items) {
        this.items = items;
    }

    public List<T> search(Predicate<T> criteria) {
        return items.stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }

    public void updateItems(List<T> newItems) {
        this.items = newItems;
    }
}