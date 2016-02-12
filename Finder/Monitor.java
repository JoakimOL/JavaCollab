import java.util.ArrayList;
import java.io.File;
/**
*@author Joakim Lier
*/
public class Monitor{
	private ArrayList<File> liste = new ArrayList<>();
	private File[] noGoList;
	private int teller = 0;
	/**
	*constructor, sets up a list of nogo-directories for the threads made on the way down.
	*@param threads number of made on the way down, not up.
	*/
	public Monitor(int threads){
		noGoList = new File[threads];
	}

	/**
	*Legger til fil, hvis den ikke finnes allerede
	*@param f Fila som er funnet
	*@return true hvis lagt til, false ellers
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
		noGoList[teller++] = f;
	}

	/**
	* Lar en trad hente sin riktige nogo-mappe basert pa ID
	*@param index Henter ut riktig nogo directory til trad basert pa ID
	*@return mappen som er nogo
	*/
	synchronized public File hentNoGo(int index){
		int temp = index-1;
		if(temp < 0){
			return null;
		}
		return noGoList[temp];
	}
	/**
	*printer ut resultatet
	*/
	synchronized public void print(){
		System.out.println("Treff i: ");
		for(File f: liste){
			System.out.format("- %s\n",f);
		}
	}
	/**
	*get element from found files - liste - and return it
	*@param index index i as int
	*@return file-element at index i
	*/
	public File getElement(int index){
		return liste.get(index);
	}
}