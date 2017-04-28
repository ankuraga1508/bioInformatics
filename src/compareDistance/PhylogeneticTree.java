package compareDistance;

import compareDistance.iterators.PostOrderIterator;
import compareDistance.iterators.PreOrderIterator;
import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;

import java.util.*;

/**
 * Created by Alexey Markin on 10/22/2016.
 *
 * Path-difference construction and related functionality
 */
public class PhylogeneticTree extends ModMutableRootedTree {

    public PhylogeneticTree() {
        super();
    }

    /**
     * Initialize the tree object
     * @param tree Tree object
     * @param indexer provides a way to uniformly index the given RootedTree
     */
    public PhylogeneticTree(RootedTree tree, TreeIndexer indexer) {
        super(tree);
        //assert((Boolean)tree.getAttribute(Attributes.INDEXED));
        indexer.indexTree(this);
        this.indexer = indexer;
    }

    /**
     * Creates a copy of a given tree
     * @param tree rooted Tree object
     */
    public PhylogeneticTree(RootedTree tree) {
        super(tree);
//        assert((Boolean)tree.getAttribute(Attributes.INDEXED));
    }


    public void setIndexer(TreeIndexer indexer) {
        this.indexer = indexer;
        if (!Attributes.isIndexed(this)) {
            indexer.indexTree(this);
        }
    }

    public TreeIndexer getIndexer() {
        return indexer;
    }

    public int getTaxaSizeForDistanceMatrix() {
        int taxaSize = this.getExternalNodes().size();
        if (indexer != null) {
            taxaSize = indexer.getTaxaSize();
        }

        return taxaSize;
    }

    /**
     * Construct a copy of the tree with the same taxa objects
     * @param copyAnnotations whether node annotations should be copied
     * @return the tree copy
     */
    public PhylogeneticTree getShallowCopy(boolean copyAnnotations) {
        PhylogeneticTree copyTree = new PhylogeneticTree();
        copyTree.fillCopyTreeRecursively(this.getRootNode(), copyAnnotations);
        return copyTree;
    }

    /**
     * Specifically need when working with a copy of a tree
     * @param nodeId ID of the node to be returned
     * @return the node or null if not found
     */
    public Node findNodeWithId(int nodeId) {
        for (Node node : this.getNodes()) {
            if ((Integer) node.getAttribute(Attributes.NODE_ID) == nodeId) {
                return node;
            }
        }

        return null;
    }

    /**
     * Very naive implementation of LCA calculation for two nodes (there are better algorithms with preprocessing step)
     * @return a least common ancestor node for the two nodes
     */
    public Node getLCA(Node n1, Node n2) {
        int depth1 = getNodeDepth(n1), depth2 = getNodeDepth(n2);
        while (depth1 > depth2) {
            n1 = getParent(n1);
            depth1 --;
        }
        while (depth2 > depth1) {
            n2 = getParent(n2);
            depth2 --;
        }

        while (n1 != n2 && n1 != null && n2 != null) {
            n1 = getParent(n1);
            n2 = getParent(n2);
        }

        if (n1 == null || n2 == null) {
            return null;
        } else {
            return n1;
        }
    }

    /**
     * An extension of the above naive method for multiple input nodes
     * @param nodes list of odes to find LCA for
     * @return the least common ancestor
     */
    public Node getLCA(List<Node> nodes) {
        if (nodes.size() == 0) {
            return null;
        } else {
            Node LCA = nodes.get(0);

            for (int i = 1; i < nodes.size(); i++) {
                LCA = getLCA(LCA, nodes.get(i));
            }

            return LCA;
        }
    }

    /**
     * Straightforward implementation of depth calculation
     * @return the length of the path (in edges) to the root -- depth
     */
    public int getNodeDepth(Node node) {
        int depth = 0;
        Node current = node;
        while (getParent(current) != null) {
            depth ++;
            current = getParent(current);
        }

        return depth;
    }

    /**
     * determines whether $u$ lies on the path from $v$ to the root
     */
    public boolean isSmallerOrEqual(Node v, Node u) {
        Node current = v;
        while (current != null) {
            if (current == u) {
                return  true;
            }
            current = getParent(current);
        }

        return false;
    }

    public void suppressInternalNode(Node node) {
        if (isRoot(node)) {
            assert node.getDegree() == 1;
            Node newRoot = getChildren(node).get(0);
            ((MutableRootedNode)newRoot).setParent(null);
            setRoot(newRoot);
            internalNodes.remove(node);
        } else {
            removeInternalNode(node);
        }
    }

    // Returns the new node
    public Node addNodeAbove(Node addNode, Node aboveNode) {
        List<Node> newNodeChildren = new ArrayList<Node>(2);
        newNodeChildren.add(aboveNode);
        newNodeChildren.add(addNode);
        Node newNode = new MutableRootedNode(newNodeChildren);

        if (isRoot(aboveNode)) {
            setRoot(newNode);
        } else {
            Node parentNode = getParent(aboveNode);
            removeChild(aboveNode, parentNode);
            addChild(newNode, parentNode); // Sets the parent inside the method
        }

        ((MutableRootedNode)aboveNode).setParent(newNode);
        ((MutableRootedNode)addNode).setParent(newNode);
        internalNodes.add(newNode);
        return newNode;
    }

