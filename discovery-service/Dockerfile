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

# ต้องใช้ชื่อ jar ให้ตรงกับที่ build ออกมา
COPY --from=build /app/target/discovery-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]