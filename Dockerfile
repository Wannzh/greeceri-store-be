# STAGE 1: BUILD
# Gunakan image Maven dengan Java 21 (sesuai pom.xml Anda)
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set working directory di dalam container
WORKDIR /app

# Copy file pom.xml dan .mvn directory
COPY pom.xml .
COPY .mvn/ .mvn/

# Download semua dependencies
RUN mvn dependency:go-offline

# Copy sisa source code
COPY src/ src/

# Ini akan menghasilkan store-0.0.1-SNAPSHOT.jar
RUN mvn clean install -DskipTests

# STAGE 2: RUN
# Gunakan image JRE (Java Runtime) versi 21 yang minimalis
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy HANYA file .jar yang sudah di-build dari stage 'builder'
COPY --from=builder /app/target/store-0.0.1-SNAPSHOT.jar app.jar

# Port yang diekspos (sesuai application.properties Anda)
EXPOSE 8080

# Perintah untuk menjalankan aplikasi saat container
# dimulai. Kita akan set Spring Profile di Render.
ENTRYPOINT ["java", "-jar", "app.jar"]