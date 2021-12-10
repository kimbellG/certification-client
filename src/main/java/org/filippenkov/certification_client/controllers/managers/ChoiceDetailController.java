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
import org.filippenkov.certification_client.models.Detail;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChoiceDetailController {

    private Detail detail;

    public Detail getDetail() {
        return detail;
    }

    @FXML
    private Button choiceDetailButton;

    @FXML
    private TableColumn<?, ?> detailNameColumn;

    @FXML
    private TableColumn<?, ?> detailNumber;

    @FXML
    private TableView<Detail> detailTable;

    @FXML
    void onChoiceDetail(ActionEvent event) {
        detail = detailTable.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) choiceDetailButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void initialize() {
        initDetailTable();
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

}
