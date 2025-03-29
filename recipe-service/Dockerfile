FROM eclipse-temurin:21.0.3_9-jre-alpine
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} /recipe-service.jar
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/recipe-service.jar"]