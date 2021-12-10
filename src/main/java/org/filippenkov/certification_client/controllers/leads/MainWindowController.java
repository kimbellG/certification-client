package org.filippenkov.certification_client.controllers.leads;

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
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.filippenkov.certification_client.MainApplication;
import org.filippenkov.certification_client.controllers.AuthController;
import org.filippenkov.certification_client.models.Lead;
import org.filippenkov.certification_client.models.LeadAdapter;
import org.filippenkov.certification_client.models.User;
import org.filippenkov.certification_client.models.UserAdapter;
import org.filippenkov.certification_client.store.DotenvProvider;

import javax.xml.catalog.CatalogFeatures;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {
    @FXML
    public Button pieChartButton;

    @FXML
    private TableColumn<?, ?> DetailColumn;

    @FXML
    private TableColumn<?, ?> IDColumn;

    @FXML
    private Button LeadDetailButton;

    @FXML
    private TableView<LeadAdapter> ShapeButton;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<?, ?> companyColumn;

    @FXML
    private Button manageButton;

    @FXML
    private TableColumn<?, ?> stageColumn;

    @FXML
    private void initialize() {
        initTable();
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


    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();
    private List<LeadAdapter> leadAdapters = new ArrayList<>();

    private void initTable() {
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        DetailColumn.setCellValueFactory(new PropertyValueFactory<>("detail"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        stageColumn.setCellValueFactory(new PropertyValueFactory<>("stage"));

        List<Lead> allUserLeads = getAllLeads(AuthController.currentUser.getId());

        allUserLeads.forEach(elem -> {
            if (elem.getCompany() == null)
                return;
            leadAdapters.add(new LeadAdapter(elem));
        });

        fillTable();
    }

    private void fillTable() {
        ShapeButton.setItems(FXCollections.observableArrayList(leadAdapters));
    }

    private List<Lead> getAllLeads(int id) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/lead?responsible_user=" + String.valueOf(id))
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

    @FXML
    public void onManageButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApplication.class.getResource("managers/main-manager-window.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(
                    new Scene(
                            (AnchorPane) loader.load()
                    )
            );
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    @FXML
    public void onPieChartButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApplication.class.getResource("lead/managers-pie-chart-view.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(
                    new Scene(
                            (AnchorPane) loader.load()
                    )
            );
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setTitle("Диаграмма");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
