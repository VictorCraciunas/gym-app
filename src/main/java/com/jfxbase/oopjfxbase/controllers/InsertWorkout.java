package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.*;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Map;

public class InsertWorkout extends SceneController {

    private int date;
    private int month;

    @FXML
    private Label monthLabel;
    @FXML Label dateLabel;

    @FXML
    private ComboBox<String> comboBoxWorkout;
    @FXML
    private ComboBox<Integer> comboBoxNrExercises;
    @FXML
    private VBox vboxExercises; // Container for dynamic ComboBoxes
    @FXML
    private Pane paneWorkout;
    @FXML
    private Button save;


    public void initialize() {

        // Apply styles to the ComboBoxes
        comboBoxWorkout.getStyleClass().add("combo-box");
        comboBoxNrExercises.getStyleClass().add("combo-box");

        // Apply styles to the VBox

        save.getStyleClass().add("save-button");


        date = SharedData.getInstance().getData();
        month = SharedData.getInstance().getMonth();
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        if (month-1 >= 0) {
            monthLabel.setText(monthNames[month-1]);
        }
        dateLabel.setText(Integer.toString(date));

        ArrayList<String> existingWorkout = PostGreSQL.isWorkout(date,month,User.getInstance().getUserId());

        if (existingWorkout.isEmpty()) {
            initializeForNoWorkout();
        } else {
            handleExistingWorkout(existingWorkout);
        }
    }


