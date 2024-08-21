package com.example.supermarket.models;

import java.util.function.Consumer;
import java.util.function.BiPredicate;

public class GeneralList<T> {
    private Node<T> head;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public GeneralList() {
        this.head = new Node<>(null); // Dummy head node
    }

    public boolean insert(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.next = head.next;
        head.next = newNode;
        return true;
    }

    public boolean delete(T value, BiPredicate<T, T> comparator) {
        Node<T> current = head;
        while (current.next != null) {
            if (comparator.test(current.next.data, value)) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public T find(T value, BiPredicate<T, T> comparator) {
        Node<T> current = head.next;
        while (current != null) {
            if (comparator.test(current.data, value)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    public void forEach(Consumer<T> action) {
        Node<T> current = head.next;
        while (current != null) {
            action.accept(current.data);
            current = current.next;
        }
    }

    public boolean isEmpty() {
        return head.next == null;
    }

    public int size() {
        int count = 0;
        Node<T> current = head.next;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public void clear() {
        head.next = null;
    }
}