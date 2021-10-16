package io.elkolotfi;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public class MainVerticle extends AbstractVerticle {
    private static final String VERTICLE_ID = UUID.randomUUID().toString();

    @Override
    public void start(Promise start) {
        vertx.deployVerticle(new HelloVerticle());

        Router router = Router.router(vertx);

        router.get("/").handler(this::helloVertx);

        router.get("/:name").handler(this::helloName);

        handleConfig(start, router);
    }

    private void handleConfig(Promise start, Router router) {

        ConfigStoreOptions defaultConfig = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "config.json"));

        ConfigRetrieverOptions retrieverOptions = new ConfigRetrieverOptions()
                .addStore(defaultConfig);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, retrieverOptions);

        retriever.getConfig()
                .onSuccess(config ->  {
                   vertx.createHttpServer().requestHandler(router).listen(config.getJsonObject("http").getInteger("port"));
                   start.complete();
                })
                .onFailure(msg -> start.fail(msg.getMessage()));
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
