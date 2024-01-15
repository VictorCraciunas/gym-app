package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.ApplicationHandler;
import com.jfxbase.oopjfxbase.utils.PostGreSQL;
import com.jfxbase.oopjfxbase.utils.SceneController;
import com.jfxbase.oopjfxbase.utils.User;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Map;

public class AdminPage extends SceneController {

    @FXML
    private VBox vBoxExercises;

    @FXML
    private void initialize(){
        createDynamicallyUsers();
    }

    private void createDynamicallyUsers(){
        Map<String,Integer> usernames= PostGreSQL.getUsernames();

        for (String s : usernames.keySet()) {
            vBoxExercises.getChildren().add(createButton(s,usernames));
        }
    }

    private Button createButton(String username, Map<String,Integer> usernames){
        Button button=new Button(username);
        button.getStyleClass().add("button-exercises");
        button.setOnAction(event -> {
            User.getInstance().setUsername(username);
                for (Map.Entry<String,Integer> entry :usernames.entrySet()) {
                    if(entry.getKey().equals(username)){
                        User.getInstance().setUserId(entry.getValue());
                        System.out.println(User.getInstance().getUserId());
                    }
                }
            ApplicationHandler.getInstance().navigateToAccount();
        });
        return button;
    }

    @FXML
    protected void goToLogin(){
        this.changeScene(SCENE_IDENTIFIER.LOGIN);
    }
    @FXML
    protected void goToHomePage(){
        this.changeScene(SCENE_IDENTIFIER.AdminPage);
    }
}
