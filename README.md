# FormDsl

FormDsl is an example to implement Server Driven Ui for Forms. It is inspired by [KotlinConf 2019: Lona: Scaling Server-driven UI](https://youtu.be/Ir8lq4rSyyc?si=KLpfLwO-yUrep91T).

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop, Server.

## Run the App

 - Run Jvm Tests
   - `./gradlew :shared:jvmTest :composeApp:desktopTest :server:test`
 - Run Server
   - `./gradlew :server:run`
 - Run Desktop App
   - `./gradlew :composeApp:run`