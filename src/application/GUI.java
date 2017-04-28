package application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.Border;

import org.apache.commons.io.FileUtils;

import compareDistance.GTPCosts;
import compareDistance.PhylogeneticTree;
import compareDistance.TreeUtils;

//import Trees.Tree;
//import Trees.TreeException;
import data.Output;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NewickImporter;
import jebl.evolution.treemetrics.RobinsonsFouldMetric;
import jebl.evolution.trees.Tree;
import scm.SCMUI;
import tabdetachable.TabPaneDetacher;

public class GUI {
    static String tree;
	String generator, compareGeneTree;
	String seed;
    public static TextArea textArea = new TextArea();
    public static TabPane tabPane = new TabPane();
    static ObservableList<Input> input = FXCollections.observableArrayList();  
    static ObservableList<Result> result = FXCollections.observableArrayList();
    private File inputFile;
	protected File outputFile;
    private String consoleOutput;
    static ObservableList<String> options = 
		    FXCollections.observableArrayList(
		        "Tree",
		        "Circular"
		    );
    static int inputId = 1;
    public static int resultId = 0;
    static int prevInputId, numberOfInput = 0;
	Tab tab = new Tab();
	static int fileInput = 0;
	ObservableList<String> dynamicOpt = FXCollections.observableArrayList();
    double compareScore = 0;
    public static Map<String, ArrayList<Input>> speciesTree = new HashMap<String, ArrayList<Input>>();
    ArrayList<String> treeList = new ArrayList<String>();
    public ArrayList<Input> geneTrees = new ArrayList<Input>();
    public static Map<String, String> speciesTreeName = new HashMap<String, String>();
    
