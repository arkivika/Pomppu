/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

/**
 * Wrapperi, joka pitää sisällään sekä Drawable-rajapinnan toteuttavan olion että sille kuuluvat
 * pelilogiikan mukaiset koordinaatit. Välitetään Canvas-rajapinnan toteuttavalle oliolle (Screen) 
 * elementin piirtämiseksi ruudulle.
 * @author arkivika
 */
public class VisibleElement {

	private Drawable drawable;
	private int x, y;
	
	/**
	 * Konstruktori, joka kietoo Drawable-rajapinnan toteuttavan olion sen pelilogiikan mukaisiin koordinaatteihin.
	 * @param _drawable Drawable-rajapinnan toteuttava olio.
	 * @param _x Elementin x-koordinaatti.
	 * @param _y Elementin y-koordinaatti.
	 */
	public VisibleElement(Drawable _drawable, int _x, int _y) {
		drawable = _drawable;
		x = _x;
		y = _y;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan Drawable-rajapinnan toteuttavan olion.
	 * @return Drawable-rajapinnan toteuttava olio.
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan elementin x-koordinaatin.
	 * @return Elementin x-koordinaatti.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan elementin y-koordinaatin.
	 * @return Elementin y-koordinaatti.
	 */
	public int getY() {
		return y;
	}
}