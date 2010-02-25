/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

import java.awt.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

/**
 * Kuva-olio, joka toteuttaa Drawable- image object.
 * Makes it possible to draw images with the drawable interface.
 * Uses VolatileImage from the java awt library.
 * @see pomppu.graphics.Drawable
 * @author arkivika
 */
public class Image implements Drawable {

	private BufferedImage buffer;
	private VolatileImage vramImg;
	private GraphicsConfiguration gfxConf;

	/**
	 * Konstruktori, joka yrittää ladata kuvan tiedostosta.
	 * Heittää IOException:in, mikäli lataus epäonnistuu.
	 * @param filepath Tiedostopolku kuvatiedostolle.
	 * @throws IOException Heitetään, mikäli tiedostoa ei ole, tai se ei ole validi.
	 */
	protected Image(String filepath) throws IOException {

		gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	
		URL url = this.getClass().getResource(filepath);
		if(url == null)
			throw new IOException("Error! File not found: " + filepath);
		  
		buffer = ImageIO.read(url);
		
		moveToVram();
		maintainImg();
	}

	protected Image(BufferedImage _buffer) {
	
		gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		 
		buffer = _buffer;
		moveToVram();
		maintainImg();
	}

	/**
	 * Aksessori, joka piirtää kuvan annettuihin koordinaatteihin.
	 * @param g "Graphics context", johon kuva piirretään.
	 * @param x Kuvan x-koordinaatti.
	 * @param y Kuvan y-koordinaatti.
	 */
	public void draw(Graphics2D g, int x, int y) {
		maintainImg();
		g.drawImage(vramImg, x, y, null);
	}

	/**
	 * Aksessori, joka palauttaa arvonaan kuvan korkeuden.
	 */
	public int getHeight() {
		return buffer.getHeight();
	}

	/**
	 * Aksessori, joka palauttaa arvonaan kuvan leveyden.
	 */
	public int getWidth() {
		return buffer.getWidth();
	}

	/**
	 * Aksessori, joka päivittää kuvan siirtämällä sen uudelleen vram:iin.
	 */
	public void update() {
		moveToVram();
	}
	
	/**
	 * Apumetodi, joka luo VolatileImage-olion BufferedImage-oliosta.
	 * Käytännössä kuvadata kopioidaan vram:iin (näytönohjaimen muistiin) tehokkuuden parantamiseksi.
	 * @see java.awt.image.VolatileImage
	 * @see java.awt.image.BufferedImage
	 */
	private void moveToVram() {
		// Luo uusi VolatileImage.
		vramImg = gfxConf.createCompatibleVolatileImage(buffer.getWidth(),
														buffer.getHeight(), 
														buffer.getTransparency());

		// Hae "drawing context" kuvalle.
		Graphics2D g2d = (Graphics2D) vramImg.getGraphics();

		// Käytä läpinäkyvyyttä, mikäli mahdollista.
		g2d.setComposite(AlphaComposite.Src);
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, vramImg.getWidth(), vramImg.getHeight());

		// Piirrä kuva ja hylkää "drawing context".
		g2d.drawImage(buffer, 0, 0, null);
		g2d.dispose();
	}
	 
	/**
	 * Apumetodi, joka tarkistaa mikäli VolatileImage-olio on validi. Mikäli se ei ole, yritetään luoda se uudelleen.
	 * @see java.awt.image.VolatileImage
	 * @see java.awt.image.BufferedImage
	 */
	private void maintainImg() {

		if (vramImg.contentsLost()) {
			moveToVram();
			maintainImg();
		}
	}
	
	/**
	 * Tarvitaan systeemiresurssien vapauttamiseksi, kun javan roskienkerääjä on vapauttanut ladatut kuvat.
	 */
	protected void finalize() {
		
		if (vramImg != null)
			vramImg.flush();
		
		if (buffer != null)
			buffer.flush();
	}
}