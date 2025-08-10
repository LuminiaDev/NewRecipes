plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

group = "com.koshakmine.newrecipes"
version = "1.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.luminiadev.com/snapshots")
}

dependencies {
    compileOnly("com.koshakmine:Lumi:1.1.0-SNAPSHOT")
}
