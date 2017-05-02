package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;
import compareDistance.PhylogeneticTree;
import compareDistance.TreeUtils;
import tabdetachable.TabPaneDetacher;

@SuppressWarnings({ "unused", "restriction" })
public class JSCall extends GUI{
	static File file;
	
	public void onChangeDropDown(String value, String tree){
		Tab tab = new Tab();
		tab.setText("New tab");
		GUI.tabPane.getTabs().add(tab);
		TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
	}
	
	public void compareWith(String algo, String speciesTree) {
		if(algo.equals("RFS")) {
			GUI rfs = new GUI();
        	rfs.compareAgainstGeneTree(file, speciesTree);
		} else if(algo.equals("Duplication")) {
			DupTree dup = new DupTree();
        	dup.compareAgainstGeneTree(file, speciesTree);
		} else if(algo.equals("Deep Coalescence")) {
			DeepCoalescence dc = new DeepCoalescence();
        	dc.compareAgainstGeneTree(file, speciesTree);
		} else if(algo.equals("Duplication with Losses")) {
			DupLosses losses = new DupLosses();
			losses.compareAgainstGeneTree(file, speciesTree);
		}	
	}
	
	public void compareAgainst(String algo, String firstTree) {
		final Stage subStage = new Stage();
		if(algo.equals("RFS")) {
			GUI rfs = new GUI();
        	rfs.askParameters(subStage, true, firstTree, file);
		} else if(algo.equals("DupTree v2")) {
			DupTree duptree = new DupTree();
			duptree.askParameters(subStage, true, firstTree, file);
		} else if(algo.equals("Deep Coalescence")) {
			DeepCoalescence dc = new DeepCoalescence();
        	dc.askParameters(subStage, true, firstTree, file);
		} else if(algo.equals("Duplication with Losses")) {
			DupLosses losses = new DupLosses();
			losses.askParameters(subStage, true, firstTree, file);
		} else if(algo.equals("PDD")) {
			PDD pdd = new PDD();
        	pdd.askParameters(subStage, true, firstTree, file);
		} else if(algo.equals("Strict Consensus")) {
			StrictConsensus sc = new StrictConsensus();
        	sc.askParameters(subStage, true, firstTree, file);
		} else if(algo.equals("select")) {
			SupertreeComparison comparison = new SupertreeComparison();
			comparison.askParameters(firstTree);
		} else if(algo.equals("upload")){
			SupertreeComparison comparison = new SupertreeComparison();
			final FileChooser fileChooser = new FileChooser();
			configureFileChooser(fileChooser);
			final Stage stage = new Stage();
			File file = fileChooser.showOpenDialog(stage);
			String secondTree = comparison.uploadSupertree(file.toString());
			comparison.askParametersForUploadedTree(firstTree, secondTree);
		}
	}
	
	public void callMultipleTreeView(String speciesTree){
		System.out.println(" SUN "  + speciesTree);
		String superString = speciesTree + "#";
		Tab tab = new Tab();
		tab.setText("Species & Gene Trees");
		ArrayList<Input> geneTrees = GUI.speciesTree.get(speciesTree);
		for(Input gene: geneTrees) {
			superString = superString + gene.getTree() + "#";
		}
		
		final VBox vBox = new VBox(5);
		WebView webView = new BrowserWithMultipleTrees().webView(superString);
		vBox.getChildren().setAll(webView);
        tab.setContent(vBox);
        
		tabPane.getTabs().add(tab);
		TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
		SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
		selectionModel.select(tab);
	}
	
	public void save(String image64){
		System.out.println("RRRRRRRRR "+ image64);
		Stage stage = new Stage();
		final FileChooser fileChooser = new FileChooser();
		configureFileChooser(fileChooser);
		 File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			image64 = image64.split(",")[1];
	    	byte[] data = Base64.decodeBase64(image64);
	    	try (OutputStream stream = new FileOutputStream(file)) {
	    	    stream.write(data);
	    	} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
	}
	
	public void configureFileChooser(final FileChooser fileChooser){                           
        fileChooser.setTitle("Save tree to a folder");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
    }
	
	public void printString(String s){
		System.out.println(s);
	}
	
	public void setFile(File file){
		this.file = file;	
	}
}

