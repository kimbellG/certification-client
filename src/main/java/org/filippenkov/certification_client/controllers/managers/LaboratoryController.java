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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.filippenkov.certification_client.models.Company;
import org.filippenkov.certification_client.models.Laboratory;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class LaboratoryController {

    @FXML
    public Label errorLabel;

    @FXML
    private Button addLabButton;

    @FXML
    private TableColumn<?, ?> labAccNumberColumn;

    @FXML
    private TableColumn<?, ?> labDateUntilColumn;

    @FXML
    private TableColumn<?, ?> labIDColumn;

    @FXML
    private TableColumn<?, ?> labNameColumn;

    @FXML
    private TextField labNameField;

    @FXML
    private TextField labNumberField;

    @FXML
    private TableView<Laboratory> labTable;

    @FXML
    private DatePicker labUntilDateField;

    @FXML
    private Button updateTableButton;

    @FXML
    void onAddLab(ActionEvent event) {
        refreshErrors();
        String name = labNameField.getText();
        String AccNumber = labNumberField.getText();
        Date valid_until = java.sql.Date.valueOf(labUntilDateField.getValue());

        Laboratory lab = new Laboratory(name, AccNumber, valid_until);

        try {
            addLab(lab);
            fillTable();
        } catch (ClassNotFoundException e) {
            errorLabel.setText("Лаборатория с таким названием уже существует");
        }
    }

    private void refreshErrors() {
        errorLabel.setText("");
    }

    private void addLab(Laboratory lab) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/laboratory")
                    .header("Content-Type", "application/json")
                    .body("{" +
                            "\"name\": \"" + lab.getName() + "\",\n" +
                            "\"accreditation_number\": \"" + lab.getAccreditation_number() + "\",\n" +
                            "\"valid_until\": \"" + lab.getValid_until() + "\"\n" +
                          "}")
                    .asJson();

            if(apiResponse.getStatus() != 201)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onUpdateTable(ActionEvent event) {
        refreshErrors();
        fillTable();
    }

    @FXML
    protected void initialize() {
        initLaboratoryTable();
    }

    private void initLaboratoryTable() {
        labIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        labNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        labAccNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accreditation_number"));
        labDateUntilColumn.setCellValueFactory(new PropertyValueFactory<>("valid_until"));

        fillTable();
    }

    private void fillTable() {
        try {
            labTable.setItems(FXCollections.observableArrayList(getAllLabs()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    private ArrayList<Laboratory> getAllLabs() throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/laboratory")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type laboratoryListType = new TypeToken<ArrayList<Laboratory>>() {
            }.getType();
            ArrayList<Laboratory> res = new Gson().fromJson(apiResponse.getBody().toString(), laboratoryListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }


}


