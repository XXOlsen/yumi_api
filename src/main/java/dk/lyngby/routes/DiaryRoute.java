package dk.lyngby.routes;

import dk.lyngby.controller.impl.DiaryController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DiaryRoute {

    private final DiaryController diaryController = new DiaryController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/diary", () -> {
                post("/", diaryController::create, RouteRoles.ADMIN, RouteRoles.ANYONE);
                get("/", diaryController::readAll, RouteRoles.ANYONE);
                get("/{id}", diaryController::read, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.ANYONE);
                put("/{id}", diaryController::update, RouteRoles.ADMIN, RouteRoles.ANYONE);
                delete("/{id}", diaryController::delete, RouteRoles.ADMIN, RouteRoles.ANYONE);
            });
        };
    }
}