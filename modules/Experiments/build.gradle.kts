plugins {
    application
}

application {
    mainClass.set("net.davidrobles.mauler.experiments.TTTRun")
}

dependencies {
    implementation(project(":Mauler"))
    implementation(project(":TicTacToe"))
    implementation(project(":TicTacToePlay"))
    implementation(project(":Othello"))
    implementation(project(":OthelloPlay"))
    implementation(project(":LinesOfAction"))
    implementation(project(":LinesOfActionPlay"))
    implementation(project(":Breakthrough"))
    implementation(project(":ConnectFour"))
    implementation(project(":Domineering"))
    implementation(project(":Havannah"))
    implementation(project(":HavannahPlay"))
    implementation(project(":Tron"))
}
