{{ define "patterdale.yml" }}
httpPort: {{ .Values.patterdale.targetPort }}
database:
  user: {{ .Values.database.user }}
  password: {{ .Values.database.password }}
  jdbcUrl: {{ .Values.database.jdbcUrl }}
connectionPool:
  maxSize: {{ .Values.patterdale.pool.maxSize }}
  minIdle: {{ .Values.patterdale.pool.minIdle }}
metrics:
  name: {{ .Values.patterdale.metrics.name }}
  labels: {{ .Values.patterdale.metrics.labels }}
{{ end }}
