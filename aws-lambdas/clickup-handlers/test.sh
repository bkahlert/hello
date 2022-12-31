#!/bin/bash

./gradlew clean build deploy || exit 1

sleep 2

aws lambda invoke \
  --function-name serverless-kotlin-dev-hello \
  --cli-binary-format raw-in-base64-out \
  --payload '{"key": "value"}' \
  >(jq .) \
  --log-type Tail \
  --query 'LogResult' \
  --output text | base64 -d
