package com.example.newgui;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HelloController implements Initializable {
    ObservableList courseList = FXCollections.observableArrayList(
            "Mathematics","Physics","Engineering","Computer Science");
    ObservableList modulesList = FXCollections.observableArrayList(
            "Programming 1","Programming 2","Data Structures","Calculus 1","Linear Algebra","Probability","Statistics","Electromagnetism","Thermodynamics","Methods in Mechanics");
    @FXML
    private ChoiceBox<String> courses;
    @FXML
    private ChoiceBox<String> modules;
    @FXML
    private ListView<String> listView;




    //@FXML
    //protected void onHelloButtonClick() {
    //    welcomeText.setText("Welcome to JavaFX Application!");
    //}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        loadCourse();
        loadModule();
    }

    private void loadCourse(){
//        courseList.removeAll(courseList);
//        String a = "Mathematics";
//        String b = "Physics";
//        String c = "Engineering";
//        String d = "Computer Science";
//        courseList.addAll(a, b, c, d);
        courses.getItems().addAll(courseList);
    }
    private void loadModule(){
//        modulesList.removeAll(modulesList);
//        String a = "Programming 1";
//        String b = "Programming 2";
//        String c = "Data Structures";
//        String d = "Calculus 1";
//        String e = "Linear Algebra";
//        String f = "Probability";
//        String g = "Statistics";
//        String h = "Electromagnetism";
//        String i = "Thermodynamics";
//        String j = "Methods in Mechanics";
//        modulesList.addAll(a, b, c, d, e, f, g, h, i, j);
        modules.getItems().addAll(modulesList);
    }

    @FXML
    public void handleFileOpen() {
        // Show a file chooser dialog to let the user select a file
        FileChooser fileChooser = new FileChooser();
        Optional.ofNullable(fileChooser.showOpenDialog(null))
                .ifPresent(selectedFile -> {
                    try {
                        // Read the contents of the file into a list of strings
                        List<String> lines = Files.readAllLines(selectedFile.toPath(), StandardCharsets.UTF_8);

                        // Clear the existing items in the list view and add the lines from the file
                        listView.getItems().clear();
                        listView.getItems().addAll(lines.stream().collect(Collectors.toList()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }
}