package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.ApplicationHandler;
import com.jfxbase.oopjfxbase.utils.PostGreSQL;
import com.jfxbase.oopjfxbase.utils.SceneController;
import com.jfxbase.oopjfxbase.utils.User;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SingUpController extends SceneController {
    @FXML
    private TextField name;

    @FXML
    private PasswordField pass;

    @FXML PasswordField checkpass;
    @FXML
    public void getData(ActionEvent actionEvent){
        System.out.println(name.getText());
        System.out.println(pass.getText());
        System.out.println(checkpass.getText());



        if (name.getText() != null && !"".equals(name.getText()) &&  // "".equals(name.getText() verifies that is not empty
                pass.getText() != null && !"".equals(pass.getText())) {
            if(pass.getText().equals(checkpass.getText())) // verifies if confirm_password=password
            {
                if (PostGreSQL.writeToDatabase(name.getText(),pass.getText()) == 1 ){
                    User.getInstance().setUsername(name.getText());
                    User.getInstance().setPassword(pass.getText());
                    ApplicationHandler.getInstance().navigateToCalendar();
                }
           }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Password not the same");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please insert a username and a password");
            alert.showAndWait();
        }

    }

    @FXML
    protected void onGoToAnotherSceneClick(){
        this.changeScene(SCENE_IDENTIFIER.LOGIN);
    }
}
