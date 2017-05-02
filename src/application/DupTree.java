package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import compareDistance.GTPCosts;
import compareDistance.PhylogeneticTree;
import compareDistance.TreeUtils;
import data.Output;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tabdetachable.TabPaneDetacher;

@SuppressWarnings("restriction")
public class DupTree extends GUI{
	String consoleOutput;
	Tab tab = new Tab();
	   
    public void compareAgainstGeneTree(File file, String speciesTree) {
        ArrayList<String> treeList = new ArrayList<String>();
		treeList = openInputFile(file.toString());
		
		final Stage subStage = new Stage();
		subStage.setTitle("Duplication");
		
        GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);

		Label generator = new Label("Select gene tree");
		grid.add(generator, 0, 4);
		
		String[] treeArray = new String[treeList.size()];
		treeArray = treeList.toArray(treeArray);
    	    
		ArrayList<Input> geneTrees = GUI.speciesTree.get(speciesTree);
		for(Input gene: geneTrees) {
			dynamicOpt.add(gene.getInput());
		}
		final ComboBox HcomboBox = new ComboBox(dynamicOpt);
    	grid.add(HcomboBox, 3 , 4);
		HcomboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	HcomboBox.setValue(t1);
            	setCompareGeneTree(t1);
            }    
        });

		Button btn = new Button("Submit");
		btn.setMinWidth(100);
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					subStage.close();
					
					String geneTree = null;
					for(Input gene: geneTrees) {
						if(gene.getInput().equals(getCompareGeneTree()))
							geneTree = gene.getTree();
					}
					compareScore = GTPCosts.getGeneDupCost(new PhylogeneticTree(TreeUtils.getTreeFromString(geneTree)),
							new PhylogeneticTree(TreeUtils.getTreeFromString(speciesTree)));

					Tab tab = new Tab();
					tab.setText("Compare Tree");
					tab.setContent(new CostView(speciesTree+"#"+geneTree+"%", compareScore, "duplication"));
					tabPane.getTabs().add(tab);
					TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
    				SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
    				selectionModel.select(tab);
    				
