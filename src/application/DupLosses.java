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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tabdetachable.TabPaneDetacher;

@SuppressWarnings("restriction")
public class DupLosses extends GUI{
	File constraintFile;
	String consoleOutput, generator, maxTrees, heuristic, fileOutput;
	Tab tab = new Tab();
	
	public void compareAgainstGeneTree(File file, String speciesTree) {
        ArrayList<String> treeList = new ArrayList<String>();
		treeList = openInputFile(file.toString());
		
		final Stage subStage = new Stage();
		subStage.setTitle("Duplication with Losses");
		
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
					tab.setContent(new CostView(speciesTree+"#"+geneTree+"%", compareScore, "duplication plus losses"));
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
		subStage.setTitle("Duplication with Losses");
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
				inputTextField.setText(file.toString());
				setInputFile(file);				
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
	            if(file !=null){
	            	outputFileTextField.setText(file.toString());
					setOutputFile(file);	
				}
			}
		});
		
		Label generator = new Label("Generator");
		collapsableGrid.add(generator, 0, 2);
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "Don't generate",
			        "Leaf Adding Heuristic",
			        "Random Tree"
			    );
		final ComboBox comboBox = new ComboBox(options);
		comboBox.setValue("Leaf Adding Heuristic");
		collapsableGrid.add(comboBox, 1 , 2);
		comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	comboBox.setValue(t1);
            	setGenerator(t1);
            }    
        });
		
		Label constraints = new Label("Constraints File");
		collapsableGrid.add(constraints, 0, 4);
		final TextField constraintsTextField = new TextField();
		collapsableGrid.add(constraintsTextField, 1, 4);
		final FileChooser constraintfileChooser = new FileChooser();
		final Button constraintButton = new Button("Browse");
		HBox constraintbrowseBtn = new HBox(10);
		constraintbrowseBtn.getChildren().add(constraintButton);
		collapsableGrid.add(constraintbrowseBtn, 3, 4);
		constraintButton.setMinWidth(100);
		constraintButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				configureFileChooser(constraintfileChooser);
				File file = constraintfileChooser.showOpenDialog(stage);
				if(file !=null){
					constraintsTextField.setText(file.toString());
					setConstraintFile(file);	
				}			
			}
		});
		
		
		Label heuristic = new Label("Heuristic");
		collapsableGrid.add(heuristic, 0, 6);
		ObservableList<String> opt = 
			    FXCollections.observableArrayList(
			        "Randomized Hill Climbing",
			        "Partial Queue Based"
			        //,"Queue Based"
			    );
		final ComboBox HcomboBox = new ComboBox(opt);
		HcomboBox.setValue("Randomized Hill Climbing");
		collapsableGrid.add(HcomboBox, 1 , 6);
		HcomboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	HcomboBox.setValue(t1);
            	setHeuristic(t1);
            }    
        });
		

		Label maxTree = new Label("Max. number of species tree");
		//collapsableGrid.add(maxTree, 0, 8);		
		final ToggleGroup group = new ToggleGroup();
		RadioButton rb1 = new RadioButton("Default");
		rb1.setToggleGroup(group);
		rb1.setSelected(true);
		RadioButton rb2 = new RadioButton("All");
		rb2.setToggleGroup(group);	
		HBox hb = new HBox(10);
		hb.getChildren().add(rb1);
		hb.getChildren().add(rb2);
		//collapsableGrid.add(hb, 1, 8);
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
		    public void changed(ObservableValue<? extends Toggle> ov,
		        Toggle old_toggle, Toggle new_toggle) {
		            if (group.getSelectedToggle() != null) {
		            	RadioButton chk = (RadioButton)new_toggle.getToggleGroup().getSelectedToggle();
		            	setMaxTrees(chk.getText());
		            }                
		        }
		});
		
		
		Label seed = new Label("Seed");
		collapsableGrid.add(seed, 0, 8);
		final TextField seedTextField = new TextField();
		collapsableGrid.add(seedTextField, 1, 8);
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
						showOutput = runLosses(file);    	
						addInputItem(file.toString(), "Losses");
						addResultItems(showOutput, "Losses");
					} else {
						File tempFile = new File(System.getProperty("java.io.tmpdir") + "temp.nwk");
						ArrayList<Input> geneTrees = GUI.speciesTree.get(firstTree);
							try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
								for(int i=0; i<geneTrees.size(); i++){
									bw.write(geneTrees.get(i).getTree()+"\n"); 
								}
							} catch (IOException ex) {
								textArea.appendText(ex.toString());
								ex.printStackTrace();
							}
						showOutput = runLosses(tempFile);
						if(compareFile != null)
							addResultItems(showOutput, "Losses", compareFile);
						else
							addResultItems(showOutput, "Losses", tempFile);
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
					textArea.appendText(ex.toString());
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
	
    public ArrayList<Output> runLosses(File file) {
    	ArrayList<Output> showOutput = new ArrayList<Output>();
    	String tree;
    	String fileOutput = "", consoleOutput = "";
    	int bestScore = 0;

    	try {
    		Process proc = null;
    		ProcessBuilder pb = null; 
			
    		if(this.constraintFile != null) {
    			if(System.getProperty("os.name").toLowerCase().contains("windows")) {
        			tempCopyToLocalDisk("lossesWin.exe");
        			pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "lossesWin.exe"
        					,"-i",
        					file.toString(),
        					"-g",
        					getGeneratorNumber(getGenerator()),
        					"--constraints",
        					getConstraintFile().toString(),
        					"--heuristic",
        					getHeuristicNumber(getHeuristic()),
        					"--seed",
        					getSeed());
        		} else if(System.getProperty("os.name").toLowerCase().contains("linux")) {
        			tempCopyToLocalDisk("lossesLin");
        			pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "lossesLin"
        					,"-i",
        					file.toString(),
        					"-g",
        					getGeneratorNumber(getGenerator()),
        					"--constraints",
        					getConstraintFile().toString(),
        					"--heuristic",
        					getHeuristicNumber(getHeuristic()),
        					"--seed",
        					getSeed());
        		} else if(System.getProperty("os.name").toLowerCase().contains("mac")) {
        			tempCopyToLocalDisk("lossesMac");
        			pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "lossesMac"
        					,"-i",
        					file.toString(),
        					"-g",
        					getGeneratorNumber(getGenerator()),
        					"--constraints",
        					getConstraintFile().toString(),
        					"--heuristic",
        					getHeuristicNumber(getHeuristic()),
        					"--seed",
        					getSeed());
        		}
    		} else {
    			if(System.getProperty("os.name").toLowerCase().contains("windows")) {
    				tempCopyToLocalDisk("lossesWin.exe");
    				pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "lossesWin.exe"
        					,"-i",
        					file.toString(),
        					"-g",
        					getGeneratorNumber(getGenerator()),
        					"--heuristic",
        					getHeuristicNumber(getHeuristic()),
        					"--seed",
        					getSeed());
    			} else if(System.getProperty("os.name").toLowerCase().contains("linux")) {
    				tempCopyToLocalDisk("lossesLin");
    				pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "lossesLin"
        					,"-i",
        					file.toString(),
        					"-g",
        					getGeneratorNumber(getGenerator()),
        					"--heuristic",
        					getHeuristicNumber(getHeuristic()),
        					"--seed",
        					getSeed());
    			} else if(System.getProperty("os.name").toLowerCase().contains("mac")) {
    				tempCopyToLocalDisk("lossesMac");
    				pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "lossesMac"
        					,"-i",
        					file.toString(),
        					"-g",
        					getGeneratorNumber(getGenerator()),
        					"--heuristic",
        					getHeuristicNumber(getHeuristic()),
        					"--seed",
        					getSeed());
    			}
        		
    		}
    		
    		proc = pb.start();
    		
    		BufferedReader stdInput = new BufferedReader(new 
            		InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
            	     InputStreamReader(proc.getErrorStream()));

        	String s = null;
        	while ((s = stdInput.readLine()) != null) {
        		consoleOutput = consoleOutput + s + "\n";
        		if(s.contains("Reconciliation cost: "))
        			bestScore= Integer.parseInt(s.replaceAll("[^0-9]", ""));
        		if(s.contains("[Species tree ")) {
        			s = s + "\n" + stdInput.readLine() + "\n";
        			s += stdInput.readLine() + "\n";
        			tree = stdInput.readLine();
        			s += tree + "\n";
        			showOutput.add(new Output(convertToNewickWIthDistance(tree), bestScore));
        			consoleOutput = consoleOutput + s + "\n";
        			fileOutput = fileOutput + s + "\n";
        		}
        	}

        	while ((s = stdError.readLine()) != null) {
        		textArea.appendText(s);
        	}
        	
        	setConsoleOutput(consoleOutput);
        	setFileOutput(fileOutput);
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
			    out.println(getFileOutput());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				textArea.appendText(e.toString());
			}
		}
	}
	
	private void setConstraintFile(File constraintFile) {
		this.constraintFile = constraintFile;
	}
	
	private String getConstraintFile(){
		if(this.constraintFile != null)
			return this.constraintFile.toString();
		else
			return "";
	}
	
	protected void setGenerator(String generator) {
		this.generator = generator;
	}
	
	protected String getGenerator(){
		return this.generator;
	}
	
	protected void setMaxTrees(String maxTrees) {
		this.maxTrees = maxTrees;
	}
	
	protected String getMaxTrees(){
		return this.maxTrees;
	}
	
	protected void setHeuristic(String heuristic) {
		this.heuristic = heuristic;
	}
	
	protected String getHeuristic(){
		return this.heuristic;
	}
	
	protected void setFileOutput(String fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	protected String getFileOutput(){
		return this.fileOutput;
	}
	
	private String getHeuristicNumber(String str) {
		if(str == null || str == "")
			return "1";
		
		if(str == "Queue Based")
			return "3";
		else if(str == "Partial Queue Based")
			return "2";
		else
			return "1";
	}

	private String getGeneratorNumber(String str) {
		if(str == null || str == "")
			return "1";
		
		if(str == "Don't generate")
			return "0";
		else if(str == "Random Tree")
			return "2";
		else
			return "1";
	}
}
