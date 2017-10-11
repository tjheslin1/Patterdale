# Configuration

The example `docker run` commands includes two volume mounts:

`docker run -d -p 7000:7000 -v /your/jdbc/ojdbc7.jar:/app/ojdbc7.jar -v /your/config/directory:/config -v /your/secrets/directory:/passwords tjheslin1/patterdale:0.15.0`

If a `logback.xml` file is included in the directory passed into the `/config` container volume, this will configure your logging.

## patterdale.yml

`httpPort` is the port the application will run on.

`cacheDuration` is the lifetime, in seconds, of the cache of SQL probe results.
After which the next call to /metrics will trigger a full scrape of all database probes and store in the cache again. This is to prevent overloading the databases with requests.

`databases` is a list of the databases the application will connect to.
Each database definition references probes defined in the `probes` list below, to be executed against that database.

Note: The metric label of `database=${databaseName}` will be automatically appended to all _metricLabels_.

e.g For the below configuration the _healthCheck_ metrics will appear as:

```
database_up{database="bobsDatabase",query="SELECT 1 FROM DUAL"} 1.0
database_up{database="alicesDatabase",query="SELECT 1 FROM DUAL"} 1.0
```

`/your/config/directory` is expected to contain a file `patterdale.yml`, below is an example:

Example `patterdale.yml` file':
```yml
httpPort: 7001
cacheDuration: 60
databases:
  - name: bobsDatabase
    user: system
    jdbcUrl: jdbc:oracle:thin:@localhost:1522:xe
    probes:
      - healthCheck
  - name: alicesDatabase
    user: system
    jdbcUrl: jdbc:oracle:thin:@localhost:1523:xe
    probes:
      - healthCheck
      - slowestQueries

connectionPool:
  maxSize: 5
  minIdle: 1

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

`/your/secrets/directory` is expected to contain a file `passwords.yml` with the following content:
It is up to you to encrypt this file and pass it safely to the application (e.g. via Kubernetes secrets).
Provided with the project is a set of helm charts with includes `secrets.yaml` which defines the volume for this file.

Example `passwords.yml` file:
```yml
passwords:
  bobsDatabase: oracle
  alicesDatabase: oracle
```
