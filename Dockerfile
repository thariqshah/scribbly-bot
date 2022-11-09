FROM openjdk:17-jdk-slim
MAINTAINER aristocat02
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]