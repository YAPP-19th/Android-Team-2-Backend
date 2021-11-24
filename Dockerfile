FROM adoptopenjdk/openjdk11:alpine-jre
ENV HOME=/usr/app
COPY --from=BUILD  $HOME/module-api/build/libs/*.jar /app.jar

RUN "java -jar app.jar --spring.datasource.url=jdbc:mysql://mysql_db:3306/dev?serverTimezone=UTC"