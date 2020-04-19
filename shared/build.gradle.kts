import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask

plugins {
  kotlin("multiplatform")
  id("co.touchlab.native.cocoapods")
  id("kotlinx-serialization")
  id("com.android.library")
}

android {
  compileSdkVersion(29)
  defaultConfig {
    minSdkVersion(Versions.min_sdk)
    targetSdkVersion(Versions.target_sdk)
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

kotlin {
  android()

  //Revert to just ios() when gradle plugin can properly resolve it
  val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos")?:false
  if(onPhone){
    iosArm64("ios")
  }else{
    iosX64("ios")
  }
  targets.getByName<KotlinNativeTarget>("ios").compilations["main"].kotlinOptions.freeCompilerArgs +=
    listOf("-Xobjc-generics", "-Xg0")


  js {
    nodejs()

    sequenceOf("", "Test").forEach {
      tasks.getByName<KotlinJsCompile>("compile${it}KotlinJs") {
        kotlinOptions {
          moduleKind = "umd"
          noStdlib = true
          metaInfo = true
        }
      }
    }
    compilations["main"].defaultSourceSet {
      dependencies {
        api(kotlin("stdlib-js"))
      }
    }
    compilations["test"].defaultSourceSet {
      dependencies {
        implementation(kotlin("test-js"))
      }
    }

    mavenPublication {
      artifactId = rootProject.name + "-js"
    }
  }


  version = "1.1"

  sourceSets {
    all {
      languageSettings.apply {
        useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
      }
    }
  }

  sourceSets["commonMain"].dependencies {
    implementation(kotlin("stdlib-common", Versions.kotlin))
    implementation("com.github.komputing:Khash:6fe00e9c11")
  }

  sourceSets["commonTest"].dependencies {
    implementation(Deps.multiplatformSettingsTest)
    implementation(Deps.KotlinTest.common)
    implementation(Deps.KotlinTest.annotations)
  }

  sourceSets["androidMain"].dependencies {
    implementation(kotlin("stdlib", Versions.kotlin))
    implementation(Deps.SqlDelight.driverAndroid)
    implementation(Deps.ktor.jvmCore)
    implementation(Deps.ktor.jvmJson)
    implementation(Deps.Coroutines.jdk)
    implementation(Deps.Coroutines.android)
    implementation(Deps.ktor.androidSerialization)
    implementation ("com.github.komputing.kethereum:crypto_api:${Versions.kethereum}")
    implementation ("com.github.komputing.kethereum:model:${Versions.kethereum}")
    implementation ("com.github.komputing.kethereum:crypto:${Versions.kethereum}")
    implementation ("com.github.komputing.kethereum:crypto_impl_spongycastle:${Versions.kethereum}")
  }

  sourceSets["androidTest"].dependencies {
    implementation(Deps.KotlinTest.jvm)
    implementation(Deps.KotlinTest.junit)
    implementation(Deps.AndroidXTest.core)
    implementation(Deps.AndroidXTest.junit)
    implementation(Deps.AndroidXTest.runner)
    implementation(Deps.AndroidXTest.rules)
    implementation("org.robolectric:robolectric:4.3")
  }
}
