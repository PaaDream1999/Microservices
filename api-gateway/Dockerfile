# ------------ build stage ------------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 1) เตรียม Maven wrapper + pom
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# 2) ใส่ source แล้ว build
COPY src src
RUN ./mvnw clean package -DskipTests

# ------------ runtime stage ------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# 3) คัด .jar มาเป็น app.jar   (ใช้ wildcard ให้ยืดหยุ่น)
COPY --from=build /app/target/*SNAPSHOT.jar app.jar

# 4) รัน — **อย่าใส่ / นำหน้า** (WORKDIR = /app แล้ว)
ENTRYPOINT ["java", "-jar", "app.jar"]