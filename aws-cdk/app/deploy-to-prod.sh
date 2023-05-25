#!/bin/bash

AWS_PROFILE=bkahlert@prod
if ! aws --profile "$AWS_PROFILE" cloudformation describe-stacks --stack-name CDKToolkit >/dev/null 2>&1; then
    cdk bootstrap
fi
cdk --profile "$AWS_PROFILE" deploy --all --require-approval never
