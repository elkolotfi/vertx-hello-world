package io.elkolotfi;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import jdk.internal.joptsimple.internal.Strings;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.get("/").handler(context -> {
            context.request().response().end("Hello Vert.x!");
        });

        router.get("/:name").handler(context -> {
           String name = context.pathParam("name");
           context.request().response().end(String.format("Hello %s!", name));
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888);
    }

}
