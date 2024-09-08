package clientStellarBurgers;

import api.stellarburgers.Ingredients;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import io.qameta.allure.restassured.AllureRestAssured;
import api.stellarburgers.Order;
import org.hamcrest.Matchers;

public class OrderClient {

    @Step("Получение данных об ингредиентах.")
    public Ingredients getIngredient() {
        return given()
                .header("Content-Type", "application/json")
                .log().all()
                .get("/api/ingredients")
                .body()
                .as(Ingredients.class);
    }

    @Step("Создание заказа с авторизацией.")
    public static Response createOrderWithAuthorization(Order order, String token) {
        return given().log().all().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("authorization", token)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации.")
    public static Response createOrderWithoutAuthorization(Order order) {
        return given().log().all()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создание заказа без ингредиентов.")
    public void checkFailedResponseApiOrders(Response response) {
        response.then().log().all()
                .assertThat().statusCode(400).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("Ingredient ids must be provided"));
    }
}
