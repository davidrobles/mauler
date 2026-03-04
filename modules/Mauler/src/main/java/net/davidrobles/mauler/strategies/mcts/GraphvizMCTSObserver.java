package net.davidrobles.mauler.strategies.mcts;

import net.davidrobles.mauler.core.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Writes the MCTS search tree to a Graphviz DOT file after each search.
 *
 * <p>Only nodes that were visited at least once are included. Each node is
 * labeled with its visit count and mean value. The output can be rendered with
 * any Graphviz layout engine, e.g.:
 * <pre>
 *   dot -Tpdf tree.dot -o tree.pdf
 * </pre>
 *
 * @param <GAME> the game type
 */
public class GraphvizMCTSObserver<GAME extends Game<GAME>> implements MCTSObserver<GAME>
{
    private final File file;

    public GraphvizMCTSObserver(File file)
    {
        this.file = file;
    }

    @Override
    public void searchFinished(MCTSNode<GAME> root)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph MCTS {\n");
        sb.append("    node [shape=box fontname=Helvetica];\n");
        writeSubtree(root, sb);
        sb.append("}\n");

        try (PrintWriter writer = new PrintWriter(file))
        {
            writer.print(sb);
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Cannot write Graphviz output to " + file, e);
        }
    }

    /**
     * Recursively writes DOT nodes and edges for {@code node} and all visited
     * descendants.
     */
    private void writeSubtree(MCTSNode<GAME> node, StringBuilder sb)
    {
        int id = System.identityHashCode(node);
        sb.append(String.format("    %d [label=\"v=%d\\nq=%.3f\"];\n",
                id, node.getVisits(), node.getValue()));

        for (MCTSNode<GAME> child : node.getChildren())
        {
            if (child.getVisits() > 0)
            {
                sb.append(String.format("    %d -> %d;\n", id, System.identityHashCode(child)));
                writeSubtree(child, sb);
            }
        }
    }
}
