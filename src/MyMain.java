import java.util.Scanner;

public class MyMain {

	public static void main(String[] args) {
		int width, height;
		int STONE_CHARACTER = 'k';
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
			}
		}
		
		scanner.close();

		evolver.InitEvolve(initialMap, height,width);
	}

}
