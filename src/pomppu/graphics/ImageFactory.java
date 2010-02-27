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
		
		if (filepath == null)
			return notFound;
			
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
	
	/**
	 * Testipäämetodi, jonka avulla varmistutaan siitä, että luokka toimii kuten sen pitäisi. Tulostaa jokaisen testin sekä sen tuloksen.
	 * @param args Ei huomioida tässä.
	 */
	public static void main(String[] args) {

		try {

			// Alustusvaihe
			
			System.out.println("Testing construction phase..");
			if (transparency != Transparency.BITMASK)
				failedTest("Transparency not BITMASK initially.");
			if (imageMap == null)
				failedTest("Couldn't construct the HashMap.");
			if (!imageMap.isEmpty())
				failedTest("HashMap initially not empty.");
			if (notFound == null)
				failedTest("Failed creating the dummy image.");
			System.out.println("..OK!");
			
			// updateTransparencies tyhjällä HashMap:lla
			
			System.out.println("Trying to update the transparencies of the elements in an empty HashMap..");
			updateTransparencies();
			System.out.println("..OK!");
			
			// Kuvan lataaminen
			
			System.out.println("Testing getImage with a valid parameter..");
			Drawable testImage = getImage("/resources/player/player.png");
			if (testImage == notFound)
				failedTest("Couldn't create a valid image.");
			System.out.println("..OK!");
			
			// HashMap:in eheys
			
			System.out.println("Testing the size of the HashMap after 1 addition..");
			if (imageMap.size() != 1)
				failedTest("The image didn't get added properly to the HashMap: size not 1.");
			System.out.println("..OK!");
			
			// Kuvan eheys
			
			System.out.println("Testing the dimensions of the created valid image..");
			if (testImage.getHeight() != 100 || testImage.getWidth() != 180)
				failedTest("Invalid dimensions for the image.");
			System.out.println("..OK!");
			
			// Epäkelvon kuvan lataaminen
			
			System.out.println("Testing getImage with an invalid parameter..");
			Drawable failImage = getImage("/reso6543urces/234player/pla234yer.png");
			if (failImage != notFound)
				failedTest("Created an invalid image instead of the dummy one.");
			System.out.println("..OK!");
			
			System.out.println("Testing getImage with a null parameter..");
			failImage = getImage(null);
			if (failImage != notFound)
				failedTest("Created invalid animations instead of the dummy one.");
			System.out.println("..OK!");
		
			// Kuvan uudelleenlataaminen
			
			System.out.println("Testing, wether the Image is cloned properly if it already exists in the HashMap..");
			Drawable testClone = getImage("/resources/player/player.png");
			if (testClone != testImage)
				failedTest("The cloned image's reference was not the one that was contained in the HashMap!");
			System.out.println("..OK!");
			
			// updateTransparencies ei-tyhjällä HashMap:lla
			
			System.out.println("Trying to update the transparencies of the elements in a non-empty HashMap..");
			updateTransparencies();
			System.out.println("..OK!");
			
			// toggleTransparency-metodi
			
			System.out.println("Testing the toggleTransparency-method..");
			toggleTransparency();
			if (transparency != Transparency.TRANSLUCENT)
				failedTest("Transparency not toggled to TRANSLUCENT.");
			System.out.println("..OK!");
			
			// getTransparency-metodi
			
			System.out.println("Testing the getTransparency-method..");
			if (getTransparency() != transparency)
				failedTest("getTransparency didn't return the correct transparency.");
			System.out.println("..OK!");

			System.out.println("Everything OK with the ImageFactory!");
		}
		catch (Exception e) {
			failedTest("Unknown exception: " + e);
		}
	}

	/**
	 * Apumetodi, joka tulostaa pieleen menneen testin sekä lopettaa ohjelman suorittamisen.
	 * @param test Pieleen mennyt testi.
	 */
	private static void failedTest(String test) {

		System.out.println("TEST FAILED: " + test);
		System.exit(0);
	}
}