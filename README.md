# Hatch Takehome SR BE

### Summary
- Built with SpringBoot 3.0.2 with JDK 17
- Implemented Security with Spring Security, JWT Authentication / Authorisation, Servlet Filters and Postgres.
- Implemented Rate limiting with Bucket4J and Redis using Servlet Filters
- Implemented APIs uses Spring Webflux for non-blocking support of Reactive Streams
- Included Unit tests and Postman collection

## Update host file
127.0.0.1   redis.server<br>
127.0.0.1   postgres.server

## Run the necessary containers

### Build and Run Postgres

`docker-compose up -d --build postgres.server`

### Build and Run pgAdmin and Create the Database

`docker-compose up -d --build pgadmin.server`

You can access pgAdmin at http://localhost:5050/

1. Register Server

2. Name the Server in the General Tab

3. Enter the following fields in the Connection Tab:
hostname: `postgres.server`
username: `username`
password: `password`

4. Save you Server settings and under the server you've just created, create a new database `jwt_security`

Note: The table _user is automatically created, destroy when SpringBoot starts/shuts down.

### Build and Run Redis

docker-compose up -d --build redis.server

## Reload Gradle
Reload Gradle dependencies using your favourite IDE

## Build using Gradle

`gradle bootJar`

## Run using Gradle

`gradle bootRun`  ** WORKING **

## To build and Run Docker using Docker-Compose

`docker-compose up -d --build springboot.server`  ** NOT WORKING. Pls refer below. **

## To build using Jib and Run Docker

`gradle jibDockerBuild` then `docker run -p 8080:8080 springboot.server` ** NOT WORKING. Pls refer below. **

## How To Test in Post Man
- Open up `Postman.postman_collection`
- Register - `http://localhost:8080/api/v1/auth/register`
- Authenticate - `http://localhost:8080/api/v1/auth/authenticate`
- Run - `http://localhost:8080/api/v1/uniqueContinentsByCountry?countryCodes=US,CA,IT,EG` with Authorization Bearer Token to test authenticated. Remove for anonymous.

## Notes
Currently, I'm not able to run the Spring-Boot app in docker because it fails to connect to Redis and I can't, for the life of me, figure it out. Perhaps you can tell me what I did wrong at the interview ^^

Overall, it was really a good experience although at times I wanted to smash my keyboard, pull my hair - ya know ^^. Seriously, I had fun but I wondered how come you guys made it more difficult than I thought. On the other hand, I'm going to use what I've learned here in my personal app. This was actually a good time to figure out how to use Spring Webflux with Spring Boot so I really appreciate that. It was something I came across at Openlane, but never had the time to figure it out.

Cheers

