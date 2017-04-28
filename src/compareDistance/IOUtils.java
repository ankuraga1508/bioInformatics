package compareDistance;

import jebl.evolution.io.*;
import jebl.evolution.trees.Tree;

import java.io.*;
import java.util.List;

/**
 * Created by Alexey Markin on 02/08/2017.
 */
public class IOUtils {
    public static void nexusToNewickOrBack(String inputPath, String convertedOutputPath) throws IOException, ImportException {
        File outFile = new File(convertedOutputPath);
        PrintWriter output = new PrintWriter(outFile);

        List<Tree> trees;
        try {
            FileReader reader = new FileReader(inputPath);
            try {
                // First try nexus
                TreeImporter importer = new NexusImporter(reader);
                trees = importer.importTrees();
                TreeExporter exporter = new NewickExporter(output);
                exporter.exportTrees(trees);
                output.close();
                reader.close();
            } catch (Exception e) {
                // Now we try newick
                reader = new FileReader(inputPath);
                TreeImporter importer = new NewickImporter(reader, false);
                trees = importer.importTrees();
                TreeExporter exporter = new NexusExporter(output);
                exporter.exportTrees(trees);
                output.close();
                reader.close();
            }
        } catch (Exception e) {
            throw new ImportException(String.format("Failed converting the specified file %s\n. Note the file should be in nexus/newick format", inputPath));
        }
    }

    public static List<Tree> importTreesFromFile(String sourceFile) throws ImportException {
        List<Tree> trees;
        FileReader reader = null;
        try {
            reader = new FileReader(sourceFile);
            try {
                // First try nexus
                TreeImporter importer = new NexusImporter(reader);
                trees = importer.importTrees();
            } catch (Exception e) {
                reader.close();
                reader = new FileReader(sourceFile);
                // Now we try newick
                TreeImporter importer = new NewickImporter(reader, false);
                trees = importer.importTrees();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    // nothing
                }
            }
            throw new ImportException(String.format("Failed importing trees from the file %s\n. Note the file should be in nexus/newick format", sourceFile));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // nothing
                }
            }
        }

        return trees;
    }
}
