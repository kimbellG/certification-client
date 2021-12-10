package org.filippenkov.certification_client.controllers.managers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.filippenkov.certification_client.MainApplication;
import org.filippenkov.certification_client.controllers.leads.LeadDetailController;

import java.io.IOException;


public class ManageController {
    @FXML
    private Button manageCompanyButton;

    @FXML
    private Button manageDetailsButton;

    @FXML
    private Button manageLabButton;

    @FXML
    private Button manageLeadsButton;

    private void openModalWindow(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApplication.class.getResource(fxml)
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
    void onManageCompanyButton(ActionEvent event) {
        openModalWindow("lead/company-manager.fxml");
    }

    @FXML
    void onManageDetailsButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApplication.class.getResource("lead/detail-standard-manager.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(
                    new Scene(
                            (TabPane) loader.load()
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
    void onManageLabButton(ActionEvent event) {
        openModalWindow("lead/laboratory-manager.fxml");
    }

    @FXML
    void onManageLeadsButton(ActionEvent event) {
        openModalWindow("lead/manage-lead.fxml");
    }

}
