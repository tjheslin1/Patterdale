# Patterdale

## What is it?

Patterdale monitors Oracle databases and exports metrics conforming to Prometheus' standards.

## Configuration

Patterdale expects two files. `patterdale.yml` and `passwords.yml`. 

`patterdale.yml` defines core application properties such as the HTTP port to run on, as well as the databases to connect to and the _probes_ to run on each of these databases.

`passwords.yml` contains the passwords related to the database users provided. It is expected to be handled appropriately, for example using Kubernetes secrets.

For more information regarding these configuration files and their conventions, see [Configuration](docs/configuration.md).

## Probes

Patterdale executes _probes_ against datbases upon request.

The currently provided _probe_ types are:

- exists (see [ExistsOracleSQLProbe.java](src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ExistsOracleSQLProbe.java))
- list (see [ListOracleSQLProbe.java](src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ListOracleSQLProbe.java))

For more information on the usages of these _probes_ and adding more probes, see [Probes](docs/probes.md). 