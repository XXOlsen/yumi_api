package dk.lyngby.routes;

import dk.lyngby.controller.impl.PageController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PageRoute {

    private final PageController pageController = new PageController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/pages", () -> {
                post("/diary/{id}", pageController::create, RouteRoles.ADMIN, RouteRoles.ANYONE);
                get("/", pageController::readAll, RouteRoles.ANYONE);
                get("/{id}", pageController::read, RouteRoles.ADMIN, RouteRoles.ANYONE);
            put("/{id}", pageController::update, RouteRoles.ADMIN, RouteRoles.ANYONE);
                delete("/{id}", pageController::delete, RouteRoles.ADMIN, RouteRoles.ANYONE);
            });
        };
    }
}