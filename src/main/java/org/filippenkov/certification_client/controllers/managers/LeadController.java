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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.filippenkov.certification_client.MainApplication;
import org.filippenkov.certification_client.controllers.AuthController;
import org.filippenkov.certification_client.controllers.leads.LeadDetailController;
import org.filippenkov.certification_client.models.Company;
import org.filippenkov.certification_client.models.Detail;
import org.filippenkov.certification_client.models.Lead;
import org.filippenkov.certification_client.models.LeadAdapter;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LeadController {

    @FXML
    private TableColumn<?, ?> DetailColumn;

    @FXML
    private TableColumn<?, ?> IDColumn;

    @FXML
    private Button LeadDetailButton;

    @FXML
    private TableView<LeadAdapter> ShapeButton;

    @FXML
    private Button addLeadButton;

    @FXML
    private TableColumn<?, ?> companyColumn;

    @FXML
    private Button deleteLeadButton;

    @FXML
    private TableColumn<?, ?> stageColumn;

    @FXML
    private Button choiceCompanyButton;

    @FXML
    private Button choiceDetailButton;

    @FXML
    private Label choiceDetailLabel;

    @FXML
    private Label choisenCompanyLabel;

    private Company company;
    private Detail detail;



    @FXML
    void onAddLeadButton(ActionEvent event) {
        if (detail == null || company == null) {
            return;
        }

        try {
           addLead();
           fillTable();

           refresh();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void refresh() {
        detail = null;
        company = null;

        choisenCompanyLabel.setText("");
        choiceDetailLabel.setText("");
    }


    private final String firstStage = "Старт";

    private void addLead() throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/lead")
                    .header("Content-Type", "application/json")
                    .body("{" +
                            "\"stage\": \"" + firstStage + "\",\n" +
                            "\"detail\": {\n" +
                            "\"id\": " + String.valueOf(detail.getId()) + "\n" +
                            "},\n" +
                            "\"company\": {\n" +
                            "\"id\": " + String.valueOf(company.getId()) + "\n" +
                            "}, \n" +
                            "\"responsible_user\": {\n" +
                            "\"id\": " + String.valueOf(AuthController.currentUser.getId()) + "\n" +
                            "}\n" +
                            "}")
                    .asJson();

            if(apiResponse.getStatus() != 201)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onChoiceCompany(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        MainApplication.class.getResource("lead/choice-company-view.fxml")
                );
                Stage stage = new Stage();
                stage.setScene(
                        new Scene(
                                (AnchorPane) loader.load()
                        )
                );
                stage.initModality(Modality.APPLICATION_MODAL);

                ChoiceCompanyController controller = loader.getController();

                stage.showAndWait();

                company = controller.getCompany();
                if (company != null) {
                    choisenCompanyLabel.setText(String.format("Номер компании: %d", company.getId()));
                }

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
    }

    @FXML
    void onChoiceDetail(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        MainApplication.class.getResource("lead/detail-choice.fxml")
                );
                Stage stage = new Stage();
                stage.setScene(
                        new Scene(
                                (AnchorPane) loader.load()
                        )
                );
                stage.initModality(Modality.APPLICATION_MODAL);

                ChoiceDetailController controller = loader.getController();

                stage.showAndWait();

                detail = controller.getDetail();
                if (detail != null) {
                    choiceDetailLabel.setText(String.format("Detail ID: %d", detail.getId()));
                }

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
    }


    @FXML
    void onDeleteLeadButton(ActionEvent event) {
        LeadAdapter lead = ShapeButton.getSelectionModel().getSelectedItem();

        try {
           deleteLead(lead);
           fillTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteLead(LeadAdapter lead) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.delete(dotenv.get("HOST") + "/lead/" +
                            String.valueOf(lead.getId()))
                    .asJson();

            if(apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("not unique attribute");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onLeadDetailButton(ActionEvent event) {
        LeadAdapter lead = ShapeButton.getSelectionModel().getSelectedItem();
        if (lead == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApplication.class.getResource("lead/detail-view.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(
                    new Scene(
                            (TabPane) loader.load()
                    )
            );
            stage.initModality(Modality.APPLICATION_MODAL);

            LeadDetailController controller = loader.getController();
            controller.setLeadID(lead.getId());

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    @FXML
    private void initialize() {
        initTable();
    }

    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();
    private List<LeadAdapter> leadAdapters = new ArrayList<>();

    private void initTable() {
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        DetailColumn.setCellValueFactory(new PropertyValueFactory<>("detail"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        stageColumn.setCellValueFactory(new PropertyValueFactory<>("stage"));

        fillTable();
    }

    private void fillTable() {
        leadAdapters.clear();
        List<Lead> allUserLeads = getAllLeads();

        allUserLeads.forEach(elem -> {
            if (elem.getCompany() == null)
                return;
            leadAdapters.add(new LeadAdapter(elem));
        });

        ShapeButton.setItems(FXCollections.observableArrayList(leadAdapters));
    }

    private List<Lead> getAllLeads() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/lead")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type usersListType = new TypeToken<ArrayList<Lead>>() {
            }.getType();
            ArrayList<Lead> res = new Gson().fromJson(apiResponse.getBody().toString(), usersListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

}
