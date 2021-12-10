package org.filippenkov.certification_client.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import org.filippenkov.certification_client.models.Lead;
import org.filippenkov.certification_client.models.User;
import org.filippenkov.certification_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PieChartController {

    @FXML
    private PieChart managerPieChart;

    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    @FXML
    protected void initialize() {
        var users = getAllUser();
        users.forEach(elem -> {
            PieChart.Data data = new PieChart.Data(elem.getFio(), getLeadForUser(elem.getId()).size());
            managerPieChart.getData().add(data);
        });

    }

    ArrayList<User> getAllUser() {
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

    ArrayList<Lead> getLeadForUser(int id) {
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

}
