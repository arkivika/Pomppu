/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Transparency;
import java.io.IOException;
import java.util.HashMap;

/**
 * Kirjastoluokka, jonka avulla luodaan kuva-oliot.
 * Kaksoiskappaleiden välttämiseksi käytetään HashMap-oliota, joka pitää kirjaa ladatuista kuva-olioista. 
 * Tarkistaa virheet sisäisesti ja palauttaa arvonaan "dummy"-kuvan, mikäli kuvan lataaminen epäonnistuu. 
 * @author arkivika
 * @see pomppu.graphics.Image
 * @see pomppu.graphics.Drawable
 */
public final class ImageFactory {
	
	private static HashMap<String, Image> imageMap = new HashMap<String, Image>();
	private static Text notFound = new Text("Image not found!", "Arial", 12, Font.PLAIN, Color.RED);
	private static int transparency = Transparency.BITMASK;
	  
	/**
	 * Aksessori, joka palauttaa arvonaan tämänhetkisen transparency (läpinäkyvyys)-arvon (TRANSLUCENT or BITMASK).
	 * @return Tämänhetkinen transparency-arvo.
	 */
	public static int getTransparency() {
		return transparency;
	}
	  
	/**
	 * Aksessori, joka muuttaa tämänhetkistä transparency-arvoa (BITMASK -> TRANSLUCENT, TRANSLUCENT -> BITMASK).
	 */
	public static void toggleTransparency() {

		if(transparency == Transparency.BITMASK)
			transparency = Transparency.TRANSLUCENT;
		else
			transparency = Transparency.BITMASK;
		  
		updateTransparencies();
		AnimationFactory.updateTransparencies();
	}

	/**
	 * Aksessori, joka palauttaa arvonaan Drawable-rajapinnan toteuttavan olion (Image) annetusta tiedostopolusta.
	 * Mikäli kuva-olio on aikaisemmin ladattu, palautetaan se automaattisesti HashMap:istä.
	 * Jos kuva-oliota ei löydy HashMap:istä eikä annetusta tiedostopolusta, palautetaan "dummy"-Drawable-olio (Text), joka sisältää tekstin "Image not found".
	 * @param filepath Tiedostopolku kuvatiedostolle.
	 * @return Image-olio, tai "dummy"-Text-olio, mikäli lataaminen epäonnistui.
	 * @see pomppu.graphics.Image
	 * @see pomppu.graphics.Drawable
	 */
	public static Drawable getImage(String filepath) {
		Image retImage = imageMap.get(filepath);
	  
		if(retImage != null)
			return retImage;
	  
			try {
				retImage = new Image(filepath);
			}
			catch(IOException e) {
				System.out.println("Error! Image not found: "+ filepath + " " + e);
				return notFound;
			}
	  
		imageMap.put(filepath, retImage);
		return retImage;
	}
  
	/**
	 * Päivittää kaikkien ImageFactory:n sisältämien kuvien läpinäkyvyysarvot.
	 */
	private static void updateTransparencies()	 {
		for(Image img : imageMap.values())
			img.update();
	}	
}