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
import org.filippenkov.certification_client.controllers.AdminViewController;
import org.filippenkov.certification_client.models.Company;
import org.filippenkov.certification_client.models.Lead;
import org.filippenkov.certification_client.models.LeadAdapter;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CompanyController {
    @FXML
    public Label phoneErrorLabel;

    @FXML
    public Label emailErrorLabel;

    @FXML
    private Label UNPErrorLabel;

    @FXML
    private Button addCompanyButton;

    @FXML
    private Label addCompanyErrorField;

    @FXML
    private TableColumn<?, ?> companyDirectorColumn;

    @FXML
    private TextField companyDirectorField;

    @FXML
    private TableColumn<?, ?> companyEmailColumn;

    @FXML
    private TextField companyEmailField;

    @FXML
    private TableColumn<?, ?> companyID;

    @FXML
    private TableColumn<?, ?> companyNameColumn;

    @FXML
    private TextField companyNameField;

    @FXML
    private TableColumn<?, ?> companyPhoneColumn;

    @FXML
    private TextField companyPhoneField;

    @FXML
    private TableView<Company> companyTable;

    @FXML
    private TableColumn<?, ?> companyUNPColumn;

    @FXML
    private TextField companyUNPField;

    @FXML
    private Button deleteCompanyButton;

    @FXML
    private Button updateTableButton;

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

    @FXML
    void onAddCompany(ActionEvent event) {
        refreshErrors();
        String unp = companyUNPField.getText();
        if (unp.length() != 9) {
            UNPErrorLabel.setText("Некорректный формат УНП.");
            return;
        }

        String email = companyEmailField.getText();
        if (!email.matches(AdminViewController.pattern)) {
            emailErrorLabel.setText(AdminViewController.wrongEmail);
                    return;
        }

        String phone = companyPhoneField.getText();
        if (phone.length() != 13) {
            phoneErrorLabel.setText("Введите корректный формат номера телефона");
            return;
        }

        String name = companyNameField.getText();
        String director = companyDirectorField.getText();

        Company company = new Company(name, unp, director, email, phone);

        try {
            addCompany(company);
            fillCompanyTable();
        } catch (ClassNotFoundException e) {
            addCompanyErrorField.setText("УНП, почта или телефон не уникальны");
        }
    }

    private void refreshErrors() {
        UNPErrorLabel.setText("");
        emailErrorLabel.setText("");
        phoneErrorLabel.setText("");
    }

    private void addCompany(Company company) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/company")
                    .header("Content-Type", "application/json")
                    .body("{" +
                            "\"name\": \"" + company.getName() + "\",\n" +
                            "\"unp\": \"" + company.getUnp() + "\",\n" +
                            "\"director\": \"" + company.getDirector() + "\", \n" +
                            "\"email\": \"" + company.getEmail() + "\", " +
                            "\"phone\": \"" + company.getPhone() + " \"\n" +
                          "}")
                    .asJson();

            if(apiResponse.getStatus() != 201)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDeleteCompany(ActionEvent event) {
        refreshErrors();
        var company = companyTable.getSelectionModel().getSelectedItem();

        try {
            deleteCompany(company);
            fillCompanyTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteCompany(Company company) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.delete(dotenv.get("HOST") + "/company/" +
                            String.valueOf(company.getId()))
                    .asJson();

            if(apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onUpdateTable(ActionEvent event) {
        refreshErrors();
        fillCompanyTable();
    }

}
