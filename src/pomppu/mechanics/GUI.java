/**
 * Pelimoottorin pakkaus, sisältää 2d-pelille ominaiset geneeriset toiminnallisuudet.
 */
package pomppu.mechanics;

import java.util.*;
import pomppu.graphics.*;

/**
 * Mahdollistaa peligrafiikan päälle piirrettävän komponentin, jolle asetetaan tilasta riippuen esimerkiksi valikkotilojen 
 * grafiikkaa tai pelitilan pelaajatietoja.<br><br>
 * GUI:n elementit ovat jaettu yhdeksään osaan seuraavasti:<br><br>
 * [0,0][1,0][2,0]<br>
 * [0,1][1,1][2,1] = ruutu jaettuna 3*3 matriisiksi.<br>
 * [0,2][1,2][2,2]<br>
 * @author arkivika
 */
public class GUI {
	
	private ArrayList<ArrayList<Queue<Drawable>>> drawables;
	private int padding;
	private Screen screen;
	
	/**
	 * Rakentaa 3*3 matriisin jonoista (Queue) eri osioiden Drawable-rajapinna toteuttavien olioiden (teksti, kuva, animaatio..) säilyttämiseksi.
	 * @see java.util.Queue	
	 */
	public GUI(Screen _screen) {
	
		padding = 0;
		drawables = new ArrayList<ArrayList<Queue<Drawable>>>();

		screen = _screen;
			
		for (int i=0; i<3; i++) {
			drawables.add(new ArrayList<Queue<Drawable>>());
			for (int j=0; j<3; j++)
				drawables.get(i).add(new LinkedList<Drawable>());
		}
	}
	
	/**
	 * Aksessori, joka tyhjentää halutun osion.
	 * @param x Halutun osion x-koordinaatti.
	 * @param y Halutun osion y-koordinaatti.
	 */
	public void clearSection(int x, int y) {
		drawables.get(x).get(y).clear();		
	}
	
	/**
	 * Aksessori, joka lisää Drawable-olion haluttuun osioon.
	 * @param drawable Haluttu Drawable-olio.
	 * @param x Halutun osion x-koordinaatti.
	 * @param y Halutun osion y-koordinaatti.
	 */
	public void addToSection(Drawable drawable, int x, int y) {
		drawables.get(x).get(y).add(drawable);		
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan tämänhetkisen padding-arvon.
	 * @return Padding-arvo. 
	 */
	public int getPadding() {
		return padding;
	}
	
	/**
	 * Aksessori, jonka avulla voidaan asettaa haluttu padding-arvo, eli se, kuinka monta pikseliä GUI:n reunimmaisten elementtien
	 * tulee olla peli-ikkunan reunasta pois päin.
	 * @param _padding Haluttu padding-arvo (>=0).
	 */
	public void setPadding(int _padding) {
		if (_padding < 0)
			return;
		padding = _padding;
	}
	
	/**
	 * Aksessori, joka kertoo, mikäli tietty x,y-koordinaattiarvo osuu GUI:n tietyn osion tiettyyn elementtiin. 	
	 * @param i GUI:n osion vaakatasoinen indeksi.
	 * @param j GUI:n osion pystysuuntainen indeksi.
	 * @param x Haluttu x-koordinaattiarvo.
	 * @param y Haluttu y-koordinaattiarvo.
	 * @return Elementin indeksi, johon osutaan määritellyllä x,y-koordinaatilla, -1 mikäli osumaa ei tapahdu.
	 */
	public int touchesElement(int i, int j, int x, int y) {

		int num = 0;
		int offset = 0;
		
		for (Drawable drawable : drawables.get(i).get(j)) {
		
			int draw_x = getRealX(i, drawable);
			int draw_y = getRealY(j, drawable);
			
			draw_y += offset;
			
			if (x >= draw_x && x <= draw_x+drawable.getWidth() && 
				y >= draw_y && y <= draw_y+drawable.getHeight()) {
			 return num;
			}
			
			offset += drawable.getHeight() + 2;
			num++;
		}
				
		return -1; 
	}

	/**
	 * Aksessori, joka kertoo tietyn GUI-olion elementtien määrän.
	 * @return GUI-olion elementtien määrä.
	 */
	public int size() {
		
		int size=0;
		
		for (int i=0; i<3; i++)
			for (int j=0; j<3; j++)
				size += drawables.get(i).get(j).size();
	
		return size;
	}
	
	/**
	 * Käy läpi GUI-olion ja palauttaa arvonaan listan sen sisältämistä elementeistä.
	 * @return Lista GUI-olion sisältämistä elementeistä.  
	 */
	public ArrayList<VisibleElement> render() {
		
		ArrayList<VisibleElement> elements = new ArrayList<VisibleElement>();

		for (int i=0; i<3; i++)
			for (int j=0; j<3; j++) {

				int offset = 0;
				
				for (Drawable drawable : drawables.get(i).get(j)) {

					int temp_x = getRealX(i, drawable);
					int temp_y = getRealY(j, drawable);
					
					elements.add(new VisibleElement(drawable, temp_x, temp_y+offset));
					
					offset += drawable.getHeight() + 2;			
				}
			}
		
		return elements;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan halutun elementin todellisen x-koordinaatin.
	 * @param i Halutun elementin osion vaakatasoinen indeksi.
	 * @param drawable Halutun elementin Drawable-rajapinnan toteuttava olio (teksti, kuva, animaatio..).
	 * @return Elementin todellinen x-koordinaatti.
	 */
	private int getRealX(int i, Drawable drawable) {
	
		int draw_x = i * (screen.getWidth()/2);
		
		switch(i) {
			case 0:
				draw_x += padding;
				break;
			case 1:
				draw_x -= drawable.getWidth()/2;
				break;
			case 2:
				draw_x -= drawable.getWidth() + padding;
		}
	
		return draw_x;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan halutun elementin todellisen y-koordinaatin.
	 * @param j Halutun elementin osion pystysuuntainen indeksi.
	 * @param drawable Halutun elementin Drawable-rajapinnan toteuttava olio (teksti, kuva, animaatio..).
	 * @return Elementin todellinen y-koordinaatti.
	 */
	private int getRealY(int j, Drawable drawable) {

		int draw_y = j * (screen.getHeight()/2);
		
		switch(j) {
			case 0:
				draw_y += padding;
				break;
			case 1:
				draw_y -= drawable.getHeight()/2;
				break;
			case 2:
				draw_y -= drawable.getHeight() + padding;
		}
		
		return draw_y;
	}
}