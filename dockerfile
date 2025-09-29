FROM eclipse-temurin:25-jre-jammy
# copy application JAR (with libraries inside)

ADD application/target/application-*.jar /opt/application.jar
RUN chmod +x /opt/application.jar
# specify default command
CMD ["java", "-jar", "/opt/application.jar"]
