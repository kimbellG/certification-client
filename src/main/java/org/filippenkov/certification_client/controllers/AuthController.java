package org.filippenkov.certification_client.controllers;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.filippenkov.certification_client.MainApplication;
import org.filippenkov.certification_client.models.AuthResponse;
import org.filippenkov.certification_client.models.User;
import org.filippenkov.certification_client.store.DotenvProvider;
import org.filippenkov.certification_client.store.UserProvider;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;


public class AuthController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();
    public static AuthResponse currentUser;

    private static final String pattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String minRequirement = "Минимум 8 символов, 1 буква и 1 цифра";
    private static final String wrongCredentials = "Данные неверны. Попробуйте ещё раз.";

    private String login;
    private String password;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMsg;

    @FXML
    private Label passwordMsg;

    @FXML
    private void initialize() {
        loginField.setPromptText("Введите Ваш логин...");
        passwordField.setPromptText("Введите Ваш пароль...");
    }

    public void onLoginBtnClick(ActionEvent actionEvent) {
        if(!loginField.getText().matches(pattern)) {
            loginField.setStyle("-fx-border-color: red");
            loginMsg.setText(minRequirement);
            return;
        }

        this.login = loginField.getText();
        this.password = passwordField.getText();
        AuthResponse response = handleLogin();

        if(response.getCode() != 200) {
            loginField.setStyle("-fx-border-color: red");
            passwordField.setStyle("-fx-border-color: red");
            passwordMsg.setText(wrongCredentials);
            return;
        }

        UserProvider provider = UserProvider.getInstance();
        provider.setUser(new User(response));


        if (provider.getUser().getIs_admin()) {
            openAdminWindow();
        } else {
            openManagerWindow();
        }
    }

    private AuthResponse handleLogin() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/log-in")
                    .header("Content-Type", "application/json")
                    .body("{\"login\": \"" + this.login + "\", \"password\": \"" + this.password + "\"}")
                    .asJson();

            AuthResponse res = new Gson().fromJson(apiResponse.getBody().toString(), AuthResponse.class);
            currentUser = res;

            res.setCode(apiResponse.getStatus());

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openAdminWindow() {
        Stage stage = (Stage) this.loginField.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("admin-view.fxml"));
        stage.setTitle("Администратор");
        try {
            stage.setScene(new Scene(fxmlLoader.load(), 1200, 750));
            stage.setX(200);
            stage.setY(20);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    private void openManagerWindow() {
        Stage stage = (Stage) this.loginField.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main.fxml"));
        stage.setTitle("Сертификация");
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }
}