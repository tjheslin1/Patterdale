## /metrics

Displays the Prometheus style metrics.

## /ready

Useful for readiness and liveliness probes. Returns a 200 and an "OK" message if successul.
Returns a failure code otherwise.

## /status

Displays the provided `patterdale.yml` file from start up.

## All other paths

Result in a 404 not found page. 