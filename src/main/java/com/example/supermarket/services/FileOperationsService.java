package com.example.supermarket.services;

import com.example.supermarket.models.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface FileOperationsService {
    void exportToCSV(List<?> data, String fileName, Stage stage);
    void importFromCSV(String entityType, Stage stage);
    String[] convertToCSV(Object item);
    void importEntity(String entityType, CSVRecord record) throws SQLException;
}