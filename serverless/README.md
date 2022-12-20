# aws-kotlin-jvm-gradle

AWS Lambda with Kotlin, Java 11, and Gradle 7

## Getting started

```shell
npm install -g serverless
```

[Serverless Example](https://www.serverless.com/examples/?prod_EXAMPLES_SEARCH_GROWTH%5BrefinementList%5D%5Bplatform%5D%5B0%5D=aws&prod_EXAMPLES_SEARCH_GROWTH%5BrefinementList%5D%5Blanguage%5D%5B0%5D=node)

## Build

```shell
./gradlew clean build
```

## Debug

```shell
# Prints fully resolved config file
serverless print

# Prints only the specified path
serverless print --path provider

# Prints info including stack output
serverless info --verbose

# Prints outputs of all stacks
aws cloudformation describe-stacks
```

## Deploy

```shell
# To default stage = dev
serverless deploy

# To specific stage
serverless deploy --stage qa

# Only sync static files
serverless s3sync
```

### Certificates

```shell
# To default stage = dev 
serverless create-cert

# To specific stage
serverless create-cert --stage qa
```

### Domains

```shell
# To default stage = dev 
serverless create_domain

# To specific stage
serverless create_domain --stage qa
```

## Invoke function

```shell
# invoke hello function
serverless invoke --function hello

# invoke and log hello function
serverless invoke --function hello --log

# locally invoke hello function
serverless invoke local --function hello
```

## Invoke function --local

```shell
serverless invoke local --verbose \
  --function getProp \
  --path events/getProp/foo.json
  

(
  cd node_modules/serverless/lib/plugins/aws/invoke-local/runtime-wrappers/java || exit 1
#  mvn test
  mvn package -DskipTests
)
```

Change the all Jackson dependencies to `2.5.5` in `node_modules/serverless/lib/plugins/aws/invoke-local/runtime-wrappers/java/pom.xml` if problems with Jackson
occur.


## Get props

### Get foo

```shell
serverless invoke \
  --log \
  --function getProp \
  --path events/getProp/foo.json
```

### Get bar

```shell
serverless invoke \
  --log \
  --function getProp \
  --path events/getProp/bar.json
```

## Set props

### Set foo to value

```shell
serverless invoke \
  --log \
  --function setProp \
  --path events/setProp/foo-to-value.json
```

### Set bar to Base64 SVG

```shell
serverless invoke \
  --log \
  --function setProp \
  --path events/setProp/bar-to-base64-svg.json
```

### Set foo to null

```shell
serverless invoke \
  --log \
  --function setProp \
  --path events/setProp/foo-to-null.json
```

### Set bar to Base64 SVG

```shell
serverless invoke \
  --log \
  --function setProp \
  --path events/setProp/bar-to-null.json
```
