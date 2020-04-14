import java.util.ArrayList;
import java.util.List;

public class Subject {

	List<Gene> geneList = new ArrayList<>();

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
