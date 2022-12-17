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

## Deploy

```shell
./gradlew deploy
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

## Extends

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

## TODO

```shell
curl -X POST https://jtyka1zrmg.execute-api.eu-central-1.amazonaws.com/props/abc  -H "Content-Type: application/json" -d "{\"id\": \"123\", \"price\": 12345, \"name\": \"myitem\"}"
```

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

## Certificates & Domains

First a certificate is needed

```shell
serverless create-cert
# or
serverless create-cert --stage=prod
```

```shell
serverless create_domain
```
