package com.jfxbase.oopjfxbase.utils;

import javafx.scene.control.Alert;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// I introduced a postgresql jar library in the module + in the library section of the project.
public class PostGreSQL {

    public static int writeToDatabase(String userName, String userPassword) { // function to sing up
        /// information required to access the database
        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "";


        //queries that we need to implement
        String QUERY_INSERT = "INSERT INTO users(username, password) VALUES (?, ?)";
        String QUERY_VERIFY = "SELECT * FROM users WHERE username = ?";


        // we create the new password ecnrypted
        String newpass=null;
        try {
                newpass=Encrypt.toHexString(Encrypt.getSHA(userPassword));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("nu o mers criptarea");
        }

        ResultSet resultSet;

        try ( // we try the connection
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psCheckUserExists = con.prepareStatement(QUERY_VERIFY);
                PreparedStatement pst = con.prepareStatement(QUERY_INSERT)
        ) {

            psCheckUserExists.setString(1, userName);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) {   // Check if there is already a username
                System.out.println("User already exists");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Username already exists");
                alert.show();
            } else {
                //create user
                pst.setString(1, userName);
                pst.setString(2, newpass);
                pst.executeUpdate();
                System.out.println("Successfully created");
                return 1;
            }
        } catch (SQLException ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
        return 0;
    }

    public static int checkToDataBase(String userName, String userPassword){ // function to login
        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_VERIFY = "SELECT * FROM users WHERE username = ? and password= ?";

        ResultSet resultSet;


        // we create the new password ecnrypted
        String newpass=null;
        try {
            newpass=Encrypt.toHexString(Encrypt.getSHA(userPassword));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("nu o mers criptarea");
        }

        //Connection to DB
        try(
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psCheckUserExists = con.prepareStatement(QUERY_VERIFY)
        ){
            psCheckUserExists.setString(1, userName);
            psCheckUserExists.setString(2, newpass);
            resultSet = psCheckUserExists.executeQuery();
            if(resultSet.next()){ // check if credentials were correct
                int id = resultSet.getInt("id");
                return id;
            }
            else{
                System.out.println("Incorrect password or username");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Incorrect password or username");
                alert.show();
            }
        }catch (SQLException ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
        return 0;
    }


    public static String getPassFromDataBase(String userName) { // function to get the password from a required username for forgot password
        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_VERIFY = "SELECT password FROM users WHERE username = ?";
        String password = null;
        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psCheckUserExists = con.prepareStatement(QUERY_VERIFY)
        ) {
            psCheckUserExists.setString(1, userName);
            ResultSet resultSet = psCheckUserExists.executeQuery();

            if (resultSet.next()) { // Check if there is a result
                password = resultSet.getString("password"); // get the password from the table
            } else {
                System.out.println("Incorrect username");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Incorrect username");
                alert.show();
            }
        } catch (SQLException ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }

        return password;
    }
    public static int getIDforgotPass(String userName, String userPassword){ // get the ID for forgot password
        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_VERIFY = "SELECT * FROM users WHERE username = ? and password= ?";

        ResultSet resultSet;

        //Connection to DB
        try(
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psCheckUserExists = con.prepareStatement(QUERY_VERIFY)
        ){
            psCheckUserExists.setString(1, userName);
            psCheckUserExists.setString(2, userPassword);
            resultSet = psCheckUserExists.executeQuery();
            if(resultSet.next()){ // check if credentials were correct
                int id = resultSet.getInt("id");
                return id;
            }
        }catch (SQLException ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
        return 0;
    }

    public static Map<Integer, String> getWorkoutNamesFromDatabase() { // get the workout and its id from the DB
        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_WORKOUT_INFO = "SELECT id, workoutname FROM typeofworkout";

        //We need the map function because we need to make a pair between workout and its id
        Map<Integer, String> workoutInfoMap = new HashMap<>();

        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetWorkoutInfo = con.prepareStatement(QUERY_GET_WORKOUT_INFO)
        ) {
            ResultSet resultSet = psGetWorkoutInfo.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("workoutname");

                workoutInfoMap.put(id, name);
            }

        } catch (SQLException ex) {
            System.out.println("Error fetching workout information");
            ex.printStackTrace();
        }

