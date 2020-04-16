import java.util.ArrayList;
import java.util.List;

public class Subject implements Comparable<Subject>{

	List<Gene> geneList = new ArrayList<>();
	int fitness;

	public List<Gene> getGeneList() {
		return geneList;
	}

	public void setGeneList(List<Gene> geneList) {
		this.geneList = geneList;
	}
	public void addGene(Gene gene)
	{
		geneList.add(gene);
	}
	public void changeGene(Gene gene, int position)
	{
		geneList.set(position, gene);
	}
	
	public Gene getGene(int index)
	{
		return geneList.get(index);
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	//Funkcia sluzi na porovnavanie, ktory jedinec ma vacsiu fitness funkciu
	@Override
	public int compareTo(Subject o) {
		return o.getFitness() - getFitness();  
	}
	
}

class Gene {
	int x,y;
	int rotation;
	
	public Gene(int x,int y, int rotation)
	{
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	public int getY() {	return y;}
	public int getX() { return x;}
	public int getRotation() {	return rotation;}
}
