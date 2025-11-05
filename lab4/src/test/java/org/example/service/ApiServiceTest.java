package org.example.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.model.Employee;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {

    @Mock
    private HttpClient client;

    @Mock
    private HttpResponse<String> response;

    private final Gson gson = new Gson();
    private final String testApiUrl = "https://jsonplaceholder.typicode.com/users";

    @Test
    void fetchFromAPI_success_parsesEmployees() throws Exception {
        when(response.statusCode()).thenReturn(200);
        String body = "[{\"id\":1,\"name\":\"Jan Kowalski\",\"email\":\"jan@x.com\",\"company\":{\"name\":\"X\"}}]";
        when(response.body()).thenReturn(body);
        when(client.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(response);

        ApiService api = new ApiService(client, gson, testApiUrl);
        List<Employee> employees = api.fetchEmployeesFromApi();

        assertThat(employees)
                .isNotNull()
                .hasSize(1)
                .extracting(Employee::getEmail)
                .containsExactly("jan@x.com");
    }


}
