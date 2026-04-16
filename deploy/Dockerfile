FROM eclipse-temurin:21-jre

WORKDIR /app

COPY movie-booking-app-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar","--spring.config.location=/app/application-prod.yml","--spring.profiles.active=prod"]
