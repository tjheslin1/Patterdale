# Example Probes

* [Health Check](#health-check)
* [Database Mode (r/o or r/w)](#database-mode-ro-or-rw)
* [Slowest Queries - Today](#slowest-queries---today)
* [Slowest Queries This Week](#slowest-queries---this-week)
* [Tablespace Free](#tablespace-free)
* [Tablespace Size](#tablespace-size)
* [Sessions Count](#sessions-count)
* [Max Number of Sessions](#max-number-of-sessions)
* [Process Count](#process-count)
* [Max Number of Processes](#max-number-of-processes)
* [Resource Usage](#resource-usage)
* [User Sessions Count](#user-sessions-count)


## Health Check
```yaml
name: healthCheck
type: exists
query: SELECT 1 FROM DUAL
metricName: database_up
metricLabels: query="SELECT 1 FROM DUAL"
```

## Database Mode (r/o or r/w)
```yaml
name: isReadWrite
type: exists
query: select 1 from DUAL where exists (select open_mode from v$database where open_mode = 'READ WRITE');
metricName: is_read_write
metricLabels: query="select 1 from DUAL where exists (select open_mode from v$database where open_mode = 'READ WRITE');"
```
   
## Slowest Queries - Today   
```yaml
name: slowestQueriesToday
type: list
metricName: slowest_queries_today
metricLabels: sqlText="%s",sqlId="%s",username="%s",childNumber="%s",diskReads="%s",executions="%s",firstLoadTime="%s",lastLoadTime="%s"
query: |
  SELECT * FROM
  (SELECT
      s.elapsed_time / s.executions / 1000 AS AVG_ELAPSED_TIME_IN_MILLIS,
      REPLACE(CASE WHEN LENGTH(s.sql_fulltext) > 80 THEN CONCAT(SUBSTR(s.sql_fulltext, 1, 80), '...') ELSE s.sql_fulltext END, '"', '''') AS SQL_TEXT,
      s.sql_id,
      d.username,
      s.child_number,
      s.disk_reads,
      s.executions,
      s.first_load_time,
      s.last_load_time
  FROM    v$sql s, dba_users d
  WHERE   s.parsing_user_id = d.user_id
  AND s.executions > 0
  AND d.username NOT IN ('SYS', 'SYSTEM', 'DBSNMP')
  AND trunc(TO_DATE(s.last_load_time, 'YYYY-MM-DD/HH24:MI:SS')) >= trunc(SYSDATE   1)
  ORDER BY s.elapsed_time / s.executions DESC)
  WHERE ROWNUM <= 10;
```

## Slowest Queries - This Week    
```yaml
name: slowestQueriesThisWeek
type: list
metricName: slowest_queries_this_week
metricLabels: sqlText="%s",sqlId="%s",username="%s",childNumber="%s",diskReads="%s",executions="%s",firstLoadTime="%s",lastLoadTime="%s"
query: |
  SELECT * FROM
  (SELECT
      s.elapsed_time / s.executions / 1000 AS AVG_ELAPSED_TIME_IN_MILLIS,
      REPLACE(CASE WHEN LENGTH(s.sql_fulltext) > 80 THEN CONCAT(SUBSTR(s.sql_fulltext, 1, 80), '...') ELSE s.sql_fulltext END, '"', '''') AS SQL_TEXT,
      s.sql_id,
      d.username,
      s.child_number,
      s.disk_reads,
      s.executions,
      s.first_load_time,
      s.last_load_time
  FROM    v$sql s, dba_users d
  WHERE   s.parsing_user_id = d.user_id
  AND s.executions > 0
  AND d.username NOT IN ('SYS', 'SYSTEM', 'DBSNMP')
  AND trunc(TO_DATE(s.last_load_time, 'YYYY-MM-DD/HH24:MI:SS')) >= trunc(SYSDATE   7)
  ORDER BY s.elapsed_time / s.executions DESC)
  WHERE ROWNUM <= 10;
```    

## Tablespace Free
```yaml
name: databaseTablespaceFree
type: list
metricName: database_tablespace_free
metricLabels: tableSpaceName="%s"
query: |
  SELECT
    SUM(bytes) AS free_space,
    tablespace_name
  FROM dba_free_space
  GROUP BY tablespace_name;
```    

## Tablespace Size
```yaml
name: databaseTablespaceSize
type: list
metricName: database_tablespace_size
metricLabels: tableSpaceName="%s"
query: |
  SELECT
    SUM(bytes) AS tbs_size,
    tablespace_name
  FROM dba_data_files
  GROUP BY tablespace_name;
```    

## Sessions Count
```yaml
name: databaseSessions
type: list
metricName: database_sessions
metricLabels:
query: |
  SELECT count(*) FROM v$session;
```
   
## Max Number of Sessions    
```yaml
name: databaseSessionsMax
type: list
metricName: database_sessions_max
metricLabels:
query: |
  SELECT value FROM v$parameter WHERE name = 'sessions';
```   
 
## Process Count
```yaml
name: databaseProcesses
type: list
metricName: database_processes
metricLabels:
query: |
  SELECT count(*) FROM v$session;
```    

## Max Number of Processes
```yaml
name: databaseProcessesMax
type: list
metricName: database_processes_max
metricLabels:
query: |
  SELECT value FROM v$parameter WHERE name = 'processes';
```
   
## Resource Usage 
```yaml
name: resourceUsage
type: list
metricName: database_resource_usage
metricLabels: resourceName="%s",usageType="%s",
query: |
  SELECT current_utilization AS utilization, resource_name, 'current' AS TYPE
  FROM   v$resource_limit
  WHERE  resource_name IN ( 'processes', 'sessions' )
  UNION
  SELECT max_utilization AS utilization, resource_name, 'max' AS TYPE
  FROM   v$resource_limit
  WHERE  resource_name IN ( 'processes', 'sessions' );
```
    
## User Sessions Count
```yaml
name: userSessions
type: list
metricName: database_user_sessions
metricLabels: userName="%s"
query: |
  SELECT COUNT(*) "Sessions", NVL(username, type)  "Username"
  FROM   v$session
  GROUP BY NVL(username, type)
  ORDER BY NVL(username, type);
```    
