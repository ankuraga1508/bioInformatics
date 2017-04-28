package scm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import commons.MutableTree;
import commons.SetUtils;
import jebl.evolution.graphs.Node;
import jebl.evolution.io.NewickExporter;
import jebl.evolution.io.NewickImporter;
import jebl.evolution.trees.Tree;

public class SCM {
	public enum Scoring { cluster, uniquetaxa, resolution, overlap, degree };
	private List<Tree> tmp, input;
	private Tree scm;
	private Scoring type;

	public SCM(String fileName, Scoring type) throws Exception {
		File inFile = new File(fileName);
		input = new LinkedList<Tree>();		
		NewickImporter imp = new NewickImporter(new FileReader(inFile), true);
		while (imp.hasTree()) {
			input.add(imp.importNextTree());
		}
		tmp = new LinkedList<Tree>(input);
		this.type = type;
	}

	private Tree merge(Tree t1, Tree t2) throws Exception {
		Tree tmp = null;
		if (SetUtils.Cross(t1.getTaxa(), t2.getTaxa()).size() > 2) {
			File trees = new File(System.getProperty("user.dir") + "/tmp/" + System.currentTimeMillis() + ".newick");
			if(!trees.exists())
				trees.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(trees));
			NewickExporter exp = new NewickExporter(bw);
			exp.exportTree(t1);
			exp.exportTree(t2);
			bw.close();		
			String [] cmd = { "python", System.getProperty("user.dir") + "/executables/strict_consensus_merge.py", trees.getAbsolutePath() };
			ProcessBuilder pb = new ProcessBuilder();
			pb.redirectErrorStream(true);				
			pb.command(cmd);				
			Process p = pb.start();
			BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;		
			while ((line = stdin.readLine()) != null) {	
				NewickImporter newick = new NewickImporter(new StringReader(line), true);								
				tmp = newick.importNextTree();				
			}
			stdin.close();
			trees.delete();
		}
		return tmp;
	}

	private double clusterScore(Tree scm) {
		return (double) scm.getInternalNodes().size();
	}

	private double uniquetaxaScore(Tree ti, Tree tj) {		
		return (double) (SetUtils.Union(ti.getTaxa(), tj.getTaxa()).size() - SetUtils.Cross(ti.getTaxa(), tj.getTaxa()).size());
	}

	private double resolutionScore(Tree scm) {
		return this.clusterScore(scm) / (double) (scm.getExternalNodes().size() - 1);
	}

	private double overlapScore(Tree ti, Tree tj) {
		return (double) SetUtils.Cross(ti.getTaxa(), tj.getTaxa()).size();
	}

	private double degreeScore(Tree scm) {
		int max = 0;
		for (Node n : scm.getInternalNodes()) {
			int degree = scm.getAdjacencies(n).size();
			if (degree > max) {
				max = degree;
			}
		}
		return (double) max;
	}

	private double getScore(Tree scm, Tree ti, Tree tj) {
		if (type.equals(Scoring.cluster)) {
			return this.clusterScore(scm);
		}
		else if (type.equals(Scoring.uniquetaxa)) {
			return this.uniquetaxaScore(ti, tj);
		}
		else if (type.equals(Scoring.resolution)) {
			return this.resolutionScore(scm);
		}
		else if (type.equals(Scoring.overlap)) {
			return this.overlapScore(ti, tj);
		}
		else if (type.equals(Scoring.degree)) {
			return this.degreeScore(scm);
		}
		else {
			return 0;
		}
	}

	public String doMerge() throws Exception {
		boolean err = false;
		while (tmp.size() > 2) {
			int optI = -1, optJ = -1;
			double max = 0, min = Double.MAX_VALUE;
			Tree optScm = null;
			for (int i = 0; i < tmp.size() - 1; i++) {
				for (int j = i + 1; j < tmp.size(); j++) {
					double score;
					if (type.equals(Scoring.overlap)) {
						score = this.getScore(scm, tmp.get(i), tmp.get(j));						
						if (score > max) {
							if (score > 2) {
								max = score;
								optI = i;
								optJ = j;
							}
						}
					}
					else if (type.equals(Scoring.uniquetaxa)) {
						score = this.getScore(scm, tmp.get(i), tmp.get(j));
						if (score < min) {
							if (this.overlapScore(tmp.get(i), tmp.get(j)) > 2) {
								min = score;
								optI = i;
								optJ = j;
							}
						}
					}
					else {
						Tree scm = this.merge(tmp.get(i), tmp.get(j));					
						if (scm != null) {							
							score = this.getScore(scm, tmp.get(i), tmp.get(j));
							if (type.equals(Scoring.degree)) {								
								if (score < min) {
									min = score;
									optI = i;
									optJ = j;
								}
							}
							else {
								if (score > max) {
									max = score;
									optI = i;
									optJ = j;
								}
							}
						}
					}
				}
			}
			if (optI == -1) {
				System.err.println("Not enough overlap taxa (< 3).");
				err = true;
				break;
			}
			else {
				optScm = this.merge(tmp.get(optI), tmp.get(optJ));
			}
			if (optI > optJ) {
				tmp.remove(optI);
				tmp.remove(optJ);
			}
			else {
				tmp.remove(optJ);
				tmp.remove(optI);
			}
			tmp.add(optScm);
		}
		if (!err) {
			scm = merge(tmp.get(0), tmp.get(1));
			if (scm != null) {
				scm = new MutableTree(scm);
				((MutableTree) scm).setEdgeLength(1.0);
			}
			tmp.remove(0);
			tmp.remove(0);
		}
			
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);		
		printSCM();		
		System.setOut(old);
		System.out.println("Here: " + baos.toString());
		return baos.toString();
	}
	
	public void printSCM() throws IOException {
		PrintWriter writer = new PrintWriter(System.out);
		NewickExporter exp = new NewickExporter(writer);
		exp.exportTree(scm);
	    writer.flush();
	    writer.close();	
	}

	public void exportSCM(String fileName) throws Exception {
		File trees = new File(fileName);
		if(!trees.exists())
			trees.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(trees));
		NewickExporter exp = new NewickExporter(bw);
		exp.exportTree(scm);
		bw.close();
	}
}