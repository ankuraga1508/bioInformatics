package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;

public class Input {   
    //Properties
	private SimpleIntegerProperty number = new SimpleIntegerProperty();
    private SimpleStringProperty input = new SimpleStringProperty();
	private String tree;
	private String algo;
	private SimpleIntegerProperty pos = new SimpleIntegerProperty();
	private SimpleIntegerProperty leaves = new SimpleIntegerProperty();
	private SimpleStringProperty fileInput = new SimpleStringProperty();

    public Input(int id, String input, String tree, int pos, String algo, int leaves, String fileInput) {
    	this.number = new SimpleIntegerProperty(id);
        this.input = new SimpleStringProperty(input);
        this.tree = tree;
        this.algo = algo;
        this.pos = new SimpleIntegerProperty(pos);
        this.leaves = new SimpleIntegerProperty(leaves);
        this.fileInput = new SimpleStringProperty(fileInput);
    }

    public Input(String input, String tree) {
        this.input = new SimpleStringProperty(input);
        this.tree = tree;
    }
    
    public int getId(){
        return this.number.get();
    }
    
    public SimpleIntegerProperty getIdProperty(){
        return this.number;
    }
     
    public String getInput(){
        return this.input.get();
    }

    public String getTree(){
        return this.tree;
    }
    
    public int getNumber(){
    	return this.number.get();
    }
    
    public int getPos(){
    	return this.pos.get();
    }
    
    public String getAlgo(){
    	return this.algo;
    }
    
    public int getLeaves(){
    	return this.leaves.get();
    }
    
    public String getFileInput(){
    	return this.fileInput.get();
    }
}