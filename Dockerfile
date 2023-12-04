FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/partydj.jar partydj.jar
ENTRYPOINT ["java","-jar","/partydj.jar"]