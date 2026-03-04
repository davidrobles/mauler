package net.davidrobles.mauler.strategies.mcts;

import net.davidrobles.mauler.core.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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
 * <p><b>Transposition handling:</b> multiple MCTS tree nodes that represent the
 * same game state (equal under {@link Object#equals}) are collapsed into a single
 * DOT node. Statistics shown are from the first-encountered tree node for that
 * state. Games that do not override {@code equals()} will fall back to identity
 * comparison and no deduplication will occur.
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

        Map<GAME, Integer> seen = new HashMap<>();
        seen.put(root.getGame(), 0);
        sb.append(String.format("    0 [label=\"v=%d\\nq=%.3f\"];\n",
                root.getVisits(), root.getValue()));
        writeChildren(root, 0, sb, seen);

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
     * Recursively writes edges (and node definitions on first encounter) for
     * all visited children of {@code node}. Each unique game state gets exactly
     * one DOT node regardless of how many MCTS tree nodes share that state.
     */
    private void writeChildren(MCTSNode<GAME> node, int nodeId, StringBuilder sb, Map<GAME, Integer> seen)
    {
        for (MCTSNode<GAME> child : node.getChildren())
        {
            if (child.getVisits() == 0)
                continue;

            boolean firstVisit = !seen.containsKey(child.getGame());
            if (firstVisit)
            {
                int childId = seen.size();
                seen.put(child.getGame(), childId);
                sb.append(String.format("    %d [label=\"v=%d\\nq=%.3f\"];\n",
                        childId, child.getVisits(), child.getValue()));
            }
            int childId = seen.get(child.getGame());
            sb.append(String.format("    %d -> %d;\n", nodeId, childId));
            if (firstVisit)
                writeChildren(child, childId, sb, seen);
        }
    }
}
