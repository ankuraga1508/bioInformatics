import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import application.Input;

try {
            
	    // run the Unix "ps -ef" command
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec("java -version");
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            
            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
        

private int numberOfTaxa(String tree) {
	int charCount = 0;
	char temp;

	for( int i = 0; i < tree.length( ); i++ ) {
	    temp = tree.charAt( i );
	    if(temp == ',')
	        charCount++;
	}
	return charCount;
}

private ArrayList<String> openInputPDDFile(String filename) {
	ArrayList<String> trees = new ArrayList<String>();
	BufferedReader br = null;
	try {
		br = new BufferedReader(new FileReader(filename));
	    String line = br.readLine();	    
	    while (line != null) {
	    	if(line.equals("Begin trees;")) {
    			while(!(line = br.readLine()).contains("End;")) {
    				trees.add(line.replaceAll("tree PDD_Median_Tree_(\\d+) = ", "").replaceAll("'",""));
    			}
    		}     
	        line = br.readLine();
	    }
	} catch(Exception e){
		e.printStackTrace();
	} finally {
	    try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return trees;
}


String tree = "((hedgehog:1.0,shrew:1.0):1.0,(((microbat:1.0,macrobat:1.0):1.0,((((cow:1.0,sheep:1.0):1.0,dolphin:1.0):1.0,pig:1.0):1.0,vicugna:1.0):1.0):1.0,(horse:1.0,(dog:1.0,cat:1.0):1.0):1.0):1.0);";

StringBuilder strBuilder = new StringBuilder(tree.replaceAll(" ", ""));
if(!strBuilder.toString().contains(":"))
	for(int i=0; i<strBuilder.length(); i++) {
		if(strBuilder.substring(i, i+1).equals(",") || strBuilder.substring(i, i+1).equals(")")) {
			strBuilder = strBuilder.insert(i, ":1");
			i = i+2;
		} else if(strBuilder.substring(i, i+1).equals(";") && !strBuilder.substring(i-1, i).equals(")") && !strBuilder.substring(i-1, i).equals("1")){
			strBuilder = strBuilder.insert(i, ":1");
			i = i+2;
		}
	}

tree = strBuilder.toString();

//public void addResultItem(String tree, String algo){
//final ComboBox comboBox = new ComboBox(options);
//comboBox.setValue("Tree");
//resultId += 1;
//result.add(new Result(resultId, "name", "0", "rooted", comboBox, tree, getFilename() + "_" + (getPrevInputId()+1), algo, showOutput.get(i).getBestScore()));
//}



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
//  FileUtils.copyDirectory(source, dest);
//} catch (IOException e) {
//	textArea.appendText(e.toString());
//  e.printStackTrace();
//}


//public BrowserViewWithoutComparison(String s, int taxa) {
//	WebView browser = new WebView();
//	WebEngine webEngine = browser.getEngine();
//  
//	JSObject windowObject = (JSObject) webEngine.executeScript("window");
//  windowObject.setMember("app", new JSCall());
//  
//  //webEngine.setJavaScriptEnabled(true);   
//  webEngine.getLoadWorker().stateProperty().addListener(
//          new ChangeListener<State>() {
//              public void changed(ObservableValue ov, State oldState, State newState) {
//                  if (newState == State.SUCCEEDED) {
////                  	JSObject jsobj = (JSObject)webEngine.executeScript("window");
////                  	jsobj.call("func", s);
//                  	windowObject.call("func", s);
//                  }
//              }
//          });
//  
//	java.net.URL url = this.getClass().getResource("/resources/TreeWithoutComparison.html");
//	webEngine.load(url.toExternalForm());
//	Pane rootPane = (Pane) GUI.tabPane.getScene().getRoot();
//  this.getChildren().add(browser);
//}



//public BrowserView(String s, int taxa, File file) {   	
//	WebView browser = new WebView();
//	WebEngine webEngine = browser.getEngine();
//	jcall = new JSCall();
//	jcall.setFile(file);
//  
//	JSObject windowObject = (JSObject) webEngine.executeScript("window");
//  windowObject.setMember("app", jcall);
//  
//  browser.prefHeight(1000);
//  browser.prefWidth(2000);
//  
//  webEngine.getLoadWorker().stateProperty().addListener(
//            new ChangeListener<State>() {
//                public void changed(ObservableValue ov, State oldState, State newState) {
//                    if (newState == State.SUCCEEDED) {
//                    	windowObject.call("func", s);
//                    }
//                }
//            });
//  java.net.URL url = this.getClass().getResource("/resources/main.html");
//  webEngine.load(url.toExternalForm());
//}

//private void createContextMenu(WebView browser2) {
//	ContextMenu contextMenu = new ContextMenu();
//    MenuItem savePage = new MenuItem("Save Image");
//    savePage.setOnAction(new EventHandler<ActionEvent>() {
//        public void handle(ActionEvent e) {           
//        	Task<Void> task = new Task<Void>() {
//
//        	     @Override protected Void call() throws Exception {              
//        	             Platform.runLater(new Runnable() {
//        	                 @Override public void run() {
//        	                	 image64 = (String) webEngine.executeScript("downloadPNG()");
//        	                 }
//        	             });
//        	         
//        	         return null;
//        	     }
//        	 };
//        	 task.run();
//
//        	FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Save Image");
//            File file = fileChooser.showSaveDialog(new Stage());
//            if (file != null) {
//            	save(file);
//            }
//        }
//    });
//    contextMenu.getItems().addAll(savePage);
//
////    browser.setOnMousePressed(e -> {
////    	if (e.getButton() == MouseButton.SECONDARY) {
////    		contextMenu.show(browser, e.getScreenX(), e.getScreenY());
////    	} else {
////         contextMenu.hide();
////    	}
////    });
//}

//private Object save(File file) {
//	image64 = (String) webEngine.executeScript("document.getElementById(\"canvas\").src");
//	image64 = image64.split(",")[1];
//	byte[] data = Base64.decodeBase64(image64);
//	try (OutputStream stream = new FileOutputStream(file)) {
//	    stream.write(data);
//	} catch (FileNotFoundException e1) {
//		e1.printStackTrace();
//	} catch (IOException e1) {
//		e1.printStackTrace();
//	}
//	return null;
//}

private String passingOutputObject(String tree) {
    return "<html>" +
    	    "<head>" +
    		"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
    	    "<link rel=\"stylesheet\" href=\"file://"+System.getProperty("user.dir")+"/resources/css/styles.css\">" + 
//    		"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/raphael-min.js\" ></script> " +
//    		"<script type=\"text/javascript\" src=\"file://"+ System.getProperty("user.dir") + "/resources/js/jsphylosvg-min.js\"></script>" + 
    		"<script type=\"text/javascript\" src=\"file:///"+ System.getProperty("user.dir") + "/resources/js/PhyloCanvas.js\"></script>" + 
//    		"<script type=\"text/javascript\" src=\"file://"+ System.getProperty("user.dir") + "/resources/js/dropdown.js\"></script>" + 
    		"<script type=\"text/javascript\">"
    		
    			+"window.addEventListener(\"load\", function(e){window.status = \"done\";});"
    			+"abc = function() { app.printString(\"Hello\");};"
    			//+ "window.onload = func;" +
    			+"function func(){"
    			+ "app.printString(\"BBBB\");"
    			+ "var newick_str = \"((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A;\";"
    			+"var phylocanvas = new PhyloCanvas.Tree('svgCanvas');"
    			//+ "app.printString(\"AA\");"
    			//+"phylocanvas.setTreeType('circular');"
    			
    			//+ "phylocanvas.contextMenu.mouseover();"
    			//+ "phylocanvas.setFont();"
    			+"phylocanvas.load(newick_str);"
    		
    			//+ "phylocanvas = new Smits.PhyloCanvas(dataObject,'svgCanvas',"+ width + ", " + height + ", 'circular');"
    			+ "}" 
    			+ "</script>"
    			+ "<script type=\"text/javascript\">"
    			+ "downloadPNG = function () {"
    			+"	html2canvas($('#svgCanvas'), {"
    			+" background : '#FFFFFF',"
    			+"		onrendered: function (canvas) {"
    			
    			
    			+"document.getElementById(\"svgCanvas\").src = ctx.toDataURL(\"image/png\");"
    			+    "}"
    			+ "	});"
    			+ "};"
    			+ "</script>"
//    			+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/jquery-3.1.1.min.js\" ></script> " 
//				+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/html2canvas.js\" ></script> " 
				  
				+"</head>"
				+ ""
				+ "<body onload=\"func()\">"
				+ "<select onchange=\"app.onChangeDropDown(this.value, app.getTree())\" class=\"simpledropdown\">"
    			+ "<option value=\"Tree\">Tree</option>"
    			+ "<option value=\"Circular\">Circular</option>"
    			+ "</select>"      			
    			+ "<div id=\"svgCanvas\"></div>"     		
    			+ "</body></html>";
}

//@Override protected void layoutChildren() {
//    double w = getWidth();
//    double h = getHeight();
//    layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
//}

//@Override protected double computePrefWidth(double height) {
//    return 1000;
//}
//
//@Override protected double computePrefHeight(double width) {
//    return 850;
//}


private String format(String str) {
	if(str == null || str == "")
		return "1";
	
	if(str.equals("All"))
		return "all";
	else
		return "1";
}


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

//public String getInputFilename() {
//String[] farr = this.inputFile.toString().split("/");
//return farr[farr.length-1];
//}



private String getCommand(String str, String tag) {
	if(str == null || str == "")
		return "";
	else
		return tag + str;
}

private boolean isEqual(ArrayList<Input> list, ArrayList<Input> geneTrees) {
	int count = 0;
	for(int i=0; i<geneTrees.size(); i++){
		for(int j=0; j<list.size(); j++){
			if(geneTrees.get(i).getTree().equals(list.get(j).getTree())) {
				count++;
			}
		}
	}
	System.out.println(" RESULT " + count  + " " +   geneTrees.size() + " " + list.size());
	if(count == geneTrees.size() && geneTrees.size() == list.size())
		return true;
	else
		return false;
}