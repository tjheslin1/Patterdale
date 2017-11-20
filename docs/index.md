## What is it?

Patterdale is a standalone application for monitoring Oracle databases. It exports metrics conforming to [Prometheus](https://github.com/prometheus/prometheus) standards.

## Contents

- [Configuration](Configuration.md)
- [Probes](Probes.md)
- [Endpoints](endpoints.md)
- [Local testing](Local-testing.md)

## Configuration

Patterdale expects two files. `patterdale.yml` and `passwords.yml`. 

`patterdale.yml` defines core application properties such as the HTTP port to run on, as well as the databases to connect to and the _probes_ to run on each of these databases.

`passwords.yml` contains the passwords related to the database users provided. It is expected to be handled appropriately, for example using Kubernetes secrets.

For more information regarding these configuration files and their conventions, see [Configuration](Configuration).

## Probes

Patterdale executes _probes_ against databases upon request.

The currently provided _probe_ types are:

- exists (see [ExistsOracleSQLProbe.java](https://github.com/tjheslin1/Patterdale/blob/master/src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ExistsOracleSQLProbe.java))
- list (see [ListOracleSQLProbe.java](https://github.com/tjheslin1/Patterdale/blob/master/src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ListOracleSQLProbe.java))

For more information on the usages of these _probes_ and adding more probes, see [Probes](Probes).
