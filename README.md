# Patterdale [![Build Status](https://travis-ci.org/tjheslin1/Patterdale.svg?branch=master)](https://travis-ci.org/tjheslin1/Patterdale)

[![Docker Pulls](https://img.shields.io/docker/pulls/tjheslin1/patterdale.svg?maxAge=604800)](https://hub.docker.com/r/tjheslin1/patterdale/)

`docker run -d -p 7000:7000 -v /your/jdbc/odjbc7.jar:/app/odjbc7.jar -v /your/config/directory:/config -v /your/secrets/directory:/passwords tjheslin1/patterdale:0.18.0`

If a `logback.xml` file is included in the directory passed into the /config container volume, this will configure your logging.

Note the volume mount: `/your/jdbc/odjbc7.jar:/app/odjbc7.jar`.
This can be downloaded manually from the
[Oracle JDBC Downloads page](http://www.oracle.com/technetwork/database/features/jdbc/jdbc-drivers-12c-download-1958347.html)
or you can provide the following properties in your `gradle.properties` file when building locally, using gradle:

`mavenOracleUsername` and `mavenOraclePassword`.

[See here for complete documentation](https://tjheslin1.github.io/Patterdale/).

## Troubleshooting

Getting one of the following exceptions when running locally?:
- `java.sql.SQLRecoverableException`
- `java.net.ConnectException`
- `com.zaxxer.hikari.pool.HikariPool$PoolInitializationException`
- or similar

`patterdale.yml` from _src/test/resources_ will need to have the `jdbcUrl`s changed locally to 
point to your local Oracle database(s), if this occurs replace _localhost_ in 
the `jdbcUrl`s with the local IP address or your databases. For local Docker images this 
will be the IP of your machine.
