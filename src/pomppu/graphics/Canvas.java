/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

import java.awt.Dimension;

/**
 * Rajapinta Screen-luokalle, joka on vastuussa peligrafiikan piirtämisestä
 * näytölle ja näin pelaajan ulottuville. Sen vastuulla on myös ohjelman ikkunan luominen sekä
 * alustaminen.
 * 
 * @author arkivika
 *
 */

public interface Canvas {

	/**
	 * Aksessori, joka tyhjentää piirrettävien Drawable-olioiden jonon.	
	 */
	public void clear();
	
	/**
	 * Lisää jonoon piirrettävän elementin, joka sisältää Drawable-rajapinnan toteuttavan olion sekä sen koordinaatit.
	 * @param elem Piirrettävien jonoon lisättävä Visibleelement-olio.
	 */
	public void addElement(VisibleElement elem);
	
	/**
	 * Aksessori, joka piirtää jonossa olevat Drawable-rajapinnan toteuttavat oliot.
	 */
	public void draw();

	/**
	 * Aksessori, joka palauttaa arvonaan ikkunan leveyden.
	 * @return Ikkunan leveyden.
	 */
	public int getWidth();

	/**
	 * Aksessori, joka palauttaa arvonaan ikkunan korkeuden.
	 * @return Ikkunan korkeuden.
	 */
	public int getHeight();
	
	/**
	 * Aksessori, joka poistaa num-määrän elementtejä piirrettävän jonon alusta.
	 * @param num Poistettavien elementtien määrä.
	 */
	public void clearTop(int num);	
	
	/**
	 * Aksessori, joka palauttaa arvonaan ikkunan reunojen mitat.
	 * @return Ikkunan reunojen mitat.
	 */
	public Dimension getInsets();

}
