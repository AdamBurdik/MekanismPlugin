plugins {
    id("java-library")
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory.paper)
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api.get())
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.custom.block.data)
}

paperPluginYaml {
    main = "me.adamix.mekanism.Mekanism"

    apiVersion = "26.2"
    version = "0.0.1"
}


java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    shadowJar {
        relocate("com.jeff_media.customblockdata", "me.adamix.mekanism.customblockdata")
    }
    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }
}
