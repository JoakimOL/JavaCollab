import java.io.File;
import java.util.regex.*;
import java.nio.file.Path;
import java.util.*;
import java.io.*;
import java.util.concurrent.CountDownLatch;
/**
*@author Joakim Lier
*/
public class Finder{
	private int stepsFromRoot;
	private String query;
	private File f;
	private File currentDir;
	private File[] filesInCurrentDir;

	private Monitor m;
	private CountDownLatch barrier;
	private Thread[] oversikt;

	private PATTERN;
	
	public Finder(String query){
		
		PATTERN = query;
		Pattern queryP = Pattern.compile(PATTERN);
		
		currentDir =new File(System.getProperty("user.dir"));
		oversikt = new Thread[f.toPath().getNameCount()+1];
		System.out.println(oversikt.length);
		System.out.println(query);
		stepsFromRoot =oversikt.length;
		m = new Monitor(stepsFromRoot);
		barrier = new CountDownLatch(f.toPath().getNameCount()+1);
	}
	/**
	*Starter trader, barriere og til slutt printer resultat
	*/
	public void start(){
		for(int i = 0; i < stepsFromRoot; i ++){
			oversikt[i] = new Thread(new FinderThread(query,currentDir,m,barrier));
			goDown();
		}
		for(int i = oversikt.length-1; i > -1; i--){
			oversikt[i].start();
		}

		//venter pa at trader blir ferdige
		try{
			barrier.await();
		}
		catch(InterruptedException ex){
			return;
		}
		finally{
			m.print();
		}
	}
	
	/**
	* go down one step.
	* no longer assuming C-drive.
	* windows exclusive.
	*@return true if successful, false if root
	*/
	public boolean goDown(){
		String drive = System.getenv().get("HOMEDRIVE");
		try{
			currentDir = new File(drive + "\\" + currentDir.toPath().subpath(0,currentDir.toPath().getNameCount()-1).toFile().getPath());
			filesInCurrentDir = currentDir.listFiles();
			return true;
		}
		catch(Exception e){
			currentDir = new File(drive+"\\");
			return false;
		}
	}
}