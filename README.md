# Patterdale-jvm [![Build Status](https://travis-ci.org/tjheslin1/Patterdale-jvm.svg?branch=master)](https://travis-ci.org/tjheslin1/Patterdale-jvm)

[![Docker Pulls](https://img.shields.io/docker/pulls/tjheslin1/patterdale-jvm.svg?maxAge=604800)](https://hub.docker.com/r/tjheslin1/patterdale-jvm/)

`docker run -d -p 7000:7000 -v /your/config/directory:/config -v /your/secrets/directory:/passwords tjheslin1/patterdale-jvm:0.8`

`/your/config/directory` is expected to contain a file `patterdale.yml` with the following content:

Example `patterdale.yml` file':
```yml
httpPort: 7000
databases:
  - name: test
    user: system
    jdbcUrl: jdbc:oracle:thin:@localhost:1522:xe
    probes:
      - query: SELECT 1 FROM DUAL
        type: exists
        metricName: database_up
        metricLabels: database="myDB",query="SELECT 1 FROM DUAL"
  - name: test2
    user: system
    jdbcUrl: jdbc:oracle:thin:@localhost:1523:xe
    probes:
      - query: SELECT 1 FROM DUAL
        type: exists
        metricName: database_up
        metricLabels: database="myDB2",query="SELECT 1 FROM DUAL"
      - query: SELECT 2 FROM DUAL
        type: exists
        metricName: database_up
        metricLabels: database="myDB2",query="SELECT 2 FROM DUAL"
      - query: SELECT * FROM slowest_queries TOP 5
        type: list
        metricName: database_up
        metricLabels: database="myDB2",slowQuery="%s"
connectionPool:
  maxSize: 5
  minIdle: 1
```

`/your/secrets/directory` is expected to contain a file `passwords.yml` with the following content:
It is up to you to encrypt this file and pass it safely to the application (e.g. via Kubernetes secrets).

Example `passwords.yml` file:
```yml
passwords:
  - databaseName: bobsDatabase
    value: abc123
  - databaseName: aliceDatabase
    value: xyz890
```