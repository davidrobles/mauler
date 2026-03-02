# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Mauler** (Multi-Algorithm Learning Evaluation Research) — a Java game framework for implementing board games and testing AI algorithms against them. No README exists; this file is the primary reference.

## Build System

This project uses **IntelliJ IDEA's native build system** — there is no Maven, Gradle, or Ant. Each module has a `.iml` file; all modules are listed in `.idea/modules.xml`.

To compile from the command line, use `javac` with the appropriate source paths and classpath pointing to `lib/`.

**Dependencies** (in `lib/`): `junit-4.11.jar`, `hamcrest-core-1.3.jar`, `guava-18.0.jar`, `gson-2.3.1.jar`, `commons-lang3-3.3.2.jar`

## Running Tests

Tests use JUnit 4.11 and live in each module's `test/` directory.

Run all tests for a module from the command line:
```sh
javac -cp lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:out/... <test-files>
java -cp lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:out/... org.junit.runner.JUnitCore <TestClassName>
```

Run a single test class (e.g., TicTacToe):
```sh
# Compile game module first, then:
java -cp lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:out/production/TicTacToe:out/production/Mauler \
  org.junit.runner.JUnitCore net.davidrobles.mauler.tictactoe.TicTacToeTest
```

Performance benchmarks are run via `main()` methods using `SpeedTest.gameSpeed()` in each game module.

## Architecture

### Core Framework (`modules/Mauler/`)

All games implement the `Game<GAME>` interface (`modules/Mauler/src/net/davidrobles/mauler/core/Game.java`):
- Key methods: `copy()`, `getCurPlayer()`, `getMoves()`, `getNumMoves()`, `makeMove(int)`, `isOver()`, `getOutcome()`
- Concrete games extend `AbstractGame` which adds `MoveObservable` support

All AI players implement `Player<GAME>` (`modules/Mauler/src/net/davidrobles/mauler/players/Player.java`):
- Key methods: `isDeterministic()`, `move(game)`, `move(game, timeout)`
- Implementations: `RandPlayer`, `Minimax`, `AlphaBeta`, `Negamax`, `IterDeep`, `MonteCarlo`, `ParTimedMC`, `AMAF`, `DepthLimitedSearch`

Tournament infrastructure: `Match` (single game), `Series` (multiple games), `RoundRobin` (round-robin tournament).

### Module Layout

```
modules/
  Mauler/              # Core: Game, Player, Match, Series, RoundRobin interfaces/impls
  Utils/               # Shared GUI utilities (DRUtil)
  TicTacToe/           # 3x3 board, int bitboards (9-bit cross/nought fields)
  Othello/             # 8x8 board, long bitboards, 8-direction flip detection
  LinesOfAction/       # Bitboard-based
  Breakthrough/
  ConnectFour/
  Domineering/
  GridWorld/
  Havannah/
  PlanetWars/
  Tron/
  ReinforcementLearning/
  ThesisExperiments/
  *Play (5 modules)/   # GUI/interactive wrappers for playable games
```

### Key Design Patterns

- **Generics everywhere:** `Game<GAME extends Game<GAME>>` and `Player<GAME extends Game<GAME>>` — games are self-referential generics for type-safe copying and move application.
- **Bitboard representation:** Games use primitive `int` (TicTacToe) or `long` (Othello) bitboards for efficient move generation and win detection.
- **Observer pattern:** `MoveObservable`/`MoveObserver` — games notify listeners when moves are made (used by GUI modules).
- **Package naming:** All source is under `net.davidrobles.mauler.<module>` (e.g., `net.davidrobles.mauler.tictactoe`).

### Implementing a New Game

1. Create a new module under `modules/` with a `src/` and optional `test/` directory.
2. Implement the `Game<YourGame>` interface, extending `AbstractGame` for observer support.
3. Use bitboard primitives for performance-critical state representation.
4. Add test class extending `GameTest<YourGame>` from the Mauler module.
