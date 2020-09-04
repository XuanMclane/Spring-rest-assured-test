package com.appsdeveloperblog.app.ws.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCreateUser {
    private final String CONTEXT_PATH = "/mobile-app-ws";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void createUser() {
        List<Map<String, Object>> userAddresses = new ArrayList<>();

        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Toronto");
        shippingAddress.put("country", "Canada");
        shippingAddress.put("streetName", "123 Street");
        shippingAddress.put("postalCode", "123456");
        shippingAddress.put("type","shipping");

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city", "Toronto");
        billingAddress.put("country", "Canada");
        billingAddress.put("streetName", "123 Street");
        billingAddress.put("postalCode", "123456");
        billingAddress.put("type","billing");

        userAddresses.add(shippingAddress);
        userAddresses.add(billingAddress);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "John");
        userDetails.put("lastName", "Smith");
        userDetails.put("email", "test083001@grr.la");
        userDetails.put("password", "123");
        userDetails.put("addresses", userAddresses);

        Response response =given().contentType("application/json")
                .accept("application/json")
                .body(userDetails)
                .when()
                .post(CONTEXT_PATH + "/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String userId = response.jsonPath().getString("userId");
        assertNotNull(userId);

        String bodyString = response.body().asString();
        try {
            JSONObject responseBodyJson = new JSONObject(bodyString);
            JSONArray addresses = responseBodyJson.getJSONArray("addresses");
            assertNotNull(addresses);
            assertTrue(addresses.length() == 2);
            String addressId = addresses.getJSONObject(0).getString("addressId");
            assertNotNull(addressId);
            assertTrue(addressId.length() == 30);
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }
}
