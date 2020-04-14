
public class Evolver {

	int height, width;
	int[][] initialMap;
	
	static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, NOT_FOUND = -1;
	
	public void InitEvolve(int[][] map,int height, int width)
	{
		this.height = height;
		this.width = width;
		initialMap = map;
		
		Subject testSubject = new Subject();
		testSubject.addGene(new Gene(2, 0,LEFT));
		testSubject.addGene(new Gene(0, 5,RIGHT));
		testSubject.addGene(new Gene(0, 6,RIGHT));
		testSubject.addGene(new Gene(0, 7,RIGHT));
		testSubject.addGene(new Gene(8, 9,RIGHT));
		testSubject.addGene(new Gene(9, 9,RIGHT));
		testSubject.addGene(new Gene(10, 9,RIGHT));
		testSubject.addGene(new Gene(10, 0,LEFT));
		testSubject.addGene(new Gene(11, 0,LEFT));
		testSubject.addGene(new Gene(8, 0,LEFT));
		
		CalculateFitness(testSubject,initialMap);

	}
	
	private int CalculateFitness(Subject subject, int[][] mapInc)
	{
		//Smer: 0 dole, 1 hore, 2 dolava, 3 doprava
		int direction = -1, iteration = 0;
		int[] fitness = new int[1];
		int[][] map = getArrayCopy(mapInc);
	
		fitness[0] = 0;	//Urobil som to tak, pretoze inak sa ta hodnota neprepisuje
		for (Gene gene : subject.getGeneList()) {
			int x = gene.getX();
			int y = gene.getY();
			int rotation = gene.getRotation(); //Urcuje, kam sa bude hybat ak zaboci - Vlavo alebo vpravo
			iteration++;
			
			//Vyberiem zaciatocny smer
			//Posuvam sa, aby som zacal pred mapou
			if(y == 0) {
				y-=1;	
				direction = DOWN;
			}
			else if (y == height-1) {
				y+=1;
				direction = UP;
			}
			else if (x == 0) {
				x-=1;
				direction = RIGHT;
			}
			else if (x == width-1) {
				x+=1;
				direction = LEFT;
			}
			
			while(direction != NOT_FOUND)
			{
				direction = Move(direction, iteration, map,x,y,0,fitness,rotation);
				
				//Posunutie na mape
				switch (direction) {
				case UP:	y-=1;					
					break;
				case DOWN: 	y+=1;
					break;
				case LEFT: 	x-=1;
					break;
				case RIGHT:	x+=1;
					break;
				default:
					break;
				}
				
			}
			
		}		
		
		//Debug - vypis mapy
		System.out.println("\nFINAL MAP: ");
		for(int i=0;i<height;i++)
		{
			for(int j=0;j<width;j++)
			{
				if(map[i][j] == -1)
					System.out.print("K ");
				else
					System.out.print(map[i][j]+" ");
			}
			System.out.print("\n");
		}
		System.out.println("\n Fitness hodnota je "+fitness[0]);
		
		return fitness[0];		
	}
	
	//Tato funkcia vracia smer, ktorym som sa pohol
	private int Move(int direction, int step, int[][] map, int x, int y, int attempt, int[] fitness, int rotation)
	{
		//System.out.println("GOING "+direction);
		//Toto by sa nemalo nikdy stat ale pre istotu
		if(direction == NOT_FOUND)
		{
			System.out.println("Chyba! -1 nemoze byt smer!");
			return NOT_FOUND;
		}
		
		//Toto znamena, ze sme sa uz otocili do 4 smerov a nenasli sme teda cestu
		if(attempt > 3)
		{
			System.out.println("Reached max attempts");
			return NOT_FOUND;
		}
				
		switch (direction) {
		case UP:	
			try {
				if(map[y-1][x] != 0) {
					if(rotation == LEFT)
						return Move(LEFT, step, map, x, y, attempt+1,fitness,rotation);
					else
						return Move(RIGHT, step, map, x, y, attempt+1,fitness,rotation);
				}
				map[y-1][x] = step;
				fitness[0]++;
				return UP;
			} catch (ArrayIndexOutOfBoundsException e) {
				return NOT_FOUND;
			}
		case DOWN: 	
			try {
				if(map[y+1][x] != 0) {
					if(rotation == LEFT)
						return Move(RIGHT, step, map, x, y, attempt+1,fitness,rotation);
					else
						return Move(LEFT, step, map, x, y, attempt+1,fitness,rotation);
				}
				map[y+1][x] = step;
				fitness[0]++;
				return DOWN;
			} catch (ArrayIndexOutOfBoundsException e)
			{
				return NOT_FOUND;
			}
			
		case LEFT:
			try {
				if(map[y][x-1] != 0) {
					if(rotation == LEFT)
						return Move(DOWN, step, map, x, y, attempt+1,fitness,rotation);
					else
						return Move(UP, step, map, x, y, attempt+1,fitness,rotation);
				}		
				map[y][x-1] = step;
				fitness[0]++;
				return LEFT;
			} catch (ArrayIndexOutOfBoundsException e) {
				return NOT_FOUND;
			}
			
		case RIGHT:	
			try {
				if(map[y][x+1] != 0) {
					if(rotation == LEFT)
						return Move(UP, step, map, x, y, attempt+1,fitness,rotation);
					else
						return Move(DOWN, step, map, x, y, attempt+1,fitness,rotation);
				}
				map[y][x+1] = step;
				fitness[0]++;
				return RIGHT;
			} catch (ArrayIndexOutOfBoundsException e) {
				return NOT_FOUND;
			}
			
		default:
			return NOT_FOUND;
		}
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
