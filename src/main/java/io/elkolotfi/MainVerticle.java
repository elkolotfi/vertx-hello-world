package io.elkolotfi;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.UUID;

public class MainVerticle extends AbstractVerticle {
    private static final String VERTICLE_ID = UUID.randomUUID().toString();
    private static final String AUTH_TOKEN = "AUTH_TOKEN";

    @Override
    public void start(Promise start) {
        vertx.deployVerticle(new HelloVerticle());

        Router router = Router.router(vertx);

        router.route().handler(context -> {
           String token = context.request().getHeader(AUTH_TOKEN);
           if (token != null && "secretToken".contentEquals(token)) {
               context.next();
           }
           else {
               context.response()
                       .setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
                       .setStatusMessage("UNAUTHORIZED")
                       .end();
           }
        });

        router.get("/api").handler(this::helloVertx);
        router.get("/api/:name").handler(this::helloName);

        router.route().handler(StaticHandler.create("web"));

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
                    Integer port = config.getJsonObject("http").getInteger("port");
                    vertx.createHttpServer().requestHandler(router).listen(port);
                    System.out.println(String.format("successfully deployed on port %d", port));
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
