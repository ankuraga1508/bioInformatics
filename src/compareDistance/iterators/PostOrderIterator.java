package compareDistance.iterators;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Alexey Markin on 10/29/2016.
 */
public class PostOrderIterator implements Iterator<Node>{

    public PostOrderIterator(RootedTree tree) {
        this.tree = tree;
        nodeStack = new Stack<Node>();
        stateStack = new Stack<Boolean>();
        addToStacks(tree.getRootNode(), false);
    }

    public PostOrderIterator(RootedTree tree, Node startNode) {
        this.tree = tree;
        nodeStack = new Stack<Node>();
        stateStack = new Stack<Boolean>();
        addToStacks(startNode, false);
    }

    public boolean hasNext() {
        return !nodeStack.isEmpty();
    }

    public Node next() {
        while (!nodeStack.isEmpty()) {
            Node topNode = nodeStack.pop();
            boolean nodeState = stateStack.pop();
            if (nodeState) {
                return topNode;
            } else {
                addToStacks(topNode, true);
                for (Node child : tree.getChildren(topNode)) {
                    addToStacks(child, false);
                }
            }
        }

        return null;
    }

    public void remove() {
        throw new UnsupportedOperationException("Removing nodes on the fly is not supported");
    }

    private void addToStacks(Node node, boolean state) {
        nodeStack.add(node);
        stateStack.add(state);
    }

    private RootedTree tree;
    private Stack<Node> nodeStack;
    private Stack<Boolean> stateStack;
}
