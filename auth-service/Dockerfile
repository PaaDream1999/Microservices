# ------------ build stage ------------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw clean package -DskipTests

# ------------ runtime stage ------------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]