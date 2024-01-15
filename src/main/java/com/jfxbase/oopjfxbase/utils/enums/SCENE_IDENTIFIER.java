package com.jfxbase.oopjfxbase.utils.enums;


public enum SCENE_IDENTIFIER {
    LOGIN("Login.fxml"),
   SingUp("SingUp.fxml"),
    ForgotPass("ForgotPassword.fxml"),
    FirstPage("CalendarView.fxml"),
    InsertWorkoutPage("InsertWorkout.fxml"),
    AccountPage("AccountPage.fxml"),
    AdminPage("AdminPage.fxml");

    public final String label;

    SCENE_IDENTIFIER(String label) {
        this.label = label;
    }
}
