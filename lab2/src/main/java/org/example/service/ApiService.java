package org.example.service;

import org.example.exception.ApiException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.*;
import com.google.gson.*;
import org.example.model.Employee;
import org.example.model.Position;

import java.util.ArrayList;
import java.util.List;


import java.io.IOException;

public class ApiService {

    public List<Employee> fetchEmployessFromApi(String apiUrl) throws ApiException {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                JsonArray users = gson.fromJson(response.body(), JsonArray.class);
                List<Employee> employees = new ArrayList<>();

                for (JsonElement userElement : users) {
                    JsonObject user = userElement.getAsJsonObject();
                    String firstName = user.get("name").getAsString().split(" ")[0];
                    String lastName = user.get("name").getAsString().split(" ")[1];
                    String email = user.get("email").getAsString();
                    String company = user.getAsJsonObject("company").get("name").getAsString();
                    Position position = Position.TEAM_LEAD;

                    Employee emp = new Employee(firstName, lastName, email, company, position);
                    employees.add(emp);
                }
                return employees;
            } else {
                System.out.println("HTTP error: " + response.statusCode());
                throw new ApiException("HTTP error: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ApiException("Error " + e.getMessage());
        } catch (JsonSyntaxException e) {
            throw new ApiException("Error while parsing JSON: " + e.getMessage());
        }
    }
}
