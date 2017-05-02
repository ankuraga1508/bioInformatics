package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;


public class BrowserWithTreeView extends Pane {
	static WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    BufferedImage bufferedImage = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);
    String html, image64;
    final FileChooser fileChooser = new FileChooser();
    int width, height;
    
    public BrowserWithTreeView(String s, int taxa) {
    	width = 800 + 100*(int)(taxa/10);
    	height = 800 + 100*(int)(taxa/10);
    	//width = 400 + 100*(int)(taxa/10);
    	//height = 400 + 100*(int)(taxa/10);
    	
    	JSCall jcall = new JSCall();
    	//jcall.setTree(s);
        getStyleClass().add("browser");
        html = passingOutputObject(s);
        webEngine.loadContent(html);
        webEngine.setJavaScriptEnabled(true);
        getChildren().add(browser);
        browser.autosize();
        browser.setContextMenuEnabled(true);
        //createContextMenu(browser);
        
    	JSObject win = (JSObject) webEngine.executeScript("window");
        win.setMember("app", new JSCall());
        
		webEngine.getLoadWorker().stateProperty().addListener(
	            new ChangeListener<State>() {
	                public void changed(ObservableValue ov, State oldState, State newState) {
	                    if (newState == State.SUCCEEDED) {
	                    	image64 = (String) webEngine.executeScript("func1(0)");
	                    	JSObject win = (JSObject) webEngine.executeScript("window");
	                        win.setMember("app", new JSCall());
	                    }
	                }
	            });
    }
    
    private void createContextMenu(WebView browser2) {
    	ContextMenu contextMenu = new ContextMenu();
        MenuItem savePage = new MenuItem("Save Image");
        savePage.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
//            	String loader = "";
//            	try (BufferedReader br = new BufferedReader(new FileReader(
//            			System.getProperty("user.dir")+"/resources/html/loader.html/"))) {
//            	    String line;
//            	    while ((line = br.readLine()) != null) {
//            	    	loader = loader + line;
//            	    }
//            	} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//            	
//            	webEngine.loadContent(loader);
            
            	Task<Void> task = new Task<Void>() {

            	     @Override protected Void call() throws Exception {              
            	             Platform.runLater(new Runnable() {
            	                 public void run() {
            	                	 image64 = (String) webEngine.executeScript("downloadPNG()");
            	                 }
            	             });
            	         
            	         return null;
            	     }
            	 };
            	 task.run();
            	
//            	
//            	Thread t1 = new Thread(new Runnable() {
//            	     public void run() {
//                     	image64 = (String) webEngine.executeScript("downloadPNG()");
//            	     }
//            	});
//            	t1.start();

            	FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");
                File file = fileChooser.showSaveDialog(new Stage());
                if (file != null) {
                	save(file);
                }
            }
        });
        contextMenu.getItems().addAll(savePage);

        browser.setOnMousePressed(e -> {
        	if (e.getButton() == MouseButton.SECONDARY) {
        		contextMenu.show(browser, e.getScreenX(), e.getScreenY());
        	} else {
             contextMenu.hide();
        	}
        });
    }
    
	private Object save(File file) {
		image64 = (String) webEngine.executeScript("document.getElementById(\"destinationCanvas\").src");
    	image64 = image64.split(",")[1];
    	
    	byte[] data = Base64.decodeBase64(image64);
    	try (OutputStream stream = new FileOutputStream(file)) {
    	    stream.write(data);
    	} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
//    	BufferedImage image = new BufferedImage((int)browser.getWidth(), (int)browser.getHeight(), BufferedImage.TYPE_INT_ARGB);
//    	byte[] imageByte = null;
//    	BASE64Decoder decoder = new BASE64Decoder();
//    	try {
//			Graphics2D drawer = image.createGraphics() ;
//			drawer.clearRect(0, 0, (int)browser.getWidth(), (int)browser.getHeight());
//			drawer.setColor(Color.WHITE);
//			drawer.setBackground(java.awt.Color.WHITE);
//			drawer.setPaint(Color.white);
//			drawer.fillRect(0, 0, (int)browser.getWidth(), (int)browser.getHeight());
//			drawer.dispose();
//
//			imageByte = decoder.decodeBuffer(image64);
//			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
//			image = ImageIO.read(bis);
//			bis.close();
//        	// write the image to a file
//        	File outputfile = file;
//        	ImageIO.write(image, "jpeg", outputfile);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return null;
	}

	private String passingOutputObject(String object) {
        return "<html>" +
        	    "<head>" +
        		"<link rel=\"stylesheet\" href=\"file://"+System.getProperty("user.dir")+"/resources/css/styles.css\">" + 
        		"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/raphael-min.js\" ></script> " +
        		"<script type=\"text/javascript\" src=\"file://"+ System.getProperty("user.dir") + "/resources/js/jsphylosvg-min.js\"></script>" + 
        		"<script type=\"text/javascript\" src=\"file://"+ System.getProperty("user.dir") + "/resources/js/dropdown.js\"></script>" + 
        		"<script type=\"text/javascript\">"+
        			"func1 = function(obj){"
        			+ "var dataObject =" + object + ";"
//        			+ "Smits.PhyloCanvas.Render.Style.line.stroke = 'rgb(0,0,0)';"
//        			+ "Smits.PhyloCanvas.Render.Style.text.fill = '#EEE';"
//        			+ "Smits.PhyloCanvas.Render.Style.textSecantBg.stroke = '#FFF';"
//        			+ "Smits.PhyloCanvas.Render.Style.barChart.fill = 'white';"
//					+ "Smits.PhyloCanvas.Render.Parameters.Rectangular.showScaleBar = true;"
        			+ "phylocanvas = new Smits.PhyloCanvas(dataObject,'svgCanvas',"+ width + ", " + height + ");"
        			//+ "app.printString(phylocanvas.exportNwk());"
        			
        			+ "};" 
        			+ "</script>"
        			+ "<script type=\"text/javascript\">"
        			+ "downloadPNG = function () {"
        			+"	html2canvas($('#svgCanvas'), {"
        			+" background : '#FFFFFF',"
        			+"		onrendered: function (canvas) {"
        			
        		//	+ "var destinationCanvas = document.getElementById(\"destinationCanvas\");"
        			//+ "var destinationCtx = destinationCanvas.getContext(\"2d\");"
        			//+"destinationCtx.drawImage(canvas, 0, 0);"
        		//	+"document.getElementById(\"svgCanvas\").src = canvas.toDataURL(\"image/png\");"
        			
//+"var c = document.getElementById(\"svgCanvas\");"
//+"var ctx = canvas.getContext(\"2d\");"
//+"var imgData = ctx.createImageData("+width+", "+height+");"
//+"document.getElementById(\"svgCanvas\").src = imgData.src;"

//        			+"var ctx = canvas.getContext(\"2d\");"
//        			+"ctx.fillStyle = \"white\";"
//        			+"ctx.fillRect(0, 0, 700, 700);"
//        			+"var imgData = ctx.createImageData(700, 700);"
//        			+"var i;"
//        			+"for (i = 0; i < imgData.data.length; i += 4) {imgData.data[i+0] = 0;imgData.data[i+1] = 0;imgData.data[i+2] = 0;imgData.data[i+3] = 0;}"
//        			+"ctx.putImageData(imgData, 700, 700);"

//				+"document.getElementById(\"svgCanvas\").src = canvas.toDataURL();"
//        			+" $('body').append('<img id = \"newImage\" src=\"'+img1+'\"/>');"
//        			+ "Canvas2Image.saveAsPNG(canvas);"
//        			+"var can = document.getElementById(\"svgCanvas\");"
//        			+"var dataURL = can.toDataURL();"
//        			+ "return dataURL;"
//        			+ "$(\"<img/>\", {"
//        			+"  id: \"image\","
//        			+"  src: canvas.toDataURL(\"image/png\"),"
//        			+"  width: '95%',"
//        	        +"  height: '95%'"
//        			+"}).appendTo($(\"#show_img\"));"
//        			+""
        			+    "}"
        			+ "	});"
//        			+ ""
        			+ "};" 
//        			+ "function getBase64Image(img) {"
//        			+ "var canvas = document.createElement(\"canvas\");"
//        			+ "canvas.width = img.width;"
//        			+ "canvas.height = img.height;"
//        			+ "var ctx = canvas.getContext(\"2d\");"
//        			+ "ctx.drawImage(img, 0, 0);"
//        			+ "var dataURL = canvas.toDataURL();"
//        			+ "return img;"
//        			+"};"
//        			+"function showhide() {"
//        			+"var img = document.getElementById('show_img');"
//        			+"if (img.style.visibility === 'hidden') {"
////        	        +"img.style.visibility = \"visible\";"
//        	    	+"} else {"
//        	        +"img.style.visibility = \"hidden\";"
//        	    	+"}"
//        			+"}"
        	    	
//        			+"function download() {"
//        			+"var node = document.getElementById('svgCanvas');"
//
//        			+"domtoimage.toPng(node)"
//        			+".then (function (dataUrl) {"
//        			 +"       var img = new Image();"
//        			 +"       document.getElementById('show_img').src = dataUrl;"
//        			 +"       document.getElementById('svgCanvas').src = dataUrl;"
//        			  +"      document.appendChild(img);"
//        			  +"  })"
//        			  +"  .catch(function (error) {"
//        			   +"     console.error('oops, something went wrong!', error);"
//        			  +"  });"    
//        	    	+ "}"
        	    	
        			+ "</script>"
        			+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/jquery-3.1.1.min.js\" ></script> " 
//        			+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/canvas2image.js\" ></script> " 
//        			+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/html2canvas.svg.js\" ></script> " 
					+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/html2canvas.js\" ></script> " 
					+"<script type=\"text/javascript\" src=\"file://"+System.getProperty("user.dir")+"/resources/js/dom-to-image.js\" ></script> " 
					+"</head><body>"
					
//					+ "<div class=\"dropdown\">"
//        			+ "<button onclick=\"myFunction()\" class=\"dropbtn\">Dropdown</button>"
//        			+ "<div id=\"myDropdown\" class=\"dropdown-content\">"
//        			+ "<a href=\"#home\">Home</a>"
//        			+ "<a href=\"#about\">About</a>"
//        			+ "</div>"
//        			+ "</div>"
        			
        			+ "<select onchange=\"app.onChangeDropDown(this.value, app.getTree())\" class=\"simpledropdown\">"
        			+ "<option value=\"Tree\">Tree</option>"
        			+ "<option value=\"Circular\">Circular</option>"
        			+ "</select>"     
        			+ "<div id=\"destinationCanvas\" width=\"2000\" height=\"2000\"></div>"   
        			+ "<div id=\"svgCanvas\"></div>"     		
        			+ "</body></html>";
    }
	
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return this.height;
    }
 
    @Override protected double computePrefHeight(double width) {
        return this.width;
    }
}