# Patterdale

## What is it?

Patterdale monitors Oracle databases and exports metrics conforming to [Prometheus](https://github.com/prometheus/prometheus) standards.

## Configuration

Patterdale expects two files. `patterdale.yml` and `passwords.yml`. 

`patterdale.yml` defines core application properties such as the HTTP port to run on, as well as the databases to connect to and the _probes_ to run on each of these databases.

`passwords.yml` contains the passwords related to the database users provided. It is expected to be handled appropriately, for example using Kubernetes secrets.

For more information regarding these configuration files and their conventions, see [Configuration](configuration.md).

## Probes

Patterdale executes _probes_ against databases upon request.

The currently provided _probe_ types are:

- exists (see [ExistsOracleSQLProbe.java](https://github.com/tjheslin1/Patterdale/blob/master/src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ExistsOracleSQLProbe.java))
- list (see [ListOracleSQLProbe.java](https://github.com/tjheslin1/Patterdale/blob/master/src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ListOracleSQLProbe.java))

For more information on the usages of these _probes_ and adding more probes, see [Probes](probes.md).

## Local testing

To run the full `./gradlew` build locally, [PatterdaleTest.java](https://github.com/tjheslin1/Patterdale/blob/master/src/test/java/endtoend/PatterdaleTest.java)
expects two oracle databases to be running locally.

You can start a pair of default databases with the following commands.
```
docker run -d -p 8081:8080 -p 1522:1521 sath89/oracle-12c
docker run -d -p 8082:8080 -p 1523:1521 sath89/oracle-12c
```

### Building snapshot docker images

```
docker build -t tjheslin1/patterdale:DEV .
docker run --name patterdale-test -d -p 7001:7001 -v ~/Patterdale-jvm/src/main/resources/:/config -v ~/Patterdale-jvm/src/main/resources/:/passwords tjheslin1/patterdale:DEV

docker logs -f ${container_id}
```

Once the app has started up and created connection pools, the URL to the /metrics page will be logged.
