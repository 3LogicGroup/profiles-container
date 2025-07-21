FROM amazoncorretto:21.0.6
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} etc/app.jar
EXPOSE 8080
WORKDIR /etc
ENTRYPOINT ["java", "--enable-preview", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8001", "app.jar"]