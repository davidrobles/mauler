Mauler
======

Mauler is a JavaScript framework for abstract strategy games.

Games
-----

- [Tic-Tac-Toe](src/games/tic-tac-toe/tic-tac-toe.js)

Algorithms
----------

- [Random](src/players/random.js)
- [Minimax](src/players/minimax.js)
- [Negamax](src/players/negamax.js)
- [Alpha-beta pruning](src/players/alpha-beta.js)
- [Monte Carlo](src/players/monte-carlo.js)
- [Monte Carlo Tree Search](src/players/mcts.js)

Play a Random Game
------------------

```js
var game = new mauler.games.TicTacToe();
console.log(game.toString());
while (!game.isGameOver() {
  var moves = game.moves();
  var randomMove = moves[Math.floor(Math.random() * moves.length)];
  game.move(randomMove);
  console.log(game.toString());
}
```
