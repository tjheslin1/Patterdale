{{ define "patterdale.yml" }}
httpPort: {{ .Values.patterdale.targetPort }}
databases:
- name: {{ .Values.testDatabase.name }}
  user: {{ .Values.testDatabase.user }}
  jdbcUrl: {{ .Values.testDatabase.jdbcUrl }}
  probes:
    - query: {{ .Values.testDatabase.probe1.query | quote }}
      type: {{ .Values.testDatabase.probe1.type | quote }}
      metricName: {{ .Values.testDatabase.probe1.metricName | quote }}
      metricLabels: {{ .Values.testDatabase.probe1.metricLabels | quote }}
- name: {{ .Values.test2Database.name }}
  user: {{ .Values.test2Database.user }}
  jdbcUrl: {{ .Values.test2Database.jdbcUrl }}
  probes:
    - query: {{ .Values.test2Database.probe1.query | quote }}
      type: {{ .Values.test2Database.probe1.type | quote }}
      metricName: {{ .Values.test2Database.probe1.metricName | quote }}
      metricLabels: {{ .Values.test2Database.probe1.metricLabels | quote }}
    - query: {{ .Values.test2Database.probe2.query | quote }}
      type: {{ .Values.test2Database.probe2.type | quote }}
      metricName: {{ .Values.test2Database.probe2.metricName | quote }}
      metricLabels: {{ .Values.test2Database.probe2.metricLabels | quote }}
connectionPool:
    maxSize: {{ .Values.patterdale.pool.maxSize }}
    minIdle: {{ .Values.patterdale.pool.minIdle }}
{{ end }}