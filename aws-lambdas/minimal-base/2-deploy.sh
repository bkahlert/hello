#!/bin/bash

STACK=minimal-base
ARTIFACT_BUCKET=$(cat bucket-name.txt)

../../gradlew :minimal-base:shadowJar

aws cloudformation package \
  --template-file template.yml \
  --s3-bucket "$ARTIFACT_BUCKET" \
  --output-template-file out.yml

aws cloudformation deploy \
  --template-file out.yml \
  --stack-name "$STACK" \
  --capabilities CAPABILITY_NAMED_IAM
