package application;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import netscape.javascript.JSObject;

public class CostView extends Pane {
	WebView browser;
	WebEngine webEngine;
	
    public CostView(String s, double cost, String method) {
    	browser = new WebView();
        webEngine = browser.getEngine();
        
    	JSObject windowObject = (JSObject) webEngine.executeScript("window");
        windowObject.setMember("app", new JSCall());
        //webEngine.setJavaScriptEnabled(true);   
        
        browser.getEngine().getLoadWorker().stateProperty().addListener(
	            new ChangeListener<State>() {
	                public void changed(ObservableValue ov, State oldState, State newState) {
	                    if (newState == State.SUCCEEDED) {
	                    	JSObject jsobj = (JSObject)webEngine.executeScript("window");
	                    	jsobj.call("setMethod", method);
	                    	jsobj.call("setCost", cost);
	                    	jsobj.call("func", s);
	                    }
	                }
	            });
        
		java.net.URL url = getClass().getResource("/CostView.html");
		browser.getEngine().load(url.toExternalForm());
		getChildren().add(browser);
    }
}