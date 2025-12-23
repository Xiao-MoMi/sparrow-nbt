### Gradle

The tag library used in my own projects with `codec` and `adventure` support!


```kotlin
repositories {
    maven("https://repo.momirealms.net/releases/")
}
```
```kotlin
dependencies {
    implementation("net.momirealms:sparrow-nbt:0.11")  // Core Module
    implementation("net.momirealms:sparrow-nbt-adventure:0.11")  // for adventure component support
    implementation("net.momirealms:sparrow-nbt-codec:0.11")  // for DFU 8.0+
    implementation("net.momirealms:sparrow-nbt-legacy-codec:0.11")  // for DFU 6.0+
}
```
Special thanks to [adventure](https://github.com/KyoriPowered/adventure) for their awesome work, which inspired this project.