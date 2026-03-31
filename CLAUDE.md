# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Mauler** (Multi-Algorithm Learning Evaluation Research) — a Java game framework for implementing board games and testing AI algorithms against them.

## Build System

This project uses **Gradle 8.13 with Kotlin DSL**. The Gradle wrapper (`./gradlew`) is the canonical way to build.

```sh
./gradlew build           # compile all modules
./gradlew projects        # list all subprojects
./gradlew spotlessApply   # auto-format code (Google Java Format, AOSP style)
./gradlew spotlessCheck   # check formatting without modifying
```

**Java toolchain:** 21

**Dependencies** (declared in `gradle/libs.versions.toml`, pulled from Maven Central):
- `junit-4.13.2`, `hamcrest-2.2`
- `guava-33.4.0-jre`, `gson-2.11.0`, `commons-lang3-3.17.0`

## Running Tests

Tests use JUnit 4.13.2 and live in each module's `src/test/java/` directory.

```sh
./gradlew test                                         # all tests
./gradlew :TicTacToe:test                              # single module
./gradlew :TicTacToe:test --tests "TicTacToeTest"      # single class
```

Test reports: `modules/<Name>/build/reports/tests/test/index.html`

Performance benchmarks are run via `main()` methods using `SpeedTest.gameSpeed()` in each game module.

## Architecture

### Core Framework (`modules/Mauler/`)

All games implement the `Game<GAME>` interface (`modules/Mauler/src/main/java/net/davidrobles/mauler/core/Game.java`):
- Key methods: `copy()`, `getCurPlayer()`, `getMoves()`, `getNumMoves()`, `makeMove(int)`, `isOver()`, `getOutcome()`, `reset()`
- Concrete games extend `ObservableGame` which adds `MoveObservable` support
- Move model is index-based: `[0, getNumMoves())`; must override `equals()`/`hashCode()` on full state

All AI strategies implement `Strategy<GAME>` (`modules/Mauler/src/main/java/net/davidrobles/mauler/core/Strategy.java`):
- Key methods: `isDeterministic()`, `move(game)`, `move(game, timeout)`
- **Minimax family:** `Minimax`, `AlphaBeta`, `Negamax`, `IterativeDeepening`, `DepthLimitedSearch`, `PVS`
- **Monte Carlo family:** `RandomStrategy`, `MonteCarlo`, `MCTS` (UCT), `MCTSNoRollout`, `MCTSWithPrior`, `MCTSRootParallel`, `UCT`, `UCTWithPrior`
- **Greedy:** `GreedyStrategy`, `EpsilonGreedyStrategy`

Tournament infrastructure: `Match` (single game), `Series` (multiple games), `RoundRobin` (round-robin tournament).

### Module Layout

```
modules/
  Mauler/              # Core: Game, Strategy, Match, Series, RoundRobin interfaces/impls
  Utils/               # Shared GUI utilities (DRUtil)
  TicTacToe/           # 3x3 board, int bitboards (9-bit cross/nought fields)
  Othello/             # 8x8 board, long bitboards, 8-direction flip detection
  LinesOfAction/       # Bitboard-based
  Breakthrough/
  ConnectFour/
  Domineering/
  Havannah/
  PlanetWars/
  Tron/
  GridWorld/           # MDP environment for RL algorithms
  ReinforcementLearning/  # Q-learning, SARSA, TD, Monte Carlo, policy/value iteration
  StrategyTests/       # Competence tests: each strategy tested as both first and second player
  Experiments/         # Standalone experiment runners (TTTRun, TTTGraphviz)
  ThesisExperiments/
  *Play (5 modules)/   # GUI wrappers: TicTacToePlay, OthelloPlay, LinesOfActionPlay, HavannahPlay, GridWorldPlay
```

### Key Design Patterns

- **Generics everywhere:** `Game<GAME extends Game<GAME>>` and `Strategy<GAME extends Game<GAME>>` — games are self-referential generics for type-safe copying and move application.
- **Bitboard representation:** Games use primitive `int` (TicTacToe) or `long` (Othello) bitboards for efficient move generation and win detection.
- **Observer pattern:** `MoveObservable`/`MoveObserver` — games notify listeners when moves are made (used by GUI modules).
- **Package naming:** All source is under `net.davidrobles.mauler.<module>` (e.g., `net.davidrobles.mauler.tictactoe`).

### Implementing a New Game

1. Create a new module under `modules/` with `src/main/java/` and `src/test/java/` directories; add a `build.gradle.kts`.
2. Register it in `settings.gradle.kts`.
3. Extend `ObservableGame<YourGame>` and call `notifyMoveObservers()` at the end of `makeMove()`.
4. Use bitboard primitives for performance-critical state representation.
5. Add a test class extending `GameTest<YourGame>` from the Mauler module (via `testFixtures` dependency).
