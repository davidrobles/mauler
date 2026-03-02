package net.davidrobles.mauler.players.mcts;

import net.davidrobles.mauler.core.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GraphvizMCTSObserver<GAME extends Game<GAME>> implements MCTSObserver<GAME> {

    private File file;

    public GraphvizMCTSObserver(File file) {
        this.file = file;
    }

    @Override
    public void simulationFinished(final MCTSNode<GAME> node) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph G {\n");
        builder.append("    size = \"3,3\";\n");
        builder.append("    ranksep = 10;\n");
        builder.append("    ratio = auto;\n");
//        graphVizHelper(node, null, builder);
        builder.append("}");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.println(builder);
        writer.flush();
    }

//    public void graphViz(MCTS<GAME> node) {
//        try {
//            FileNotFoundException out = new MCTSNode("/Users/drobles/Dropbox/Temp/Graphviz/dr.dot");
//            out.println(builder);
//            out.flush();
//        } catch (MCTSNode e) {
//            e.printStackTrace();
//        }
//    }

//    public void graphVizHelper(MCTSNode<GAME> node, MCTSNode<GAME> father, StringBuilder builder) {
//        if (father != null && node.getCount() > 0) {
//            builder.append("    " + father.getNodeId() + " -> " + node.getNodeId() + ";\n");
//        }
//        for (MCTSNode<GAME> child : node.getChildren()) {
//            graphVizHelper(child, node, builder);
//        }
//    }


//    public void traverseNodes(MCTSNode<GAME> node) {
//        System.out.println(node.getGame());
//        System.out.println(node);
//        System.out.println("Value: " + node.getValue());
//        System.out.println("Count: " + node.getCount());
//        System.out.println("=================");
//        if (node.getChildren().isEmpty()) {
//            return;
//        }
//        for (MCTSNode<GAME> n : node.getChildren()) {
//            traverseNodes(n);
//        }
//    }
}
