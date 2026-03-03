# Mauler

**Multi-Algorithm Learning Evaluation Research** — a Java framework for implementing two-player board games and benchmarking AI search algorithms against them.

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

## AI Players

**Minimax family** (`players/minimax/`): `Minimax`, `AlphaBeta`, `Negamax`, `IterDeep`, `DepthLimitedSearch`

**Monte Carlo family** (`players/mc/`, `players/mcts/`): `MonteCarlo`, `MCTS` (UCT), `UCT`, `ParTimedMC` (parallel), `AMAF`, `MCTSPrior`, `MCTSNoRollout`, `MCTSRootP` (root parallelization)

**Other**: `RandPlayer`, `GreedyPlayer`, `EpsilonGreedy`, `TerminalEvaluator` (evaluation function wrapper)

## Architecture

All games implement `Game<GAME>` and all players implement `Player<GAME>`, where the self-referential generic (`GAME extends Game<GAME>`) enforces type-safe `copy()` and `makeMove()` across the framework.

```
Game<GAME>                     Player<GAME>
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

## Reinforcement Learning

The `ReinforcementLearning` module implements tabular RL algorithms: `QLearning`, `TabularSARSA`, `TabularSARSALambda`, `TabularTD0`, `TabularTDLambda`, `FirstVisitMC`, `ValueIteration`, `PolicyIteration`, `DPPolicyIteration`. The `GridWorld` module provides an MDP environment for testing these algorithms.

## Build

The project uses **Gradle 8.13** with Kotlin DSL. Java 11 toolchain is required.

```sh
./gradlew build        # compile all 19 modules
./gradlew projects     # list all subprojects
```

Dependencies are pulled from Maven Central (no `lib/` directory needed): `guava-33.4.0`, `gson-2.11.0`, `commons-lang3-3.17.0`, `junit-4.13.2`, `hamcrest-2.2`

## Running Tests

```sh
./gradlew test                          # run all tests
./gradlew :TicTacToe:test              # run tests for a single module
./gradlew :TicTacToe:test --tests "net.davidrobles.mauler.tictactoe.TicTacToeTest.testCopy"  # single test method
```

Test reports are written to `modules/<Name>/build/reports/tests/test/index.html`.
