package compareDistance;


import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.Tree;

import java.util.*;

/**
 * Created by Alexey Markin on 10/24/2016.
 *
 * Annotates the nodes of a given tree and taxa in a consistent way
 */
public class TreeIndexer {
    private Map<String, Integer> labelMapping;
    private Set<Taxon> taxaSet;

    public TreeIndexer(Set<Taxon> taxaSet) {
        this.taxaSet = taxaSet;
        labelMapping = new HashMap<String, Integer>(taxaSet.size());
        int index = 0;
        for (Taxon taxon : taxaSet) {
            labelMapping.put(taxon.getName(), index);
            index ++;
        }
    }

    // TODO: throw an exception if the tree has an invalid taxa set
    public void indexTree(PhylogeneticTree tree) {
        for (Taxon leafTaxon : tree.getTrueTaxa()) {
            if (labelMapping.containsKey(leafTaxon.getName())) {
                if (leafTaxon.getAttribute(Attributes.TAXA_ID) != null) {
//                    System.out.println("before: " + Attributes.getTaxonID(leafTaxon));
                    leafTaxon.removeAttribute(Attributes.TAXA_ID);
                }
                leafTaxon.setAttribute(Attributes.TAXA_ID, labelMapping.get(leafTaxon.getName()));
//                System.out.println("after: " + Attributes.getTaxonID(leafTaxon));
            }
        }

        int nodeId = 0;
        for (Node node : tree.getNodes()) {
            if (node.getAttribute(Attributes.NODE_ID) != null) {
                node.removeAttribute(Attributes.NODE_ID);
            }
            node.setAttribute(Attributes.NODE_ID, nodeId);
            nodeId ++;
        }

        tree.setAttribute(Attributes.INDEXED, true);

    }

    public int getIndexByTaxonName(String taxon) {
        if (labelMapping.containsKey(taxon)) {
            return labelMapping.get(taxon);
        }
        return -1;
    }

    public int getTaxaSize() {
        return labelMapping.keySet().size();
    }

    public Set<Taxon> getTaxaSet() {
        return new HashSet<Taxon>(taxaSet); // make a copy, so that it won't be modified
    }
}
