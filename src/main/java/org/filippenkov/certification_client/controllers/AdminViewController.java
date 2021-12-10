package org.filippenkov.certification_client.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.filippenkov.certification_client.models.UserAdapter;
import org.filippenkov.certification_client.models.User;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdminViewController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    public static final String pattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String wrongEmail = "Введите корректную электронную почту";
    private static final String minRequirement = "Минимум 8 символов, 1 буква и 1 цифра";
    private static final String wrongCredentials = "Пользователь с таким логином уже существует.";


    private List<UserAdapter> adaptedUsers = new ArrayList<>();

    //Table and columns
    @FXML
    private TableView usersTable;

    @FXML
    private TableColumn loginColumn;

    @FXML
    private TableColumn surnameColumn;

    @FXML
    private TableColumn adminColumn;

    //Adding a user fields
    @FXML
    private TextField addLoginField;
    private String login;

    @FXML
    private TextField addSurnameField;
    private String surname;

    @FXML
    private PasswordField addPasswordField;
    private String password;

    @FXML
    private CheckBox isAdminCheckBox;
    private Boolean isAdmin;

    @FXML
    private Label loginMsg;
    @FXML
    private Label passwordMsg;
    @FXML
    private Label surnameMsg;

    @FXML
    private Button deleteUserButton;

    @FXML
    private void initialize() {
        initTableComponent();

        initAddUserComponent();

        isAdmin = false;
        isAdminCheckBox.setOnAction(event -> {
            if (isAdmin)
                isAdmin = false;
            else
                isAdmin = true;
        });

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
                deleteUserButton.setDisable(false);
            else
                deleteUserButton.setDisable(true);
        });
    }

    @FXML
    private void onRefreshTable(ActionEvent actionEvent) {
        List<User> users = getAllUsers();

        adaptedUsers.clear();
        users.forEach(elem -> {
            adaptedUsers.add(new UserAdapter(elem));
        });

        fillTable();
    }

    @FXML
    private void handleDelete(ActionEvent actionEvent) {
        UserAdapter selectedUser = (UserAdapter) usersTable.getSelectionModel().getSelectedItem();
        int id = selectedUser.getId();
        adaptedUsers.remove(selectedUser);

        deleteUser(id);

        fillTable();
    }

    @FXML
    private void onAddUserButtonClick(ActionEvent actionEvent) {
        resetWarnings();
        if(beforeSendValidateInput() != 0) return;

        login = addLoginField.getText();
        surname = addSurnameField.getText();
        password = this.addPasswordField.getText();
        isAdmin = isAdminCheckBox.isSelected();

        User user = new User(login, surname, password, isAdmin);

        User createdUser = addUser(user);

        if(afterSendValidateInput(createdUser) != 0) return;

        adaptedUsers.add(new UserAdapter(user));
        fillTable();
    }

    private void resetWarnings() {
        addLoginField.setStyle("");
        addSurnameField.setStyle("");
        addPasswordField.setStyle("");
        isAdminCheckBox.setSelected(false);

        loginMsg.setText("");
        surnameMsg.setText("");
        passwordMsg.setText("");
    }

    private int beforeSendValidateInput() {
        if(!addLoginField.getText().matches(pattern)) {
            addLoginField.setStyle("-fx-border-color: red");
            loginMsg.setText(wrongEmail);
            return 1;
        }

        return 0;
    }

    private int afterSendValidateInput(User createdUser) {
        if(createdUser == null) {
            addLoginField.setStyle("-fx-border-color: red");
            loginMsg.setText(wrongCredentials);
            return 1;
        }
        return 0;
    }

    private void initTableComponent() {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("fio"));
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        List<User> users = getAllUsers();

        users.forEach(elem -> {
            adaptedUsers.add(new UserAdapter(elem));
        });

        setTableEditing();

        fillTable();
    }

    private void setTableEditing() {
        usersTable.setEditable(true);

        loginColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        loginColumn.setOnEditCommit(
            (EventHandler<TableColumn.CellEditEvent<UserAdapter, String>>) cellEditEvent -> {
                int index = cellEditEvent.getTablePosition().getRow();
                adaptedUsers.get(index).setEmail(cellEditEvent.getNewValue());

                User user = new User(adaptedUsers.get(index));
                updateUser(user);
            }
        );

        surnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameColumn.setOnEditCommit(
            (EventHandler<TableColumn.CellEditEvent<UserAdapter, String>>) cellEditEvent -> {
                int index = cellEditEvent.getTablePosition().getRow();
                adaptedUsers.get(index).setFio(cellEditEvent.getNewValue());

                User user = new User(adaptedUsers.get(index));
                updateUser(user);
            }
        );

        adminColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn("Админ", "Менеджер"));
        adminColumn.setOnEditCommit(
            (EventHandler<TableColumn.CellEditEvent<UserAdapter, String>>) cellEditEvent -> {
                int index = cellEditEvent.getTablePosition().getRow();
                adaptedUsers.get(index).setStatus(cellEditEvent.getNewValue());

                User user = new User(adaptedUsers.get(index));
                updateUser(user);
            }
        );
    }

    private void fillTable() {
        usersTable.setItems(FXCollections.observableArrayList(adaptedUsers));
    }

    private void initAddUserComponent() {
    }

    //API Calls
    private User addUser(User user) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/sign-up")
                    .header("Content-Type", "application/json")
                    .body("{\"email\": \"" + user.getEmail() + "\", \"password\": \""
                            + user.getPassword() + "\", \"fio\": \""
                            + user.getFio() + "\", \"is_admin\": \""
                            + String.valueOf(user.getIs_admin()) + "\"}")
                    .asJson();

            if(apiResponse.getStatus() == 400) return null;

            User createdUser = new Gson().fromJson(apiResponse.getBody().toString(), User.class);

            return createdUser;
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<User> getAllUsers() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/users")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type usersListType = new TypeToken<ArrayList<User>>(){}.getType();
            ArrayList<User> res = new Gson().fromJson(apiResponse.getBody().toString(), usersListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User updateUser(User user) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/profile/" + user.getId())
                    .header("Content-Type", "application/json")
                    .body("{\"email\": \"" + user.getEmail() + "\", \"password\": \""
                            + user.getPassword() + "\", \"fio\": \""
                            + user.getFio() + "\", \"is_admin\": \""
                            + String.valueOf(user.getIs_admin()) + "\"}")
                   .asJson();

            if(apiResponse.getStatus() == 400) return null;

            User createdUser = new Gson().fromJson(apiResponse.getBody().toString(), User.class);

            return createdUser;
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String deleteUser(int id) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.delete(dotenv.get("HOST") + "/profile/" + id)
                    .header("Content-Type", "application/json")
                    .asJson();

            return apiResponse.getBody().toString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }
}
