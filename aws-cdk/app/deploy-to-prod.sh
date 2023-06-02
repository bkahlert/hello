#!/bin/bash

export JAVA_HOME='/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19'

AWS_PROFILE=bkahlert@prod
if ! aws --profile "$AWS_PROFILE" cloudformation describe-stacks --stack-name CDKToolkit >/dev/null 2>&1; then
    cdk bootstrap
fi
cdk --profile "$AWS_PROFILE" deploy --all --require-approval never
