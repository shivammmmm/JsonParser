FROM openjdk:8-alpine

COPY ./pom.xml /app/pom.xml

COPY ./Tamra-Asses /app/Tamra-Asses/

RUN cd /app/Tamra-Asses && \
    mvn -T 1C clean package \
    chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/dockerConf/entrypoint.sh"]


