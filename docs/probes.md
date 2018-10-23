Probes are defined in `patterdale.yml` and are specific to a database.
If you want to run the same probe against multiple databases, simply duplication the definition (e.g _SELECT 1 FROM DUAL_).

An example set of probe definitions in `patterdale.yml`:

```yml
databases:
    ...
    - query: SELECT 1 FROM DUAL
      type: exists
      metricName: database_up
      metricLabels: database="myDB2",query="SELECT 1 FROM DUAL"
    - query: select elapsed_time, sql_text from   v$sql order by ELAPSED_TIME desc FETCH NEXT 2 ROWS ONLY;
      type: list
      metricName: slowest_queries
      metricLabels: database="myDB2",slowQuery="%s"
```

The first query, _SELECT 1 FROM DUAL_, has the type _exists_ maps to [ExistsOracleSQLProbe.java](src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ExistsOracleSQLProbe.java).
The second query, _SELECT * FROM slowest_queries TOP 5_, maps to [ListOracleSQLProbe.java](src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ListOracleSQLProbe.java).

## Exists

The "exists" type expects a SQL query to be run which returns one row and one column.

If the value in that column is a '1'. The probe is treated as a success. _SELECT 1 FROM DUAL_ is a perfect example of query for this type of _probe_ and is recommended to be run on every database which is configured.

Any other integer value is treated as a failure.

Using the above configuration, the probe will result in a line on the applications '/metrics' web page in the format:

`database_up{database="myDB2",query="SELECT 1 FROM DUAL"} 1.0`

if the query was successful

`database_up{database="myDB2",query="SELECT 1 FROM DUAL"} 0.0`

if the query was unsuccessful

`database_up{database="myDB2",query="SELECT 1 FROM DUAL"} -1.0`

if the query wasn't executed succesfully against the database

This conforms to the [Prometheus](https://github.com/prometheus/prometheus) standard.

## List

The "list" type expects a SQL query to be run which returns a number of rows.
The first column is expected to be a number, representing the metric value.

The rest of the columns will be treated as Strings and filtered into the _metricLabels_.
For this the _metricLabels_ is expected to have a key/value pair per extra column after the first, in the same order as the SQL:
`columnName="%s"` where _%s_ will be filtered in using `java.lang.String#format`.

Using the above configuration, the probe will result in a number of lines on the applications '/metrics' web page in the format:

```
slowest_queries{database="myDB2",slowQuery="SELECT * FROM HUGE_TABLE"} 50.5
slowest_queries{database="myDB2",slowQuery="SELECT name FROM TINY_TABLE"} 0.4
```

This conforms to the [Prometheus](https://github.com/prometheus/prometheus) standard.