package io.elkolotfi;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public class MainVerticle extends AbstractVerticle {
    private static final String VERTICLE_ID = UUID.randomUUID().toString();

    @Override
    public void start() {
        vertx.deployVerticle(new HelloVerticle());

        Router router = Router.router(vertx);

        router.get("/").handler(this::helloVertx);

        router.get("/:name").handler(this::helloName);

        int httpPort = 8080;
        try {
            httpPort = Integer.parseInt(System.getProperty("http.port", String.valueOf(httpPort)));
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(httpPort);
    }

    private void helloVertx(RoutingContext context) {
        vertx.eventBus().request("hello.vertx.addr", null, reply -> {
            String response = (String) reply.result().body();
            response += "from: " + VERTICLE_ID;
            context.request().response().end(response);
        });
    }

    private void helloName(RoutingContext context) {
        String name = context.pathParam("name");
        vertx.eventBus().request("hello.named.addr", name, reply -> {
            String response = (String) reply.result().body();
            response += "from: " + VERTICLE_ID;
            context.request().response().end(response);
        });

    }

}
