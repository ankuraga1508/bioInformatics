package application;

import java.awt.FlowLayout;
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

import data.Output;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tabdetachable.TabPaneDetacher;

public class StrictConsensus extends GUI{
	String consoleOutput, objective, bound, fileOutput;
	Tab tab = new Tab();
	final Stage subStage = new Stage();
	
	@SuppressWarnings("unchecked")
	public void askParameters(final Stage stage, boolean flag, String firstTree, File compareFile) {
		subStage.setTitle("Strict Consensus Approach");
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
		
		Label objective = new Label("Objective cost-function");
		grid.add(objective, 0, 6);
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "Gene Duplication",
			        "Deep Coalescence",
			        "Robinson Foulds"
			    );
		final ComboBox comboBox = new ComboBox(options);
		comboBox.setValue("RobinsonFoulds");
		setObjective("RobinsonFoulds");
		grid.add(comboBox, 1 , 6);
		comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	comboBox.setValue(t1);
            	setObjective(t1);
            }    
        });
		
		Label bound = new Label("Out-degree Bound");
		collapsableGrid.add(bound, 0, 1);
		final TextField boundTextField = new TextField();
		collapsableGrid.add(boundTextField, 1, 1);
		boundTextField.setText("9");

		Button btn = new Button("Submit");
		btn.setMinWidth(100);
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					setBound(boundTextField.getText());
					
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
							showOutput = runStrictConsensus(file);
							addResultItems(showOutput, "Strict Consensus");
						}
					 else{
						File tempFile = new File(System.getProperty("java.io.tmpdir") + "temp.nwk");
						ArrayList<Input> geneTrees = GUI.speciesTree.get(firstTree);
							try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
								for(int i=0; i<geneTrees.size(); i++){
									bw.write(geneTrees.get(i).getTree()); 
								}
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						showOutput = runStrictConsensus(tempFile);
						if(compareFile != null)
							addResultItems(showOutput, "Strict Consensus", compareFile);
						else
							addResultItems(showOutput, "Strict Consensus", tempFile);
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
					System.out.println(ex.toString());
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
	
	public void addInputItem(String filename, String algo, ArrayList<String> refinedTrees){
		String[] farr = filename.split("/");
		setNumberOfInput(refinedTrees.size());
		setPrevInputId(inputId);
		
		for(int i=0 ;i<refinedTrees.size(); i++) {
			geneTrees.add(new Input(getFilename() + "_" + (i+1), refinedTrees.get(i)));
			input.add(new Input(inputId, getFilename() + "_refined_" + (i+1), refinedTrees.get(i), i , algo, numberOfTaxa(refinedTrees.get(i)),"Input " + fileInput));
			inputId += 1;
		}
	}
	
    public ArrayList<Output> runStrictConsensus(File file) {
    	ArrayList<Output> showOutput = new ArrayList<Output>();
    	ArrayList<String> refinedTrees = new ArrayList<String>();
    	String tree;
    	String fileOutput = "", consoleOutput = "";
    	int bestScore = 0;

    	try {
    		Process proc = null;
    		ProcessBuilder pb = null; 
    		tempCopyToLocalDisk("StrictConsensusApproach.jar");
    		 
    		proc = Runtime.getRuntime().exec("java -Dpython.console.encoding=UTF-8 -jar " +
    				System.getProperty("java.io.tmpdir") + "StrictConsensusApproach.jar " + file + " Newick " + getObjective() + " " + getBound());
            
    		BufferedReader stdInput = new BufferedReader(new 
            		InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
            	     InputStreamReader(proc.getErrorStream()));

        	String s = null;
        	while ((s = stdInput.readLine()) != null) {
        		consoleOutput = consoleOutput + s + "\n";
        		
        		if(s.contains("parsimony score:")) {
        			bestScore= Integer.parseInt(s.replaceAll("[^0-9]", "").replaceAll(" ", ""));
        		}
        		
        		if(s.contains("[Species tree]")) {
        			fileOutput = fileOutput + s + "\n";
        			tree = stdInput.readLine();
        			showOutput.add(new Output(convertToNewickWIthDistance(tree),bestScore));
        			consoleOutput = consoleOutput + tree + "\n";
        			fileOutput = fileOutput + tree + "\n";
        		}
        		
        		if(s.contains("[Refined Gene Trees]")) {
        			fileOutput = fileOutput + s + "\n";
        			while ((s = stdInput.readLine()) != null && s != "\n"){
        				refinedTrees.add(s);
            			consoleOutput = consoleOutput + s + "\n";
            			fileOutput = fileOutput + s + "\n";
        			}
        		}
        	}

        	System.out.println("Here is the standard error of the command (if any):\n");
        	while ((s = stdError.readLine()) != null) {
        		textArea.appendText(s);
        		System.out.println(s);
        		subStage.close();
        	}
        	
        	System.out.println(consoleOutput);
        	
        	setConsoleOutput(consoleOutput);
        	setFileOutput(fileOutput);
        	addInputItem(file.toString(), "Strict Consensus", refinedTrees);
        } catch (Exception ex) {
            ex.printStackTrace();
        	textArea.appendText(ex.toString());  
            System.out.println(ex);
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
			}
		}
	}
	
	protected void setObjective(String objective) {
		this.objective = objective;
	}
	
	protected String getObjective(){
		return this.objective;
	}
	
	protected void setFileOutput(String fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	protected String getFileOutput(){
		return this.fileOutput;
	}
	
	protected void setBound(String bound) {
		this.bound = bound;
	}
	
	protected String getBound(){
		return this.bound;
	}
}
