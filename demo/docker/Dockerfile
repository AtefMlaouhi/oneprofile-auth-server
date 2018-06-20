FROM java:8-alpine

RUN mkdir /demo

WORKDIR /demo

RUN sh -c 'touch app-fat-exec.jar'

CMD ["java", "-jar", "app-fat-exec.jar"]
