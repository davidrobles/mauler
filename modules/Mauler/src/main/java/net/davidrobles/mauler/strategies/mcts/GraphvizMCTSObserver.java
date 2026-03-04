package net.davidrobles.mauler.strategies.mcts;

import net.davidrobles.mauler.core.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
    private final Function<MCTSNode<GAME>, String> labelFn;

    /**
     * Creates an observer that labels each node with its visit count and mean value.
     */
    public GraphvizMCTSObserver(File file)
    {
        this(file, node -> String.format("v=%d\\nq=%.3f", node.getVisits(), node.getValue()));
    }

    /**
     * Creates an observer with a custom label function.
     *
     * <p>The function receives the {@link MCTSNode} and must return either a
     * plain string (output as {@code "..."}) or an HTML label
     * (starts with {@code <}, output as {@code <...>}).
     */
    public GraphvizMCTSObserver(File file, Function<MCTSNode<GAME>, String> labelFn)
    {
        this.file    = file;
        this.labelFn = labelFn;
    }

    @Override
    public void searchFinished(MCTSNode<GAME> root)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph MCTS {\n");
        sb.append("    node [shape=box fontname=Helvetica];\n");

        Map<GAME, Integer> seen = new HashMap<>();
        seen.put(root.getGame(), 0);
        appendNodeDef(sb, 0, root);
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

    /** Appends the DOT node definition, using plain or HTML label as appropriate. */
    private void appendNodeDef(StringBuilder sb, int id, MCTSNode<GAME> node)
    {
        String label = labelFn.apply(node);
        if (label.startsWith("<"))
            sb.append(String.format("    %d [label=%s];\n", id, label));
        else
            sb.append(String.format("    %d [label=\"%s\"];\n", id, label));
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
                appendNodeDef(sb, childId, child);
            }
            int childId = seen.get(child.getGame());
            sb.append(String.format("    %d -> %d;\n", nodeId, childId));
            if (firstVisit)
                writeChildren(child, childId, sb, seen);
        }
    }
}