    public VBox addVBox(final Stage stage) {
		
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("Supertree Methods");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        Hyperlink links[] = new Hyperlink[] {
        	new Hyperlink("Strict Consensus"),
            new Hyperlink("Gene Duplication"),
            new Hyperlink("Deep Coalescence"),
            new Hyperlink("Duplication-Loss"),
            new Hyperlink("Robinson-Foulds"),
            new Hyperlink("Path Difference")};

        for (int i=0; i<links.length; i++) {
            VBox.setMargin(links[i], new Insets(0, 0, 0, 8));
            vbox.getChildren().add(links[i]);
            
            if(links[i].getText().equals("Robinson-Foulds")) {
                links[i].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        askParameters(stage, false, "", null);
                    }
                });
            } else if(links[i].getText().equals("Gene Duplication")) {
            	links[i].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                    	DupTree duptree = new DupTree();
                    	duptree.askParameters(stage, false, "", null);
                    }
                });
            } else if(links[i].getText().equals("Path Difference")) {
            	links[i].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                    	PDD pdd = new PDD();
                    	pdd.askParameters(stage, false, "", null);
                    }
                });
            } else if(links[i].getText().equals("Deep Coalescence")) {
            	links[i].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                    	DeepCoalescence deep = new DeepCoalescence();
                    	deep.askParameters(stage, false, "", null);
                    }
                });
            } else if(links[i].getText().equals("Duplication-Loss")) {
            	links[i].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                    	DupLosses losses = new DupLosses();
                    	losses.askParameters(stage, false, "", null);
                    }
                });
            } else if(links[i].getText().equals("Strict Consensus")) {
            	links[i].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                    	StrictConsensus sc = new StrictConsensus();
                    	sc.askParameters(stage, false, "", null);
                    }
                });
            }
        }
        
        final Separator separator1 = new Separator();
        vbox.getChildren().add(2, separator1);
        final Separator separator2 = new Separator();
        vbox.getChildren().add(7, separator2);
        
        return vbox;
    }
    
    public void compareAgainstGeneTree(File file, String speciesTree) {
        ArrayList<String> treeList = new ArrayList<String>();
		treeList = openInputFile(file.toString());
		
		final Stage subStage = new Stage();
		subStage.setTitle("Robinson Foulds");
		
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
					RobinsonsFouldMetric rf = new RobinsonsFouldMetric();
					
					String geneTree = null;
					for(Input gene: geneTrees) {
						if(gene.getInput().equals(getCompareGeneTree()))
							geneTree = gene.getTree();
					}
					compareScore = rf.getMetric(TreeUtils.getTreeFromString(geneTree),
							TreeUtils.getTreeFromString(speciesTree));
					
					Tab tab = new Tab();
					tab.setText("Compare Tree");
					tab.setContent(new CostView(speciesTree+"#"+geneTree+"%", compareScore, "robinson-foulds"));
					GUI.tabPane.getTabs().add(tab);
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
		subStage.setTitle("Robinson Foulds");
	    subStage.initModality(Modality.APPLICATION_MODAL);
		
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
                fileChooser.setTitle("Save Output File");
                File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                	outputFileTextField.setText(file.toString());
                	setOutputFile(file);
                }
			}
		});	
		
		Label generator = new Label("Generator");
		collapsableGrid.add(generator, 0, 4);
		ObservableList<String> opt = 
			    FXCollections.observableArrayList(
			        "Don't generate",
			        "Stepwise taxon addition heuristic",
			        "Random tree"
			    );
		final ComboBox HcomboBox = new ComboBox(opt);
		HcomboBox.setValue("Stepwise taxon addition heuristic");
    	setGenerator("Stepwise taxon addition heuristic");
		collapsableGrid.add(HcomboBox, 1 , 4);
		HcomboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	HcomboBox.setValue(t1);
            	setGenerator(t1);
            }    
        });

		Label seed = new Label("Seed");
		collapsableGrid.add(seed, 0, 5);

		final TextField seedTextField = new TextField();
		collapsableGrid.add(seedTextField, 1, 5);
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
					ArrayList<Output> showOutput = null;
					
					 if (file != null) {
							fileInput++;
							Main.filter.add("Input " + fileInput);
							addInputItem(file.toString(), "RF");
							showOutput = runRFS(file);
							addResultItems(showOutput, "RF");
						}
					 else {
						File tempFile = new File(System.getProperty("java.io.tmpdir") + "temp.nwk");
						ArrayList<Input> geneTrees = GUI.speciesTree.get(firstTree);
						for(int i=0; i<geneTrees.size(); i++) {
							System.out.println(" HH " + geneTrees.get(i).getTree());
						}
							try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
								for(int i=0; i<geneTrees.size(); i++){
									bw.write(geneTrees.get(i).getTree()); 
								}
							} catch (IOException ex) {
								textArea.setText(ex.toString());
								ex.printStackTrace();
							}					
						
						showOutput = runRFS(tempFile);
						if(compareFile != null)
							addResultItems(showOutput, "RF", compareFile);
						else
							addResultItems(showOutput, "RF", tempFile);
					}
					f.setVisible(false);
					
					textArea.appendText(getConsoleOutput());
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

	public void addInputItem(String filename, String algo){
		String[] farr = filename.split("/");
		treeList = openInputFile(filename);
		
		setNumberOfInput(treeList.size());
		setPrevInputId(inputId);
		
		for(int i=0 ;i<treeList.size(); i++) {
    		geneTrees.add(new Input(getFilename() + "_" + (i+1), treeList.get(i)));
			input.add(new Input(inputId, getFilename() + "_" + (i+1), treeList.get(i), i , algo, numberOfTaxa(treeList.get(i)),"Input " + fileInput));
			inputId += 1;
		}
	}

	protected int getLeaves(String t) {
    	Tree tree = null;
		NewickImporter newick = new NewickImporter(new StringReader(t), true);							
		try {
			tree = newick.importNextTree();
		} catch (IOException e1) {
			textArea.setText(e1.toString());
			return 0;
		} catch (ImportException e1) {
			textArea.setText(e1.toString());
			return 0;
		}
		
		return tree.getExternalNodes().size();
	}

	public void addResultItems(ArrayList<Output> showOutput, String algo){	
		for(int i=0; i < showOutput.size(); i++) {
			resultId += 1;
			setSpeciesTree(showOutput.get(i).getTree());
			speciesTreeName.put("Median tree " + resultId, showOutput.get(i).getTree());
			if(getNumberOfInput() == 1)
				result.add(new Result(resultId, "name" + i, numberOfTaxa(showOutput.get(i).getTree()), "rooted", showOutput.get(i).getTree(), 
						getFilename() + "_species_" + (getPrevInputId()+1), algo, showOutput.get(i).getBestScore(), "Input "+fileInput)); 
			else
				result.add(new Result(resultId, "name" + i, numberOfTaxa(showOutput.get(i).getTree()), "rooted", showOutput.get(i).getTree(), 
						getFilename() + "_species_" + getPrevInputId() + "-" + (getPrevInputId()+getNumberOfInput()-1), algo, showOutput.get(i).getBestScore(),"Input "+fileInput)); 
		System.out.println("median tree taxa " + numberOfTaxa(showOutput.get(i).getTree()));
		}
	}
	
	public void addResultItems(ArrayList<Output> showOutput, String algo, File file){	
	
		for(int i=0; i < showOutput.size(); i++) {
			setSpeciesTree(showOutput.get(i).getTree(), file);
			resultId += 1;
			speciesTreeName.put("Median tree " + resultId, showOutput.get(i).getTree());
			if(getNumberOfInput() == 1)
				result.add(new Result(resultId, "name" + i, numberOfTaxa(showOutput.get(i).getTree()), "rooted", showOutput.get(i).getTree(), 
						getFilename(file.toString()) + "_species_" + (getPrevInputId()+1), algo, showOutput.get(i).getBestScore(), "Input "+fileInput)); 
			else
				result.add(new Result(resultId, "name" + i, numberOfTaxa(showOutput.get(i).getTree()), "rooted", showOutput.get(i).getTree(), 
						getFilename(file.toString()) + "_species_" + getPrevInputId() + "-" + (getPrevInputId()+getNumberOfInput()-1), algo, showOutput.get(i).getBestScore(),"Input "+fileInput)); 	
		}
	}
	
