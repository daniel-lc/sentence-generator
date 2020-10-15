FROM adoptopenjdk:11-jre-openj9
EXPOSE 7070
ADD /build/libs/sentence-generator-0.0.1-SNAPSHOT.jar sentence-generator.jar
ENTRYPOINT ["java", "-jar", "sentence-generator.jar"]