//					Alert alert = new Alert(AlertType.INFORMATION);
//					alert.setTitle("Information Dialog");
//					alert.setHeaderText("Cost b/w species tree and gene tree is " + compareScore);
//					alert.showAndWait();
					
				} catch(Exception ex) {
					subStage.close();
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Look, an Error Dialog");
					alert.setContentText("Ooops, there was an error! \n" + ex.toString());
					alert.showAndWait();
				} finally {
				}
			}
		});
		
		grid.add(hbBtn, 3, 6);

		Scene scene = new Scene(grid, 300, 300);
		subStage.setScene(scene);
		subStage.show();	
    }
    
	public void askParameters(final Stage stage, boolean flag, String firstTree, File compareFile) {
		final Stage subStage = new Stage();
		subStage.setTitle("Gene Duplications");
		subStage.initOwner(stage);
	    subStage.initModality(Modality.WINDOW_MODAL);
		
        GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		
		TitledPane gridTitlePane_1 = new TitledPane();       
		gridTitlePane_1.setText("Input Parameters");
		gridTitlePane_1.setContent(grid);
		gridTitlePane_1.setExpanded(true); 
		
		GridPane collapsableGrid = new GridPane();
		collapsableGrid.setAlignment(Pos.CENTER);
		collapsableGrid.setHgap(10);
		collapsableGrid.setVgap(10);
		
		TitledPane gridTitlePane_2 = new TitledPane();       
		gridTitlePane_2.setText("Default Parameters");
		gridTitlePane_2.setContent(collapsableGrid);
		gridTitlePane_2.setExpanded(false); 
		gridTitlePane_2.setMinWidth(550);
		
		GridPane mainGrid = new GridPane();
		mainGrid.setAlignment(Pos.CENTER);
		mainGrid.setHgap(10);
		mainGrid.setVgap(10);

		Label inputFile = new Label("Input gene tree(s)");
		grid.add(inputFile, 0, 2);

		final TextField inputTextField = new TextField();
		grid.add(inputTextField, 1, 2);
		if(flag) {
			if(compareFile != null)
				inputTextField.setText(compareFile.toString());
			inputTextField.setDisable(true);
		}

		final FileChooser fileChooser = new FileChooser();
		final Button openButton = new Button("Browse");
		HBox browseBtn = new HBox(10);
		browseBtn.getChildren().add(openButton);
		grid.add(browseBtn, 3, 2);
		openButton.setMinWidth(100);
		if(flag)
			openButton.setDisable(true);

		openButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				configureFileChooser(fileChooser);
				File file = fileChooser.showOpenDialog(stage);
				if(file != null) {
					inputTextField.setText(file.toString());
					setInputFile(file);	
				}			
			}
		});
		
		Label outputFile = new Label("Output File (Optional)");
		grid.add(outputFile, 0, 4);
		final TextField outputFileTextField = new TextField();
		grid.add(outputFileTextField, 1, 4);
		final Button outputButton = new Button("Browse");
		HBox outputBrowseButton = new HBox(10);
		outputBrowseButton.getChildren().add(outputButton);
		grid.add(outputBrowseButton, 3, 4);
		outputButton.setMinWidth(100);

		outputButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				FileChooser fileChooser = new FileChooser();
	            fileChooser.setTitle("Save");
	            File file = fileChooser.showSaveDialog(stage);
	            if(file != null) {
	            	outputFileTextField.setText(file.toString());
					setOutputFile(file);	
				}
				
			}
		});

		Label seed = new Label("Seed");
		collapsableGrid.add(seed, 0, 3);

		final TextField seedTextField = new TextField();
		collapsableGrid.add(seedTextField, 1, 3);
		seedTextField.setText("10");

		Button btn = new Button("Submit");
		btn.setMinWidth(100);
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					setSeed(seedTextField.getText());
					
					subStage.hide();
					subStage.close();
					
					final JProgressBar progressBar = new JProgressBar();
				    progressBar.setIndeterminate(false);
				    progressBar.setStringPainted(true);
				    progressBar.setString("Please wait...");
				    progressBar.setBounds(70, 60, 150, 40);
				    
				    JFrame f = new JFrame();
				    f.setSize(300, 200);
				    f.setLayout(null);
				    f.add(progressBar);			    
				    f.setResizable(false);
				    f.setLocationRelativeTo(null);
				    f.setVisible(true);
				    
					File file = getInputFile();
					ArrayList<Output> showOutput = new ArrayList<Output>();
					if (file != null) {
						fileInput++;
						Main.filter.add("Input " + fileInput);
						showOutput = runDupTree(file);   	
						addInputItem(file.toString(), "DupTree");
						addResultItems(showOutput, "Gene Duplication");
					} else {
						File tempFile = new File(System.getProperty("java.io.tmpdir") + "temp.nwk");
						ArrayList<Input> geneTrees = GUI.speciesTree.get(firstTree);
							try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
								for(int i=0; i<geneTrees.size(); i++){
									bw.write(geneTrees.get(i).getTree()+"\n"); 
								}
							} catch (IOException ex) {
								ex.printStackTrace();
								textArea.setText(ex.toString());
							}
						showOutput = runDupTree(tempFile);
						if(compareFile != null)
							addResultItems(showOutput, "Gene Duplication", compareFile);
						else
							addResultItems(showOutput, "Gene Duplication", tempFile);
					} 
					f.setVisible(false);
					
					GUI.textArea.appendText(getConsoleOutput()); 
					
					tab.setText("Median tree " + (resultId));
					tree = showOutput.get(0).getTree();
					
					final VBox vBox = new VBox(5);
					WebView webView = null;
					if(!flag)
						webView = new BrowserView().webView(tree, file);
					else
						webView = new CompareView().webView(firstTree, tree);
					
                    vBox.getChildren().setAll(webView);
                    tab.setContent(vBox); 
					
                    GUI.tabPane.getTabs().add(tab);
					TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
					SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
					selectionModel.select(tab);
					subStage.close();		
					saveOutputInFile(getOutputFile());
				} catch(Exception ex) {
					textArea.setText(ex.toString());
				} finally {
					subStage.close();
				}
			}
		});

		mainGrid.add(gridTitlePane_1, 1, 1);
		mainGrid.add(gridTitlePane_2, 1, 2);
		mainGrid.add(hbBtn, 1, 4);

		Scene scene = new Scene(mainGrid, 600, 600);
		subStage.setScene(scene);
		subStage.show();			
	}
	
    public ArrayList<Output> runDupTree(File file) {
    	String tree = "";
    	String consoleOutput = "";
    	ArrayList<Output> showOutput = new ArrayList<Output>();
    	int bestScore = 0;
    	
    	try {
    		Process proc = null;
    		ProcessBuilder pb = null;
    		
    		if(System.getProperty("os.name").toLowerCase().contains("linux")) {
    			tempCopyToLocalDisk("duptree2.linux64");
    			pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "duptree2.linux64"
    					,"-i",""+file,"--seed",getSeed());
    		}
    		else if(System.getProperty("os.name").toLowerCase().contains("windows")) {
    			tempCopyToLocalDisk("duptree2.exe");
    			pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "duptree2.exe"
    					,"-i",""+file,"--seed",getSeed());
    		}

    		else if(System.getProperty("os.name").toLowerCase().contains("mac")) {
    			tempCopyToLocalDisk("duptree2.macosx");
    			pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "duptree2.macosx"
    					,"-i",""+file,"--seed",getSeed());
    		}
    		
    		proc = pb.start();
    		
    		BufferedReader stdInput = new BufferedReader(new 
            		InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
            	     InputStreamReader(proc.getErrorStream()));

        	String s = null;
        	while ((s = stdInput.readLine()) != null) {
        		consoleOutput = consoleOutput + s + "\n";
        		if(s.contains("final duplications:"))
        			bestScore= Integer.parseInt(s.replaceAll("[^0-9]", ""));
        		if(s.equals("[ species tree ]")) {
        			while(!(s = stdInput.readLine()).contains("total elapsed time")) {
        				tree = tree + s + "";
        				consoleOutput = consoleOutput + s + "\n";
        			}
        			showOutput.add(new Output(convertToNewickWIthDistance(tree), bestScore));
        		}
        	}

        	while ((s = stdError.readLine()) != null) {
        		textArea.appendText(s);
        	}
        	
        	setConsoleOutput(consoleOutput);
        } catch (Exception ex) {
            ex.printStackTrace();
            textArea.appendText(ex.toString());
        }
    	return showOutput;
    }

	public void configureFileChooserOutput(final FileChooser fileChooser){                           
        fileChooser.setTitle("Select Output File");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
    }
	
	private void saveOutputInFile(String outputFile) {
		if(outputFile != null && outputFile != "") {
			try(PrintWriter out = new PrintWriter(outputFile.toString())){
			    out.println(getConsoleOutput());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				textArea.setText(e.toString());
			}
		}
	}
}
