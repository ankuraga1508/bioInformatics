package application;

import java.awt.image.BufferedImage;
import java.io.File;
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
}