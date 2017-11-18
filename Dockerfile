FROM openjdk:8u131-jdk-alpine

RUN mkdir /app && mkdir /config
WORKDIR /app

COPY build/libs/patterdale-all.jar .

CMD ["java", "-jar", \
"-Dlogback.configurationFile=/config/logback.xml", \
"-Dconfig.file=/config/patterdale.yml", \
"-Dpasswords.file=/passwords/passwords.yml", \
"-Xms256m -Xmx1024m", "-XX:MaxMetaspaceSize512m", "patterdale-all.jar"]
