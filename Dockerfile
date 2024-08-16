FROM openjdk:21
COPY target/github-repo-app-0.0.1-SNAPSHOT.jar githubrepoapp.jar
ENTRYPOINT ["java", "-jar", "githubrepoapp.jar"]

