group = "lab.outdooractive"

patches {
    about {
        name = "Outdooractive Diagnostic Patches"
        description = "Patch sperimentali di diagnostica per Outdooractive"
        source = "https://github.com/Edoardo9708/Outdooractive-Morphe-Patches"
        author = "Edoardo9708"
        contact = "https://github.com/Edoardo9708/Outdooractive-Morphe-Patches/issues"
        website = "https://github.com/Edoardo9708/Outdooractive-Morphe-Patches"
        license = "GPLv3"
    }
}

val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Generate the Morphe patch list"
        dependsOn(build)
        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    publish {
        dependsOn("generatePatchesList")
    }
}
