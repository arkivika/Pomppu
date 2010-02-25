/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

import java.awt.Graphics2D;

/**
 * Rajapinta piirrettäville elementeille.
 * @author arkivika
 */
public interface Drawable {
	
	/**
	 * Aksessori, joka piirtää halutun elementin. Mikäli kyseessä on animaatio, metodi myös edistää sitä (vain jos autoAnim on päällä). 
	 */
	public void draw(Graphics2D g, int x, int y);
	
	/**
	 * Aksessori, joka palauttaa arvonaan elementin leveyden.
	 * @return Elementin leveys.
	 */
	public int getWidth();

	/**
	 * Aksessori, joka palauttaa arvonaan elementin korkeuden.
	 * @return Elementin korkeus.
	 */
	public int getHeight();
	
	/**
	 * Aksessori, joka päivittää elementin läpinäkyvyysarvot.
	 */
	public void update();
	
}
