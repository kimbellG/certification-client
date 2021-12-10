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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.filippenkov.certification_client.models.*;
import org.filippenkov.certification_client.store.DotenvProvider;
import java.awt.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class LeadDetailController {

    @FXML
    public Label serialErrorLabel;

    @FXML
    public Label batchErrorLabel;

    @FXML
    public Button openCertificateButton;

    private Long leadID;
    private Lead lead;

    @FXML
    private Button addBatchButton;

    @FXML
    private Button addSerialButton;

    @FXML
    private Button addStandardButton;

    @FXML
    private Button addTestButton;

    @FXML
    private Button changeStageButton;

    @FXML
    private Label companyDirectorField;

    @FXML
    private Label companyEmailField;

    @FXML
    private Label companyNameField;

    @FXML
    private Label companyPhoneLabel;

    @FXML
    private Label companyUNPLabel;

    @FXML
    private Label detailCodeLabel;

    @FXML
    private Label detailIDLabel;

    @FXML
    private Label detailNameLabel;

    @FXML
    private Label leadBatchNumberLead;

    @FXML
    private Label leadIDLabel;

    @FXML
    private Label leadSerialNumberLabel;

    @FXML
    private Label leadStageLabel;

    @FXML
    private ChoiceBox<String> standardChoice;

    @FXML
    public Button deleteStandardButton;

    @FXML
    private TableColumn<?, ?> standardDescriptionColumn;

    @FXML
    private TableColumn<?, ?> standardNameColumn;

    @FXML
    private TableView<Standard> standardTable;

    @FXML
    public TableView<TestAdapter> testTable;

    @FXML
    private TableColumn<?, ?> testDateColumn;

    @FXML
    private DatePicker testDateField;

    @FXML
    private TableColumn<?, ?> testIDColumn;

    @FXML
    private ChoiceBox<String> testLabChoice;

    @FXML
    private TableColumn<?, ?> testLabColumn;

    @FXML
    private TableColumn<?, ?> testNumberColumn;

    @FXML
    private TextField testNumberField;


    @FXML
    public TextField changeStageField;

    @FXML
    public TextField addBatchNumberField;

    @FXML
    public TextField addSerialField;

    @FXML
    void addSerialButton(ActionEvent event) {
        String serial = addSerialField.getText();
        try {
            var serialLong = Long.parseLong(serial);
            addSerial(serialLong);
            lead = getLead(lead.getId());
            initCommonPage();
        } catch (NumberFormatException e) {
            serialErrorLabel.setText("Некорректный формат числа");
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddBatchButton(ActionEvent event) {
        String batch = addBatchNumberField.getText();
        try {
            var batchLong = Long.parseLong(batch);
            addBatch(batchLong);
            lead = getLead(lead.getId());
            initCommonPage();
        } catch (NumberFormatException e) {
            serialErrorLabel.setText("Некорректный формат числа");
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddStandardButton(ActionEvent event) {
        int i = standardChoice.getSelectionModel().getSelectedIndex();
        if (i == -1) {
            return;
        }

        var standard = choicesStandard.get(i);
        try {
            addStandard(standard.getId());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        standardList.add(standard);

        standardTable.setItems(FXCollections.observableArrayList(standardList));
        initStandardChoice();
    }

    @FXML
    public void onDeleteStandardButton(ActionEvent actionEvent) {
        Standard standard = standardTable.getSelectionModel().getSelectedItem();
        if (standard == null) {
            return;
        }

        try {
            deleteStandard(standard.getId());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        standardList.remove(standard);
        standardTable.setItems(FXCollections.observableArrayList(standardList));
        initStandardChoice();
    }

    @FXML
    void onAddTestButton(ActionEvent event) {
        String number = testNumberField.getText();
        Laboratory laboratory = laboratories.get(testLabChoice.getSelectionModel().getSelectedIndex());
        Date testDate = java.sql.Date.valueOf(testDateField.getValue());

        Test test = new Test(number, laboratory, testDate);

        try {
            addTest(test);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        testList.clear();

        try {
            lead = getLead(leadID);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        initTestTable();
    }

    @FXML
    void onChangeStageButton(ActionEvent event) {
        String newStage = changeStageField.getText();
        try {
            setNewStage(newStage);
            lead = getLead(lead.getId());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        initCommonPage();
    }

    @FXML
    private void initialize() {

    }

    private void initCompanyTab() {
        companyNameField.setText(lead.getCompany().getName());
        companyUNPLabel.setText(String.format("УНП: %s", lead.getCompany().getUnp()));
        companyDirectorField.setText(String.format("Директор: %s", lead.getCompany().getDirector()));
        companyEmailField.setText(String.format("Email: %s", lead.getCompany().getEmail()));
        companyPhoneLabel.setText(String.format("Номер телефона: %s", lead.getCompany().getPhone()));
    }

    private void initDetailPage() {
        detailIDLabel.setText(String.format("ID: %d", lead.getDetail().getId()));
        detailNameLabel.setText(String.format("Name: %s", lead.getDetail().getName()));
        detailCodeLabel.setText(String.format("Индификационный номер: %d", lead.getDetail().getCode()));

        initStandardTable();
        initStandardChoice();
    }

    private ArrayList<Standard> standardList = new ArrayList<Standard>();

    private void initStandardTable() {
        try {
            lead = getLead(leadID);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        standardNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        standardDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));


        lead.getDetail().getStandards().forEach(elem -> {
            standardList.add(elem);
        });

        standardTable.setItems(FXCollections.observableList(standardList));
    }

    ArrayList<Standard> choicesStandard = new ArrayList<>();

    private void initStandardChoice() {
        var standards = getStandards();

        try {
            lead = getLead(leadID);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        standardChoice.getItems().clear();

        for (int i = 0; i < standards.size();) {
            if (lead.getDetail().getStandards().contains(standards.get(i))) {
                standards.remove(i);
                continue;
            }

            standardChoice.getItems().add(standards.get(i).getName());
            i++;
        }
        choicesStandard = standards;
    }

    private ArrayList<TestAdapter> testList = new ArrayList<>();

    private void initTestPage() {
        initTestTable();
        initLaboratoryChoice();
    }

    private void initTestTable() {
        testIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        testNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        testLabColumn.setCellValueFactory(new PropertyValueFactory<>("laboratory"));
        testDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        lead.getTests().forEach(elem -> {
            if (elem.getLaboratory() == null) {
                return;
            }
            testList.add(new TestAdapter(elem));
        });

        testTable.setItems(FXCollections.observableList(testList));
    }

    ArrayList<Laboratory> laboratories = new ArrayList<>();

    private void initLaboratoryChoice() {
        laboratories = getLaboratories();

        laboratories.forEach(elem -> {
            testLabChoice.getItems().add(elem.getName());
        });
    }

    public void setLeadID(Long id) {
        this.leadID = id;
        try {
            lead = getLead(leadID);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        initCommonPage();
        initCompanyTab();
        initTestPage();
        initDetailPage();
    }

    private void initCommonPage() {
        if (lead.getBatchNumber() == null) {
            leadBatchNumberLead.setText(String.format("Номер партии:"));
        } else {
            addBatchNumberField.setDisable(true);
            addBatchButton.setDisable(true);
            leadBatchNumberLead.setText(String.format("Номер партии: %d", lead.getBatchNumber()));
        }

        if (lead.getSerial_number() == null) {
            leadSerialNumberLabel.setText(String.format("Серийный номер:"));
        } else {
            addSerialField.setDisable(true);
            addSerialButton.setDisable(true);
            leadSerialNumberLabel.setText(String.format("Серийный номер: %d", lead.getSerial_number()));
        }

        leadIDLabel.setText(String.format("ID: %d", lead.getId()));
        leadStageLabel.setText(String.format("Статус: %s", lead.getStage()));
    }

    private Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    private Lead getLead(Long id) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/lead/" + String.valueOf(id))
                    .header("Content-Type", "application/json")
                    .asJson();

            Lead res = new Gson().fromJson(apiResponse.getBody().toString(), Lead.class);

            if (apiResponse.getStatus() != 200) {
                throw new ClassNotFoundException(String.format("lead with id(%d) not found", id));
            }

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Standard> getStandards() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/standard")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type standardListType = new TypeToken<ArrayList<Standard>>() {
            }.getType();
            ArrayList<Standard> res = new Gson().fromJson(apiResponse.getBody().toString(), standardListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addStandard(Long id) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/detail/" +
                    String.valueOf(lead.getDetail().getId()) + "/standard")
                    .header("Content-Type", "application/json")
                    .body("{\"standard_id\": " + String.valueOf(id) + "}")
                    .asJson();

            if(apiResponse.getStatus() != 201)
                throw new ClassNotFoundException("invalid data");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void deleteStandard(Long id) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.delete(dotenv.get("HOST") + "/detail/" +
                            String.valueOf(lead.getDetail().getId()) + "/standard/" + String.valueOf(id))
                    .asJson();

            if (apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("invalid data");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Laboratory> getLaboratories() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/laboratory")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type standardListType = new TypeToken<ArrayList<Laboratory>>() {
            }.getType();
            ArrayList<Laboratory> res = new Gson().fromJson(apiResponse.getBody().toString(), standardListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addTest(Test test) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/lead/" +
                            String.valueOf(lead.getId()) + "/test")
                    .header("Content-Type", "application/json")
                    .body("{\"number\": \"" + test.getNumber() + "\",\n" +
                            "\"laboratory\": {\n" +
                            "\"id\": " + String.valueOf(test.getLaboratory().getId()) + "\n" +
                            "}," +
                            "\"test_date\": \"" + test.getTestDate().toString() + "\" }")
                    .asJson();

            if(apiResponse.getStatus() != 201)
                throw new ClassNotFoundException("invalid data");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void setNewStage(String stage) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/lead/" +
                            String.valueOf(lead.getId()) + "/stage")
                    .header("Content-Type", "application/json")
                    .body("{\"stage\": \"" + stage + "\"}")
                    .asJson();

            if(apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("invalid data");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void addSerial(long serial) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/lead/" +
                            String.valueOf(lead.getId()) + "/serial")
                    .header("Content-Type", "application/json")
                    .body("{\"serial\": " + String.valueOf(serial) + "}")
                    .asJson();

            if(apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("invalid data");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void addBatch(long batch) throws ClassNotFoundException {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/lead/" +
                            String.valueOf(lead.getId()) + "/batch")
                    .header("Content-Type", "application/json")
                    .body("{\"batch\": " + String.valueOf(batch) + "}")
                    .asJson();

            if(apiResponse.getStatus() != 200)
                throw new ClassNotFoundException("invalid data");

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public static boolean openWebpage(String uri) {
        try {
            Runtime.getRuntime().exec("xdg-open "+ uri);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void onOpenCertificateButton(ActionEvent actionEvent) {
        if (lead.getStage().equals("Ready")) {
            openWebpage(dotenv.get("HOST") + "/lead/" +
                    String.valueOf(lead.getId()) + "/pdf");
        }
    }
}
