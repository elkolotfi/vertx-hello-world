package io.elkolotfi;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.deployVerticle(new HelloVerticle());

        Router router = Router.router(vertx);

        router.get("/").handler(this::helloVertx);

        router.get("/:name").handler(this::helloName);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888);
    }

    private void helloVertx(RoutingContext context) {
        vertx.eventBus().request("hello.vertx.addr", null, reply -> {
            String response = (String) reply.result().body();
            context.request().response().end(response);
        });
    }

    private void helloName(RoutingContext context) {
        String name = context.pathParam("name");
        vertx.eventBus().request("hello.named.addr", name, reply -> {
            String response = (String) reply.result().body();
            context.request().response().end(response);
        });

    }

}
