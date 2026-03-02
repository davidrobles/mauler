dependencies {
    implementation(project(":Mauler"))
    implementation(project(":Utils"))
    testImplementation(libs.junit)
    testImplementation(testFixtures(project(":Mauler")))
}
