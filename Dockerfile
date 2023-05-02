FROM openjdk:11-jdk-slim
ADD /target/BankStatementSynchronizer-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]