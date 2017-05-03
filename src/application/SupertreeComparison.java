package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import compareDistance.TreeUtils;
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
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jebl.evolution.treemetrics.RobinsonsFouldMetric;
import tabdetachable.TabPaneDetacher;

@SuppressWarnings("restriction")
public class SupertreeComparison extends GUI {
	ObservableList<String> dynamicSupertreeOpt = FXCollections.observableArrayList();
	private String supertreeName, method;
	
	public void askParameters(String st){
	    for (Entry<String, ArrayList<Input>> entry : GUI.speciesTree.entrySet()) {
	    	dynamicSupertreeOpt.add(getSuperTree(entry.getKey()));
	    }
	    Collections.sort(dynamicSupertreeOpt);
	    
	    final Stage subStage = new Stage();
		subStage.setTitle("Select Supertree & method");
	    subStage.initModality(Modality.APPLICATION_MODAL);
		
        GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);

		Label inputFile = new Label("Select supertree");
		grid.add(inputFile, 0, 2);
		
		final ComboBox HcomboBox = new ComboBox(dynamicSupertreeOpt);
    	grid.add(HcomboBox, 1 , 2);
		HcomboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	HcomboBox.setValue(t1);
            	setCompareSuperTree(t1);
            }
   
        });
		
		Label method = new Label("Select method");
		grid.add(method, 0, 4);
		ObservableList<String> opt = 
			    FXCollections.observableArrayList(
			        "Robinson-Foulds",
			        "Triplet"
			    );
		final ComboBox HcomboBoxMethod = new ComboBox(opt);
    	grid.add(HcomboBoxMethod, 1 , 4);
    	HcomboBoxMethod.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {                
            	HcomboBoxMethod.setValue(t1);
            	setMethod(t1);
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
					
					if(getMethod().equals("Triplet")){
						saveTreeInNexus(st, 1);
						saveTreeInNexus(GUI.speciesTreeName.get(getCompareSuperTree()), 2);
						runTriplet();
					} else if(getMethod().equals("Robinson-Foulds")) {
						RobinsonsFouldMetric rf = new RobinsonsFouldMetric();
						compareScore = rf.getMetric(TreeUtils.getTreeFromString(st),
								TreeUtils.getTreeFromString(GUI.speciesTreeName.get(getCompareSuperTree())));					
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Information Dialog");
						alert.setHeaderText("Robinson-Foulds distance between these two trees is " + compareScore);
						alert.showAndWait();
					}
					
					Tab tab = new Tab();
					tab.setText("Comparison");
					
					final VBox vBox = new VBox(5);
					WebView webView = new CompareView().webView(st, GUI.speciesTreeName.get(getCompareSuperTree()));				
                    vBox.getChildren().setAll(webView);
                    tab.setContent(vBox);
					
					tabPane.getTabs().add(tab);
					TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
    				SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
    				selectionModel.select(tab);
					
				} catch(Exception ex) {
					subStage.close();
					
					ex.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Look, an Error Dialog");
					alert.setContentText("Ooops, there was an error! \n" + ex.toString());
					alert.showAndWait();
				} finally {
					subStage.close();
				}
			}
		});

		grid.add(hbBtn, 1, 7);

		Scene scene = new Scene(grid, 400, 300);
		subStage.setScene(scene);
		subStage.show();	
	}
	
	public void askParametersForUploadedTree(String firstTree, String secondTree){
	    for (Entry<String, ArrayList<Input>> entry : GUI.speciesTree.entrySet()) {
	    	dynamicSupertreeOpt.add(getSuperTree(entry.getKey()));
	    }
	    Collections.sort(dynamicSupertreeOpt);
	    
	    final Stage subStage = new Stage();
		subStage.setTitle("Select Supertree & method");
	    subStage.initModality(Modality.APPLICATION_MODAL);
		
        GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);

		Label inputFile = new Label("Select supertree");
		grid.add(inputFile, 0, 2);
		
		final ComboBox HcomboBox = new ComboBox(dynamicSupertreeOpt);
    	grid.add(HcomboBox, 1 , 2);
		HcomboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	HcomboBox.setValue(t1);
            }
   
        });
		HcomboBox.setDisable(true);
		
		Label method = new Label("Select method");
		grid.add(method, 0, 4);
		ObservableList<String> opt = 
			    FXCollections.observableArrayList(
			        "Robinson-Foulds",
			        "Triplet"
			    );
		final ComboBox HcomboBoxMethod = new ComboBox(opt);
    	grid.add(HcomboBoxMethod, 1 , 4);
    	HcomboBoxMethod.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {                
            	HcomboBoxMethod.setValue(t1);
            	setMethod(t1);
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
					
					if(getMethod().equals("Triplet")){
						saveTreeInNexus(firstTree, 1);
						saveTreeInNexus(secondTree, 2);
						runTriplet();
					} else if(getMethod().equals("Robinson-Foulds")) {
						RobinsonsFouldMetric rf = new RobinsonsFouldMetric();
						compareScore = rf.getMetric(TreeUtils.getTreeFromString(firstTree),
								TreeUtils.getTreeFromString(secondTree));					
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Information Dialog");
						alert.setHeaderText("Robinson-Foulds distance between these two trees is " + compareScore);
						alert.showAndWait();
					}
					
					Tab tab = new Tab();
					tab.setText("Comparison");
					
					final VBox vBox = new VBox(5);
					WebView webView = new CompareView().webView(firstTree, secondTree);					
                    vBox.getChildren().setAll(webView);
                    tab.setContent(vBox);
                    
					tabPane.getTabs().add(tab);
					TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
    				SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
    				selectionModel.select(tab);
					
				} catch(Exception ex) {
					subStage.close();
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Look, an Error Dialog");
					alert.setContentText("Ooops, there was an error! \n" + ex.toString());
					alert.showAndWait();
				} finally {
					subStage.close();
				}
			}
		});

		grid.add(hbBtn, 1, 7);

		Scene scene = new Scene(grid, 400, 300);
		subStage.setScene(scene);
		subStage.show();
	}
	
	private void runTriplet(){
		Process proc = null;
		try {
			tempCopyToLocalDisk("tripletsat.jar");
			tempCopyToLocalDisk("biojava.jar");
			tempCopyToLocalDisk("jgrapht-jdk1.5.jar");
			
			proc = Runtime.getRuntime().exec("java -jar " + System.getProperty("java.io.tmpdir") +
					"/tripletsat.jar " + System.getProperty("java.io.tmpdir") + "/input1.nex " + 
					System.getProperty("java.io.tmpdir") + "/input2.nex");
			BufferedReader stdInput = new BufferedReader(new 
            		InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
            	     InputStreamReader(proc.getErrorStream()));

        	String s, content = "";
        	while ((s = stdInput.readLine()) != null) {
        		content = content + s;
        	}
        	
        	Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(content);
			alert.showAndWait();

        	while ((s = stdError.readLine()) != null) {
        		System.out.println(s);
        	}
        	
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("Ooops, there was an error! \n" + e.toString());
			alert.showAndWait();
		}
	}
	
	private void saveTreeInNexus(String st, int i){		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("java.io.tmpdir") + "/input"+i+".nex"))) {
			String content = "#NEXUS\nBEGIN TREES;\nTREE species = " + st + "\nEND;";
			bw.write(content);
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private void setCompareSuperTree(String supertreeName) {
		this.supertreeName = supertreeName;
	}
	
	private String getCompareSuperTree() {
		return this.supertreeName;
	}
	
	private void setMethod(String method) {
		this.method = method;
	}
	
	private String getMethod() {
		return this.method;
	}
	
	private String getSuperTree(String st) {
		for (Entry<String, String> entry : GUI.speciesTreeName.entrySet()) {
	    	if (entry.getValue().equals(st)) {
	    		return entry.getKey();
	        }
	    }
		return "";
	}
	
	public String uploadSupertree(String file){
		ArrayList<String> uploadTree = openInputFile(file);

		resultId += 1;
		speciesTreeName.put("Median tree " + resultId, uploadTree.get(0));
		result.add(new Result(resultId, "name" + 0, numberOfTaxa(uploadTree.get(0)), "rooted", uploadTree.get(0), 
						getFilename(file.toString()) + "_species", "Unknown", 0, "Input "+fileInput)); 	
		return uploadTree.get(0);
	}
	
	public String getFilename(String filename) {
		String[] farr = null;
		if(System.getProperty("os.name").contains("Linux"))
			farr = filename.split("/");
		else if(System.getProperty("os.name").contains("Windows")) {
			char[] carr = filename.toCharArray();
			for(int i=0; i<carr.length; i++) {
				if(carr[i] == '\\') {
					carr[i] = '/';
				}
			}
			farr = String.valueOf(carr).split("/");
		}
		
		return farr[farr.length-1];
	}
}