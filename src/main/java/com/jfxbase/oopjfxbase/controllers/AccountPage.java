package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.*;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

public class AccountPage extends SceneController {

    @FXML
    private ComboBox<String> comboBoxWorkout;
    @FXML
    private VBox vboxExercises;
    @FXML
    private Button account;
    @FXML
    private Label accountLabel;


    @FXML
    private void initialize() {

        accountLabel.setText(User.getInstance().getUsername());

        // We need the map function because we need the id of a chosen workout
        Map<Integer, String> workoutNames = PostGreSQL.getWorkoutNamesFromDatabase();

        // Dropdown for choosing a workout
        comboBoxWorkout.setItems(FXCollections.observableArrayList(workoutNames.values()));

        // Add a listener for ComboBox selection change
        comboBoxWorkout.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            vboxExercises.getStyleClass().add("form-pane");
            createDynamicButtons();
        });

        createDynamicButtons(); // Initially create buttons based on the default selection

        if(Admin.getInstance().getUsername().equals("admin")){
            account.setVisible(false);
        }
    }



    @FXML
    private void goToHomePage(){

        if (Admin.getInstance().getUsername().equals("admin")) {
            this.changeScene(SCENE_IDENTIFIER.AdminPage);
        }
        else{
            ApplicationHandler.getInstance().navigateToCalendar();
        }
    }
    @FXML
    private void goToAccount(){
        ApplicationHandler.getInstance().navigateToAccount();
    }
    @FXML
    protected void goToLogin(){
        this.changeScene(SCENE_IDENTIFIER.LOGIN);
    }

    private void createDynamicButtons() {
        vboxExercises.getChildren().clear();

        String selectedWorkout = comboBoxWorkout.getValue();
        if (selectedWorkout != null) {
            Integer selectedWorkoutId = getWorkoutId(selectedWorkout);
            Map<Integer, String> exerciseNames = PostGreSQL.getExercisesFromDatabase(selectedWorkoutId);
            for (String exerciseName : exerciseNames.values()) {
                createButtonWithLabel(exerciseName);
            }
        }
    }
    private void createButtonWithLabel(String exerciseName) {
        Button button = new Button(exerciseName);
        button.getStyleClass().add("button-exercises");

        Label prLabel = new Label();
        prLabel.getStyleClass().add("label-PR");
        prLabel.setVisible(false); // Initially, the label is invisible

        button.setOnAction(event -> {
            // Toggle the visibility of the PR label
            togglePrVisibility(button, exerciseName, prLabel);
        });

        HBox hbox = new HBox(40); // Set the spacing to 10px
        hbox.getChildren().addAll(button, prLabel);
        vboxExercises.getChildren().add(hbox);
    }

    private void togglePrVisibility(Button button, String exerciseName, Label prLabel) {
        if (!prLabel.isVisible()) {
            // If the label is invisible, fetch the PR, update and show the label
            Integer pr = PostGreSQL.getPR(exerciseName,User.getInstance().getUserId());
            prLabel.setText("PR: " + pr.toString() + " kg");
            prLabel.setVisible(true);
        } else {
            // If the label is visible, hide it
            prLabel.setVisible(false);
        }
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
}
