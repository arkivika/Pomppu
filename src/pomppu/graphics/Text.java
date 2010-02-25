/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

import java.awt.*;
import java.awt.font.*;

/**
 * Teksti-olio.
 * Mahdollistaa tekstirivien tulostamisen ruudulle Drawable-rajapinnan avulla.
 * @see pomppu.graphics.Drawable
 * @author arkivika
 */
public class Text implements Drawable {
	
	private String text;
	private Font font;
	private Color col;
	
	private TextLayout layout;
	
	/**
	 * Konstruktori, joka luo halutun tekstin.
	 * @param _text Tekstisyöte.
	 * @param _font Fontin nimi.
	 * @param _style Fontin tyyli.
	 * @param _size Fontin koko.
	 * @param _col Fontin väri.
	 * @see java.awt.Font
	 * @see java.awt.FontMetrics
	 */
	public Text(String _text, String _font, int _style, int _size, Color _col) {
		text = _text;
		font = new Font(_font, _style, _size);
		col = _col;
		layout = new TextLayout(_text, font, new FontRenderContext(null, false, false));
	}
	
	/**
	 * Aksessori, joka piirtää annetun tekstisyötteen parametreinä annetuihin koordinaatteihin.
	 * @param g Tekstisyötteen piirtämiseen käytettävä grafiikka-olio.
	 * @param x Haluttu x-koordinaatti.
	 * @param y Haluttu y-koordinaatti.
	 */
	public void draw(Graphics2D g, int x, int y) {

		g.setFont(font);
		g.setColor(col);
		g.drawString(text, x, y + getHeight());
	}

	/**
	 * Aksessori, joka palauttaa arvonaan fontin korkeuden.
	 * @return Fontin korkeus.
	 */
	public int getHeight() {
		return (int)(layout.getBounds().getHeight());
	}

	/**
	 * Aksessori, joka palauttaa arvonaan fontin leveyden.
	 * @return Fontin leveys.
	 */
	public int getWidth() {
		return (int)layout.getBounds().getWidth();
	}
	
	/**
	 * Aksessori, joka päivittää tekstin. 
	 * @param _text Päivitetty teksti.
	 */
	public void updateText(String _text) {
		text = _text;		
	}

	/**
	 * Tyhjä, tarvitaan toteuttamaan rajapinta.
	 */
	public void update() {
	}
}
