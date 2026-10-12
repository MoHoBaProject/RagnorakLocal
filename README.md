# RagnorakLocal
# tika-demo

Spring Boot 4.1 + Java 25 + Apache Tika 3.3 — file upload → text / metadata / MIME type.

## Run
    mvn spring-boot:run
Open http://localhost:8080

## Test
    mvn test

## API
    curl http://localhost:8080/api/health
    curl -F "file=@sample.pdf" http://localhost:8080/api/detect
    curl -F "file=@sample.pdf" http://localhost:8080/api/extract
    curl -F "file=@sample.pdf" http://localhost:8080/api/metadata

## Docker
    docker build -t tika-demo .
    docker run -p 8080:8080 tika-demo
