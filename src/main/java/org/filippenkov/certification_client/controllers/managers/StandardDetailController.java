package org.filippenkov.certification_client.controllers.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.filippenkov.certification_client.models.Detail;
import org.filippenkov.certification_client.models.Laboratory;
import org.filippenkov.certification_client.models.Standard;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StandardDetailController {

    @FXML
    private Button addDetailButton;

    @FXML
    private Button addStandardButton;

    @FXML
    private Button deleteDetailButton;

    @FXML
    private Button deleteStandardButton;

    @FXML
    private TableColumn<?, ?> detailNameColumn;

    @FXML
    private Label detailNameErrorLabel;

    @FXML
    private TextField detailNamefield;

    @FXML
    private TableColumn<?, ?> detailNumber;

    @FXML
    private Label detailNumberErrorLabel;

    @FXML
    private TextField detailNumberField;

    @FXML
    private TableView<Detail> detailTable;

    @FXML
    private TableColumn<?, ?> standardDescriptionColumn;

    @FXML
    private TextField standardDescriptionField;

    @FXML
    private Label standardErrorLabel;

    @FXML
    private TableColumn<?, ?> standardNameColumn;

    @FXML
    private TextField standardNameField;

    @FXML
    private TableView<Standard> standardTable;

    @FXML
    void onAddDetail(ActionEvent event) {
        refreshDetailErrors();

        String name = detailNamefield.getText();
        String notification = validateDetailName(name);
        if (!notification.equals("")) {
            detailNameErrorLabel.setText(notification);
            return;
        }

        try {
            Long code = Long.parseLong(detailNumberField.getText());
            notification = validateDetailCode(code);
            if (!notification.equals("")) {
                detailNumberErrorLabel.setText(notification);
                return;
            }
           addDetail(new Detail(name, code));
           fillDetailTable();

        } catch (NumberFormatException e) {
            detailNumberErrorLabel.setText("Введите число");
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private String validateDetailName(String name) {
        if (name.length() > 300) {
            return "Длинное название.";
        }

        for (var detail : details) {
            if (detail.getName() == name) {
                return "Уже имеется.";
            }
        }

        return "";
    }

    private String validateDetailCode(Long code) {
            for (var detail : details) {
                if (detail.getCode() == code) {
                    return "Уже имеется";
                }
            }
            return "";
    }

    private void addDetail(Detail detail) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/detail")
                    .header("Content-Type", "application/json")
                    .body("{" +
                            "\"name\": \"" + detail.getName() + "\",\n" +
                            "\"code\": \"" + String.valueOf(detail.getCode()) + "\"\n" +
                            "}")
                    .asJson();

            if(apiResponse.getStatus() != 201)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    private void refreshDetailErrors() {
        detailNameErrorLabel.setText("");
        detailNumberErrorLabel.setText("");
    }

    @FXML
    void onAddStandard(ActionEvent event) {
        refreshStandardErrors();
        String name = standardNameField.getText();
        String description = standardDescriptionField.getText();

        try {
            addStandard(new Standard(name, description));
            fillStandardTable();

        } catch(ClassNotFoundException e) {
            standardErrorLabel.setText("Название стандарта должно быть уникальным");
        }

    }

    private void refreshStandardErrors() {
        standardErrorLabel.setText("");
    }

    private void addStandard(Standard standard) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/standard")
                    .header("Content-Type", "application/json")
                    .body("{" +
                            "\"name\": \"" + standard.getName() + "\",\n" +
                            "\"description\": \"" + standard.getDescription() + "\"\n" +
                            "}")
                    .asJson();

            if(apiResponse.getStatus() != 201)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onDeleteDetail(ActionEvent event) {
        refreshDetailErrors();
        Detail detail = detailTable.getSelectionModel().getSelectedItem();

        try {
            deleteTable(detail);
            fillDetailTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void deleteTable(Detail detail) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.delete(dotenv.get("HOST") + "/detail/" +
                            String.valueOf(detail.getId()))
                    .asJson();

            if(apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDeleteStandard(ActionEvent event) {
        refreshStandardErrors();
        Standard standard = standardTable.getSelectionModel().getSelectedItem();

        try {
            deleteStandard(standard);
            fillStandardTable();
        } catch (ClassNotFoundException e) {
            standardErrorLabel.setText("Удалите все детали для имеющихся стандартов");
        }
    }

    private void deleteStandard(Standard standard) throws ClassNotFoundException{
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.delete(dotenv.get("HOST") + "/standard/" +
                            String.valueOf(standard.getId()))
                    .asJson();

            if(apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {
        initDetailTable();
        initStandardTable();
    }

    private void initDetailTable() {
        detailNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        detailNumber.setCellValueFactory(new PropertyValueFactory<>("code"));

        fillDetailTable();
    }

    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();
    ArrayList<Detail> details = new ArrayList<>();

    private void fillDetailTable() {
        details = getAllDetails();
        detailTable.setItems(FXCollections.observableArrayList(details));
    }


    private ArrayList<Detail> getAllDetails() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/detail")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type detailListType = new TypeToken<ArrayList<Detail>>() {
            }.getType();
            ArrayList<Detail> res = new Gson().fromJson(apiResponse.getBody().toString(), detailListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initStandardTable() {
        standardNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        standardDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        fillStandardTable();
    }

    private void fillStandardTable() {
        standardTable.setItems(FXCollections.observableArrayList(getAllStandard()));
    }

    private ArrayList<Standard> getAllStandard() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/standard")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type detailListType = new TypeToken<ArrayList<Standard>>() {
            }.getType();
            ArrayList<Standard> res = new Gson().fromJson(apiResponse.getBody().toString(), detailListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }
}
