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
import java.util.function.Function;
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
        // Show a file chooser dialog to the user and get file destination
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            // Convert the register to a string
            String registerStr = studentRegister.getRegister().values().stream()
                    .map(student -> student.getId() + "," + student.getName() + "," + student.getCourse() + ","
                            + student.getModule() + "," + student.getMarks())
                    .collect(Collectors.joining(System.lineSeparator()));

            // Save the string to the selected file
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.print(registerStr);
            } catch (IOException e) {
                // Handle the exception
            }
        }
    }

    public void handleDisplayAll() {
        if (studentRegister.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Data Loaded");
            alert.setHeaderText(null);
            alert.setContentText("No students registered.");
            alert.showAndWait();
            listView.getItems().clear();
        } else {
//            //studentRegister.printAllStudents();
//            listView.getItems().clear();
//            //listView.getItems().addAll(studentRegister.values().stream()
//            listView.getItems().addAll(studentRegister.getRegister().values().stream()
//                    .map(Student::toString1)
//                    .collect(Collectors.toList()));
            listView.getItems().clear();
            listView.getItems().addAll(studentRegister.getAllStudentStrings());
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
            alert.setContentText("ID and Marks must be numbers..");
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
                alert.setContentText("Student not found..");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid student ID..");
            alert.showAndWait();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error..");
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

        Optional.of(idText).filter(id -> !id.isBlank())
                .or(() -> Optional.of(nameText).filter(name -> !name.isBlank()))
                .or(() -> Optional.of(courseText).filter(course -> !course.isBlank()))
                .or(() -> Optional.of(moduleText).filter(module -> !module.isBlank()))
                .or(() -> Optional.of(marksText).filter(marks -> !marks.isBlank()))
                .ifPresentOrElse(criteria -> {
                    // at least one criteria provided
                    Predicate<Student> idPredicate = studentRegister.getIdPredicate(idText);
                    Predicate<Student> namePredicate = studentRegister.getNamePredicate(nameText);
                    Predicate<Student> coursePredicate = studentRegister.getCoursePredicate(courseText);
                    Predicate<Student> modulePredicate = studentRegister.getModulePredicate(moduleText);
                    Predicate<Student> marksPredicate = studentRegister.getMarksPredicate(marksText);

                    List<Student> matchingStudents = studentRegister.getRegister().values().stream()
                            .filter(idPredicate.and(namePredicate).and(coursePredicate).and(modulePredicate).and(marksPredicate))
                            .collect(Collectors.toList());

                    if (matchingStudents.isEmpty()) {
                        // no matching students found
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("No matching students found.");
                        alert.setHeaderText(null);
                        alert.setContentText("No matching students were found for the provided search criteria..");
                        alert.showAndWait();
                    } else {
                        // display matching students
                        listView.getItems().clear();
                        matchingStudents.forEach(student -> listView.getItems().add(student.toString1()));
                    }
                }, () -> {
                    // no criteria provided
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Search Criteria Required");
                    alert.setHeaderText(null);
                    alert.setContentText("Please provide at least one search criteria..");
                    alert.showAndWait();
                });
    }

}