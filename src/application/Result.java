package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("restriction")
public class Result {
    
    //Properties 
	private SimpleIntegerProperty number = new SimpleIntegerProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleIntegerProperty leaves = new SimpleIntegerProperty();
    private SimpleStringProperty rooted = new SimpleStringProperty();
    private SimpleStringProperty tree = new SimpleStringProperty();
    private SimpleStringProperty inputName = new SimpleStringProperty();
    private SimpleStringProperty algo = new SimpleStringProperty();
    private SimpleIntegerProperty bestScore = new SimpleIntegerProperty();
    private SimpleStringProperty fileInput = new SimpleStringProperty();
    
    public Result(int id, String name, int leaves, String rooted, String tree, String inputName, String algo, int bestScore, String fileInput) {
    	this.number = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.leaves = new SimpleIntegerProperty(leaves);
        this.rooted = new SimpleStringProperty(rooted);
        this.tree =  new SimpleStringProperty(tree);
        this.inputName = new SimpleStringProperty(inputName);
        this.algo = new SimpleStringProperty(algo);
        this.bestScore = new SimpleIntegerProperty(bestScore);
        this.fileInput = new SimpleStringProperty(fileInput);
    }

    public String getName(){
        return this.name.get();
    }

    public int getLeaves(){
        return this.leaves.get();
    }

    public String getRooted(){
        return this.rooted.get();
    }
    
    public String getTree(){
        return this.tree.get();
    }
    
    public void setNumber(int number) {
		this.number.set(number); 
	}
    
    public int getNumber(){
    	return this.number.get();
    }
    
    public String getInputName(){
    	return this.inputName.get();
    }
    
    public String getAlgo(){
    	return this.algo.get();
    }
    
    public int getBestScore(){
    	return this.bestScore.get();
    }
    
    public String getFileInput(){
    	return this.fileInput.get();
    }
}