    /**
     * Checks whether the tree contains multifurcations
     * @return {@code true}, if there are no multifurcations
     */
    public boolean isFullyBinary() {
        Iterator<Node> preOrderIterator = new PreOrderIterator(this);
        while (preOrderIterator.hasNext()) {
            Node node = preOrderIterator.next();
            if (getChildren(node).size() > 2) {
                return false;
            }
        }

        return true;
    }

    public List<Node> refineMultifurcation(Node multifurcatedNode, Set<Node> splitSide1, Set<Node> splitSide2) {
        Set<Node> children = new HashSet<Node>(splitSide1);
        children.addAll(splitSide2);
        Set<Node> actualChildren = new HashSet<Node>(getChildren(multifurcatedNode));
        if (!children.containsAll(actualChildren) || !actualChildren.containsAll(children)) {
            throw new Error("Invalid arguments");
        }

        final MutableRootedNode child1 = new MutableRootedNode(new ArrayList<Node>(splitSide1));
        child1.setParent(multifurcatedNode);
        for (Node node : splitSide1) {
            ((MutableRootedNode) node).setParent(child1);
        }

        final MutableRootedNode child2 = new MutableRootedNode(new ArrayList<Node>(splitSide2));
        child2.setParent(multifurcatedNode);
        for (Node node : splitSide2) {
            ((MutableRootedNode) node).setParent(child2);
        }

        List<Node> newChildren = new ArrayList<Node>(){{add(child1); add(child2);}};
        ((MutableRootedNode) multifurcatedNode).replaceChildren(newChildren);

        return newChildren;
    }

    public List<Node> getSiblings(Node node) {
        if (isRoot(node)) {
            return new ArrayList<Node>();
        }

        List<Node> siblings = getChildren((getParent(node)));
        siblings.remove(node);
        return siblings;
    }

    public Node getNodeWithTaxonName(String taxonName) {
        for (Taxon taxon : this.getTaxa()) {
            if (taxon.getName().equals(taxonName)) {
                return this.getNode(taxon);
            }
        }

        return null;
    }

    public Set<Taxon> getTrueTaxa() {
        return getNodeCluster(getRootNode());
    }

    public void removeTaxon(Taxon taxon) {
        if (getTaxa().contains(taxon)) {
            this.externalNodes.remove(taxon);
        }
    }

    @Override
    public String toString() {
        return jebl.evolution.trees.Utils.toNewick(this);
    }

    public Set<Taxon> getNodeCluster(Node node) {
        Set<Taxon> taxa = new HashSet<Taxon>();
        Iterator<Node> subtreeNodesIterator = new PostOrderIterator(this, node);
        while (subtreeNodesIterator.hasNext()) {
            Node subtreeNode = subtreeNodesIterator.next();
            if (subtreeNode.getDegree() == 1 && this.getTaxon(subtreeNode) != null) {
                taxa.add(this.getTaxon(subtreeNode));
            }
        }

        return taxa;
    }

    public Set<Node> getLeafSet(Node node) {
        Set<Node> leaves = new HashSet<Node>();
        Iterator<Node> subtreeNodesIterator = new PostOrderIterator(this, node);
        while (subtreeNodesIterator.hasNext()) {
            Node subtreeNode = subtreeNodesIterator.next();
            if (subtreeNode.getDegree() == 1 && this.getTaxon(subtreeNode) != null) {
                leaves.add(subtreeNode);
            }
        }

        return leaves;
    }

    public Set<Node> getLeafSet() {
        return getLeafSet(getRootNode());
    }


    // -------------PRIVATE----------------

    private Node fillCopyTreeRecursively(Node node, boolean copyAnnotations) {
        Node copyNode = null;
        if (node.getDegree() == 1 && getTaxon(node) != null) {
            if (copyAnnotations) {
                copyNode = this.createExternalNode(getTaxon(node));
            } else {
                copyNode = this.createExternalNode(Taxon.getTaxon(getTaxon(node).getName()));
            }
        } else {
            List<Node> children = new ArrayList<Node>();
            for (Node child : getChildren(node)) {
                children.add(fillCopyTreeRecursively(child, copyAnnotations));
            }
            copyNode = this.createInternalNode(children);
        }

        if (copyAnnotations) {
            copyNode.setAttribute(Attributes.NODE_ID, node.getAttribute(Attributes.NODE_ID));
            copyNode.setAttribute(Attributes.CLUSTER, node.getAttribute(Attributes.CLUSTER));
            copyNode.setAttribute(Attributes.CLUSTER_SIZE, node.getAttribute(Attributes.CLUSTER_SIZE));
        }

        return copyNode;
    }


    private TreeIndexer indexer;

}
