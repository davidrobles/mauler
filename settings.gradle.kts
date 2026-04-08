rootProject.name = "JSandbox"

include(
    ":Mauler",
    ":Utils",
    ":PlanetWars",
    ":TicTacToe",
    ":TicTacToePlay",
    ":Othello",
    ":OthelloPlay",
    ":LinesOfAction",
    ":LinesOfActionPlay",
    ":Breakthrough",
    ":ConnectFour",
    ":Domineering",
    ":Havannah",
    ":HavannahPlay",
    ":Tron",
    ":ThesisExperiments",
    ":Experiments",
    ":StrategyTests"
)

project(":Mauler").projectDir = file("modules/Mauler")
project(":Utils").projectDir = file("modules/Utils")
project(":PlanetWars").projectDir = file("modules/PlanetWars")
project(":TicTacToe").projectDir = file("modules/TicTacToe")
project(":TicTacToePlay").projectDir = file("modules/TicTacToePlay")
project(":Othello").projectDir = file("modules/Othello")
project(":OthelloPlay").projectDir = file("modules/OthelloPlay")
project(":LinesOfAction").projectDir = file("modules/LinesOfAction")
project(":LinesOfActionPlay").projectDir = file("modules/LinesOfActionPlay")
project(":Breakthrough").projectDir = file("modules/Breakthrough")
project(":ConnectFour").projectDir = file("modules/ConnectFour")
project(":Domineering").projectDir = file("modules/Domineering")
project(":Havannah").projectDir = file("modules/Havannah")
project(":HavannahPlay").projectDir = file("modules/HavannahPlay")
project(":Tron").projectDir = file("modules/Tron")
project(":ThesisExperiments").projectDir = file("modules/ThesisExperiments")
project(":Experiments").projectDir = file("modules/Experiments")
project(":StrategyTests").projectDir = file("modules/StrategyTests")

