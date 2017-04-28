package application;

import java.io.File;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import netscape.javascript.JSObject;

public class BrowserViewWithoutComparison extends Pane {
	private WebView browser;
	private WebEngine webEngine;
    JSCall jcall;
    
	public BrowserViewWithoutComparison(){
    	
    }
    
    public WebView webView(final String s) {
    	WebView browser = new WebView();
    	final WebEngine webEngine = browser.getEngine();
        
    	jcall = new JSCall();
        
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

        final java.net.URL url = this.getClass().getResource("/resources/TreeWithoutComparison.html");
        webEngine.load(url.toExternalForm());
        
        return browser;
    }
    
//    public BrowserViewWithoutComparison(String s, int taxa) {
//    	WebView browser = new WebView();
//    	WebEngine webEngine = browser.getEngine();
//        
//    	JSObject windowObject = (JSObject) webEngine.executeScript("window");
//        windowObject.setMember("app", new JSCall());
//        
//        //webEngine.setJavaScriptEnabled(true);   
//        webEngine.getLoadWorker().stateProperty().addListener(
//	            new ChangeListener<State>() {
//	                public void changed(ObservableValue ov, State oldState, State newState) {
//	                    if (newState == State.SUCCEEDED) {
////	                    	JSObject jsobj = (JSObject)webEngine.executeScript("window");
////	                    	jsobj.call("func", s);
//	                    	windowObject.call("func", s);
//	                    }
//	                }
//	            });
//        
//		java.net.URL url = this.getClass().getResource("/resources/TreeWithoutComparison.html");
//		webEngine.load(url.toExternalForm());
//		Pane rootPane = (Pane) GUI.tabPane.getScene().getRoot();
//        this.getChildren().add(browser);
//    }
}