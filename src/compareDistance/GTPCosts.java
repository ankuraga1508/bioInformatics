package compareDistance;

import compareDistance.PhylogeneticTree;
import compareDistance.TreeUtils;
import compareDistance.iterators.PostOrderIterator;
import compareDistance.iterators.PreOrderIterator;
import jebl.evolution.graphs.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alexey Markin on 01/29/2017.
 */
public class GTPCosts {
    private static final String LCAMappingNode = "LCA_MAPPING_NODE";

    /**
     * Annotates gene tree with LCA mappings and return the number of duplication events
     * @return number of duplication events induced by a mapping of the given gene tree onto the species tree
     */
    public static int getGeneDupCost(PhylogeneticTree geneTree, PhylogeneticTree speciesTree) {
        if (!TreeUtils.isLarger(speciesTree, geneTree)) {
            return -1;
        }
        
        int duplicationCost = 0;
        Iterator<Node> postOrderIterator = new PostOrderIterator(geneTree);
        while (postOrderIterator.hasNext()) {
            Node current = postOrderIterator.next();
            Node LCAMapped = null;
            if (geneTree.isExternal(current) && geneTree.getTaxon(current) != null) {
                LCAMapped = speciesTree.getNodeWithTaxonName(geneTree.getTaxon(current).getName());
            } else {
                List<Node> childrenMappings = new ArrayList<Node>(geneTree.getChildren(current).size());
                for (Node child : geneTree.getChildren(current)) {
                    childrenMappings.add((Node) child.getAttribute(LCAMappingNode));
                }

                LCAMapped = speciesTree.getLCA(childrenMappings);
                boolean duplication = false;
                for (Node mappedNode : childrenMappings) {
                    if (mappedNode == LCAMapped) {
                        duplication = true;
                        break;
                    }
                }

                if (duplication) {
                    duplicationCost ++;
                }
            }
            current.removeAttribute(LCAMappingNode);
            current.setAttribute(LCAMappingNode, LCAMapped);
        }

        return duplicationCost;
    }

    public static int getGeneDCCost(PhylogeneticTree geneTree, PhylogeneticTree speciesTree) {
        getGeneDupCost(geneTree, speciesTree);  // to compute annotations
        Iterator<Node> postOrderIterator = new PostOrderIterator(geneTree);
        int DCCost = 0;
        while (postOrderIterator.hasNext()) {
            Node current = postOrderIterator.next();
            if (current != geneTree.getRootNode()) {
                Node currentMappedNode = (Node) current.getAttribute(LCAMappingNode);
                Node parentMappedNode = (Node) (geneTree.getParent(current)).getAttribute(LCAMappingNode);

                // find number of edges between the two mapped nodes
                int pathSize = 0;
                Node temp = currentMappedNode;
                while (temp != parentMappedNode) {
                    temp = speciesTree.getParent(temp);
                    pathSize ++;
                }

                DCCost += pathSize - 1;
            }
        }

        return DCCost;

    }

    public static int getGeneDLCost(PhylogeneticTree geneTree, PhylogeneticTree speciesTree) {
        return getGeneDCCost(geneTree, speciesTree) + 3 * getGeneDupCost(geneTree, speciesTree);
    }
}
