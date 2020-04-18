import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Evolver {

	int height, width, stoneCount;
	int[][] initialMap;
	List<Subject> actualGen;
	List<Gene> geneList;
	
	
	//nacitanie z konfigu
	int generationCount,geneCount,eliteCount,repeatCount;
	double mutationRate;
	
	static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, NOT_FOUND = -1;
	static final boolean PRINT_DEBUG = false;
	
	public void initEvolve(int[][] map,int height, int width, int stoneCount)
	{
		this.height = height;
		this.width = width;
		this.stoneCount = stoneCount;
		initialMap = map;
		
		if(!loadConfig()) {
			System.out.println("Loading config failed.");
			return;
		}
		
		startEvolve(true, "first_method.csv");		
		startEvolve(false, "second_method.csv");		
	}
	
	private boolean loadConfig()
	{
		try {

			InputStream inputStream = new FileInputStream("src/config.properties");
            Properties properties = new Properties();

            properties.load(inputStream);
            
            generationCount = Integer.valueOf(properties.getProperty("population_size"));
            eliteCount = Integer.valueOf(properties.getProperty("elite_count"));
            repeatCount = Integer.valueOf(properties.getProperty("repeat_count"));
            mutationRate = Double.valueOf(properties.getProperty("mutation_rate"));
            
            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
		
		return false;
	}
	
	
	private void startEvolve(boolean firstMethod, String fileName)
	{
		int sumFitness = 0;
		
		//Priprava na zapisovanie do csv suboru
		StringBuilder sb = new StringBuilder();
		sb.append("Priemer,Maximum\n");
		
		//Vytvorenie prvej generacie
		geneCount = width + height/4 + stoneCount; 
		geneList = getAllGenes();
		actualGen = createFirstGeneration();
		
		for (Subject subject : actualGen) {
			int fitness = getFitness(subject, initialMap);
			subject.setFitness(fitness);
			sumFitness+=fitness;
		}
			
		//Zoradenie podla fitness
		Collections.sort(actualGen);
		printStatus(sb);
		System.out.println("Initial gen done");
		
		//Vykonava cyklus celej evolucie
		for(int i=0;i<repeatCount;i++)
		{
			actualGen = createGeneration(sumFitness,firstMethod);
			sumFitness = 0;
			for (Subject subject : actualGen) {
				int fitness = getFitness(subject, initialMap);
				subject.setFitness(fitness);
				sumFitness+=fitness;
			}
			
			//Zoradenie podla fitness
			Collections.sort(actualGen);
			//System.out.println("Generation "+(i+1)+" - Max fitness: "+actualGen.get(0).getFitness()+"\n\n");
			
			if(i%5 == 0)
			{
				printStatus(sb);
			}
		}
		
		//Zapis do csv suboru
		try {
			PrintWriter writer = new PrintWriter(new File(fileName));
			writer.write(sb.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void printStatus(StringBuilder sb)
	{
		double average = 0;
		for (Subject subject : actualGen) {
			average+=subject.getFitness();
		}
		average/=generationCount;
		System.out.println("Average: "+average+" | Max: "+actualGen.get(0).getFitness());
		
		sb.append(average);
		sb.append(",");
		sb.append(actualGen.get(0).getFitness());
		sb.append("\n");
	}
	
	//Vytvori novu generaciu
	private List<Subject> createGeneration(int sumFitness,boolean firstMethod)
	{
		//Ak je generacia taka zla, ze sa jej nepodarilo nic tak vytvorim novu nahodnu - ano, toto sa mi stalo.
		if(sumFitness == 0) {
			return createFirstGeneration();
		}
		
		List<Subject> newGenerationList = new ArrayList<Subject>();
		
		if(firstMethod) {
			//Elitarstvo
			for(int i=0;i<eliteCount;i++)
			{
				newGenerationList.add(actualGen.get(i));
			}
			
			//Nova generacia pomocou rulety
			while(newGenerationList.size() < generationCount) {
				newGenerationList.add(getEvolvedSubject(getRouletteSubject(sumFitness), getRouletteSubject(sumFitness)));
			}
		}
		else {
			//Vyber podla poradia bez elitarstva
			while(newGenerationList.size() < generationCount) {
				newGenerationList.add(getEvolvedSubject(getOrderSubject(), getOrderSubject()));
			}
		}
		
		return newGenerationList;
	}
	
	//Tato funkcia simuluje ruletu
	private Subject getRouletteSubject(int sumFitness)
	{
		final Random random = new Random();
		int randomIndex = random.nextInt() % sumFitness;
		int actIndex = 0;
		
		for (Subject subject : actualGen) {
			actIndex+=subject.getFitness();
			if(actIndex>= randomIndex)
				return subject;
		}
		
		System.out.println("Roulette error!");
		return null;
	}
	
	private Subject getOrderSubject()
	{
		final Random random = new Random();
		int max = 0;
		
		for(int i =1;i<generationCount;i++) { max+=i;}		
		
		int randomIndex = random.nextInt() % max;
		int actIndex = 0;
		
		for (Subject subject : actualGen) {
			actIndex+=generationCount;
			if(actIndex>= randomIndex)
				return subject;
		}
		
		System.out.println("ORDER error!");
		return null;
	}
	
	//Vykonava crossover a mutaciu
	private Subject getEvolvedSubject(Subject s1, Subject s2)
	{
		Subject newSubject = new Subject();
		final Random random = new Random();
		boolean firstSubject;
		
		//Vykona sa crossover
		for(int i=0;i<geneCount;i++)
		{
			firstSubject = random.nextBoolean(); //Nahodne vyberiem, ci dam gen z prveho alebo druheho jedinca
			Gene newGene;
			
			//Mutacia
			if(Math.random() < mutationRate) {
				//Toto som pouzil predtym ale riesenie naslo moc rychlo
				Collections.shuffle(geneList);
				newGene = geneList.get(0);
				
				/*if(random.nextBoolean())
					newGene = new Gene(random.nextInt()%width, random.nextInt()%height, LEFT);
				else
					newGene = new Gene(random.nextInt()%width, random.nextInt()%height, RIGHT);*/
 			}
			else {		
				//Crossover
				if(firstSubject) {
					newGene = s1.getGene(i);
				}
				else {
					newGene = s2.getGene(i);
				}
			}			
			newSubject.addGene(newGene);						
		}	
		return newSubject;
	}
	
	//Vytvori prvu nahodnu generaciu
	private List<Subject> createFirstGeneration()
	{
		List<Subject> subjects = new ArrayList<>();
			
		for(int i=0; i<generationCount; i++)
		{
			//final Random random = new Random();
			Subject newSubject = new Subject();
			Collections.shuffle(geneList); //Nahodne ich pomiesa
			
			for(int j =0; j<geneCount;j++)
			{
				/*if(random.nextBoolean())
					newSubject.addGene(new Gene(random.nextInt()%width, random.nextInt()%height, LEFT));
				else
					newSubject.addGene(new Gene(random.nextInt()%width, random.nextInt()%height, RIGHT));*/
				
				newSubject.addGene(geneList.get(j));
			}
			subjects.add(newSubject);
		}
		
		return subjects;
	}
	
	private List<Gene> getAllGenes()
	{
		List<Gene> geneList = new ArrayList<>();
		
		for(int i =0; i<width;i++)
		{
			geneList.add(new Gene(i, 0, LEFT));
			geneList.add(new Gene(i, 0, RIGHT));
			geneList.add(new Gene(i, height-1, LEFT));
			geneList.add(new Gene(i, height-1, RIGHT));
		}
		//0 a poslednu uz mam zaratanu hore
		for(int i = 1; i<height-1;i++)
		{
			geneList.add(new Gene(0, i, LEFT));
			geneList.add(new Gene(0, i, RIGHT));
			geneList.add(new Gene(width-1, i, LEFT));
			geneList.add(new Gene(width-1, i, RIGHT));
		}
		
		return geneList;		
	}
	
	private int getFitness(Subject subject, int[][] mapInc)
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

			if(PRINT_DEBUG)
				System.out.println("\nStep "+iteration+" starting x: "+x+" | y: "+y);
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
				direction = move(direction, iteration, map,x,y,0,fitness,rotation);
				
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
			
		if(PRINT_DEBUG)
		{
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
			System.out.println("Fitness hodnota je "+fitness[0]);
		}
		
		return fitness[0];		
	}
	
	//Tato funkcia vracia smer, ktorym som sa pohol
	private int move(int direction, int step, int[][] map, int x, int y, int attempt, int[] fitness, int rotation)
	{
		//Toto by sa nemalo nikdy stat ale pre istotu
		if(direction == NOT_FOUND)
		{
			if(PRINT_DEBUG)
				System.out.println("Chyba! -1 nemoze byt smer!");
			return NOT_FOUND;
		}
		
		//Toto znamena, ze sme sa uz otocili do 4 smerov a nenasli sme teda cestu
		if(attempt > 3)
		{
			if(PRINT_DEBUG)
				System.out.println("Reached max attempts");
			return NOT_FOUND;
		}
				
		try {
			switch (direction) {
			case UP:	
					if(map[y-1][x] != 0) {
						if(rotation == LEFT)
							return move(LEFT, step, map, x, y, attempt+1,fitness,rotation);
						else
							return move(RIGHT, step, map, x, y, attempt+1,fitness,rotation);
					}
					map[y-1][x] = step;
					fitness[0]++;
					return UP;
			case DOWN: 	
					if(map[y+1][x] != 0) {
						if(rotation == LEFT)
							return move(RIGHT, step, map, x, y, attempt+1,fitness,rotation);
						else
							return move(LEFT, step, map, x, y, attempt+1,fitness,rotation);
					}
					map[y+1][x] = step;
					fitness[0]++;
					return DOWN;			
			case LEFT:
					if(map[y][x-1] != 0) {
						if(rotation == LEFT)
							return move(DOWN, step, map, x, y, attempt+1,fitness,rotation);
						else
							return move(UP, step, map, x, y, attempt+1,fitness,rotation);
					}		
					map[y][x-1] = step;
					fitness[0]++;
					return LEFT;			
			case RIGHT:	
					if(map[y][x+1] != 0) {
						if(rotation == LEFT)
							return move(UP, step, map, x, y, attempt+1,fitness,rotation);
						else
							return move(DOWN, step, map, x, y, attempt+1,fitness,rotation);
					}
					map[y][x+1] = step;
					fitness[0]++;
					return RIGHT;		
			default:
				return NOT_FOUND;
			}
		}catch (ArrayIndexOutOfBoundsException e)
		{
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
