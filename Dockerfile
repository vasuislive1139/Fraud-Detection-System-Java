FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace/app

# Copy maven wrapper and pom file
COPY backend/mvnw .
COPY backend/.mvn .mvn
COPY backend/pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY backend/src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
