{{ define "passwords.yml" }}
passwords:
  {{ range $name, $pass := .Values.secrets }}
  {{ $name }}: {{ $pass }}
  {{ end }}
{{ end }}
