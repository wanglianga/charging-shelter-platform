FROM node:18-alpine AS frontend-build
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.8.6-openjdk-8-slim AS backend-build
WORKDIR /backend
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=backend-build /backend/target/*.jar app.jar
COPY --from=frontend-build /frontend/dist /app/static
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
