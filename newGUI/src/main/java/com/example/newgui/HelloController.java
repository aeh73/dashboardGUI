package com.example.newgui;

import com.example.student.Student;
import com.example.student.StudentRegister;
import com.example.student.StudentRegisterFileHandler;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.example.student.StudentRegister.studentRegister;


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
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField courseField;
    @FXML
    private TextField moduleField;
    @FXML
    private TextField marksField;
    private Path filePath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        loadCourse();
        loadModule();
    }

    public void clearInputs(){
        idField.clear();
        nameField.clear();
        courseField.clear();
        moduleField.clear();
        marksField.clear();
    }
    private void loadCourse(){

        courses.getItems().addAll(courseList);
    }
    private void loadModule(){

        modules.getItems().addAll(modulesList);
    }

    @FXML
    private void handleFileOpen() {
        // Show a file chooser dialog to let the user select a file
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Store the file path
                filePath = selectedFile.toPath();

                // Load the contents of the file into a ConcurrentHashMap
                StudentRegisterFileHandler fileHandler = new StudentRegisterFileHandler();
                ConcurrentHashMap<Integer, Student> registerData = fileHandler.load(filePath);

                // Clear the existing items in the list view and add the students from the register
                listView.getItems().clear();
                listView.getItems().addAll(registerData.values().stream()
                        .map(Student::toString1) // use the toString method of Student
                        .collect(Collectors.toList()));

                // Set the register to the new ConcurrentHashMap
                studentRegister.setRegister(registerData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void handleSaveFile() throws IOException {
        // Show a file chooser dialog to the user and get the selected file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // Create a PrintWriter to write to the selected file
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write the header row
                writer.println("Student ID, Name, Course, Module, Marks");

                // Write the data rows
                ObservableList<Student> items = listView.getItems();
                for (Student student : items) {
                    writer.println(student.toCsvString());
                }
            } catch (IOException e) {
                // Handle the exception
            }
        }
    }
//    @FXML
//    private void handleSaveFile() {
//        // Show a file save dialog to let the user select a file to save to
//        FileChooser fileChooser = new FileChooser();
//        File selectedFile = fileChooser.showSaveDialog(null);
//        if (selectedFile != null) {
//            try {
//                // Get the data to save from the list view
//                List<Student> students = listView.getItems().stream()
//                        .map(Student::fromString1) // use the static method fromString to create a new student object from the string representation
//                        .collect(Collectors.toList());
//
//                // Convert the list of students to a string representation
//                String data = students.stream()
//                        .map(Student::toString2) // use the toString2 method of Student to get a string representation of each student object
//                        .collect(Collectors.joining("\n"));
//
//                // Save the data to the selected file
//                Files.write(selectedFile.toPath(), data.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public void handleFileOpen() {
//        // Show a file chooser dialog to let the user select a file
//        FileChooser fileChooser = new FileChooser();
//        File selectedFile = fileChooser.showOpenDialog(null);
//        if (selectedFile != null) {
//            try {
//                // Load the contents of the file into a ConcurrentHashMap
//                StudentRegisterFileHandler fileHandler = new StudentRegisterFileHandler();
//                ConcurrentHashMap<Integer, Student> studentRegister = fileHandler.load(selectedFile.toPath());
//
//                // Clear the existing items in the list view and add the students from the register
//                listView.getItems().clear();
//                listView.getItems().addAll(studentRegister.values().stream()
//                        .map(Student::toString1) // use the toString method of Student
//                        .collect(Collectors.toList()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    /*NEEDS WORK DOESNT REGISTER THE REGISTER */
    public void handleDisplayAll() {
        if (studentRegister.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Data Loaded");
            alert.setHeaderText(null);
            alert.setContentText("No students registered.");
            alert.showAndWait();
            listView.getItems().clear();
        } else {
            //studentRegister.printAllStudents();
            listView.getItems().clear();
            //listView.getItems().addAll(studentRegister.values().stream()
            listView.getItems().addAll(studentRegister.getRegister().values().stream()
                    .map(Student::toString1)
                    .collect(Collectors.toList()));
        }
    }

    public void handleClearDisplay() {
        listView.getItems().clear();
    }

    @FXML
    private void handleAddStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String course = courseField.getText();
            String module = moduleField.getText();
            int marks = Integer.parseInt(marksField.getText());

            Student student = new Student(id, name, course, module, marks);
            studentRegister.addStudent(student);
            listView.getItems().add(student.toString1());

            // Save the updated student register to file
            studentRegister.saveFile(filePath);
            clearInputs();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("The student has been successfully added.");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid student ID");
            alert.showAndWait();
        } catch (IllegalArgumentException | IllegalStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Saving File");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while saving the student register to file.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleRemoveStudent(){
        try {
            int id = Integer.parseInt(idField.getText());
            Student removedStudent = studentRegister.removeStudent(id);
            if (removedStudent != null) {
                // Update the list view
                listView.getItems().remove(removedStudent.toString1());

                // Save the updated student register to file
                studentRegister.saveFile(filePath);
                clearInputs();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("The student has been successfully removed.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Student not found");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid student ID");
            alert.showAndWait();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void handleSearch() {
        String idText = idField.getText();
        String nameText = nameField.getText().toLowerCase();
        String courseText = courseField.getText().toLowerCase();
        String moduleText = moduleField.getText().toLowerCase();
        String marksText = marksField.getText();

        Predicate<Student> idPredicate = studentRegister.getIdPredicate(idText);
        Predicate<Student> namePredicate = studentRegister.getNamePredicate(nameText);
        Predicate<Student> coursePredicate = studentRegister.getCoursePredicate(courseText);
        Predicate<Student> modulePredicate = studentRegister.getModulePredicate(moduleText);
        Predicate<Student> marksPredicate = studentRegister.getMarksPredicate(marksText);

        List<Student> matchingStudents = studentRegister.getRegister().values().stream()
                .filter(idPredicate.and(namePredicate).and(coursePredicate).and(modulePredicate).and(marksPredicate))
                .collect(Collectors.toList());

        listView.getItems().clear();
        matchingStudents.forEach(student -> listView.getItems().add(student.toString1()));
    }
//    @FXML
//    public void handleFileOpen() {
//        // Show a file chooser dialog to let the user select a file
//        FileChooser fileChooser = new FileChooser();
//        Optional.ofNullable(fileChooser.showOpenDialog(null))
//                .ifPresent(selectedFile -> {
//                    try {
//                        // Read the contents of the file into a list of strings
//                        List<String> lines = Files.readAllLines(selectedFile.toPath(), StandardCharsets.UTF_8);
//
//                        // Clear the existing items in the list view and add the lines from the file
//                        listView.getItems().clear();
//                        listView.getItems().addAll(lines.stream().collect(Collectors.toList()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                });
//
//    }
}