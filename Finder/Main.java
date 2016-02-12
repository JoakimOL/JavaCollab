import java.io.BufferedReader;
import java.io.File;
import java.util.Scanner;
/**
*@author Joakim Lier
*/
public class Main{
	public static void main(String[] args){
		//FinderThread thread = new FinderThread("Test.txt",new File("C:\\users\\"));
		//thread.run();
		Finder finder;
		if(args.length != 0){
			finder = new Finder(args[0],true);
			long forSok = System.currentTimeMillis();
			finder.start();
			System.out.println("tid: " + (System.currentTimeMillis()-forSok));
		}
		else{
			Scanner sc = new Scanner(System.in);
			System.out.println("tast inn navn pa fil du vil soke pa");
			finder = new Finder(sc.nextLine(),false);
			long forSok = System.currentTimeMillis();
			finder.start();
			System.out.format("tid: %dms",(System.currentTimeMillis()-forSok));	
		}
	}
}