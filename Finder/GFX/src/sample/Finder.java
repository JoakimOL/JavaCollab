import java.io.File;
import java.util.regex.*;
import java.nio.file.Path;
import java.util.*;
import java.util.ArrayList;
import java.io.*;
import java.util.concurrent.CountDownLatch;
/**
*@author Joakim Lier
*/
public class Finder{
	private int stepsUp;
	private int stepsFromRoot;
	private String query;
	private boolean args;
	private String drive;
	
	private File f;
	private File currentDir;
	private File[] filesInCurrentDir;

	private Monitor m;
	private CountDownLatch barrierdown;
	private CountDownLatch barrierup;
	private Thread[] oversiktDown;
	private ArrayList<Thread> oversiktUp = new ArrayList<>();
	private String PATTERN;
	
	public Finder(String query,boolean args){
		//stepsUp = goToTop();
		this.query = query;
		this.args = args;
		currentDir =new File(System.getProperty("user.dir"));
		oversiktDown = new Thread[currentDir.toPath().getNameCount()+1];
		stepsFromRoot =oversiktDown.length;
		m = new Monitor(stepsFromRoot);
		barrierdown = new CountDownLatch(currentDir.toPath().getNameCount()+1);
	}
	/**
	*Starter trader, barriere og til slutt printer resultat
	*/
	public void start(){
		for(int i = 0; i < stepsFromRoot; i ++){
			oversiktDown[i] = new Thread(new FinderThread(query,currentDir,m,barrierdown,false,args));
			goDown();
		}
		
		for(int i = oversiktDown.length-1; i > -1; i--){
			oversiktDown[i].start();
		}

		int temp =sjekkOppover();
		barrierup = new CountDownLatch(temp);
		goToTop();
		for(Thread t: oversiktUp){
			t.start();
		}

		//venter pa at trader blir ferdige
		try{
			barrierdown.await();
			barrierup.await();
		}
		catch(InterruptedException ex){
			return;
		}
		finally{
			m.print();
		}
	}

	/**
	*goes an arbitrary path upwards till it reaches a dead end.
	*@return number of steps upwards
	*/
	public int goToTop(){
		goUp();
		FinderThread temp = new FinderThread(query,currentDir,m,barrierup,true,args);
		int teller = 1;
		while(goUp()){
			teller++;
			if(!currentDir.equals(temp.getCurrentDir())){
				temp.setNoGo(currentDir);
			}
			//System.out.format("\ntrad id: %d sin nogo satt til:\n%s\n",temp.getId(),currentDir);
			oversiktUp.add(new Thread(temp));
			temp = new FinderThread(query,currentDir,m,barrierup,true,args);
		}
		oversiktUp.add(new Thread(temp));
		return teller;
	}

	public int sjekkOppover(){
		File oldDir = currentDir;
		goUp();
		int teller = 1;
		while(goUp()){
			teller++;
		}
		currentDir = oldDir;
		return teller;
	}
	
	/**
	*Goes up to program files if available, else arbitrary
	*@return true if successful, false if no path available
	*/
	public boolean goUp(){
		File[] temp = currentDir.listFiles();
		File directory = null;
		if(currentDir.equals(new File("C:\\"))){
			directory = new File("C:\\Program Files");
		}
		//else if(!directory.exists()){
		else{
			String s1 = currentDir + "\\Common Files";
			for(File f: temp){
				if(f.isDirectory() && !f.isHidden() && !f.toString().equals(s1)){
					directory = f;
					break;
				}
			}
		}

		if(directory != null){
			currentDir = directory;
			return true;
		}
		return false;
	}

	/**
	* go down one step.
	* no longer assuming C-drive.
	* windows exclusive.
	*@return true if successful, false if root
	*/
	public boolean goDown(){
		drive = System.getenv().get("HOMEDRIVE");
		try{
			currentDir = new File(drive + "\\" + currentDir.toPath().subpath(0,currentDir.toPath().getNameCount()-1).toFile().getPath());
			filesInCurrentDir = currentDir.listFiles();
			return true;
		}
		catch(IllegalArgumentException e){
			currentDir = new File(drive+"\\");
			return false;
		}
	}
	/**
	*returns the value of the stepsUp variable
	*@return number of steps upwards till end
	*/
	public int getStepsUp(){
		return stepsUp;
	}
}