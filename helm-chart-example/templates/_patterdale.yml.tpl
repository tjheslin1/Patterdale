{{ define "patterdale.yml" -}}
httpPort: {{ .Values.patterdale.targetPort }}
cacheDuration: {{ .Values.patterdale.cacheDuration }}
databases:
  {{ range .Values.databases -}}
  - name: {{ .name }}
    user: {{ .user }}
    jdbcUrl: {{ .jdbcUrl }}
    probes:
      {{ range .probes -}}
      - type: {{ .type | quote }}
        query: {{ .query | quote }}
        metricName: {{ .metricName | quote }}
        metricLabels: {{ .metricLabels | quote }}
      {{ end -}}
  {{ end }}
connectionPool:
  maxSize: {{ .Values.patterdale.pool.maxSize }}
  minIdle: {{ .Values.patterdale.pool.minIdle }}
{{ end }}