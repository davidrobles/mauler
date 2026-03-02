plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(project(":Utils"))
    implementation(libs.guava)
    implementation(libs.commons.lang3)
    testImplementation(libs.junit)
    testFixturesImplementation(libs.junit)
}
