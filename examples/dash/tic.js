(function() {

    // Create a Game Interface for canvas, model, ui?

    var tic = new mauler.games.tic.TicTacToe();

    var canvasView = new mauler.games.tic.CanvasView({
        model: tic,
        width: 400,
        height: 400,
        canvas: document.getElementById("tic-canvas")
    });

    var GameHistoryView = Backbone.View.extend({

        tagName: "table",

        className: "game-history",

        initialize: function(model) {
            this.model = model;
        },

        render: function() {
            this.$el.html("");
            this.renderHeader();
            this.renderBody();
            return this;
        },

        renderHeader: function() {
            this.$el.html("<tr><td>No. 1</td><td>Black</td><td>White</td></tr>");
        },

        renderBody: function() {
            var str = "";
            for (var i = 0; i < this.model.getSize() - 1; i++) {
                if (i % 2 === 0) {
                    str += "<tr><td>*</td>";
                }
                str += "<td>" + this.model.getMove(i) + "</td>";
                if (i % 2 === 1) {
                    str += "</tr>";
                }
                if ((i % 2 === 0 && ((i + 2) === this.model.getSize()))) {
                    str += "<td></td></tr>";
                }
            }
            this.$el.append(str);
        }

    });

    var match = new mauler.Match({
        game: tic,
        players: [new mauler.players.Random(), new mauler.players.Random()]
    });

    match.next();
    match.next();
    match.next();
//    match.next();

    var gameHistoryView = new GameHistoryView(match);
    $(".dashboard").append(gameHistoryView.render().el);

    canvasView.update("test", match.getGame());
    console.log(match.getGame().toString());

}());