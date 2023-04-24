## Build Angular
FROM node:19 as angular

WORKDIR /app

COPY client/angular.json .
COPY client/package.json .
COPY client/package-lock.json .
COPY client/tsconfig.json .
COPY client/tsconfig.app.json .
COPY client/tsconfig.spec.json .
COPY client/src ./src
COPY client/ngsw-config.json .

RUN npm install -g @angular/cli

RUN npm install
RUN ng build

## Build Spring Boot
FROM maven:3.9.0-eclipse-temurin-19 AS springboot

WORKDIR /app

COPY server/mvnw .
COPY server/mvnw.cmd .
COPY server/pom.xml .
COPY server/src ./src
COPY --from=angular /app/dist/client/* ./src/main/resources/static

RUN mvn package -Dmaven.test.skip=true

# Copy the final Jar file
FROM eclipse-temurin:19-jre

WORKDIR /app

COPY --from=springboot /app/target/server-0.0.1-SNAPSHOT.jar server.jar

ARG GOOGLE_API_KEY
ARG TIH_API_KEY
ARG SPACES_ACCESS_KEY
ARG SPACES_SECRET_KEY
ARG SPRING_DATASOURCE_URL
ARG SPRING_MAIL_HOST
ARG SPRING_MAIL_PORT
ARG SPRING_MAIL_USERNAME
ARG SPRING_MAIL_PASSWORD
ARG SPRING_REDIS_HOST
ARG SPRING_REDIS_PORT
ARG SPRING_REDIS_PASSWORD

ENV GOOGLE_API_KEY=${GOOGLE_API_KEY}
ENV TIH_API_KEY=${TIH_API_KEY}
ENV SPACES_ACCESS_KEY=${SPACES_ACCESS_KEY}
ENV SPACES_SECRET_KEY=${SPACES_SECRET_KEY}
ENV SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
ENV SPRING_MAIL_HOST=${SPRING_MAIL_HOST}
ENV SPRING_MAIL_PORT=${SPRING_MAIL_PORT}
ENV SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
ENV SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}
ENV SPRING_REDIS_HOST=${SPRING_REDIS_HOST}
ENV SPRING_REDIS_PORT=${SPRING_REDIS_PORT}
ENV SPRING_REDIS_PASSWORD=${SPRING_REDIS_PASSWORD}

ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT java -Dserver.port=${PORT} -jar server.jar