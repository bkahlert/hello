# AWS Lambda

Minimal example of an AWS Lambda function with Kotlin, Java 11, and Gradle 7
using the base and base-test module.

## Testing

### Create bucket to store compiled Lambda function

```shell
./1-create-bucket.sh
```

### Build and deploy Lambda function

```shell
./2-deploy.sh
```

### Invoke Lambda function

```shell
./3-invoke.sh
```

### Remove the created AWS resources

```shell
./4-destroy.sh
```
