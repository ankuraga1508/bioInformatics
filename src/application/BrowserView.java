package application;

import java.awt.image.BufferedImage;
import java.io.File;

import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import netscape.javascript.JSObject;

@SuppressWarnings("restriction")
public class BrowserView extends Pane {
    BufferedImage bufferedImage = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);
    String html, image64;
    final FileChooser fileChooser = new FileChooser();
    int width, height;
    public JSCall jcall;   
    
    public BrowserView(){
    	
    }
    
    public WebView webView(final String s, File file) {
    	WebView browser = new WebView();
    	final WebEngine webEngine = browser.getEngine();
        
    	jcall = new JSCall();
    	jcall.setFile(file);
        
    	final JSObject windowObject = (JSObject) webEngine.executeScript("window");
        windowObject.setMember("app", jcall);
        
        webEngine.getLoadWorker().stateProperty().addListener(
	            new ChangeListener<State>() {
	                public void changed(ObservableValue ov, State oldState, State newState) {
	                    if (newState == State.SUCCEEDED) {
	                    	windowObject.call("func", s);
	                    }
	                }
	            });

        final java.net.URL url = this.getClass().getResource("/resources/main.html");
        webEngine.load(url.toExternalForm());
        
        return browser;
    }
    
//  public BrowserView(String s, int taxa, File file) {   	
//  	WebView browser = new WebView();
//  	WebEngine webEngine = browser.getEngine();
//  	jcall = new JSCall();
//  	jcall.setFile(file);
//      
//  	JSObject windowObject = (JSObject) webEngine.executeScript("window");
//      windowObject.setMember("app", jcall);
//      
//      browser.prefHeight(1000);
//      browser.prefWidth(2000);
//      
//      webEngine.getLoadWorker().stateProperty().addListener(
//	            new ChangeListener<State>() {
//	                public void changed(ObservableValue ov, State oldState, State newState) {
//	                    if (newState == State.SUCCEEDED) {
//	                    	windowObject.call("func", s);
//	                    }
//	                }
//	            });
//      java.net.URL url = this.getClass().getResource("/resources/main.html");
//      webEngine.load(url.toExternalForm());
//  }
    
//    private void createContextMenu(WebView browser2) {
//    	ContextMenu contextMenu = new ContextMenu();
//        MenuItem savePage = new MenuItem("Save Image");
//        savePage.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {           
//            	Task<Void> task = new Task<Void>() {
//
//            	     @Override protected Void call() throws Exception {              
//            	             Platform.runLater(new Runnable() {
//            	                 @Override public void run() {
//            	                	 image64 = (String) webEngine.executeScript("downloadPNG()");
//            	                 }
//            	             });
//            	         
//            	         return null;
//            	     }
//            	 };
//            	 task.run();
//
//            	FileChooser fileChooser = new FileChooser();
//                fileChooser.setTitle("Save Image");
//                File file = fileChooser.showSaveDialog(new Stage());
//                if (file != null) {
//                	save(file);
//                }
//            }
//        });
//        contextMenu.getItems().addAll(savePage);
//
////        browser.setOnMousePressed(e -> {
////        	if (e.getButton() == MouseButton.SECONDARY) {
////        		contextMenu.show(browser, e.getScreenX(), e.getScreenY());
////        	} else {
////             contextMenu.hide();
////        	}
////        });
//    }
    
//	private Object save(File file) {
//		image64 = (String) webEngine.executeScript("document.getElementById(\"canvas\").src");
//    	image64 = image64.split(",")[1];
//    	byte[] data = Base64.decodeBase64(image64);
//    	try (OutputStream stream = new FileOutputStream(file)) {
//    	    stream.write(data);
//    	} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		return null;
//	}

	private String passingOutputObject(String tree) {
        return "<html>" +
        	    "<head>" +
        		"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
        	    "<link rel=\"stylesheet\" href=\"file://"+System.getProperty("user.dir")+"/resources/css/styles.css\">" + 
//        		"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/raphael-min.js\" ></script> " +
//        		"<script type=\"text/javascript\" src=\"file://"+ System.getProperty("user.dir") + "/resources/js/jsphylosvg-min.js\"></script>" + 
        		"<script type=\"text/javascript\" src=\"file:///"+ System.getProperty("user.dir") + "/resources/js/PhyloCanvas.js\"></script>" + 
//        		"<script type=\"text/javascript\" src=\"file://"+ System.getProperty("user.dir") + "/resources/js/dropdown.js\"></script>" + 
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
//        			+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/jquery-3.1.1.min.js\" ></script> " 
//					+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/html2canvas.js\" ></script> " 
					  
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
	
//    @Override protected void layoutChildren() {
//        double w = getWidth();
//        double h = getHeight();
//        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
//    }
 
//    @Override protected double computePrefWidth(double height) {
//        return 1000;
//    }
// 
//    @Override protected double computePrefHeight(double width) {
//        return 850;
//    }
}