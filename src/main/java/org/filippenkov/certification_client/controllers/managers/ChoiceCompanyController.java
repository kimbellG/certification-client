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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.filippenkov.certification_client.models.Company;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChoiceCompanyController {

    private Company company;
    public Company getCompany() {
        return company;
    }

    @FXML
    private Button choiceCompanyButton;

    @FXML
    private TableColumn<?, ?> companyDirectorColumn;

    @FXML
    private TableColumn<?, ?> companyEmailColumn;

    @FXML
    private TableColumn<?, ?> companyID;

    @FXML
    private TableColumn<?, ?> companyNameColumn;

    @FXML
    private TableColumn<?, ?> companyPhoneColumn;

    @FXML
    private TableView<Company> companyTable;

    @FXML
    private TableColumn<?, ?> companyUNPColumn;

    @FXML
    void onChoiceCompanyButton(ActionEvent event) {
        company = companyTable.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) choiceCompanyButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void initialize() {
        initCompanyTable();
    }

    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();
    private List<Company> companies = new ArrayList<>();

    private void initCompanyTable() {
        companyID.setCellValueFactory(new PropertyValueFactory<>("id"));
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        companyUNPColumn.setCellValueFactory(new PropertyValueFactory<>("unp"));
        companyDirectorColumn.setCellValueFactory(new PropertyValueFactory<>("director"));
        companyEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        companyPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        fillCompanyTable();
    }

    private void fillCompanyTable() {
        companies = getAllCompanies();
        companyTable.setItems(FXCollections.observableArrayList(companies));
    }


    private ArrayList<Company> getAllCompanies() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/company")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type companyListType = new TypeToken<ArrayList<Company>>() {
            }.getType();
            ArrayList<Company> res = new Gson().fromJson(apiResponse.getBody().toString(), companyListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

}
