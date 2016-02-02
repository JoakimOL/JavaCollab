import java.util.ArrayList;
import java.io.File;
/**
*@author Joakim Lier
*/
public class Monitor{
	private ArrayList<File> liste = new ArrayList<>();

	private final int antallThreads;

	public Monitor(int threads){
		antallThreads = threads;
	}

	/**
	*Legger til fil, hvis den ikke finnes allerede
	*@param f Fila som er funnet
	*@return true hvis lagt til, false ellerss
	*/
	synchronized public boolean leggTil(File f){
		if(!liste.contains(f)){
			liste.add(f);
			return true;
		}
		return false;
	}

	/**
	*Legger til en gitt fil i oversikten over mapper som ikke skal sokes i.
	*@param f Filen som skal legges til i nogo-arrayen, slik at ingen andre trader soker
	*		i den filen
	*/
	synchronized public void leggInnNogo(File f){
		return;
	}
	/**
	* Lar en trad hente sin riktige nogo-mappe basert pa ID
	*@param index Henter ut riktig nogo directory til trad basert pa ID
	*@return mappen som er nogo
	*/
	synchronized public File hentNoGo(){
		return null;
	}

	/**
	*printer ut resultatet
	*/
	synchronized public void print(){
		System.out.println("Treff i: ");
		for(File f: liste){
			System.out.format("funnet i %s\n",f);
		}
	}
}