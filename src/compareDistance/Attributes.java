package compareDistance;

import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;

import java.util.List;

/**
 * Created by Alexey Markin on 10/24/2016.
 */
public class Attributes {
    // Taxon object attribute
    public static final String TAXA_ID = "TAXA_ID";

    // Node object attribute
    public static final String NODE_ID = "NODE_ID";
    public static final String CLUSTER_SIZE = "CLUSTER_SIZE";
    public static final String CLUSTER = "CLUSTER";

    // Tree object attribute
    public static final String INDEXED = "INDEXED";
    public static final String TAXA_ANNOTATED = "TAXA_ANNOTATED";

    public static int getNodeID(Node node) {
        return (Integer) node.getAttribute(Attributes.NODE_ID);
    }

    public static int getTaxonID(Taxon taxon) {
        return (Integer) taxon.getAttribute(Attributes.TAXA_ID);
    }

    public static List<Taxon> getCluster(Node node) {
        return (List<Taxon>) node.getAttribute(CLUSTER);
    }
    public static int getClusterSize(Node node) {
        return (Integer) node.getAttribute(CLUSTER_SIZE);
    }

    public static boolean isIndexed(RootedTree tree) {
        if (tree.getAttribute(INDEXED) == null) {
            return false;
        }
        return tree.getAttribute(INDEXED) == (Boolean)true;
    }
}
