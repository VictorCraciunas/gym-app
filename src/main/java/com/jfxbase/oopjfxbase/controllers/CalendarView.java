package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.*;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarView extends SceneController {
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label monthLabel;
    @FXML
    private Label yearLabel;

    private int month;
    private int year;

    public CalendarView() {
        // Initialize with the current month and year
        Calendar now = Calendar.getInstance();
        this.month = now.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        this.year = now.get(Calendar.YEAR);
    }

    @FXML
    private void initialize() {calendarBuilder();
    }

    private StackPane createStackPane(Text text, int MonthToInsert) {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(text);
        stackPane.setAlignment(Pos.CENTER);

        Integer dateToInsert = Integer.parseInt(text.getText());
        ArrayList<String> existingWorkout = PostGreSQL.isWorkout(dateToInsert,MonthToInsert,User.getInstance().getUserId());

        if (!existingWorkout.isEmpty()) {
            // Set the background color to blue
            Label label = new Label(PostGreSQL.getWorkout(existingWorkout.get(0)));
            stackPane.getChildren().add(label);
            StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
            stackPane.getStyleClass().add("workout-cell"); // Add a style class
        }

        // Add a mouse click event handler to the stackPane
        stackPane.setOnMouseClicked(event -> handleCellClick(text.getText(),MonthToInsert));

        // Add a listener to change cursor when the stackPane is added to a scene
        stackPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                stackPane.setOnMouseEntered(event -> newScene.setCursor(Cursor.HAND));
                stackPane.setOnMouseExited(event -> newScene.setCursor(Cursor.DEFAULT));
            }
        });

        return stackPane;
    }

    // Add a method to handle the cell click event
    private void handleCellClick(String date, int MonthToInsert) {
        // save the values for the next InsertWorkout scene
        SharedData.getInstance().setData(Integer.parseInt(date));
        SharedData.getInstance().setMonth(MonthToInsert);

        ApplicationHandler.getInstance().navigateToInsertWorkout();
    }

    private void clearCalendarGrid() {
        // Clear all children from the calendar grid except those in the first row
        if (!calendarGrid.getChildren().isEmpty()) {
            calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        }
    }

    private void calendarBuilder() {
        // Clears the calendar before rebuilding
        clearCalendarGrid();

        // Sets the calendar to the first day of the current month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);

        // Calculate the start day of the week for the first of the month
        int startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // Adjust the calendar to the start of the first week
        calendar.add(Calendar.DATE, -startDay);

        // update the month and year
        updateMonthLabel();
        updateYearLabel();

        // Populates the calendar grid for 6 weeks, covering all possible days
        for (int i = 1; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Text text = new Text(String.valueOf(calendar.get(Calendar.DATE)));
                StackPane stackPane = createStackPane(text,calendar.get(Calendar.MONTH)+1);
                stackPane.getStyleClass().add("calendar-cell");

                if (calendar.get(Calendar.MONTH) == month - 1) {
                    text.getStyleClass().add("date-text");
                    stackPane.getStyleClass().add("current-day");

                } else {
                    text.setFill(Color.GRAY); // Dates of the other months
                    stackPane.getStyleClass().add("other-month-day");
                }

                calendarGrid.add(stackPane, j, i);
                calendar.add(Calendar.DATE, 1); // Move to the next date
            }
        }
    }

    private void updateMonthLabel() {
        // Updates the label to display the current month
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthLabel.setText(monthNames[month - 1]);
    }

    private void updateYearLabel() {
        // Updates the label to display the current year
        yearLabel.setText(String.valueOf(year));
    }

    @FXML
    private void prevMonth() {
        // Moves to the previous month and rebuilds the calendar
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
        calendarBuilder();
    }

    @FXML
    private void nextMonth() {
        // Moves to the next month and rebuilds the calendar
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }
        calendarBuilder();
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