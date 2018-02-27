The example `docker run` command from the README includes two volume mounts:

`docker run -d -p 7000:7000 -v /your/jdbc/ojdbc8.jar:/app/ojdbc8.jar -v /your/config/directory:/config -v /your/secrets/directory:/passwords tjheslin1/patterdale:1.1.2`

## System properties

### `logback.xml`

If a `logback.xml` file is included in the directory passed into the `/config` container volume, 
this will override the default logging.

### `config.file`

The local path to `patterdale.yml`.

### `status.page`

Optionally a path to a file can be specified. This file will be displayed on the `/status` endpoint.
The default will display the `patterdale.yml` configuration by referring to the `config.file` property.

### `passwords.file`

The local path to the `passwords.yml` file. 
Application configuration as well as database connection and probe information is defined here.

## patterdale.yml

`/config` is expected to contain a file `patterdale.yml`:

`httpPort` is the port the application will run on.

`cacheDuration` is the lifetime, in seconds, of the cache of SQL probe results.
After which the next call to /metrics will trigger a full scrape of all database probes and store in the cache again. This is to prevent overloading the databases with requests.

`probeConnectionWaitInSeconds` is the time, in seconds, to wait during a probe scrape for the initial database connection pools to be initialised.
If this times out, no metrics for that probe are returned. 
The app will wait for this time for each scrape, once it has passed once it will always pass.

`databases` is a list of the databases the application will connect to.
Each database definition references probes defined in the `probes` list below, to be executed against that database.
The optional `metricLabels` field defines a list of metric label and value key-pairs that will be added to every probe for that database.

Note: The metric label of `database=${databaseName}` will be automatically appended to all _metricLabels_.

e.g For the below configuration the _healthCheck_ metrics will appear as:

```
database_up{database="bobsDatabase",query="SELECT 1 FROM DUAL"} 1.0
database_up{database="alicesDatabase",query="SELECT 1 FROM DUAL"} 1.0
```

### Example `patterdale.yml` file':
```yml
httpPort: 7001
cacheDuration: 60
probeConnectionWaitInSeconds: 10
databases:
  - name: bobsDatabase
    user: system
    jdbcUrl: jdbc:oracle:thin:@localhost:1522:xe
    probes:
      - healthCheck
    metricLabels:
      label: value
  - name: alicesDatabase
    user: system
    jdbcUrl: jdbc:oracle:thin:@localhost:1523:xe
    probes:
      - healthCheck
      - slowestQueries

connectionPool:
  maxSize: 5
  minIdle: 1
  maxConnectionRetries: 10
  connectionRetryDelayInSeconds: 60

probes:
  - name: healthCheck
    type: exists
    query: SELECT 1 FROM DUAL
    metricName: database_up
    metricLabels: query="SELECT 1 FROM DUAL"
  - name: slowestQueries
    type: list
    metricName: slowest_queries
    metricLabels: sqlText="%s",sqlId="%s",username="%s",childNumber="%s",diskReads="%s",executions="%s",firstLoadTime="%s",lastLoadTime="%s"
    query: |
        SELECT * FROM
        (SELECT
            s.elapsed_time / s.executions / 1000 AS AVG_ELAPSED_TIME_IN_MILLIS,
            SUBSTR(s.sql_fulltext, 1, 80) AS SQL_TEXT,
            s.sql_id,
            d.username,
            s.child_number,
            s.disk_reads,
            s.executions,
            s.first_load_time,
            s.last_load_time
        FROM    v$sql s, dba_users d
        WHERE   s.parsing_user_id = d.user_id
        AND trunc(TO_DATE(s.last_load_time, 'YYYY-MM-DD/HH24:MI:SS')) >= trunc(SYSDATE - 1)
        ORDER BY elapsed_time DESC)
        WHERE ROWNUM <= 5;
```

## passwords.yml

`/secrets` is expected to contain a file `passwords.yml` with the following content:
It is up to you to encrypt this file and pass it safely to the application (e.g. via Kubernetes secrets).
Provided with the project is a set of helm charts with includes `secrets.yaml` which defines the volume for this file.

Example `passwords.yml` file:
```yml
passwords:
  bobsDatabase: oracle
  alicesDatabase: oracle
```

Note that the keys `bobsDatabase` and `alicesDatabase` correspond to the database `name` fields in `patterdale.yml`.
This key matching is how the correct password is paired to the correct database configuration.