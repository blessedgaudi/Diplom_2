package user;

import api.stellarburgers.User;
import clientStellarBurgers.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UserCreatingTests {

    private String name;
    private String email;
    private String password;
    private User user;
    private UserClient userClient;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        name = "Dambldor";
        email = "Dambldor@yandex.ru";
        password = "Albus223345";
        userClient = new UserClient();
        user = new User();
    }

    @Test
    @DisplayName("Проверка создание уникального пользователя.")
    @Description("Регистрация уникального пользователя c корректными данными.")
    public void checkCreateUserTest() {
        user = new User(name, email, password);
        Response response = UserClient.postCreateNewUser(user);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован.")
    @Description("Регистрация уже зарегистрированного пользователя.")
    public void checkRegisteredUserTest() {
        user = new User(name, email, password);
        UserClient.postCreateNewUser(user);
        Response response = UserClient.postCreateNewUser(user);
        response.then().log().all()
                .assertThat().statusCode(403).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("User already exists"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени.")
    @Description("Регистрация поверка пользователя без имени, но с заполненными email и password")
    public void createUserWithoutNameTest() {
        user.setEmail(email);
        user.setPassword(password);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без email.")
    @Description("Регистрация поверка пользователя без email, но с заполненными именем и паролем")
    public void createUserWithoutEmailTest() {
        user.setName(name);
        user.setPassword(password);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без пароля.")
    @Description("Регистрация поверка пользователя без пароля, но с заполненными именем и email.")
    public void createUserWithoutPasswordTest() {
        user.setEmail(email);
        user.setName(name);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени и email.")
    @Description("Регистрация поверка пользователя без имени и email, но с заполненными password.")
    public void createUserWithoutNameAndEmailTest() {
        user.setPassword(password);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени и пароля.")
    @Description("Регистрация поверка пользователя без имени и пароля, но с заполненными email.")
    public void createUserWithoutNameAndPasswordTest() {
        user.setEmail(email);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без email и пароля.")
    @Description("Регистрация поверка пользователя без email и пароля, но с заполненными именем.")
    public void createUserWithoutEmailAndPasswordTest() {
        user.setName(name);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без всех полей.")
    @Description("Регистрация поверка пользователя без имени, email, пароля.")
    public void createUserWithoutNameAndEmailAndPasswordTest() {
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }
    @After
    public void tearDown(){
        // Удаление созданного пользователя
        String accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        if (accessToken !=null) {
            userClient.deleteUser(accessToken);
        }
    }
}
