import java.util.ArrayList;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.nio.file.Path;
import java.util.regex.*;
/**
*Trad som soker etter et gitt query
*@author Joakim Lier
*/
public class FinderThread implements Runnable{	
	
	static private int nr;
	
	private final int id;
	private final boolean up;
	private final boolean args;
	private String query;
	private File[] possibleRoutes;
	private File currentDir;
	private File[] noGo = {new File("C:\\windows\\"),null,null};
	private Monitor m;
	private CountDownLatch barrier;
	private String PATTERN;
	private Pattern queryP;

	public FinderThread(String query,File currentDir,Monitor m,CountDownLatch barrier, boolean up,boolean args){
		this.currentDir = currentDir;
		this.query = query;
		this.m = m;
		this.args = args;
		this.barrier = barrier;
		possibleRoutes = findPossibleRoutes();
		id = nr;
		nr++;
		this.up = up;
		if(!up){
			m.leggInnNogo(currentDir);
		}
		if(!args){
			System.out.format("trad nummer %d, i mappe %s\n",id,currentDir);
		}

		PATTERN = query;
		queryP = Pattern.compile(PATTERN);
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
	*@param index set which nogo to set
	*/
	public void setNoGo(File noGo,int index){
		if(index > this.noGo.length){
			return;
		}
		this.noGo[index] = noGo;
	}

	/**
	*finner mapper i currentDir som kan sokes i.
	*@return array med mapper
	*/
	public File[] findPossibleRoutes(){
		//Kan garantert gjores bedre
		//Todo
		File[] temp = currentDir.listFiles();
		ArrayList<File> temp2 = new ArrayList<>();
		for(File f: temp)	{
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
		//Kan garantert gjores bedre
		//Todo
		File[] temp = f.listFiles();
		ArrayList<File> temp2 = new ArrayList<>();
		if(temp != null){
			for(File fil: temp){
				if(fil.isDirectory()){
					temp2.add(fil);
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
		if(id == nr){
			return true;
		}
		File[] routes = findPossibleRoutes();
		for(File f: possibleRoutes){
			//System.out.println(f);
			if(f != noGo[0] && f != noGo[1] && f != null){
				goUp(f);
			}
		}
		return true;
	}
	/**
	*Goes up to all possible routes from given file
	*@param fil file to go up from
	*@return true
	*/
	public boolean goUp(File fil){
		search(query,fil);
		File[] routes = findPossibleRoutes(fil);
		for(File f: possibleRoutes){
			if(f != noGo[0] && f != noGo[1] && f != null){
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
			Matcher queryM = queryP.matcher(f.getAbsolutePath());
			if(queryM.find()){
				if(m.leggTil(f)){
					if(!args){
						System.out.println("Funnet i " + f);
					}

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
		if(!up){
			noGo[1] = m.hentNoGo(id);
		}
		//System.out.format("dette er trad nummer %d, min currentdir er %s\nmine nogo dirs er \n-%s\n-%s\n",id,currentDir,noGo[0],noGo[1]);
		search(query,currentDir);
		if(id != nr-1){
			goUp();
		}
		barrier.countDown();
		if(!args){
			System.out.format("trad nummer %d er ferdig.\n",id);
		}

	}
	/**
	* returns path as a string for string representation
	* @return path as string
	*/
	@Override
	public String toString(){
		return currentDir.toString();
	}
	/**
	* returns the id of this thread as an int
	*@return id this threads id number
	*/
	public int getId(){
		return id;
	}
}