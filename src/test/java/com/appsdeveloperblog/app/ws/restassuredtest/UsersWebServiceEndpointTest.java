package com.appsdeveloperblog.app.ws.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersWebServiceEndpointTest {
    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String EMAIL_ADDRESS = "test083001@grr.la";
    private final String JSON = "application/json";
    private static String authorizationHeader;
    private static String userId;
    private static List<Map<String, String>> addresses;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }


    /*
    * POST /users/Login
    */
    @Test
    void a() {
        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", EMAIL_ADDRESS);
        loginDetails.put("password", "123");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post(CONTEXT_PATH +"/users/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        authorizationHeader = response.header("Authorization");
        userId = response.header("UserId");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }

    /*
     * GET /users/{userId}
     */
    @Test
    void b() {
        Response response = given()
                .pathParam("userId", userId)
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .when()
                .get(CONTEXT_PATH +"/users/{userId}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");

        assertNotNull(response);
        assertNotNull(userPublicId);
        assertNotNull(userEmail);
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertEquals(EMAIL_ADDRESS, userEmail);

        assertTrue(addresses.size() == 2);
        assertTrue(addressId.length() == 30);
    }

    /*
     * PUT /users/{userId}
     */
    @Test
    void c() {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Bill");
        userDetails.put("lastName", "Gates");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization", authorizationHeader)
                .pathParam("userId", userId)
                .body(userDetails)
                .when()
                .put(CONTEXT_PATH + "/users/{userId}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

        assertEquals("Bill", firstName);
        assertEquals("Gates", lastName);
        assertNotNull(storedAddresses);
        assertTrue(addresses.size() == storedAddresses.size());
        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
    }


    /*
     * DELETE /users/{userId}
     */
    @Test
    void d() {
        Response response = given()
                .header("Authorization", authorizationHeader)
                .pathParam("userId", userId)
                .accept(JSON)
                .when()
                .delete(CONTEXT_PATH + "/users/{userId}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");
        assertEquals("SUCCESS", operationResult);
    }
}
