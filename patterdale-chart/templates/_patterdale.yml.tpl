{{ define "patterdale.yml" }}
httpPort: {{ .Values.patterdale.targetPort }}
database:
  serverName: {{ .Values.patterdale.database.serverName}}
  name: {{ .Values.patterdale.database.name }}
  networkProtocol: {{ .Values.patterdale.database.networkProtocol }}
  driverType: {{ .Values.patterdale.database.driverType }}
  jdbcUrl: {{ .Values.patterdale.database.jdbcUrl }}
connectionPool:
  maxSize: {{ .Values.patterdale.pool.maxSize }}
  minIdle: {{ .Values.patterdale.pool.minIdle }}
{{ end }}
