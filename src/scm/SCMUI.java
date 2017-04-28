package scm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import application.BrowserView;
import application.GUI;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tabdetachable.TabPaneDetacher;

public class SCMUI extends GUI {
	
	File inputFile;
	String fileOutput, scoring;
	
	public void askParameters(final Stage stage) {
		final Stage subStage = new Stage();
		subStage.setTitle("SCM");
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
		final FileChooser fileChooser = new FileChooser();
		final Button openButton = new Button("Browse");
		HBox browseBtn = new HBox(10);
		browseBtn.getChildren().add(openButton);
		grid.add(browseBtn, 3, 2);
		openButton.setMinWidth(100);
		openButton.setOnAction(new EventHandler<ActionEvent>() {
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

		Label generator = new Label("Scoring");
		collapsableGrid.add(generator, 0, 3);
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "cluster",
			        "uniquetaxa",
			        "resolution",
			        "overlap",
			        "degree"
			    );
		final ComboBox comboBox = new ComboBox(options);
		comboBox.setValue("cluster");
		setScoring("cluster");
		collapsableGrid.add(comboBox, 1 , 3);
		comboBox.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue ov, String t, String t1) {                
            	comboBox.setValue(t1);
            	setScoring(t1);
            }    
        });

		Button btn = new Button("Submit");
		btn.setMinWidth(100);
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(final ActionEvent e) {
				
				File file = getInputFile();
				ArrayList<Output> showOutput = null;
				if (file != null) {
					showOutput = runSCM(file);
					GUI.textArea.appendText(showOutput.get(0).toString());
					addInputItem(file.toString(), "SCM");
				}

				Tab tab = new Tab();
				tab.setText("Median tree " + (resultId+1));
				//tab.setText(getFilename());
				//tab.setContent(new BrowserView( "{ newick : '" + showOutput.get(0).getTree() + "' } ", numberOfTaxa(showOutput.get(0).getTree()), null));
				
				GUI.tabPane.getTabs().addAll(tab);
				TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
				SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
				selectionModel.select(tab);
				
				saveOutputInFile(getOutputFile());			
				addResultItems(showOutput, "SCM");
				
				subStage.close();
			}
		});

		mainGrid.add(gridTitlePane_1, 1, 1);
		mainGrid.add(gridTitlePane_2, 1, 2);
		mainGrid.add(hbBtn, 1, 4);

		Scene scene = new Scene(mainGrid, 600, 600);
		subStage.setScene(scene);
		subStage.show();			
	}
	
	public ArrayList<Output> runSCM(File file) {
		ArrayList<Output> showOutput = new ArrayList<Output>();
		SCM test;
		try {
			test = new SCM(file.toString(), SCM.Scoring.valueOf(getScoring()));
			showOutput.add(new Output(test.doMerge(), 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return showOutput;
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
	
	protected void setFileOutput(String fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	protected String getFileOutput(){
		return this.fileOutput;
	}
	
	protected void setScoring(String scoring) {
		this.scoring = scoring;
	}
	
	protected String getScoring(){
		return this.scoring;
	}
}
