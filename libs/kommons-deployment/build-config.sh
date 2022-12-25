#!/bin/bash
declare target=${1?target missing}
declare target_dir=${target%/*}
mkdir -p "$target_dir"
{
  echo 'package com.bkahlert.kommons.deployment.gen'
  echo ''
  echo 'internal val info = """'
  printf '%s\n' "$(
    aws cloudformation list-exports \
      --no-paginate \
      --output json \
      --query 'Exports[?Name==`sls-hello-dev-DomainNameHttp` || Name==`sls-hello-dev-HostedUiUrl` || Name==`sls-hello-dev-WebAppClientID`].{key: Name, value: Value}'
  )"
  echo '"""'
} >"$target"
