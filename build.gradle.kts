plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

group = "com.koshakmine.newrecipes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.lanink.cn/repository/maven-public/")
}

dependencies {
    compileOnly("cn.nukkit:Nukkit:MOT-SNAPSHOT")
}
