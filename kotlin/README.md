# Advent of Code Kotlin
Project structure is based on [https://github.com/Jadarma/advent-of-code-kotlin-template](https://github.com/Jadarma/advent-of-code-kotlin-template).
For full documentation see the README of that repo, as well as the library [Jadarma/advent-of-code-kotlin](https://github.com/Jadarma/advent-of-code-kotlin).


## ▶️ Running your Code

### From IntelliJ (Recommended)

If you have the [Kotest Plugin](https://plugins.jetbrains.com/plugin/14080-kotest) installed, the IDE will show a 
'Run' gutter icon near any `AdventSpec` in your code.
After that, you can re-run the same test using `Ctrl+F5`, or pressing the _Rerun_ button in the test tool window.
This provides the most comfortable feedback loop when solving new problems.
You may also run tests in bulk by right clicking on a test package.

### From Gradle / CLI

You can also run your tests from Gradle.

To run a specific test, pass it via its FQN:

```shell
./gradlew test --tests 'aockt.y9999.Y9999D01Test'
```

You may also filter and run tests in bulk.
For example, running all tests within the same year package:

```shell
./gradlew test --tests 'aockt.y9999'
```

If for whatever reason you want to run tests in bulk but skip the parts that have been marked as expensive, you can 
filter them out as well:

```shell
./gradlew test -Dkotest.tags='!Expensive'
```
