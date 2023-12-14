package dk.lyngby.routes;

import dk.lyngby.controller.impl.PageController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RoomRoute {

    private final PageController roomController = new PageController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/rooms", () -> {
                post("/hotel/{id}", roomController::create, RouteRoles.ADMIN, RouteRoles.ANYONE);
                get("/", roomController::readAll, RouteRoles.ANYONE);
                get("/{id}", roomController::read, RouteRoles.ADMIN, RouteRoles.ANYONE);
            put("/{id}", roomController::update, RouteRoles.ADMIN, RouteRoles.ANYONE);
                delete("/{id}", roomController::delete, RouteRoles.ADMIN, RouteRoles.ANYONE);
            });
        };
    }
}