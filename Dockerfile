# Use Eclipse Temurin as the base image for the build stage with Java 17
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /opt/app

# Copy Gradle wrapper and configuration files
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./

# Download dependencies and cache them
RUN ./gradlew dependencies --no-daemon

# Copy the source code and build the bootable JAR file using the bootJar task
COPY ./src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

# Verify that the JAR file was created successfully
RUN test -f build/libs/*.jar || (echo "JAR file not found!" && exit 1)

# Use Eclipse Temurin as the base image for the runtime stage with Java 17
FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app
EXPOSE 8080

# Copy the built JAR file from the build stage
COPY --from=builder /opt/app/build/libs/*.jar /app.jar

# Define the command to run the JAR file
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
