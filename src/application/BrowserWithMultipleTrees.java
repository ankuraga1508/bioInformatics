package application;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import netscape.javascript.JSObject;

@SuppressWarnings("restriction")
public class BrowserWithMultipleTrees extends Pane {
    JSCall jcall;
    
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

        final java.net.URL url = this.getClass().getResource("/resources/multipleTreeView.html");
        webEngine.load(url.toExternalForm());
        
        return browser;
    }
}