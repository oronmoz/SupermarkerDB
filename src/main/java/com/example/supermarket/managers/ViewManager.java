package com.example.supermarket.managers;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class ViewManager {
    private StackPane contentArea;
    private Map<String, Node> views;

    public ViewManager(StackPane contentArea) {
        this.contentArea = contentArea;
        this.views = new HashMap<>();
    }

    public void addView(String name, Node view) {
        views.put(name, view);
    }

    public void showView(String name) {
        if (views.containsKey(name)) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(views.get(name));
        } else {
            throw new IllegalArgumentException("View not found: " + name);
        }
    }

    public void removeView(String name) {
        views.remove(name);
    }
}