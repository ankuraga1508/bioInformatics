package compareDistance.iterators;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by Alexey Markin on 11/11/2016.
 */
public class PreOrderIterator implements Iterator<Node>{
    public PreOrderIterator(RootedTree tree) {
        this.tree = tree;
        this.startNode = tree.getRootNode();
        nodeStack = new Stack<Node>();
        nodeStack.add(startNode);
    }

    public PreOrderIterator(RootedTree tree, Node startNode) {
        this.tree = tree;
        this.startNode = startNode;
        nodeStack = new Stack<Node>();
        nodeStack.add(startNode);
    }

    public boolean hasNext() {
        return !nodeStack.isEmpty();
    }

    public Node next() {
        Node nextNode = nodeStack.pop();
        for (Node child : this.tree.getChildren(nextNode)) {
            nodeStack.add(child);
        }

        return nextNode;
    }

    public void remove() {
        throw new UnsupportedOperationException("Removing nodes on the fly is not supported");
    }

    private Stack<Node> nodeStack;

    private RootedTree tree;
    private Node startNode;
}