//	public void addResultItem(String tree, String algo){
//		final ComboBox comboBox = new ComboBox(options);
//		comboBox.setValue("Tree");
//		resultId += 1;
//		result.add(new Result(resultId, "name", "0", "rooted", comboBox, tree, getFilename() + "_" + (getPrevInputId()+1), algo, showOutput.get(i).getBestScore()));
//	}
	
	public void configureFileChooser(final FileChooser fileChooser){                           
        fileChooser.setTitle("Select Newick File");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
    }

    public ArrayList<Output> runRFS(File file) {
    	ArrayList<Output> showOutput = new ArrayList<Output>();
    	BufferedReader stdInput = null;
    	BufferedReader stdError = null;
    	int bestScore;
    	try {
    		
    		Process proc = null;
    		ProcessBuilder pb = null; 
			tempCopyToLocalDisk("RFS.exe");
			
    		if(System.getProperty("os.name").contains("Linux"))
    			proc = Runtime.getRuntime().exec("executables/RFS.linux "
    					+ "-i " + file 
    					+ " -g " + getGenerator()
    					+ " --seed " + getSeed());
    		else if(System.getProperty("os.name").contains("Windows"))
    			pb = new ProcessBuilder(System.getProperty("java.io.tmpdir") + "RFS.exe"
    					,"-i",""+file,"-g",getGenerator(),"--seed",getSeed());
    		else if(System.getProperty("os.name").toLowerCase().contains("mac"))
    			proc = Runtime.getRuntime().exec("executables/RFS.macintel "
    					+ "-i " + file 
    					+ " -g " + getGenerator()
    					+ " --seed " + getSeed());
            
    		proc = pb.start();
    		
    		stdInput = new BufferedReader(new 
            		InputStreamReader(proc.getInputStream()));

            stdError = new BufferedReader(new 
            	     InputStreamReader(proc.getErrorStream()));

        	String s, fileOutput = "";
        	boolean flag = false;
        	while ((s = stdInput.readLine()) != null) {
        		consoleOutput = consoleOutput + s + "\n";
        		if(s.contains("[RF distance:")) {
        			fileOutput = fileOutput + s + "\n";
        			bestScore= Integer.parseInt(s.replaceAll("[^0-9]", ""));
        			s = stdInput.readLine();
        			String convertedString = convertToNewickWIthDistance(s);
        			showOutput.add(new Output(convertedString, bestScore));
        			consoleOutput = consoleOutput + s + "\n";
        			
        			flag = true;
        		}
        		
        		if(flag)
        			fileOutput = fileOutput + s + "\n";
        	}

        	while ((s = stdError.readLine()) != null) {
        		consoleOutput = consoleOutput + s + "\n";
        		fileOutput = fileOutput + s;
        		textArea.appendText(s);  
        	}
        	
        	saveOutput(getOutputFile(), fileOutput);
        	setConsoleOutput(consoleOutput);
        } catch (Exception ex) {
            ex.printStackTrace();
            textArea.appendText(ex.toString());
        } finally{
        	try {
				stdInput.close();
				stdError.close();
			} catch (IOException e) {
				textArea.setText(e.toString());
				e.printStackTrace();
			}
        }
    	return showOutput;
    }
    
    public void tempCopyToLocalDisk(String string) {
    	URL inputUrl = getClass().getResource("/executables/" + string);
    	File dest = new File(System.getProperty("java.io.tmpdir") + string);
    	try {
    		if(!dest.exists())
    			FileUtils.copyURLToFile(inputUrl, dest);
		} catch (IOException e) {
			textArea.appendText(e.toString());
			e.printStackTrace();
		}
	}

	public void setSpeciesTree(String st, File filename) {
    	treeList = openInputFile(filename.toString());
		
		for(int i=0 ;i<treeList.size(); i++) {
    		geneTrees.add(new Input(getFilename(filename.toString()) + "_" + (i+1), treeList.get(i)));
		}
    	speciesTree.put(st, geneTrees);
	}
    
    public void setSpeciesTree(String st) {
    	speciesTree.put(st, geneTrees);
	}
    
    public ArrayList<Input> getSpeciesTree(String st) {
    	return speciesTree.get(st);
	}

	private void saveOutput(String outputFile, String output) {
    	if(outputFile != null && outputFile != "") {
        	try{
        	    PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
        	    writer.println(output);
        	    writer.close();
        	} catch (IOException e) {
        		textArea.setText(e.toString());
        	}
    	}
	}
    
    protected ArrayList<String> openInputFile(String filename) {
    	ArrayList<String> output = new ArrayList<String>();
    	BufferedReader br = null;
    	try {
    		br = new BufferedReader(new FileReader(filename));
    	    String line = br.readLine();

    	    while (line != null) {
    	    	if(line.contains(";")) {
    	    		String arr[] = line.split(";");
    	    		for(int i=0; i<arr.length; i++) {
    	    			output.add(convertToNewickWIthDistance(arr[i]+";"));
    	    		}
    	    	}
    	 	   	line = br.readLine();
    	    }
    	} catch(Exception e){
    		textArea.setText(e.toString());
    		e.printStackTrace();
    	} finally {
    	    try {
				br.close();
			} catch (IOException e) {
				textArea.setText(e.toString());
				e.printStackTrace();
			}
    	}
    	return output;
	}
    
    public String convertToNewickWIthDistance(String tree) {
    	StringBuilder strBuilder = new StringBuilder(tree.replaceAll(" ", ""));
		if(!strBuilder.toString().contains(":"))
			for(int i=0; i<strBuilder.length(); i++) {
				if(strBuilder.substring(i, i+1).equals(",") || strBuilder.substring(i, i+1).equals(")")) {
					strBuilder = strBuilder.insert(i, ":1");
					i = i+2;
				} 
//				else if(strBuilder.substring(i, i+1).equals(";") && !strBuilder.substring(i-1, i).equals(")") && !strBuilder.substring(i-1, i).equals("1")){
//					strBuilder = strBuilder.insert(i, ":1");
//					i = i+2;
//				}
			}
		return strBuilder.toString();
    }
    
    public int numberOfTaxa(String tree) {
    	int charCount = 0;
    	char temp;

    	for( int i = 0; i < tree.length( ); i++ ) {
    	    temp = tree.charAt( i );
    	    if(temp == ',')
    	        charCount++;
    	}
		return charCount;
	}
    
    public ObservableList<Result> getResult(){
    	return result;
    }
    
    public TabPane getTabPane(){
    	return tabPane;
    }
    
    public ObservableList<Input> getInput(){
    	return input;
    }
    	
	protected void setNumberOfInput(int numberOfInput) {
		this.numberOfInput = numberOfInput;
	}
	
	private int getNumberOfInput() {
		return this.numberOfInput;
	}
    
