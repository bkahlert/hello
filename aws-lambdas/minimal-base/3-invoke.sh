#!/bin/bash

STACK=minimal-base

FUNCTION=$(
  aws cloudformation describe-stack-resource \
    --stack-name "$STACK" \
    --logical-resource-id function \
    --query 'StackResourceDetail.PhysicalResourceId' \
    --output text
)

aws lambda invoke \
  --function-name "$FUNCTION" \
  --cli-binary-format raw-in-base64-out \
  --payload '{"body": "Lorem ipsum"}' >(jq . >&2) \
  --log-type Tail \
  --query 'LogResult' \
  --output text | base64 -d
