/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

import java.util.ArrayList;
import java.awt.Graphics2D;

/**
* Animaatio-luokka. Animaatio on käytännössä sarja Drawable-rajapinnan toteuttavia olioita.
* Myös Animaatio-luokka itse toteuttaa Drawable-rajapinnan ja se pystytään näinollen
* piirtämään Canvas-rajapinnan toteuttavan olion (Screen) avulla. Animaatiot tulee luoda
* AnimationFactory-kirjastoluokan metodien avulla duplikaattien välttämiseksi.
* @author arkivika
* @see pomppu.graphics.AnimationFactory
* @see pomppu.graphics.Drawable
* @see pomppu.graphics.Canvas
*/
public class Animation implements Drawable {

	private double currentFrame, speed;
	private boolean autoAnimation, mirrored;
	private ArrayList<Image> frames;
	
	/**
	 * Konstruktori, joka luo Animation-olion ArrayList-oliosta, joka sisältää Drawable-rajapinnan toteuttavia olioita (Image, Text, Animation..).
	 * Asettaa myös animaation toistonopeuden.
	 * Ainoastaan sisäiseen käyttöön. Animaatioiden luomiseen tulee käyttää AnimationFactory-kirjastoluokan metodeita.
	 * @param _frames Drawable-rajapinnan toteuttavat oliot, joista animaatio rakennetaan.
	 * @param _speed Animaation toistonopeus.
	 */
	protected Animation(ArrayList<Image> _frames, double _speed, boolean _mirrored) {
		frames = _frames;
		currentFrame = 0;
		speed = _speed;
		autoAnimation = true;
		mirrored = _mirrored;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan animaation framet. Ainoastaan testaustarkoitukseen.
	 * @return Animaation framet (Image-olio) ArrayList-oliossa.
	 */
	public ArrayList<Image> getFrames() {
		return frames;
	}

	/**
	 * Aksessori, joka piirtää aktiivisena olevan framen annettuihin koordinaatteihin.
	 * @param g "Graphics context", johon animaatio piirretään.
	 * @param x Animaation x-koordinaatti.
	 * @param y Animaation y-koordinaatti.
	 */
	public void draw(Graphics2D g, int x, int y) {

		frames.get((int)currentFrame).draw(g, x, y);
	
		if (autoAnimation) {
			if (!mirrored) {
				currentFrame += speed;
				if (currentFrame >= frames.size())
					currentFrame = 0; }
			else {
				currentFrame -= speed;
				if (currentFrame < 0)
					currentFrame = frames.size()-1;
			}
		}
	}

	/**
	 * Aksessori, joka palauttaa arvonaan animaation tämänhetkisen framen korkeuden.
	 * @return Animaation tämänhetkisen framen korkeus. 
	 */
	public int getHeight() {
		return (frames == null || frames.size() == 0) ? 0 : frames.get((int)currentFrame).getHeight();
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan animaation tämänhetkisen framen leveyden.
	 * @return Animaation tämänhetkisen framen leveys. 
	 */
	public int getWidth() {
		return (frames == null || frames.size() == 0) ? 0 : frames.get((int)currentFrame).getWidth();
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan animaation nopuden.
	 * @return Animaation nopeus. 
	 */
	public double getSpeed() {
		return speed;
	}
	
	/**
	 * Aksessori, joka asettaa annetun parametrin animaation nopeudeksi.
	 * @param _speed Haluttu nopeus.
	 */
	public void setSpeed(double _speed) {
		if (_speed >= 0)
			speed = _speed;		
	}
	
	/**
	 * Aksessori, joka asettaa automaattisen animaation etenemisen päälle tai pois.
	 * @param _auto True, mikäli automaattinen animointi halutaan päälle, muuten false.
	 */
	public void setAutoAnimation(boolean _auto) {
		autoAnimation = _auto;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan animaation tämänhetkisen framen.
	 * @return Animaation tämänhetkinen frame.
	 */
	public int getFrame() {
		return (int)currentFrame;
	}

	/**
	 * Aksessori, jonka avulla voidaan asettaa animaatiolle haluttu frame.
	 * @param frame Halutun frame indeksi.
	 */
	public void setFrame(int frame) {
		if (frame >= 0 && frame < frames.size())
			currentFrame = frame;
	}

	/**
	 * Aksessori, jonka avulla animaatiota edistetään nopeuden verran.
	 */
	public void advanceFrame() {
		currentFrame += speed;
		if (currentFrame >= frames.size())
			currentFrame = 0;
	}

	/**
	 * Aksessori, jonka avulla animaatiota kelataan taaksepäin nopeuden verran.
	 */
	public void rewindFrame() {
		currentFrame -= speed;
		if (currentFrame < 0)
			currentFrame = frames.size()-1;
	}
	
	/**
	 * Aksessori, jonka avulla animaatio asetetaan alkaamaan alusta.
	 */
	public void reset() {
		currentFrame = 0.0;
	}

	/**
	 * Aksessori, joka päivittää kaikki animaation Drawable-rajapinnan totetuttavat oliot,
	 * käytännössä lataamalla ne uudestaan VRAM:iin ja päivittämällä läpinäkyvyysarvot.
	 */
	public void update() {
		for(Drawable frame : frames)
			frame.update();
	}	

	/**
	 * Aksessori, joka palauttaa arvonaan kloonin kyseisestä animaatiosta.
	 * @return Kloonattu Animation-olio. 
	 */
	public Animation clone() {
		return new Animation(frames, speed, mirrored);
	}
}
