module com.example.supermarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires org.apache.commons.io;
    requires java.desktop;
    requires javafx.base;
    requires org.apache.commons.csv;

    opens com.example.supermarket to javafx.fxml;
    opens com.example.supermarket.controllers to javafx.fxml;
    opens com.example.supermarket.models to javafx.base;
    opens com.example.views to javafx.fxml;
    opens com.example.supermarket.components to javafx.fxml;


    exports com.example.supermarket;
    exports com.example.supermarket.controllers;
}