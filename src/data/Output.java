package data;

public class Output {
	String tree;
	int bestScore;
	
	public Output(String tree, int bestScore) {
		this.tree = tree;
		this.bestScore = bestScore;
	}
	
	public String getTree() {
		return this.tree;
	}
	
	public int getBestScore() {
		return this.bestScore;
	}
	
	public String toString() {
		return "Score : " + this.bestScore + "\n" + this.tree ;
	}
}