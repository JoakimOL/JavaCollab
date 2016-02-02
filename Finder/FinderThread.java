import java.util.ArrayList;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.nio.file.Path;
/**
*Trad som soker etter et gitt query
*@author Joakim Lier
*/
public class FinderThread implements Runnable{	
	static private int nr;
	private final int id;
	private String query;
	private File[] possibleRoutes;
	private File currentDir;
	private File[1] noGo = {new File("C:\\windows\\"),null};
	private Monitor m;
	private CountDownLatch barrier;

	public FinderThread(String query,File currentDir,Monitor m,CountDownLatch barrier){
		this.currentDir = currentDir;
		this.query = query;
		this.m = m;
		this.barrier = barrier;
		possibleRoutes = findPossibleRoutes();
		id = nr;
		nr++;
		System.out.format("trad nummer %d, i mappe %s\n",id,currentDir);
	}

	/**
	*henter mappen traden er i
	*@return current directory
	*/
	public File getCurrentDir(){
		return currentDir;
	}

	/**
	*sett hvilke mapper som ikke skal sokes i.
	*@param noGo fil som ikke skal sokes i.
	*/
	public void setNoGo(File noGo){
		this.noGo[1] = noGo;
	}

	/**
	*finner mapper i currentDir som kan sokes i.
	*@return array med mapper
	*/
	public File[] findPossibleRoutes(){
		File[] temp = currentDir.listFiles();
		ArrayList<File> temp2 = new ArrayList<>();
		for(File f: temp){
			if(f.isDirectory()){
				temp2.add(f);
			}
		}
		temp = null;
		possibleRoutes = new File[temp2.size()];
		for(int i = 0; i < temp2.size(); i++){
			possibleRoutes[i] = temp2.get(i);
		}
		return possibleRoutes;
	}
	/**
	*genererer en liste over mapper en kan sjekke.
	*@param f Filen som skal finne ruter
	*@return Liste over mapper
	*/
	public File[] findPossibleRoutes(File f){
		File[] temp = f.listFiles();
		ArrayList<File> temp2 = new ArrayList<>();
		if(temp != null){
			for(File fil: temp){
				if(f != null){
					if(fil.isDirectory()){
						temp2.add(fil);
					}
				}	
			}
		}
		temp = null;
		possibleRoutes = new File[temp2.size()];
		for(int i = 0; i < temp2.size(); i++){
			possibleRoutes[i] = temp2.get(i);
		}
		return possibleRoutes;
	}

	/**
	*startup. Goes up to all possible routes
	*@return true
	*/
	public boolean goUp(){
		search(query,currentDir);
		File[] routes = findPossibleRoutes();
		for(File f: possibleRoutes){
			//System.out.println(f);
			if(f != noGo && f != null){
				goUp(f);
			}
		}
		return true;
	}
	/**
	*Go up from file
	*@param Fil file to go up from
	*@return true
	*/
	public boolean goUp(File fil){
		search(query,fil);
		File[] routes = findPossibleRoutes(fil);
		for(File f: possibleRoutes){
			//System.out.format("soker i %s\n",f);
			//System.out.println(f);
			if(f != noGo && f != null){
				goUp(f);
			}
		}
		return true;
	}

	/**
	*searches current directory for query
	*@param query query to search for 
	*@param dir what directory to search in
	*@return true if found, else false
	*/
	public boolean search(String query, File dir){
		File[] dirFiles = dir.listFiles();
		if(dirFiles == null){
			return false;
		}
		for(File f: dirFiles){
			if(f.getPath().equals(dir.getPath()+"\\"+query)){
				//System.out.format("trad nummer %d FANT DEN!\ndir: %s\n", id,f.toString());
				if(m.leggTil(f)){
					System.out.println("Funnet i " + f);
				}
				return true;
			}
		}
		return false;
	}

	/**
	*start thread
	*/
	@Override
	public void run(){
		//System.out.format("\ntrad nummer %d sin nogo: %s\n",id, noGo == null ? "":noGo.toString()+"\n");
		search(query,currentDir);
		goUp();
		System.out.format("trad nummer %d er ferdig.\n",id);
		barrier.countDown();
	}
	/**
	* returns path as a string for string representation
	* @return path as string
	*/
	@Override
	public String toString(){
		return currentDir.toString();
	}
}