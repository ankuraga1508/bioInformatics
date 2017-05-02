package application;

import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.Border;

import data.Output;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tabdetachable.TabPaneDetacher;

@SuppressWarnings("restriction")
public class PDD extends GUI{
	File outputFile;
	String consoleOutput = "", numberOfTrees = "";
	Tab tab = new Tab();
	
	public void askParameters(final Stage stage, boolean flag, String firstTree, File compareFile) {
		final Stage subStage = new Stage();
		subStage.setTitle("Path Difference");
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
				if(file != null){
					inputTextField.setText(file.toString());
					setInputFile(file);
				}
			}
		});
		
		Label outputFile = new Label("Output File");
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
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Save Folder");
				File selectedDirectory = chooser.showDialog(stage);
				if(selectedDirectory != null){
					outputFileTextField.setText(selectedDirectory.toString());
					setOutputFile(selectedDirectory);
				}
			}
		});

		Label seed = new Label("Number of Taxa");
		collapsableGrid.add(seed, 0, 3);

		final TextField numberOfTreesTextField = new TextField();
		collapsableGrid.add(numberOfTreesTextField, 1, 3);
		numberOfTreesTextField.setText("1");

		Button btn = new Button("Submit");
		btn.setMinWidth(100);
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				
				try {
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
				    
					setNumberOfTrees(numberOfTreesTextField.getText());
					File file = getInputFile();
					ArrayList<Output> showOutput = new ArrayList<Output>();
					
					if (file != null) {
						fileInput++;
						Main.filter.add("Input " + fileInput);
						showOutput = runPDD(file);    	
						addInputItem(file.toString(), "PDD");
						addResultItems(showOutput, "PDD");
					} else {
						File tempFile = new File(System.getProperty("java.io.tmpdir") + "temp.nwk");
						ArrayList<Input> geneTrees = GUI.speciesTree.get(firstTree);
							try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
								for(int i=0; i<geneTrees.size(); i++){
									bw.write(geneTrees.get(i).getTree()+"\n"); 
								}
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						showOutput = runPDD(tempFile);
						if(compareFile != null)
							addResultItems(showOutput, "PDD", compareFile);
						else
							addResultItems(showOutput, "PDD", tempFile);
					}

					GUI.textArea.appendText(getConsoleOutput());
					f.setVisible(false);

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
				} catch(Exception ex) {
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
	
    public ArrayList<Output> runPDD(File file) {
    	String tree = "";
    	String consoleOutput = "";
    	ArrayList<Output> showOutput = new ArrayList<Output>();
    	int bestScore = 0;
    	
    	try {
    		Process proc = null;
    		ProcessBuilder pb = null; 
    		tempCopyToLocalDisk("pdd-median-0.1.jar");
    		
    		proc = Runtime.getRuntime().exec("java -jar " + System.getProperty("java.io.tmpdir") + "pdd-median-0.1.jar "
    				+ file + " " + getOutputFile().toString() + " " + 10 + " " + getNumberOfTrees());
            
    		BufferedReader stdInput = new BufferedReader(new 
            		InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
            	     InputStreamReader(proc.getErrorStream()));

            String errorMsg = "";
        	boolean error = false;
        	String s = null;
        	while ((s = stdInput.readLine()) != null) {
        		System.out.println(s);
        		consoleOutput = consoleOutput + s + "\n";
        		if(s.equals("----------------")) {
        			stdInput.readLine();
        			tree = stdInput.readLine().replaceAll(" ","");
        			consoleOutput = consoleOutput + tree + "\n";
        		}
        		
        		if(s.contains("Final tree PD distance is "))
        			bestScore= (int) Float.parseFloat(s.replaceAll("Final tree PD distance is ", ""));
        		
        		if(s.contains("Error message")) {
        			errorMsg = errorMsg + s;
        			error = true;
        		}
        	}

        	
        	while ((s = stdError.readLine()) != null) {
        		errorMsg = errorMsg + s;
        		System.out.println(s);
        		textArea.appendText(s);
        		error = true;
        	}
        	
        	if(error) {
        		Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Dialog");
    			alert.setHeaderText("Error Dialog");
    			alert.setContentText("\n" + errorMsg);
    			alert.showAndWait();
        	}
        	
        	setConsoleOutput(consoleOutput);
        } catch (Exception ex) {
            ex.printStackTrace();
            textArea.appendText(ex.toString());
        }

    	showOutput.add(new Output(convertToNewickWIthDistance(tree), bestScore));
    	return showOutput;
    }

	public void configureFileChooserOutput(final FileChooser fileChooser){                           
        fileChooser.setTitle("Select Output File");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
    }
	
	protected void setConsoleOutput(String consoleOutput) {
		this.consoleOutput = consoleOutput;
	}
	
	protected String getConsoleOutput() {
		return this.consoleOutput;
	}

	private void setNumberOfTrees(String numberOfTrees) {
		this.numberOfTrees = numberOfTrees;
	}
	
	private String getNumberOfTrees(){
		return this.numberOfTrees;
	}
}

