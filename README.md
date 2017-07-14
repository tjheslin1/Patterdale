# Patterdale-jvm [![Build Status](https://travis-ci.org/tjheslin1/Patterdale-jvm.svg?branch=master)](https://travis-ci.org/tjheslin1/Patterdale-jvm)

[![Docker Pulls](https://img.shields.io/docker/pulls/tjheslin1/patterdale-jvm.svg?maxAge=604800)](https://hub.docker.com/r/tjheslin1/patterdale-jvm/)

`docker run -d -p 7000:7000 -v /your/config/directory:/config tjheslin1/patterdale-jvm:0.5`

`/your/config/directory` is expected to contain a file `patterdale.yml` with the following content:

Example:
```yml
httpPort: 7000
database:
  user: system
  password: oracle
  jdbcUrl: jdbc:oracle:thin:system/oracle@localhost:1521:xe
connectionPool:
  maxSize: 5
  minIdle: 1
```