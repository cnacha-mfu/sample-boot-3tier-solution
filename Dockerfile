# Packages the REST service (the middle tier) only.
#
# Build the whole project first, from THIS folder:
#     mvn install -DskipTests
#     docker build -t library-web-service .
#
# The jar is the web service's, not the top project's - the top project has
# packaging=pom and produces no jar of its own.
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY library-web-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
