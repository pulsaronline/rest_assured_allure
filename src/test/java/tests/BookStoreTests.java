package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import models.AuthorisationResponse;
import models.Books;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static filters.CustomLogFilter.customLogFilter;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class BookStoreTests {
    @Test
    void noLogsTest() {
        given()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withAllLogsTest() {
        given()
                .log().all()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().all()
                 .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withSomeLogsTest() {
        given()
                .log().uri()
                .log().body()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withSomePostTest() {
        given()
                .contentType(JSON)
                .body("{ \"userName\": \"alex\", \"password\": \"W1_#zqwerty\" }")
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    void withAllureListenerTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");

        given()
                .contentType(JSON)
                .filter(new AllureRestAssured())
                .body(data)
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    void withCustomFilterTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");

        given()
                .contentType(JSON)
                .filter(customLogFilter().withCustomTemplates())
                .body(data)
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    void withAssertJTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");
        String response =
                given()
                        .contentType(JSON)
                        .filter(customLogFilter().withCustomTemplates())
                        .body(data)
                        .when()
                        .log().uri()
                        .log().body()
                        .post("https://demoqa.com/Account/v1/GenerateToken")
                        .then()
                        .log().body()
                        .extract().asString();
        assert (response).contains("\"status\":\"Success\"");
        assert (response).contains("\"result\":\"User authorized successfully.\"");
    }

    @Test
    void withModelTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");
        AuthorisationResponse response =
                given()
                        .contentType(JSON)
                        .filter(customLogFilter().withCustomTemplates())
                        .body(data)
                        .when()
                        .log().uri()
                        .log().body()
                        .post("https://demoqa.com/Account/v1/GenerateToken")
                        .then()
                        .log().body()
                        .extract().as(AuthorisationResponse.class);
        assert (response.getStatus()).contains("Success");
        assert (response.getResult()).contains("User authorized successfully.");
    }

    @Test
    void booksModelTest() {
        Books books =
                given()
                        .log().uri()
                        .log().body()
                        .get("https://demoqa.com/BookStore/v1/Books")
                        .then()
                        .log().body()
                        .extract().as(Books.class);
        //System.out.println(books);
    }

    @Test
    void booksJsonSchemaTest() {
        given()
                .log().uri()
                .log().body()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .body(matchesJsonSchemaInClasspath("jsonSchemas/booklist_response.json"));
    }
}
