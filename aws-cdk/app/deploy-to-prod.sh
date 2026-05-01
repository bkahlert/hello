#!/usr/bin/env bash

export JAVA_HOME='/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19'

AWS_PROFILE=prod

# SSO session must be active. If not, this opens a browser for the passkey flow.
if ! aws --profile "$AWS_PROFILE" sts get-caller-identity >/dev/null 2>&1; then
    aws sso login --profile "$AWS_PROFILE"
fi

cdk --profile "$AWS_PROFILE" deploy --all --require-approval never
