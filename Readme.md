# Vertx hello world

A simple Hello world application built with vert.x in Java

## Start from scratch

In order to generate a similar application yourself you can use maven:

```bash
mvn io.reactiverse:vertx-maven-plugin::setup
```

You just fill prompted options and you're ready to go.

## Run it

To build and run the application, you can simply type:

```bash
mvn clean package vertx:run
```

vert.x is now up and running, no need for restart, just edit and save your files and the server will detect those changes.

## Test it

Once it's running, an Http server is listening to port 8888.

So make a GET call to http://localhost:8888 With header key "AUTH_TOKEN" and value "secretToken":

```bash
curl http://localhost:8888 --header 'AUTH_TOKEN: secretToken'
> Hello Vert.x!
```

## Branches

You're actually on **chained-routes** branch. Checkout other branches for more examples of Vert.x
