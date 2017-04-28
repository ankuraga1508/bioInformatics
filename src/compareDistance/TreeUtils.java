package compareDistance;

import compareDistance.iterators.PreOrderIterator;
import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NewickImporter;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;
import jebl.evolution.trees.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created by Alexey Markin on 11/03/2016.
 */
public class TreeUtils {
    public static void pruneLeavesWithTaxa(PhylogeneticTree tree, Set<Taxon> pruneSet, int[] regraftAboveMap,
                                           boolean[] deletedNodesIndicator) {
        if (regraftAboveMap != null) {
            assert deletedNodesIndicator != null;
            Arrays.fill(deletedNodesIndicator, false);
        }

        Set<String> pruneSetStrings = new HashSet<String>(pruneSet.size());
        for (Taxon taxon : pruneSet) {
            pruneSetStrings.add(taxon.getName());
        }

        pruneRecursively(tree, pruneSetStrings, regraftAboveMap, deletedNodesIndicator, tree.getRootNode(), regraftAboveMap != null);
    }

    public static int countCherries(PhylogeneticTree tree) {
        int cherries = 0;
        for (Node node : tree.getInternalNodes()) {
            List<Node> children = tree.getChildren(node);
            if (children.size() == 2 && tree.isExternal(children.get(0)) && tree.isExternal(children.get(1))) {
                cherries ++;
            }
        }

        return cherries;
    }

    public static PhylogeneticTree resolveMultifurcationsRandomly(PhylogeneticTree originalTree) {
        PhylogeneticTree tree = originalTree.getShallowCopy(true);
        Iterator<Node> preOrderIterator = new PreOrderIterator(tree);
        while (preOrderIterator.hasNext()) {
            Node node = preOrderIterator.next();
            List<Node> children = tree.getChildren(node);
            if (children.size() > 2) {
                makeRandomSplitOfMultifurcation(tree, node);
            }
        }

        // check if there are no multifurcations anymore, and whether the tree is not damaged
        if (tree.isFullyBinary() && tree.getNodes().size() == originalTree.getTrueTaxa().size() * 2 - 1) {
            return tree;
        } else {
            throw new Error("Error occurred while resolving multifurcations");
        }
    }

    public synchronized static RootedTree performSPR(PhylogeneticTree sourceTree, Node pruneNode, Node regraftAboveNode) {
        PhylogeneticTree copyTree = sourceTree.getShallowCopy(true);
        if (sourceTree.isRoot(pruneNode)) {
            // Can't prune the root
            return copyTree;
        }

        if (sourceTree.getChildren(regraftAboveNode).contains(pruneNode)) {
            // This SPR not modify the tree.
            // Plus this check guarantees that regraftAboveNode won't be removed while suppressing the parent of the pruneNode
            return copyTree;
        }

        int pruneNodeId = (Integer) pruneNode.getAttribute(Attributes.NODE_ID);
        int regraftAboveNodeId = (Integer) regraftAboveNode.getAttribute(Attributes.NODE_ID);

        Node copyPruneNode = copyTree.findNodeWithId(pruneNodeId);
        Node copyRegraftAboveNode = copyTree.findNodeWithId(regraftAboveNodeId);
        Node pruneNodeParent = copyTree.getParent(copyPruneNode);

        copyTree.removeChild(copyPruneNode, pruneNodeParent);
        copyTree.suppressInternalNode(pruneNodeParent);

        Node newNode = copyTree.addNodeAbove(copyPruneNode, copyRegraftAboveNode);
        newNode.setAttribute(Attributes.NODE_ID, pruneNodeParent.getAttribute(Attributes.NODE_ID));

        return copyTree;
    }

    public synchronized static List<PhylogeneticTree> pruneInputTrees(PhylogeneticTree indexedSupertree, List<PhylogeneticTree> inputTrees) {
        TreeIndexer indexer = indexedSupertree.getIndexer();
        Set<Taxon> supertreeTaxa = indexedSupertree.getTrueTaxa();
        Set<Taxon> inputTreesTaxa = getTreeSetTaxa(inputTrees);

        assert(supertreeTaxa.size() <= inputTreesTaxa.size());
        if (supertreeTaxa.size() == inputTreesTaxa.size()) {
            for (PhylogeneticTree inputTree : inputTrees) {
                inputTree.setIndexer(indexer);
            }
            return inputTrees;
        }

        List<PhylogeneticTree> prunedInputTrees = new ArrayList<PhylogeneticTree>();
        Set<Taxon> taxaToPrune = new HashSet<Taxon>(inputTreesTaxa);
        taxaToPrune.removeAll(supertreeTaxa);
        for (PhylogeneticTree inputTree : inputTrees) {
            PhylogeneticTree prunedTree = inputTree.getShallowCopy(false);
            prunedTree.setIndexer(indexer);
            TreeUtils.pruneLeavesWithTaxa(prunedTree, taxaToPrune,
                    null, null);
            if (prunedTree.getTrueTaxa().size() > 2) {
                prunedInputTrees.add(prunedTree);
            }
        }

        return prunedInputTrees;
    }

    public synchronized static Set<Taxon> getTreeSetTaxa(List<? extends Tree> inputTrees) {
        Set<Taxon> taxa = new HashSet<Taxon>();
        Set<String> taxaNames = new HashSet<String>();
        for (Tree inputTree : inputTrees) {
            for (Taxon taxon : inputTree.getTaxa()) {
                if (!taxaNames.contains(taxon.getName())) {
                    taxa.add(taxon);
                    taxaNames.add(taxon.getName());
                }
            }
        }

        return taxa;
    }