        return workoutInfoMap;
    }

    public static Map<Integer, String> getExercisesFromDatabase(int workoutId) { // get the exercises of a specific workout (that's why we need the workoutID)
        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT exercises.id, name FROM exercises JOIN typeofworkout ON exercises.workoutname_id = typeofworkout.id WHERE typeofworkout.id = ?";

        Map<Integer, String> exerciseNames = new HashMap<>();


        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            psGetExerciseNames.setInt(1, workoutId);
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                exerciseNames.put(id,name);
            }

        } catch (SQLException ex) {
            System.out.println("Error fetching exercise names");
            ex.printStackTrace();
        }

        return exerciseNames;
    }

    public static void insertUserWorkout(Integer exerciseId, Integer nrSets, Integer nrReps, Integer userId, Integer kg, Integer date,Integer month) { // insert the workout of a user in DB
        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_INSERT_USER_WORKOUT = "INSERT INTO userworkout(exercisename_id, sets, reps, user_id, kg, date, month) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psInsertUserWorkout = con.prepareStatement(QUERY_INSERT_USER_WORKOUT)
        ) {
            psInsertUserWorkout.setInt(1, exerciseId);
            psInsertUserWorkout.setInt(2, nrSets);
            psInsertUserWorkout.setInt(3, nrReps);
            psInsertUserWorkout.setInt(4, userId);
            psInsertUserWorkout.setInt(5, kg);
            psInsertUserWorkout.setInt(6, date);
            psInsertUserWorkout.setInt(7, month);

            int rowsAffected = psInsertUserWorkout.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User workout inserted successfully.");
            } else {
                System.out.println("Failed to insert user workout.");
            }
        } catch (SQLException ex) {
            System.out.println("Error inserting user workout");
            ex.printStackTrace();
        }
    }

    public static ArrayList<String> isWorkout(Integer date, Integer month, Integer userId) { // get the workout(exercises) from userWorkout table. Also with this function we get to know if there was a workout

        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT name from exercises join userworkout on exercises.id=userworkout.exercisename_id where userworkout.date= ? AND userworkout.month= ? AND userworkout.user_id= ?";

        ArrayList<String> getExercises=new ArrayList<>();


        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            psGetExerciseNames.setInt(1, date);
            psGetExerciseNames.setInt(2, month);
            psGetExerciseNames.setInt(3, userId);
//            psGetExerciseNames.setInt(3, userId);
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                getExercises.add(name);
            }



        } catch (SQLException ex) {
            System.out.println("Error fetching exercise names");
            ex.printStackTrace();
        }

        return getExercises;
    }
    public static ArrayList<Integer> getSets(Integer date, Integer month, Integer userId) { // get the sets from userWorkout table

        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT sets from exercises join userworkout on exercises.id=userworkout.exercisename_id where userworkout.date= ? AND userworkout.month= ? AND userworkout.user_id= ?";

        ArrayList<Integer> getSets=new ArrayList<>();


        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            psGetExerciseNames.setInt(1, date);
            psGetExerciseNames.setInt(2, month);
            psGetExerciseNames.setInt(3, userId);
//            psGetExerciseNames.setInt(3, userId);
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            while (resultSet.next()) {
                Integer nrSets = resultSet.getInt("sets");
                getSets.add(nrSets);
            }



        } catch (SQLException ex) {
            System.out.println("Error fetching exercise names");
            ex.printStackTrace();
        }

        return getSets;
    }
    public static ArrayList<Integer> getReps(Integer date, Integer month, Integer userId) { // get the reps from userWorkout table

        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT reps from exercises join userworkout on exercises.id=userworkout.exercisename_id where userworkout.date= ? AND userworkout.month= ? AND userworkout.user_id= ? ";

        ArrayList<Integer> getReps=new ArrayList<>();


        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            psGetExerciseNames.setInt(1, date);
            psGetExerciseNames.setInt(2, month);
            psGetExerciseNames.setInt(3, userId);
//            psGetExerciseNames.setInt(3, userId);
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            while (resultSet.next()) {
                Integer nrReps = resultSet.getInt("reps");
                getReps.add(nrReps);
            }



        } catch (SQLException ex) {
            System.out.println("Error fetching exercise names");
            ex.printStackTrace();
        }

        return getReps;
    }
    public static ArrayList<Integer> getKg(Integer date, Integer month, Integer userId) { // get the kg from userWorkout table

        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT kg from exercises join userworkout on exercises.id=userworkout.exercisename_id where userworkout.date= ? AND userworkout.month= ? AND userworkout.user_id= ?";

        ArrayList<Integer> getKG=new ArrayList<>();


        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            psGetExerciseNames.setInt(1, date);
            psGetExerciseNames.setInt(2, month);
            psGetExerciseNames.setInt(3, userId);
//            psGetExerciseNames.setInt(3, userId);
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            while (resultSet.next()) {
                Integer nrKg = resultSet.getInt("kg");
                getKG.add(nrKg);
            }



        } catch (SQLException ex) {
            System.out.println("Error fetching exercise names");
            ex.printStackTrace();
        }

        return getKG;
    }


    public static String getWorkout(String exerciseName) { // get the workout name from typeofworkout table
        // get the type of workout from typeofworkout table

        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT workoutname FROM typeofworkout JOIN exercises ON typeofworkout.id = exercises.workoutname_id WHERE name = ?";

        String workoutName = null; // Initialize workoutName to null

        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            psGetExerciseNames.setString(1, exerciseName);
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            if (resultSet.next()) {
                workoutName = resultSet.getString("workoutname");
            }

        } catch (SQLException ex) {
            System.out.println("Error fetching workout name");
            ex.printStackTrace();
        }

        return workoutName;
    }

    public static Integer getPR(String exerciseName,Integer userId) { // get the highest kg from the table userworkout
        // get the type of workout from typeofworkout table

        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT kg FROM userworkout JOIN exercises ON  userworkout.exercisename_id=exercises.id where name= ? AND userworkout.user_id= ? ORDER BY kg DESC";

        Integer PR = 0; // Initialize PR to 0

        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            psGetExerciseNames.setString(1, exerciseName);
            psGetExerciseNames.setInt(2, userId);
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            if (resultSet.next()) {
                PR = resultSet.getInt("kg");
            }

        } catch (SQLException ex) {
            System.out.println("Error fetching workout name");
            ex.printStackTrace();
        }

        return PR;
    }

    public static Map<String, Integer> getUsernames() { // get the users from the DataBase
        // get the type of workout from typeofworkout table

        String DB_URL = "jdbc:postgresql://localhost:5432/javafx";
        String DB_USER = "postgres";
        String DB_PASSWORD = "1k2k3k4k";

        String QUERY_GET_EXERCISE_NAMES = "SELECT username,id from users";

        Map<String,Integer> usernames=new HashMap<>();

        try (
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement psGetExerciseNames = con.prepareStatement(QUERY_GET_EXERCISE_NAMES)
        ) {
            ResultSet resultSet = psGetExerciseNames.executeQuery();

            while (resultSet.next()) {
                Integer id=resultSet.getInt("id");
                String username=resultSet.getString("username");
                usernames.put(username,id);
            }

        } catch (SQLException ex) {
            System.out.println("Error fetching workout name");
            ex.printStackTrace();
        }

        return usernames;
    }


}
