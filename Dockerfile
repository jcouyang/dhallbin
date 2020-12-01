FROM alpine
WORKDIR /home
RUN apk add zip
ADD https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip ./newrelic-java.zip
RUN unzip ./newrelic-java.zip

FROM openjdk:8-jre-slim
COPY --from=0 /home/newrelic .
ADD ./dhallbin .
ENV PORT 80
CMD env JAVA_OPTS="-Xms128m -Xmx512m -javaagent:$(pwd)/newrelic.jar" ./dhallbin -port=:${PORT}