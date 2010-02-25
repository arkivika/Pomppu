/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.ImageIO;

/**
 * Kirjastoluokka, jonka avulla luodaan animaatio-oliot.
 * Kaksoiskappaleiden välttämiseksi käytetään HashMap-oliota, joka pitää kirjaa ladatuista animaatioista. 
 * Tarkistaa virheet sisäisesti ja palauttaa arvonaan "dummy"-animaation, mikäli animaation lataaminen epäonnistuu. 
 * @author arkivika
 * @see pomppu.graphics.Animation
 */
public final class AnimationFactory {

	private static HashMap<String, ArrayList<Animation>> animMap = new HashMap<String, ArrayList<Animation>>();
	
	/**
	 * Sisäinen apumetodi, joka luo animaation animaatiotaulun sisältävästä BufferedImage-oliosta.
	 * @param buffer BufferedImage-olio, joka sisältää animaatiotaulun.
	 * @param width Yksittäisen animaatiokehyksen leveys.
	 * @param height Yksittäisen animaatiokehyksen korkeus.
	 * @param row Animaatiotaulun rivi-indeksi, jolta animaatio halutaan luoda.
	 * @param speed Nopeus, jolla luotua animaatiota toistetaan.
	 * @param mirror True, mikäli halutaan luodaan peilikuva animaatiosta. Muuten false.
	 * @return Animaatio-olio.
	 * @see pomppu.graphics.Animation
	 */
	private static Animation createAnimation(BufferedImage buffer, int width, int height, int row, double speed, boolean mirror) {

		ArrayList<Image> drawImages = new ArrayList<Image>();
		
		AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
		at.translate(-width, 0);		
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		int numCol = buffer.getWidth() / width;

		for (int i=0; i<numCol; i++) {
			BufferedImage subImage = buffer.getSubimage(i*width, row*height, width, height);
			drawImages.add(new Image((mirror) ? op.filter(subImage, null) : subImage));
		}
		
		return new Animation(drawImages, speed);
	}
	
	/**
	 * Sisäinen apumetodi, joka luo ArrayList-listan Animaatio-olioista. Animaatiot ladataan animaatiotaulusta, joka sijaitsee annetun tiedostopolun päässä.
	 * Tarkistaa virheet sisäisesti ja palauttaa arvonaan tyhjän ArrayList-olion, mikäli animaatiotaulun sisältävää tiedostoa ei löydetä tai sitä ei voida avata.
	 * @param filepath Tiedostopolku kuvatiedostolle, joka sisältää animaatiotaulun.
	 * @param width Yksittäisen animaatiokehyksen leveys.
	 * @param height Yksittäisen animaatiokehyksen korkeus.
	 * @param speed Nopeus, jolla luotua animaatiota toistetaan.
	 * @param mirror True, mikäli animaatioista luodaan myös peilikuvat. Muuten false.
	 * @return ArrayList-olion, joka sisältää luodut Animation-oliot. Mikäli lataus epäonnistuu, palautetaan tyhjä ArrayList-olio.
	 * @see pomppu.graphics.Animation
	 */
	private static ArrayList<Animation> createAnimations(String filepath, int width, int height, double speed, boolean mirror) {
		
		ArrayList<Animation> returnList = new ArrayList<Animation>();

		try	 {
			URL url = returnList.getClass().getResource(filepath);
			if(url == null)
				throw new IOException("Error! File not found: " + filepath);

			BufferedImage buffer = ImageIO.read(url);
			
			int numRow = buffer.getHeight() / height;
			
			for (int i=0; i<numRow; i++) {
				returnList.add(createAnimation(buffer, width, height, i, speed, false));
				if (mirror)
					returnList.add(createAnimation(buffer, width, height, i, speed, true));
			}
		}
		catch (IOException e) {
			System.out.println("Error! Spritesheet not found: " + filepath + "\n" + e);
		}

		return returnList;
	}

	/**
	 * Julkinen metodi, joka palauttaa Animation-olioita sisältävän ArrayList-olion. Animaatiot ladataan tiedostosta, johon filepath-parametri viittaa.
	 * Mikäli animaatio on jo valmiiksi ladattu, se palautetaan suoraan sisäisestä HashMap:istä.
	 * Mikäli animaatioita ei löydy HashMap:istä, eikä sitä saada ladattua tiedostosta, palautetaan tyhjä ArrayList-olio.
	 * @param filepath Tiedostopolku kuvatiedostolle, joka sisältää animaatiotaulun.
	 * @param width Yksittäisen animaatiokehyksen leveys.
	 * @param height Yksittäisen animaatiokehyksen korkeus.
	 * @param speed Nopeus, jolla luotua animaatiota toistetaan.
	 * @param mirror True, mikäli animaatioista luodaan myös peilikuvat. Muuten false.
	 * @return An ArrayList of Animations. If unsuccessful it returns a dummy ArrayList of Animations of the size 0.
	 * @see pomppu.graphics.Animation
	 */
	public static ArrayList<Animation> getAnimations(String filepath, int width, int height, double speed, boolean mirror) {
	
		if(animMap.containsKey(filepath))
			return cloneAnimations(animMap.get(filepath));
	  
		ArrayList<Animation> animations = createAnimations(filepath, width, height, speed, mirror);
		animMap.put(filepath, animations);
		
		return animations;
	}
	
	/**
	 * Päivittää transparency-arvot kaikille AnimationFactoryn luomille animaatioille.
	 */
	public static void updateTransparencies() {

		 for(ArrayList<Animation> animList : animMap.values())
			 for(Animation anim : animList)
				 anim.update();
	}
	 
	/**
	 * Kloonaa Animation-olioita sisältävän ArrayList-olion. Kloonatut animaatiot käyttävät samaa ladattua kuvatiedostoa 
	 * kuin alkuperäinenkin, mutta omaavat yksilöllisen tilan.
	 * @param _animations Kloonattava ArrayList-olio, joka sisältää Animation-oliot.
	 * @return Kloonattu, Animation-olioita sisältävä ArrayList-olio.
	 * @see pomppu.graphics.Animation
	 */
	public static ArrayList<Animation>cloneAnimations( ArrayList<Animation> _animations) {
		
		ArrayList<Animation> animations = new ArrayList<Animation>();
		
		for (Animation anim : _animations)
			animations.add(anim.clone());
		
		return animations;
	}
}
