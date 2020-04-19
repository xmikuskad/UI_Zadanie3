import java.util.Random;
import java.util.Scanner;

public class MyMain {

	static int STONE_CHARACTER = -1;
	
	public static void main(String[] args) {
		int width, height, stoneCount = 0;
		Evolver evolver = new Evolver();
		
		//Nacitanie vstupu
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Vyska: ");
		height = scanner.nextInt();
		System.out.println("Sirka: ");
		width = scanner.nextInt();	
		
		int[][] initialMap = new int[height][width];		
		System.out.println("Aktualny stav: (k kamen, 0 nic)");		
		for(int i=0;i<height;i++)
		{
			for(int j=0;j<width;j++)
			{
				initialMap[i][j]= scanner.nextInt(); 
				if(initialMap[i][j] == STONE_CHARACTER)	stoneCount++;
			}
		}
		
		scanner.close();

		//testRandomMap();
		
		evolver.initEvolve(initialMap, height,width,stoneCount);
		
	}
	
	private static void testRandomMap()
	{
		final Random random = new Random();
		int height = random.nextInt(10)+10;
		int width = random.nextInt(10)+10;
		int stoneCount = 0;
		
		int[][] initialMap = new int[height][width];		
		
		
		for(int i=0;i<height;i++)
		{
			for(int j=0;j<width;j++)
			{
				if(Math.random() <0.1) {
					initialMap[i][j] = STONE_CHARACTER;	
					stoneCount++;
				}
				else {
					initialMap[i][j] = 0;
				}
			}
		}		
		
		System.out.println("H: "+height+" W: "+width+" K: "+stoneCount + " MAX FIT: "+(height*width-stoneCount));
		Evolver evolver = new Evolver();
		evolver.printMap(initialMap,width,height,true);
		evolver.initEvolve(initialMap, height,width,stoneCount);
	}

}
