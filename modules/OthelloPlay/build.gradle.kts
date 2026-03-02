plugins {
    application
}

application {
    mainClass.set("net.davidrobles.mauler.othello.OthelloRun")
}

dependencies {
    implementation(project(":Mauler"))
    implementation(project(":Othello"))
    implementation(project(":Utils"))
    implementation(libs.gson)
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest.core)
}
