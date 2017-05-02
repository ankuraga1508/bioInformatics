package application;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

@SuppressWarnings("restriction")
public class WelcomePage extends Pane {    
    public WebView webView() {
    	WebView browser = new WebView();
    	final WebEngine webEngine = browser.getEngine();
        final java.net.URL url = this.getClass().getResource("/resources/welcome.html");
        webEngine.load(url.toExternalForm());
        
        return browser;
    }
}