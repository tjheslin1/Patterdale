# Configuration

The example `docker run` commands includes two volume mounts:

`docker run -d -p 7000:7000 -v /your/config/directory:/config -v /your/secrets/directory:/passwords tjheslin1/patterdale:0.12.0`

## patterdale.yml

`httpPort` is the port the application will run on.

`cacheDuration` is the lifetime, in seconds, of the cache of SQL probe results.
After which the next call to /metrics will trigger a full scrape of all database probes and store in the cache again. This is to prevent overloading the databases with requests.

`databases` is a list of the databases the application will connect to.
Each database definition has a list of probes which will be executed against that database.

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
      - type: exists
        query: SELECT 1 FROM DUAL
        metricName: database_up
        metricLabels: database="myDB",query="SELECT 1 FROM DUAL"
  - name: alicesDatabase
    user: system
    jdbcUrl: jdbc:oracle:thin:@localhost:1523:xe
    probes:
      - type: exists
        metricName: database_up
        metricLabels: database="myDB2",query="SELECT 1 FROM DUAL"
        query: SELECT 1 FROM DUAL
      - type: list
        metricName: slowest_queries
        metricLabels: database="myDB2",sqlText="%s",sqlId="%s",username="%s",childNumber="%s",diskReads="%s",executions="%s",firstLoadTime="%s",lastLoadTime="%s"
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
connectionPool:
  maxSize: 5
  minIdle: 1
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