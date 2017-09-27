{{ define "patterdale.yml" }}
httpPort: {{ .Values.patterdale.targetPort }}
databases:
  {{ range .Values.databases }}
  - name: {{ .name }}
    user: {{ .user }}
    jdbcUrl: {{ .jdbcUrl }}
    probes:
      {{ range .probes }}
      - query: {{ .query | quote }}
        type: {{ .type | quote }}
        metricName: {{ .metricName | quote }}
        metricLabels: {{ .metricLabels | quote }}
      {{ end }}
  {{ end }}
connectionPool:
    maxSize: {{ .Values.patterdale.pool.maxSize }}
    minIdle: {{ .Values.patterdale.pool.minIdle }}
{{ end }}