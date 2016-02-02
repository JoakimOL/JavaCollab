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

		Scanner sc = new Scanner(System.in);
		System.out.println("tast inn navn pa fil du vil soke pa");
		Finder finder = new Finder(sc.nextLine());
		long forSok = System.currentTimeMillis();
		finder.start();
		System.out.println("tid: " + (System.currentTimeMillis()-forSok));	
	}
}