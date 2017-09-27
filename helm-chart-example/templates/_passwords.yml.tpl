{{ define "passwords.yml" }}
passwords:
  {{ range .Values.databases }}
  - databaseName: {{ .name }}
    value: {{ .password }}
  {{ end }}
{{ end }}