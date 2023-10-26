
#
# Build stage
#
FROM maven:3.8.7-amazoncorretto-17 AS build
LABEL authors="papa-arthhur"
WORKDIR /home/app
COPY src ./src
COPY pom.xml .
RUN mvn -f ./pom.xml clean package

#
# Package stage
#
FROM amazoncorretto:17-alpine3.16-full
COPY --from=build /home/app/target/*.jar /usr/local/lib/orderservice.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/orderservice.jar"]