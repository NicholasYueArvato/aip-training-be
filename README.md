### Introduction 
BEP project

### Getting Started
Prepare a postgres database locally using default settings
```
Database name: bep
User: bep
Password: bep
Port: 5432
```

Build project once
```
cd bep
mvn clean install
```

Start system and DB will be instantiated automatically.
```
mvn spring-boot:run
```
The default port is 9200


### Running front end
#### Hosting backend rest api and resources

Build the project using profile build_angular_app
```
mvn clean install -Pbuild_angular_app
```
To host resources for angular web application, the application has to be started as war.
```
java -jar target\bep-base-1.00-SNAPSHOT.jar
```
Note that during development this step can be skipped as it can be started as tomcat project using IDE such as intelij and eclipse

#### Running angular web app
Running angular web app from webpack is done as following
```
cd src\main\webapp
runserve.cmd
```
The default port is 4200


### How to get a token
```
curl --location --request POST 'http://localhost:9200/bep/login' \
--header 'Content-Type: application/json' \
--data-raw '{"username":"admin","password":"Arvato@567"}'
```

### swagger
Swagger open API is by defaulted imported with AEP SDK, the url is here.
http://localhost:9200/becentral/v3/api-docs

### Others




