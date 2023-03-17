package com.example.student;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentRegister {

    private static ConcurrentHashMap<Integer, Student> register = new ConcurrentHashMap<>();
    public static StudentRegister studentRegister = new StudentRegister(register);
    private static final String FILENAME = "student_register.txt";
    private static StudentRegisterFileHandler fileHandler;


    public static ConcurrentHashMap<Integer, Student> getRegister() {
        return register;
    }

    public static void setRegister(ConcurrentHashMap<Integer, Student> register) {
        StudentRegister.register = register;
    }
    /*Method to create studentregister objects with no arguments - used in testing*/
//    public StudentRegister() {
//            this.register = register;
//	}

    /*constructor to be used for file I/O*/
    public StudentRegister(ConcurrentHashMap<Integer, Student> register) {
        StudentRegister.register = register;
    }
    public boolean isEmpty() {
        return register.isEmpty();
    }

//    public static void saveFile() {
//        //ADD the save method after a student has been added so when next
//        //query is loaded the new student will be part of it
//        try {
//            StudentRegisterFileHandler.save(register);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
    public void saveFile(Path filePath) throws IOException {
        StudentRegisterFileHandler handler = new StudentRegisterFileHandler();
        handler.save(register, filePath);
    }

//    public static void loadFile() {
//        fileHandler = new StudentRegisterFileHandler();
//        try {
//            register = fileHandler.load(FILENAME);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //return register;
//    }


    public void addStudent(Student student) {
        Optional.ofNullable(student)
                .filter(s -> s.getId() > 0
                        && s.getName() != null && !s.getName().isEmpty()
                        && s.getCourse() != null && !s.getCourse().isEmpty()
                        && s.getModule() != null && !s.getModule().isEmpty()
                        && s.getMarks() >= 0 && s.getMarks() <= 100)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student data.."));
        register.entrySet()
                .stream()
                .filter(entry -> entry.getKey() == student.getId())
                .findAny()
                .ifPresent(entry -> {
                    throw new IllegalStateException("Student ID already in use..");
                });
        register.put(student.getId(), student);
        // saveFile();
    }


    public Student removeStudent(int id) {
        Optional<Student> student = Optional.ofNullable(register.get(id));
        student.orElseThrow(() -> new IllegalArgumentException("Student not found.."));
        register.remove(id);
        // saveFile();
        return student.get();

    }


    public Stream<Student> getAllStudents() {
        return register.values().stream();


    }


public List<String> getAllStudentStrings() {
    return register.values().stream().map(Student::toString1).collect(Collectors.toList());
}



    public void getStudentsByName(String name) {
        register.values().stream()
                .filter(student -> student.getName().equalsIgnoreCase(name))
                .forEach(System.out::println);
    }


    public void getStudentsByNameStartingWith(String letter) {
        register.values().stream()
                .filter(student -> student.getName().startsWith(letter))
                .forEach(System.out::println);
    }
    public Predicate<Student> getNamePredicate(String nameText) {
        return s -> nameText.isEmpty() || s.getName().toLowerCase().startsWith(nameText);
    }


    public void getStudentById(int id) {
        register.values().stream()
                .filter(student -> student.getId() == id)
                .findFirst()
                .ifPresent(System.out::println);
    }
    public Predicate<Student> getIdPredicate(String idText) {
        return s -> idText.isEmpty() || Integer.toString(s.getId()).equals(idText);
    }


    public void getStudentByCourse(String course) {
        register.values().stream()
                .filter(student -> student.getCourse().equalsIgnoreCase(course))
                .forEach(System.out::println);
    }
    public Predicate<Student> getCoursePredicate(String courseText) {
        return s -> courseText.isEmpty() || s.getCourse().toLowerCase().startsWith(courseText);
    }

    public void getStudentByModule(String module) {
        register.values().stream()
                .filter(student -> student.getModule().equalsIgnoreCase(module))
                .forEach(System.out::println);
    }
    public Predicate<Student> getModulePredicate(String moduleText) {
        return s -> moduleText.isEmpty() || s.getModule().toLowerCase().startsWith(moduleText);
    }
    public Predicate<Student> getMarksPredicate(String marksText) {
        return s -> marksText.isEmpty() || Integer.toString(s.getMarks()).equals(marksText);
    }

    public void getStudentsByModuleAndSortByMarksDescending(String module) {
        register.values().stream()
                .filter(student -> student.getModule().equalsIgnoreCase(module))
                .sorted((student1, student2) -> student2.getMarks() - student1.getMarks())
                .forEach(System.out::println);
    }

    public void getModuleAverageMark(String module) {
        double averageMark = register.values().stream()
                .filter(student -> student.getModule().contains(module))
                .mapToDouble(student -> student.getMarks())
                .average()
                .orElse(Double.NaN);

        //System.out.println("The average mark for " + module + " is: " + averageMark);
        System.out.printf("The average mark for %s is: %.2f%%\n", module, averageMark);
    }









    public static void main(String[] args) {



    }
}