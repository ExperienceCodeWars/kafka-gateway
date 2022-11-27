FROM bellsoft/liberica-openjdk-alpine-musl
COPY ./target/kafka-gateway-*.jar /home/jboss/kafka-gateway.jar
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar","/home/jboss/kafka-gateway.jar"]