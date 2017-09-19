{{ define "patterdale.yml" }}
httpPort: {{ .Values.patterdale.targetPort }}
databases:
- name: {{ .Values.database.name }}
  user: {{ .Values.database.user }}
  password: {{ .Values.database.password }}
  jdbcUrl: {{ .Values.database.jdbcUrl }}
  probes:
    - query: {{ .Values.database.probes.query | quote }}
      type: {{ .Values.database.probes.type | quote }}
      metricName: {{ .Values.database.probes.metricName | quote }}
      metricLabels: {{ .Values.database.probes.metricLabels | quote }}
connectionPool:
    maxSize: {{ .Values.patterdale.pool.maxSize }}
    minIdle: {{ .Values.patterdale.pool.minIdle }}
{{ end }}