plugins {
    java
}

dependencies {
    testImplementation(project(":Mauler"))
    testImplementation(project(":TicTacToe"))
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest.core)
}
