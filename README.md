# Hello!

## Cheat sheet

### Re-build everything

Gradle's `base` plugin is applied to every included build
and the umbrella project.
Therefor the tasks `assemble`, `build`, `clean`, and `check`
work as expect—also in the umbrella project.

```shell
# For macOS users, if ".DS_Store" files come in your way.
find . -name .DS_Store -print0 | xargs -0 rm

./gradlew clean build
```

### Update Gradle wrapper

As this is a composite build all Gradle wrappers should be updated.

Otherwise tasks started from inside an included build might find plugins
previously built by another Gradle wrapper with a different version.
This can lead to problems if the two Gradle version make use of a different
Kotlin DSL.

```shell
./deep-gradle wrapper --gradle-version 8.0-rc-1 
```

> 💡`deep-gradle` can be found at the root of this repo. It invokes `gradle`
> with the same passed arguments in each root project directory.

## References

- [Web Tutorials](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Web)

- [Examples](https://github.com/JetBrains/compose-jb/tree/master/examples)
