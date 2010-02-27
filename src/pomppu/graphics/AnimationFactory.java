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
	private static Animation createAnimation(BufferedImage buffer, int width, int height, int row, double speed, boolean mirror, boolean _mirrored) {

		ArrayList<Image> drawImages = new ArrayList<Image>();

		if (buffer == null)
			return new Animation(drawImages, speed, _mirrored);
		
		AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
		at.translate(-width, 0);		
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		int numCol = buffer.getWidth() / width;

		for (int i=0; i<numCol; i++) {
			BufferedImage subImage = buffer.getSubimage(i*width, row*height, width, height);
			drawImages.add(new Image((mirror) ? op.filter(subImage, null) : subImage));
		}

		return new Animation(drawImages, speed, _mirrored);
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
	private static ArrayList<Animation> createAnimations(String filepath, int width, int height, double speed, boolean mirror, boolean _mirrored) {
		
		ArrayList<Animation> returnList = new ArrayList<Animation>();

		try	 {

			if (filepath == null)
				throw new IOException("Error! File not found: " + filepath); 

			URL url = returnList.getClass().getResource(filepath);
			if(url == null)
				throw new IOException("Error! File not found: " + filepath);

			BufferedImage buffer = ImageIO.read(url);
			
			int numRow = buffer.getHeight() / height;
			
			for (int i=0; i<numRow; i++) {
				returnList.add(createAnimation(buffer, width, height, i, speed, false, _mirrored));
				if (mirror)
					returnList.add(createAnimation(buffer, width, height, i, speed, true, _mirrored));
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
	public static ArrayList<Animation> getAnimations(String filepath, int width, int height, double speed, boolean mirror, boolean _mirrored) {
	
		if(animMap.containsKey(filepath))
			return cloneAnimations(animMap.get(filepath));
	  
		ArrayList<Animation> animations = createAnimations(filepath, width, height, speed, mirror, _mirrored);
		
		if (animations != null && animations.size() != 0)
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
		
		if (_animations != null)
			for (Animation anim : _animations)
				animations.add(anim.clone());
		
		return animations;
	}
	
	/**
	 * Testipäämetodi, jonka avulla varmistutaan siitä, että luokka toimii kuten sen pitäisi. Tulostaa jokaisen testin sekä sen tuloksen.
	 * @param args Ei huomioida tässä.
	 */
	public static void main(String args[])	{

		try {
			// Alustuksen testaus
			System.out.println("Testing construction phase...");
			if(animMap == null)
				failedTest("Couldn't construct the HashMap.");
			System.out.print("..");
			 
			if(!animMap.isEmpty())
				failedTest("HashMap initially not empty");
			System.out.print("..");
			 
			// Tyhjän HashMap:in päivittäminen
			updateTransparencies();
			System.out.println("..OK!");
			 
			System.out.println("Testing spritesheet loading method...");

			// Animaation lataaminen valideilla parametreilla
			ArrayList<Animation> testAnim = createAnimations("/resources/player/player.png", 32, 50, 0.5, true, false);
			if(testAnim == null || testAnim.size() <= 0 || testAnim.size() > 4)
				failedTest("Couldn't create valid ArrayList of Animations (Spritesheet method)");
			System.out.print("..");
			 
			for(Animation anim : testAnim) {
				
				if(anim.getFrames().size() != 5)
					failedTest("Couldn't create valid Animation (Spritesheet method)");
				System.out.print("..");
			 
				if(anim.getHeight() != 50 || anim.getWidth() != 32)
					failedTest("Dimensions invalid for Animation (Spritesheet method)");
				System.out.print("..");
			}
			 
			// Animaation lataaminen null-stringillä
			ArrayList<Animation> failAnim = createAnimations(null, 64, 64, 0.3, true, false);
			if(failAnim != null && failAnim.size() > 0)
				failedTest("Created invalid Animation instead of a dummy one. (Spritesheet method)");
			System.out.print("..");
			 
			// Animaation lataaminen kelvottomalla tiedostopolulla
			failAnim = createAnimations("töttöröödz", 64, 64, 0.3, true, false);
			if(failAnim != null && failAnim.size() > 0)
				failedTest("Created invalid Animation instead of a dummy one. (Spritesheet method)");
			System.out.println("..OK!");
			 
			System.out.println("Testing cloning method...");
			
			// Eheän kloonin luominen
			ArrayList<Animation> cloneAnim = cloneAnimations(testAnim);
			if(cloneAnim == testAnim)
				failedTest("Clone operation failed, references were the same. (Cloning method)");
			System.out.print("..");
			 
			if(cloneAnim.size() != testAnim.size())
				failedTest("Cloned Animation different than original. (Cloning method)");
			System.out.print("..");
			 
			for(int i = 0; i < testAnim.size(); i++) {
				
				if(testAnim.get(i).getFrames().size() != cloneAnim.get(i).getFrames().size())
					failedTest("Cloned Animation different than original. (Cloning method)");
				System.out.print("..");
			 
				if(testAnim.get(i).getHeight() != cloneAnim.get(i).getHeight() ||
				   testAnim.get(i).getWidth() != cloneAnim.get(i).getWidth())
					failedTest("Dimensions invalid for cloned Animation (Cloning method)");
			
				System.out.print("..");
			}
			 
			// Epäkelvon kloonin lataaminen. Korvike animaation palauttaminen
			failAnim = cloneAnimations(failAnim);
			if(failAnim != null && failAnim.size() > 0)
				failedTest("Clone became an invalid Animation instead of a dummy one. (Cloning method)");
			System.out.print("..");
			 
			// Epäkelpo klooni null-parametrillä
			failAnim = cloneAnimations(null);
			if(failAnim != null && failAnim.size() > 0)
				failedTest("Clone became an invalid Animation instead of a dummy one. (Cloning method)");
			System.out.println("..OK!");
			 
			System.out.println("Testing HashMap method...");
			// Kelvollisilla parametreillä
			testAnim = getAnimations("/resources/player/player.png", 32, 50, 0.5, true, false);
			if(testAnim == null || testAnim.size() <= 0 || testAnim.size() > 4)
				failedTest("Couldn't create valid animation (HashMap method)");
			System.out.print("..");
			 
			for(Animation anim : testAnim) {
				if(anim.getFrames().size() != 5)
					failedTest("Couldn't create valid ArrayList of Animations (HashMap method)");
				System.out.print("..");
			 
				if(anim.getHeight() != 50 || anim.getWidth() != 32)
					failedTest("Dimensions invalid for animation (HashMap method)");
				System.out.print("..");
			}
			 
			if(animMap.size() != 1)
				failedTest("HashMap size invalid (" + animMap.size() + ") (HashMap method)");
			System.out.print("..");
			 
			// Ei-tyhjän HashMap:in päivittäminen
			updateTransparencies();
			System.out.print("..");
			 
			// Animaation lataaminen null-stringillä
			failAnim = getAnimations(null, 64, 64, 0.3, true, false);
			if(failAnim != null && failAnim.size() > 0)
				failedTest("Created invalid Animation instead of a dummy one. (HashMap method)");
			System.out.print("..");
			 
			// Animaation lataaminen epäkelvolla tiedostopolulla 
			failAnim = getAnimations("töttöröödz", 64, 64, 0.3, true, false);
			if(failAnim != null && failAnim.size() > 0)
				failedTest("Created invalid Animation instead of a dummy one. (HashMap method)");
			System.out.print("..");
			 
			// Eheän kloonin luominen HashMap:ista.
			cloneAnim = getAnimations("/resources/player/player.png", 32, 50, 0.3, true, false);
			if(cloneAnim == testAnim)
				failedTest("Clone operation failed, references were the same. (HashMap method)");
			System.out.print("..");
			 
			if(cloneAnim.size() != testAnim.size())
				failedTest("Cloned Animation different than original. (HashMap method)");
			System.out.print("..");
			 
			for(int i = 0; i < testAnim.size(); i++) {
				
				if(testAnim.get(i).getFrames().size() != cloneAnim.get(i).getFrames().size())
					failedTest("Cloned animation different than original. (HashMap method)");
				System.out.print("..");
				 
				if(testAnim.get(i).getHeight() != cloneAnim.get(i).getHeight() ||
				   testAnim.get(i).getWidth() != cloneAnim.get(i).getWidth())
					failedTest("Dimensions invalid for cloned Animation (HashMap method)");
				
				System.out.print("..");
			}
			 
			if(animMap.size() != 1)
				failedTest("HashMap size invalid (" + animMap.size() + ") (HashMap method)");
			System.out.println("..OK!");
			 
			System.out.println("Testing BufferedImage method...");
			// Animaation luominen BufferedImage-oliosta
			Animation test = createAnimation(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB), 64, 64, 0, 0.33, false, false);
			if(test == null || test.getFrames().size() != 1)
				failedTest("Couldn't create valid animation (BufferedImage method)");
			System.out.print("..");
			
			if(test.getHeight() != 64 || test.getWidth() != 64)
				failedTest("Animation dimensions invalid (BufferedImage method)");
			System.out.print("..");
			 
			// Animaation luominen BufferedImage-oliosta null parametrilla
			Animation fail = createAnimation(null, 64, 64, 0, 0.33, false, false);
			if(fail != null && fail.getFrames().size() > 0)
				failedTest("Created invalid Animation instead of a dummy one. (BufferedImage method)");
			System.out.print("..");
			 
			// Animaation luominen BufferedImage-oliosta väärillä arvoilla
			fail = createAnimation(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB), 1337, 1337, 15, 0.33, false, false);
			if(fail != null && fail.getFrames().size() > 0)
				failedTest("Created invalid Animation instead of a dummy one. (BufferedImage method)");
			System.out.println("..OK!");
		}
		catch(Exception e) {
			failedTest("Unknown exception: " + e);
		}
	System.out.println("Everything OK with the AnimationFactory!");
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