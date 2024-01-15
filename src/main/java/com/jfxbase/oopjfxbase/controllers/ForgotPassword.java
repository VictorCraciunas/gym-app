package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.*;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;


public class ForgotPassword extends SceneController {

    @FXML
    private TextField username;
    @FXML
    private TextField email;
    private boolean passwordMatches = false;


    @FXML
    public void sendClick(ActionEvent event){
        if (username.getText() != null && !"".equals(username.getText()) && // "".equals(name.getText() verifies that is not empty
                email.getText() != null && !"".equals(email.getText())) {

            String password = PostGreSQL.getPassFromDataBase(username.getText()); // get the password from DB

            User.getInstance().setUsername(username.getText());
            User.getInstance().setPassword(password);
            User.getInstance().setUserId(PostGreSQL.getIDforgotPass(username.getText(), password));


            if (password != null) {
                EmailSender.sendEmail(email.getText(), password);
                showPasswordDialog(password);

            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please insert a username and an email");
            alert.show();
        }
    }

    // Creating a pop-out to introduce the password sent by email
    private void showPasswordDialog(String password) {
        while (!passwordMatches)

        {
            // Create a modal dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Enter Password");
            dialog.initModality(Modality.APPLICATION_MODAL);

            // Set up the password input field
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Enter Password");

            // Add the password field to the dialog
            dialog.getDialogPane().setContent(passwordField);

            // Add buttons to the dialog
            ButtonType submitButtonType = new ButtonType("Submit", ButtonType.OK.getButtonData());
            dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

            // Enable/Disable the submit button depending on whether a password was entered
            dialog.getDialogPane().lookupButton(submitButtonType).setDisable(true);

            // Listen for changes in the password field and enable/disable the submit button accordingly
            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                dialog.getDialogPane().lookupButton(submitButtonType).setDisable(newValue.trim().isEmpty());
            });


            // Set the result converter for the dialog
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == submitButtonType) {
                    return passwordField.getText();
                }
                return null;
            });


            // Show the dialog and wait for user input
            dialog.showAndWait().ifPresent(enteredPassword -> {
                if (enteredPassword.equals(password)) { //check the password
                    this.passwordMatches = true; // set the the flag to true for leaving the while. In this way we leave the dialog
                    ApplicationHandler.getInstance().navigateToCalendar();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Incorrect password");
                    alert.showAndWait();
                }
            });
        }
    }
    @FXML
    protected void onGoToAnotherSceneClick(){
        this.changeScene(SCENE_IDENTIFIER.LOGIN);
    }
}
