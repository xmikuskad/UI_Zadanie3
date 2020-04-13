
public class Evolver {

	int height, width;
	int[][] initialMap;
	
	public void InitEvolve(int[][] map,int height, int width)
	{
		this.height = height;
		this.width = width;
		initialMap = map;
	}
	
	private void CalculateFitness(Subject subject)
	{
		
	}
	
	//Vytvori kopiu pola... Nenasiel som ziadnu java funkciu, ktora by to robila pre 2 rozmerne pole
	private int[][] getArrayCopy(int[][] arrayInc)
	{
		int[][] newArray = new int[height][width];
		
		for(int i=0; i<arrayInc.length; i++)
			  for(int j=0; j<arrayInc[i].length; j++)
			    newArray[i][j]=arrayInc[i][j];
		
		return newArray;
	}
	
}
