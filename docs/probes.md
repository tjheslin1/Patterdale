# Probes

Probes are defined in `patterdale.yml` and are specific to a database. If you want to run the same probe against multiple databases, simply duplication the definition (e.g _SELECT 1 FROM DUAL_).

An example set of probe definitions in `patterdale.yml`:

```yml
databases:
    ...
    - query: SELECT 1 FROM DUAL
      type: exists
      metricName: database_up
      metricLabels: database="myDB2",query="SELECT 1 FROM DUAL"
    - query: SELECT * FROM slowest_queries TOP 5
      type: list
      metricName: slowest_queries
      metricLabels: database="myDB2",slowQuery="%s"
```

The first query, _SELECT 1 FROM DUAL_, has the type _exists_ maps to [ExistsOracleSQLProbe.java](src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ExistsOracleSQLProbe.java).
The second query, _SELECT * FROM slowest_queries TOP 5_, maps to [ListOracleSQLProbe.java](src/main/java/io/github/tjheslin1/patterdale/metrics/probe/ListOracleSQLProbe.java).

## Exists

The "exists" _probe_ type expects a SQL query to be run which returns one row and one column.

If the value in that column is a '1'. The probe is treated as a success. _SELECT 1 FROM DUAL' is a perfect example of query for this type of _probe_ and is recommended to be run on every database which is configured.

Any other integer value is treated as a failure.

Using the above configuration, the probe will result in a line on the applications '/metrics' web page in the format:

`database_up{database="myDB2",query="SELECT 1 FROM DUAL"} 1.0`
or
`database_up{database="myDB2",query="SELECT 1 FROM DUAL"} 0.0`

This conforms to the [Prometheus](https://github.com/prometheus/prometheus) standard.

## List

The "list" _probe_ type expects a SQL query to be run which returns any number of rows, with 2 columns. The first column is a String (VARCHAR) and the second volumn is a double value.

The String value in the first column is filtered into the _metricLabels_ provided in the probes defintion in `patterdale.yml`. This is done using Java's `java.lang.String#format` method.

The second column should return a double value which will be used as the metric's value.

For example you could provided the query:

_SELECT SLOWEST_QUERY_SQL, AVERAGE_DURATION FROM MY_METRICS_DB_VIEW_.

Using the above configuration, the probe will result in a number of lines on the applications '/metrics' web page in the format:

```
slowest_queries{database="myDB2",slowQuery="SELECT * FROM HUGE_TABLE"} 50.5
slowest_queries{database="myDB2",slowQuery="SELECT name FROM TINY_TABLE"} 0.4
```

This conforms to the [Prometheus](https://github.com/prometheus/prometheus) standard.