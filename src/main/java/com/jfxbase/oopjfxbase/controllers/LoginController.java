package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.*;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends SceneController {

    @FXML
    private TextField name;

    @FXML
    private PasswordField pass;


    @FXML
    public void getData(ActionEvent actionEvent) {
        System.out.println(name.getText());
        System.out.println(pass.getText());

        if (name.getText() != null && !"".equals(name.getText()) && // "".equals(name.getText() verifies that is not empty
                pass.getText() != null && !"".equals(pass.getText())) {
            if(name.getText().equals("admin") && pass.getText().equals("admin")){
                Admin.getInstance().setUsername("admin");
                Admin.getInstance().setPassword("admin");
                this.changeScene(SCENE_IDENTIFIER.AdminPage);
            }
            else{
                Admin.getInstance().setUsername("notadmin");
                Admin.getInstance().setPassword("notadmin");
                if (PostGreSQL.checkToDataBase(name.getText(), pass.getText()) != 0) {// check if the credentials are correct

                    User.getInstance().setUsername(name.getText());
                    User.getInstance().setPassword(pass.getText());
                    User.getInstance().setUserId(PostGreSQL.checkToDataBase(name.getText(), pass.getText()));

                    ApplicationHandler.getInstance().navigateToCalendar();
                }
            }

        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please insert a username and the password");
            alert.show ();
        }
    }

    @FXML
    protected void onGoToAnotherSceneClick() {
        this.changeScene(SCENE_IDENTIFIER.SingUp);
    }

    @FXML
    protected void onGoToForgotPasswordSceneClick() {
        this.changeScene(SCENE_IDENTIFIER.ForgotPass);
    }

}
