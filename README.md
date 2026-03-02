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

**Other**: `RandPlayer`, `GreedyPlayer`, `EpsilonGreedy`, `UtilFunc` (evaluation function wrapper)

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

The project uses **IntelliJ IDEA's native build system** (no Maven or Gradle). Open the root directory in IntelliJ IDEA and build with the IDE. Compiled output goes to `out/`.

Dependencies in `lib/`: `junit-4.11`, `hamcrest-core-1.3`, `guava-18.0`, `gson-2.3.1`, `commons-lang3-3.3.2`

## Running Tests

Tests use JUnit 4.11 and are in each module's `test/` directory. Run them via IntelliJ or directly with the JUnit runner:

```sh
java -cp lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:<compiled-classes> \
  org.junit.runner.JUnitCore <TestClassName>
```
