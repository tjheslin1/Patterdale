{{ define "patterdale.yml" }}
httpPort: {{ .Values.patterdale.targetPort }}
database:
  serverName: {{ .Values.database.serverName}}
  name: {{ .Values.database.name }}
  networkProtocol: {{ .Values.database.networkProtocol }}
  driverType: {{ .Values.database.driverType }}
  jdbcUrl: {{ .Values.database.jdbcUrl }}
connectionPool:
  maxSize: {{ .Values.patterdale.pool.maxSize }}
  minIdle: {{ .Values.patterdale.pool.minIdle }}
{{ end }}
