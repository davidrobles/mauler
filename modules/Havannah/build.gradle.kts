dependencies {
    implementation(project(":Mauler"))
    testImplementation(libs.junit)
    testImplementation(testFixtures(project(":Mauler")))
}
