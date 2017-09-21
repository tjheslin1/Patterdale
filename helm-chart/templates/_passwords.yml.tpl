{{ define "passwords.yml" }}
passwords:
  - databaseName: {{ .Values.testDatabase.name }}
    value: {{ .Values.testDatabase.password }}
  - databaseName: {{ .Values.test2Database.name }}
    value: {{ .Values.test2Database.password }}
{{ end }}