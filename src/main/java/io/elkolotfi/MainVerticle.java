package io.elkolotfi;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.get("/").handler(this::helloVertx);

        router.get("/:name").handler(this::helloName);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888);
    }

    private void helloVertx(RoutingContext context) {
        context.request().response().end("Hello Vert.x!");
    }

    private void helloName(RoutingContext context) {
        String name = context.pathParam("name");
        context.request().response().end(String.format("Hello %s!", name));
    }

}
