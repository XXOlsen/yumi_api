package dk.lyngby.routes;

import dk.lyngby.controller.impl.HotelController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HotelRoute {

    private final HotelController hotelController = new HotelController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/hotels", () -> {
                post("/", hotelController::create, RouteRoles.ADMIN, RouteRoles.ANYONE);
                get("/", hotelController::readAll, RouteRoles.ANYONE);
                get("/{id}", hotelController::read, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.ANYONE);
                put("/{id}", hotelController::update, RouteRoles.ADMIN, RouteRoles.ANYONE);
                delete("/{id}", hotelController::delete, RouteRoles.ADMIN, RouteRoles.ANYONE);
            });
        };
    }
}