    private void initializeForNoWorkout() {


        // We need the map function because we need the id of a chosen workout
        Map<Integer, String> workoutNames = PostGreSQL.getWorkoutNamesFromDatabase();

        // Dropdown for choosing a workout
        comboBoxWorkout.setItems(FXCollections.observableArrayList(workoutNames.values()));

        // Dropdown for choosing the number of exercises
        comboBoxNrExercises.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // We choose the exercises if we change the NrExercises
        comboBoxNrExercises.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (comboBoxWorkout.getValue() != null) {
                vboxExercises.getStyleClass().add("form-pane");
                createDropDownsExercises(newValue);
            }
        });

        // We choose the exercises if we change the workout while having a NrExercises
        comboBoxWorkout.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (comboBoxNrExercises.getValue() != null) {
                vboxExercises.getStyleClass().add("form-pane");
                createDropDownsExercises(comboBoxNrExercises.getValue());
            }
        });
    }


    private void handleExistingWorkout(ArrayList<String> existingWorkout) {


        paneWorkout.getChildren().clear();

        // Clear previous rows if any
        vboxExercises.getChildren().clear();

        vboxExercises.getStyleClass().add("form-pane");


        int k=0; // with k we get the reps/sets/kg of a specific exercise from the DB
        for (String exercise : existingWorkout) {

            HBox row = new HBox(10);
            Label exerciseLabel = new Label(exercise);
            exerciseLabel.getStyleClass().add("label-workout");
//            exerciseLabel.setMaxWidth(Double.MAX_VALUE); // Allow the label to expand
//            exerciseLabel.setWrapText(true);

            ArrayList<Integer> nrSets = PostGreSQL.getSets(SharedData.getInstance().getData(), SharedData.getInstance().getMonth(),User.getInstance().getUserId());
            ArrayList<Integer> nrReps = PostGreSQL.getReps(SharedData.getInstance().getData(), SharedData.getInstance().getMonth(),User.getInstance().getUserId());
            ArrayList<Integer> Kg = PostGreSQL.getKg(SharedData.getInstance().getData(), SharedData.getInstance().getMonth(),User.getInstance().getUserId());

            Label labelSets=new Label("Sets: " + (nrSets.get(k)));
            Label labelReps=new Label("Reps: "+ (nrReps.get(k)));
            Label labelKg=new Label("Kg: " + (Kg.get(k)));
            k++;

            labelSets.getStyleClass().add("label-sets-reps-kg");
            labelReps.getStyleClass().add("label-sets-reps-kg");
            labelKg.getStyleClass().add("label-sets-reps-kg");

            row.getChildren().addAll(exerciseLabel, labelSets, labelReps, labelKg);
            vboxExercises.getChildren().add(row);

        }

        // Hide the comboBoxes
        comboBoxWorkout.setVisible(false);
        comboBoxNrExercises.setVisible(false);
        save.setVisible(false);


        // Display the Workout that you had in that day
        String workoutName=PostGreSQL.getWorkout(existingWorkout.get(0));
        Label workoutLabel=new Label(workoutName);
        workoutLabel.getStyleClass().add("workout-name");
        paneWorkout.getChildren().add(workoutLabel);
    }


    private void createDropDownsExercises(int nrOfExercises) {
        vboxExercises.getChildren().clear(); // Clear previous rows

        String selectedWorkout = comboBoxWorkout.getValue(); // Get the selected workout name

        if (selectedWorkout != null) {
            Integer selectedWorkoutId = getWorkoutId(selectedWorkout);

            for (int i = 0; i < nrOfExercises; i++) {
                HBox row = new HBox(10);

                // Create exercise dropdown
                ComboBox<String> exerciseComboBox = createDropDown(selectedWorkoutId);

                // Create text fields for sets, reps, and kg
                TextField setsField = createTextField("Sets");
                TextField repsField = createTextField("Reps");
                TextField kgField = createTextField("Kg");

                setsField.getStyleClass().add("text-field1");
                repsField.getStyleClass().add("text-field1");
                kgField.getStyleClass().add("text-field1");

                // Add components to the row
                row.getChildren().addAll(exerciseComboBox, setsField, repsField, kgField);

                vboxExercises.getChildren().add(row); // Add the row to the VBox
            }
        }
    }

    private ComboBox<String> createDropDown(Integer workoutId) {
        ComboBox<String> comboBox = new ComboBox<>();
        Map<Integer, String> exerciseNames = PostGreSQL.getExercisesFromDatabase(workoutId);
        comboBox.setItems(FXCollections.observableArrayList(exerciseNames.values()));
        return comboBox;
    }


    private Integer getWorkoutId(String workoutName) {
        Map<Integer, String> workoutNames = PostGreSQL.getWorkoutNamesFromDatabase();
        for (Map.Entry<Integer, String> entry : workoutNames.entrySet()) {
            if (entry.getValue().equals(workoutName)) {
                return entry.getKey();
            }
        }
        return null; // Return null if not found
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.getStyleClass().add("text-field");
        return textField;
    }
    @FXML
    private void SaveWorkoutInDB() {
        Integer userId = User.getInstance().getUserId();

        // Iterate over each row in vboxExercises
        for (Node node : vboxExercises.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                ComboBox<String> exerciseComboBox = (ComboBox<String>) row.getChildren().get(0);
                TextField setsField = (TextField) row.getChildren().get(1);
                TextField repsField = (TextField) row.getChildren().get(2);
                TextField kgField = (TextField) row.getChildren().get(3);

                // Parse values and handle potential exceptions/formatting issues
                try {
                    Integer exerciseId = getExerciseId(exerciseComboBox.getValue());
                    Integer sets = Integer.parseInt(setsField.getText());
                    Integer reps = Integer.parseInt(repsField.getText());
                    Integer kg = Integer.parseInt(kgField.getText());

                    // Call method to insert data into the database
                    PostGreSQL.insertUserWorkout(exerciseId, sets, reps, userId, kg, SharedData.getInstance().getData(), SharedData.getInstance().getMonth());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format");
                }
            }
        }
    }

    private Integer getExerciseId(String exerciseName) {
        // First, get the selected workout's ID
        String selectedWorkout = comboBoxWorkout.getValue();
        if (selectedWorkout == null) {
            return null; // No workout selected
        }

        Integer workoutId = getWorkoutId(selectedWorkout);
        if (workoutId == null) {
            return null; // Workout ID not found
        }

        // Fetch exercises for the selected workout
        Map<Integer, String> exerciseNames = PostGreSQL.getExercisesFromDatabase(workoutId);
        for (Map.Entry<Integer, String> entry : exerciseNames.entrySet()) {
            if (entry.getValue().equals(exerciseName)) {
                return entry.getKey();
            }
        }

        return null; // Exercise ID not found
    }

    @FXML
    private void goToHomePage(){
        ApplicationHandler.getInstance().navigateToCalendar();
    }
    @FXML
    private void goToAccount(){
        ApplicationHandler.getInstance().navigateToAccount();
    }
    @FXML
    protected void goToLogin(){
        this.changeScene(SCENE_IDENTIFIER.LOGIN);
    }

}