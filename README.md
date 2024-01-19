# FormDsl

FormDsl is an example to implement Server Driven Ui for Forms. It is inspired by [KotlinConf 2019: Lona: Scaling Server-driven UI](https://youtu.be/Ir8lq4rSyyc?si=KLpfLwO-yUrep91T).

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop, Server.

## Run the App

 - Run jvm tests
   - `./gradlew :shared:jvmTest :composeApp:desktopTest :server:test `
 - Run ios tests
   - `./gradlew :composeApp:cleanIosX64Test :composeApp:iosX64Test :shared:cleanIosX64Test :shared:iosX64Test`
 - Run server
   - `./gradlew :server:run`
 - Run desktop App
   - `./gradlew :composeApp:desktopRun -DmainClass=MainKt --quiet`