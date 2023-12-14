package dk.lyngby.controller.impl;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dto.DiaryDTO;
import dk.lyngby.dto.PageDTO;
import dk.lyngby.model.Diary;
import dk.lyngby.model.Role;
import dk.lyngby.model.Page;
import dk.lyngby.model.User;
import io.javalin.Javalin;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HotelControllerTest
{
    private static Javalin app;
    private static final String BASE_URL = "http://localhost:7777/api/v1";
    private static DiaryController hotelController;
    private static EntityManagerFactory emfTest;
    private static Object adminToken;
    private static Object userToken;

    private static Diary h1, h2;
    private static User user, admin;
    private static Role userRole, adminRole;

    @BeforeAll
    static void beforeAll()
    {
        HibernateConfig.setTest(true);
        emfTest = HibernateConfig.getEntityManagerFactory();
        hotelController = new DiaryController();
        app = Javalin.create();
        ApplicationConfig.startServer(app, 7777);

        // Create users and roles
        user = new User("usertest", "user123");
        admin = new User("admintest", "admin123");
        userRole = new Role("user");
        adminRole = new Role("admin");
        user.addRole(userRole);
        admin.addRole(adminRole);
        try (var em = emfTest.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }

        // Get tokens
        UserController userController = new UserController();
        adminToken = getToken(admin.getUsername(), "admin123");
        userToken = getToken(user.getUsername(), "user123");
    }

    @BeforeEach
    void setUp()
    {
        Set<Page> calRooms = getCalRooms();
        Set<Page> hilRooms = getBatesRooms();

        try (var em = emfTest.createEntityManager())
        {
            em.getTransaction().begin();

            // Delete all rows
            em.createQuery("DELETE FROM Page r").executeUpdate();
            em.createQuery("DELETE FROM Diary h").executeUpdate();

            // Reset sequence
            em.createNativeQuery("ALTER SEQUENCE room_room_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_hotel_id_seq RESTART WITH 1").executeUpdate();

            // Insert test data for hotels and rooms
            h1 = new Diary("Hotel California", "California", Diary.HotelType.LUXURY);
            h2 = new Diary("Bates Motel", "Lyngby", Diary.HotelType.STANDARD);
            h1.setRooms(calRooms);
            h2.setRooms(hilRooms);
            em.persist(h1);
            em.persist(h2);

            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDown()
    {
        HibernateConfig.setTest(false);
        ApplicationConfig.stopServer(app);
    }

    @Test
    void read()
    {
        given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .when()
                .get(BASE_URL + "/hotels/" + h1.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200)
                .body("id", equalTo(h1.getId()));
    }

    @Test
    void readAll()
    {
        // Given -> When -> Then
        List<DiaryDTO> hotelDtoList =
                given()
                        .contentType("application/json")
                        .when()
                        .get(BASE_URL + "/hotels")
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.OK_200)  // could also just be 200
                        .extract().body().jsonPath().getList("", DiaryDTO.class);

        DiaryDTO h1DTO = new DiaryDTO(h1);
        DiaryDTO h2DTO = new DiaryDTO(h2);

        assertEquals(hotelDtoList.size(), 2);
        assertThat(hotelDtoList, containsInAnyOrder(h1DTO, h2DTO));
    }

    @Test
    void create()
    {
        Diary h3 = new Diary("Cab-inn", "Østergade 2", Diary.HotelType.BUDGET);
        Page r1 = new Page(117, new BigDecimal(4500), Page.RoomType.SINGLE);
        Page r2 = new Page(118, new BigDecimal(2300), Page.RoomType.DOUBLE);
        h3.addRoom(r1);
        h3.addRoom(r2);
        DiaryDTO newHotel = new DiaryDTO(h3);

        List<PageDTO> roomDtos =
                given()
                        .header("Authorization", adminToken)
                        .contentType(ContentType.JSON)
                        .body(newHotel)
                        .when()
                        .post(BASE_URL + "/hotels")
                        .then()
                        .statusCode(201)
                        .body("id", equalTo(3))
                        .body("hotelName", equalTo("Cab-inn"))
                        .body("hotelAddress", equalTo("Østergade 2"))
                        .body("hotelType", equalTo("BUDGET"))
                        .body("rooms", hasSize(2))
                        .extract().body().jsonPath().getList("rooms", PageDTO.class);

        assertThat(roomDtos, containsInAnyOrder(new PageDTO(r1), new PageDTO(r2)));
    }

    @Test
    void update()
    {
        // Update the Bates Motel to luxury

        DiaryDTO updateHotel = new DiaryDTO("Bates Motel", "Lyngby", Diary.HotelType.LUXURY);
        given()
                .header("Authorization", adminToken)
                .contentType(ContentType.JSON)
                .body(updateHotel)
                .log().all()
                .when()
                .put(BASE_URL + "/hotels/" + h2.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(h2.getId()))
                .body("hotelName", equalTo("Bates Motel"))
                .body("hotelAddress", equalTo("Lyngby"))
                .body("hotelType", equalTo("LUXURY"))
                .body("rooms", hasSize(6));
    }

    @Test
    void delete()
    {
        // Remove hotel California
        given()
                .header("Authorization", adminToken)
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_URL + "/hotels/" + h1.getId())
                .then()
                .statusCode(204);

        // Check that it is gone
        given()
                .header("Authorization", adminToken)
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_URL + "/hotels/" + h1.getId())
                .then()
                .statusCode(404);
    }

    @NotNull
    private static Set<Page> getCalRooms()
    {
        Page r100 = new Page(100, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r101 = new Page(101, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r102 = new Page(102, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r103 = new Page(103, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r104 = new Page(104, new BigDecimal(3200), Page.RoomType.DOUBLE);
        Page r105 = new Page(105, new BigDecimal(4500), Page.RoomType.SUITE);

        Page[] roomArray = {r100, r101, r102, r103, r104, r105};
        return Set.of(roomArray);
    }

    @NotNull
    private static Set<Page> getBatesRooms()
    {
        Page r111 = new Page(111, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r112 = new Page(112, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r113 = new Page(113, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r114 = new Page(114, new BigDecimal(2520), Page.RoomType.DOUBLE);
        Page r115 = new Page(115, new BigDecimal(3200), Page.RoomType.DOUBLE);
        Page r116 = new Page(116, new BigDecimal(4500), Page.RoomType.SUITE);

        Page[] roomArray = {r111, r112, r113, r114, r115, r116};
        return Set.of(roomArray);
    }

    public static Object getToken(String username, String password)
    {
        return login(username, password);
    }

    private static Object login(String username, String password)
    {
        String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        var token = given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("http://localhost:7777/api/v1/auth/login")
                .then()
                .extract()
                .response()
                .body()
                .path("token");

        return "Bearer " + token;
    }


}