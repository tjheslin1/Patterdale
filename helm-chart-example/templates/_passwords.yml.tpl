{{ define "passwords.yml" }}
passwords:
  {{ range $name, $pass := .Values.databasePasswords }}
  {{ $name }}: {{ $pass }}
  {{ end }}
{{ end }}
