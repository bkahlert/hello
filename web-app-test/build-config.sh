#!/bin/bash
{
  echo '/* jshint es''version: 6 */'
  echo '/* jshint strict: false */'
  printf 'const config = %s;\n' "$(
    aws cloudformation list-exports \
      --no-paginate \
      --output json \
      --query 'Exports[?Name==`sls-hello-dev-DomainNameHttp` || Name==`sls-hello-dev-HostedUiUrl` || Name==`sls-hello-dev-WebAppClientID`].{key: Name, value: Value}'
  )"
  echo 'export const apiUrl = `https://${config.filter(c => c.key === "sls-hello-dev-DomainNameHttp")[0].value}`;'
  echo 'export const hostedUiUrl = config.filter(c => c.key === "sls-hello-dev-HostedUiUrl")[0].value;'
  echo 'export const clientId = config.filter(c => c.key === "sls-hello-dev-WebAppClientID")[0].value;'
} >config.js
