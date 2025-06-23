# ------------ build stage ------------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# ถ้ามี Maven Wrapper
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw clean package -DskipTests

# ------------ runtime stage ------------
FROM eclipse-temurin:21-jre
WORKDIR /app
# ปรับ path ถ้า jar ชื่อไม่ตรง
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]