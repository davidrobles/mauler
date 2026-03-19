# Mauler

**Multi-Algorithm Learning Evaluation Research** — a Java framework for implementing two-player board games and benchmarking AI search algorithms against them.

## Quick Start

```java
// Run 1000 games of TicTacToe: AlphaBeta vs MCTS
Series<TicTacToe> series = new Series<>(
    TicTacToe::new,
    1000,
    List.of(new AlphaBeta<>(), new MCTS<>())
);
series.run();
System.out.printf("Wins: %d | Losses: %d | Draws: %d%n",
    series.getWins(0), series.getLosses(0), series.getDraws());
```

## Games

| Module | Description |
|--------|-------------|
| TicTacToe | 3×3, bitboard-based |
| Othello | 8×8 Reversi, long bitboards with 8-direction flip detection |
| LinesOfAction | Classic board game |
| Breakthrough | Pawn-based board game |
| ConnectFour | Column-drop game |
| Domineering | Tile placement game |
| Havannah | Hex-board connection game |
| Tron | Movement/trail game |
| PlanetWars | Real-time strategy game |

Each game has a corresponding `*Play` module that adds a Swing GUI for interactive play.

## AI Strategies

**Minimax family**: `Minimax`, `AlphaBeta`, `Negamax`, `IterativeDeepening`, `DepthLimitedSearch`

**Monte Carlo family**: `MonteCarlo`, `MCTS` (UCT), `UCT`, `ParTimedMC` (parallel), `MCTSWithPrior`, `MCTSNoRollout`, `MCTSRootParallel`

**Other**: `RandomStrategy`, `GreedyStrategy`, `EpsilonGreedyStrategy`, `TerminalEvaluator` (evaluation function wrapper)

## Architecture

All games implement `Game<GAME>` and all strategies implement `Strategy<GAME>`, where the self-referential generic (`GAME extends Game<GAME>`) enforces type-safe `copy()` and `makeMove()` across the framework.

```
Game<GAME>                     Strategy<GAME>
  copy()                         move(game)
  getCurPlayer()                 move(game, timeout)
  getMoves() / getNumMoves()     isDeterministic()
  makeMove(int)
  isOver() / getOutcome()
  reset() / newInstance()
```

Games extend `AbstractGame` to gain `MoveObservable` support (used by GUI modules to react to moves).

Tournament infrastructure: `Match` → `Series` → `RoundRobin`

Performance benchmarking: `SpeedTest.gameSpeed(game, timeoutSeconds)` returns games/second.

## Other Modules

| Module | Description |
|--------|-------------|
| ReinforcementLearning | Tabular RL algorithms (Q-learning, SARSA, TD, Monte Carlo, policy/value iteration) |
| GridWorld | MDP environment for testing RL algorithms |
| StrategyTests | Competence tests that run each strategy as both first and second player |
| Experiments | Standalone experiment runners (e.g., `TTTRun`) |

## Build

The project uses **Gradle 8.13** with Kotlin DSL. Java 11 toolchain is required.

```sh
./gradlew build        # compile all 21 modules
./gradlew projects     # list all subprojects
```

Dependencies are pulled from Maven Central: `guava-33.4.0`, `gson-2.11.0`, `commons-lang3-3.17.0`, `junit-4.13.2`, `hamcrest-2.2`

## Running Tests

```sh
./gradlew test                          # run all tests
./gradlew :TicTacToe:test              # run tests for a single module
./gradlew :TicTacToe:test --tests "net.davidrobles.mauler.tictactoe.TicTacToeTest.testCopy"  # single test method
```

Test reports are written to `modules/<Name>/build/reports/tests/test/index.html`.

## Running GUI Applications

```sh
./gradlew :TicTacToePlay:run
./gradlew :OthelloPlay:run
./gradlew :LinesOfActionPlay:run
./gradlew :HavannahPlay:run
./gradlew :GridWorldPlay:run
```

## Code Style

All code is formatted with **Google Java Format** (AOSP style, 4-space indent) via Spotless.

```sh
./gradlew spotlessApply   # auto-format all code
./gradlew spotlessCheck   # check formatting without modifying files
```
