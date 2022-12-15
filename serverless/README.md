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
serverless plugin install -n serverless-offline
serverless offline
```


## TODO

```shell
curl -X POST https://4p2r0qp2n1.execute-api.eu-central-1.amazonaws.com/props/abc  -H "Content-Type: application/json" -d "{\"id\": \"123\", \"price\": 12345, \"name\": \"myitem\"}"
```
