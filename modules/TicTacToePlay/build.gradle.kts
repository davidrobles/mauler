plugins {
    application
}

application {
    mainClass.set("net.davidrobles.shogun.tictactoe.TicGUI")
}

dependencies {
    implementation(project(":Mauler"))
    implementation(project(":TicTacToe"))
    implementation(project(":Utils"))
}