    public static boolean isLarger(RootedTree tree1, RootedTree tree2) {
        PhylogeneticTree PhylogeneticTree1 = new PhylogeneticTree(tree1);
        PhylogeneticTree PhylogeneticTree2 = new PhylogeneticTree(tree2);
        return PhylogeneticTree1.getNodeCluster(PhylogeneticTree1.getRootNode()).containsAll(PhylogeneticTree2.getNodeCluster(PhylogeneticTree2.getRootNode()));
    }

    public static Set<Taxon> missingTaxa(RootedTree superTree, RootedTree inputTree) {
        assert isLarger(superTree, inputTree);
        Set<Taxon> superTreeTaxa = superTree.getTaxa();
        superTreeTaxa.removeAll(inputTree.getTaxa());
        return superTreeTaxa;
    }

    public static RootedTree getTreeFromString(String newickString) throws ImportException, IOException {
        NewickImporter importer = new NewickImporter(new StringReader(newickString), false);
        try {
            Tree result = importer.importNextTree();
            RootedTree rootedResult = Utils.rootTheTree(result);
            return rootedResult;
        } catch (IOException e) {
        	throw e;
        } catch (ImportException e) {
        	throw e;
        }
    }

    public static List<Node> findDirectedPath(PhylogeneticTree tree, Node startNode, Node endNode) {
        List<Node> startPathToRoot = getPathToRoot(tree, startNode);
        List<Node> endPathToRoot = getPathToRoot(tree, endNode);

        while (startPathToRoot.size() - 1 > 0 && endPathToRoot.size() > 0 && startPathToRoot.get(startPathToRoot.size() - 1) == endPathToRoot.get(endPathToRoot.size() - 1)) {
            startPathToRoot.remove(startPathToRoot.size() - 1);
            endPathToRoot.remove(endPathToRoot.size() - 1);
        }

        startPathToRoot.add(tree.getParent(startPathToRoot.get(startPathToRoot.size() - 1)));
        Collections.reverse(endPathToRoot);
        startPathToRoot.addAll(endPathToRoot);

        return startPathToRoot;
    }

    //---------------PRIVATE------------
    private static void makeRandomSplitOfMultifurcation(PhylogeneticTree tree, Node multifurcatedInternalNode) {
        List<Node> children = tree.getChildren(multifurcatedInternalNode);
        if (children.size() > 2) {
            Random rnd = new Random(0);
            int splitPosition = 1 + rnd.nextInt(children.size() - 1);
            Set<Node> splitSet1 = new HashSet<Node>(children.subList(0, splitPosition));
            Set<Node> splitSet2 = new HashSet<Node>(children.subList(splitPosition, children.size()));
            List<Node> newChildren = tree.refineMultifurcation(multifurcatedInternalNode, splitSet1, splitSet2);

            for (Node newChild : newChildren) {
                if (tree.getChildren(newChild).size() > 2) {
                    makeRandomSplitOfMultifurcation(tree, newChild);
                }
            }
        }
    }

    private static List<Node> getPathToRoot(PhylogeneticTree tree, Node startNode) {
        List<Node> path = new ArrayList<Node>();
        Node current = startNode;
        do {
            path.add(current);
            current = tree.getParent(current);
        } while(current != null);

        return path;
    }

    private static List<Integer> pruneRecursively(PhylogeneticTree tree, Set<String> pruneSet, int[] regraftAboveMap,
                                                  boolean[] deletedNodesIndicator, Node node, boolean fillMap) {
        int nodeId = Attributes.getNodeID(node);
        if (fillMap) {
            regraftAboveMap[nodeId] = nodeId;
        }

        List<Integer> deletedNodesIDs = new ArrayList<Integer>();
        if (node.getDegree() == 1 && tree.getTaxon(node) != null) {
            // leaf node
            if (fillMap && pruneSet.contains(tree.getTaxon(node).getName())) {
                deletedNodesIndicator[nodeId] = true;
            }
            if (pruneSet.contains(tree.getTaxon(node).getName())) {
                deletedNodesIDs.add(nodeId);
//                tree.removeTaxon(tree.getTaxon(node));
                return deletedNodesIDs;
            } else {
                return null;
            }
        }

        for (Node child : tree.getChildren(node)) {
            List<Integer> childDeletedList = pruneRecursively(tree, pruneSet, regraftAboveMap, deletedNodesIndicator, child, fillMap);
            if (childDeletedList != null) {
                tree.removeChild(child, node);
                deletedNodesIDs.addAll(childDeletedList);
            }
        }

        if (tree.getChildren(node).size() == 0) {
            // need to delete this node as well
            deletedNodesIDs.add(nodeId);
            if (fillMap) {
                deletedNodesIndicator[nodeId] = true;
            }
            return deletedNodesIDs;
        } else if (tree.getChildren(node).size() == 1) {
            Node onlyChild = tree.getChildren(node).get(0);
            int onlyChildId = Attributes.getNodeID(onlyChild);
            if (fillMap) {
                for (int deletedNodeId : deletedNodesIDs) {
                    regraftAboveMap[deletedNodeId] = onlyChildId;
                }
                regraftAboveMap[nodeId] = onlyChildId;
            }
            tree.suppressInternalNode(node);

            return null;
        } else {
            return null;
        }

    }
}