//	public void setFile(File file) {
//		this.file = file;
//	}
//	
//	public File getFile() {
//		return this.file;
//	}
		
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	
	public File getInputFile(){
		return this.inputFile;
	}
	
	public void setCompareGeneTree(String compareGeneTree){
		this.compareGeneTree = compareGeneTree;
	}
	
	public String getCompareGeneTree(){
		return this.compareGeneTree;
	}
	
	protected void setConsoleOutput(String consoleOutput) {
		this.consoleOutput = consoleOutput;
	}
	
	protected String getConsoleOutput() {
		return this.consoleOutput;
	}
	
	protected void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
	
	protected String getOutputFile(){
		if(this.outputFile != null)
			return this.outputFile.toString();
		else
			return "";
	}
	
	protected void setGenerator(String generator) {
		this.generator = generator;
	}
	
	protected String getGenerator(){
		if(this.generator.equals("Don't generate"))
			return "0";
		else if(this.generator.equals("Random tree"))
			return "2";
		else 
			return "1";
	}
	
	protected void setSeed(String seed) {
		this.seed = seed;
	}
	
	protected String getSeed(){
		return this.seed;
	}
	
	protected void setPrevInputId(int prevInputId) {
		this.prevInputId = prevInputId;
	}
	
	protected int getPrevInputId(){
		return this.prevInputId;
	}
	
	public String getFilename() {
		String[] farr = null;
		if(System.getProperty("os.name").contains("Linux"))
			farr = this.inputFile.toString().split("/");
		else if(System.getProperty("os.name").contains("Windows")) {
			char[] carr = this.inputFile.toString().toCharArray();
			for(int i=0; i<carr.length; i++) {
				if(carr[i] == '\\') {
					carr[i] = '/';
				}
			}
			farr = String.valueOf(carr).split("/");
		}
		
		return farr[farr.length-1];
	}
	
	public String getFilename(String file) {
		String[] farr = null;
		if(System.getProperty("os.name").contains("Linux"))
			farr = file.toString().split("/");
		else if(System.getProperty("os.name").contains("Windows")) {
			char[] carr = file.toString().toCharArray();
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

//File file;
//InputStream in;
//BufferedWriter bw = null;
//FileWriter fw = null;
//BufferedReader reader = null;
//try {
//	in = getClass().getResourceAsStream("/executables/" + string); 
//	reader = new BufferedReader(new InputStreamReader(in));
//	
//	fw = new FileWriter(System.getProperty("java.io.tmpdir") + string);
//	bw = new BufferedWriter(fw);
//	
//	String line;
//	while ((line = reader.readLine()) != null) {
//		bw.write(line);
//	}
//} catch (Exception e1) {
//	e1.printStackTrace();
//	textArea.appendText(e1.toString());
//}
//finally {
//
//	try {
//
//		if (reader != null)
//			reader.close();
//
//		if (fw != null)
//			fw.close();
//
//	} catch (IOException ex) {
//		textArea.appendText(ex.toString());
//		ex.printStackTrace();
//
//	}
//
//}






//URL path = this.getClass().getClassLoader().getResource("executables/");
//File source = new File(path.getFile());
//File dest = new File(System.getProperty("java.io.tmpdir"));
//try {
//    FileUtils.copyDirectory(source, dest);
//} catch (IOException e) {
//	textArea.appendText(e.toString());
//    e.printStackTrace();
//}