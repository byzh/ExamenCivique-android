plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
}

// Sync task: copy shared/questions.json → app assets + iOS resources
tasks.register<Copy>("syncQuestions") {
    description = "Copy shared/questions.json to Android assets and iOS resources"
    from("shared/questions.json")
    into("app/src/main/assets")
    doLast {
        val iosTarget = file("../ExamenCivique-iOS/ExamenCivique/Resources/questions.json")
        if (iosTarget.parentFile.exists()) {
            file("shared/questions.json").copyTo(iosTarget, overwrite = true)
            println("✅ Synced questions.json → iOS project")
        }
        println("✅ Synced questions.json → Android assets")
    }
}
