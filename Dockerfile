# Gunakan JDK sebagai base image
FROM openjdk:21-jdk

# Buat direktori kerja
WORKDIR /app

# Salin jar hasil build ke image
COPY target/spring-restful-api-0.0.1-SNAPSHOT.jar app.jar

# Jalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]