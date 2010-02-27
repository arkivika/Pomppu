/** Sisältää tarvittavat luokat input- ja output-toimintoihin, joiden avulla ohjelmalle
 * välitetään informaatiota ulkoisista lähteistä, kuten näppäimistöltä tai hiireltä. Myös 
 * tiedostoista lukeminen sisältyy I/O-toiminnallisuuksiin.
 */
package pomppu.io;
 
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
 
import pomppu.game.Map;
import pomppu.graphics.AnimationFactory;
import pomppu.graphics.Drawable;
import pomppu.graphics.Animation;
import pomppu.graphics.ImageFactory;
import pomppu.mechanics.*;
 
/**
 * Kirjastoluokka, jonka avulla karttatiedot ladataan tiedostosta. 
 * @author arkivika
 */
public final class MapFactory {
 
	// Viholliset
	
	static ArrayList<Animation> spikey = null;
	
	// Maa
	
	static Drawable ground_top_left 		= null;
	static Drawable ground_top_left_inv		= null;
	static Drawable ground_top 				= null;
	static Drawable ground_top_right_inv	= null;
	static Drawable ground_top_right 		= null;
	static Drawable ground_left 			= null;
	static Drawable ground_center 			= null;
	static Drawable ground_right 			= null;
	static Drawable ground_bottom_left 		= null;
	static Drawable ground_bottom_left_inv	= null;
	static Drawable ground_bottom			= null;
	static Drawable ground_bottom_right_inv	= null;
	static Drawable ground_bottom_right 	= null;

	// Vesi
	
	static Drawable  water_platform 	= null;
	static Drawable  water_top 			= null;
	static Drawable  water 				= null;
	static Animation waterfall			= null;
	static Animation waterfall_top		= null;
	static Animation waterfall_platform	= null;
	static Animation geyser_top 		= null;
	static Animation geyser 			= null;
	
	// Kolikko
	
	static Animation coin = null;
	
	// Laatikko
	
	static Drawable crate = null;
	
	// Kivi
	
	static Drawable stone = null;
	
	// Maali
	
	static Drawable win = null;
	
	/**
	 * Kirjastoluokan metodi, joka avaa tiedoston, parsii sen merkki merkiltä ja rakentaa niistä kartan, joka koostuu sekä
	 * staattisista (rakennuspalat, "tile") että dynaamisista objekteista (viholliset).
	 * @param filepath Karttatiedoston sijainti.
	 * @return Map-olio.
	 * @throws IOException Mikäli tiedostoa ei löydy, tai sitä ei voida avata.
	 */
	public static Map readMap(String filepath) throws IOException {
	
		int i, j, pl_x, pl_y;
		
		i = j = 0;
		pl_x = pl_y = -1;

		// Viholliset
		
		spikey = AnimationFactory.getAnimations("/resources/enemies/spikey.png", 60, 60, 0.3, true, false);
		
		if (spikey == null || spikey.size() < 1)
			throw new IOException("Error! Nonplayer-object \"spikey's\" animations don't exist!");
		
		// Maa
		
		ground_top_left 		= ImageFactory.getImage("/resources/ground/ground_top_left.png");
		ground_top_left_inv		= ImageFactory.getImage("/resources/ground/ground_top_left_inv.png");
		ground_top 				= ImageFactory.getImage("/resources/ground/ground_top.png");
		ground_top_right_inv	= ImageFactory.getImage("/resources/ground/ground_top_right_inv.png");
		ground_top_right 		= ImageFactory.getImage("/resources/ground/ground_top_right.png");
		ground_left 			= ImageFactory.getImage("/resources/ground/ground_left.png");
		ground_center 			= ImageFactory.getImage("/resources/ground/ground_center.png");
		ground_right 			= ImageFactory.getImage("/resources/ground/ground_right.png");
		ground_bottom_left 		= ImageFactory.getImage("/resources/ground/ground_bottom_left.png");
		ground_bottom_left_inv	= ImageFactory.getImage("/resources/ground/ground_bottom_left_inv.png");
		ground_bottom			= ImageFactory.getImage("/resources/ground/ground_bottom.png");
		ground_bottom_right_inv	= ImageFactory.getImage("/resources/ground/ground_bottom_right_inv.png");
		ground_bottom_right 	= ImageFactory.getImage("/resources/ground/ground_bottom_right.png");

		// Vesi
		
		water_platform 	= ImageFactory.getImage("/resources/water/water_platform.png");
		water_top 		= ImageFactory.getImage("/resources/water/water_top.png");
		water 			= ImageFactory.getImage("/resources/water/water.png");
		
		waterfall_platform = getSingleAnimation("/resources/water/waterfall_platform.png", 32, 32, 0.5, false);
		waterfall_top = getSingleAnimation("/resources/water/waterfall_top.png", 32, 32, 0.5, false);
		waterfall = getSingleAnimation("/resources/water/waterfall.png", 32, 32, 0.5, false);
		geyser_top = getSingleAnimation("/resources/water/geyser_top.png", 32, 32, 0.5, true);
		geyser = getSingleAnimation("/resources/water/geyser.png", 32, 32, 0.5, true);
		
		// Maali
		
		win	= ImageFactory.getImage("/resources/objects/win.png");
				
		// Kolikko

		coin = getSingleAnimation("/resources/objects/coin.png", 32, 32, 0.33, false);
		
		// Laatikko
		
		crate = ImageFactory.getImage("/resources/objects/crate.png");

		// Kivi
		
		stone = ImageFactory.getImage("/resources/objects/stone.png");

		ArrayList<ArrayList<StaticObject>> staticObjects = new ArrayList<ArrayList<StaticObject>>();
		ArrayList<DynamicObject> dynamicObjects = new ArrayList<DynamicObject>();

		URL url = null;
		
		if (filepath != null)
			url = staticObjects.getClass().getResource(filepath);

		if(url == null)
			throw new IOException("Error! File not found: \"" + filepath + "\"");
  
		Scanner parser;
		try {
			parser = new Scanner(url.openStream());
		}
		catch(IOException e) {
			throw new IOException("Error! Couldn't open resource: " + e);
		}

		while(parser.hasNext()) {

			String line = parser.nextLine();

			ArrayList<StaticObject> tileList = new ArrayList<StaticObject>();
			
			for (i=0; i<line.length(); i++) {
				
				switch (line.charAt(i)) {
					
					// Tyhjä (ilma)
				
					case ' ':
						tileList.add( null );
						break;

					// Maapalat

					case '1':
						tileList.add( new StaticObject( ground_top_left, i, j, 1, true ) );
						break;
					case '2':
						tileList.add( new StaticObject( ground_top, i, j, 2, true ) );
						break;
					case '3':
						tileList.add( new StaticObject( ground_top_right, i, j, 3, true ) );
						break;
					case '4':
						tileList.add( new StaticObject( ground_left, i, j, 4, true ) );
						break;
					case '5':
						tileList.add( new StaticObject( ground_center, i, j, 51, true ) );
						break;
					case '6':
						tileList.add( new StaticObject( ground_right, i, j, 6, true ) );
						break;
					case '7':
						tileList.add( new StaticObject( ground_bottom_left, i, j, 7, true ) );
						break;
					case '8':
						tileList.add( new StaticObject( ground_bottom, i, j, 8, true ) );
						break;
					case '9':
						tileList.add( new StaticObject( ground_bottom_right, i, j, 9, true ) );
						break;
					case 'a':
						tileList.add( new StaticObject( ground_top_left_inv, i, j, 52, true ) );
						break;
					case 'b':
						tileList.add( new StaticObject( ground_top_right_inv, i, j, 53, true ) );
						break;
					case 'c':
						tileList.add( new StaticObject( ground_bottom_left_inv, i, j, 54, true ) );
						break;
					case 'd':
						tileList.add( new StaticObject( ground_bottom_right_inv, i, j, 55, true ) );
						break;
	
					// Vesi
					
					case 'W':
						tileList.add( new StaticObject( water_top, i, j, 100, true ) );
						break;
					case 'w':
						tileList.add( new StaticObject( water, i, j, 101, true ) );
						break;
					case 'p':
						tileList.add( new StaticObject( water_platform, i, j, 102, true ) );
						break;
					case 'P':
						tileList.add( new StaticObject( waterfall_platform, i, j, 105, true ) );
						break;
					case 'f':
						tileList.add( new StaticObject( waterfall.clone(), i, j, 103, true ) );
						break;
					case 'F':
						tileList.add( new StaticObject( waterfall_top.clone(), i, j, 106, true ) );
						break;
					case 'g':
						tileList.add( new StaticObject( geyser.clone(), i, j, 104, true ) );
						break;
					case 'G':
						tileList.add( new StaticObject( geyser_top.clone(), i, j, 107, true ) );
						break;
						
					// Laatikko
						
					case 'C':
						tileList.add( new StaticObject( crate, i, j, 0, true ) );
						break;
						
					// Raha
						
					case '$':
						tileList.add( new StaticObject( coin.clone(), i, j, 56, true ) );
						break;
						
					// Maali
						
					case 'E':
						tileList.add( new StaticObject( win, i, j, 200, true ) );
						break;
					
					// Tiili
						
					case 'R':
						tileList.add(new StaticObject( stone, i, j, 10, true ) );
						break;
						
					// Aloituspaikka
						
					case 'S':
						tileList.add(null);
						pl_x = i;
						pl_y = j;
						break;

					// Viholliset
						
					case 'à':
						tileList.add( null );
						dynamicObjects.add(createEnemy(AnimationFactory.cloneAnimations(spikey), 2, -1, i*32, j*32, 10.0));
						break;

					case 'á':
						tileList.add( null );
						dynamicObjects.add(createEnemy(AnimationFactory.cloneAnimations(spikey), 2, 1, i*32, j*32, 10.0));
						break;
						
					case 'è':
						tileList.add( null );
						dynamicObjects.add(createEnemy(AnimationFactory.cloneAnimations(spikey), 3, -1, i*32, j*32, 10.0));
						break;
						
					case 'é':
						tileList.add( null );
						dynamicObjects.add(createEnemy(AnimationFactory.cloneAnimations(spikey), 3, 1, i*32, j*32, 10.0));
						break;
						
					// Jos ei mitään muuta niin ilmaa sitten! :)
						
					default:
						tileList.add( null );
				}
			}
	 
			staticObjects.add(tileList);
			j++;
		}
	 
		parser.close();

		if (pl_x == -1 || pl_y == -1)
			return null;

		return new Map(staticObjects, dynamicObjects, pl_x, pl_y);
	}

	/**
	 * Apumetodi, joka luo vihollistyyppisen dynaamisen objektin pelikarttaan.
	 * @param anims Objektissa käytettävät animaatiot.
	 * @param type Objektin tyyppi.
	 * @param direction Objektin aloitussuunta (-1 tai 1).
	 * @param x Objektin x-aloituskoordinaatti.
	 * @param y Objektin y-aloituskoordinaatti.
	 * @return Alustettu dynaaminen objekti.
	 */
	private static DynamicObject createEnemy(ArrayList<Animation> anims, int type, int direction, int x, int y, double jumpspeed) {
		
		DynamicObject temp;
		
		temp = new DynamicObject(anims);
		temp.setType(type);
		temp.setDirection(direction);
		temp.setPos(x, y);
		temp.setJumpSpeed(jumpspeed);
		
		return temp;
	}

	/**
	 * Apumetodi, joka luo tiedostosta vain yhden animaation.
	 * @param filepath Tiedostopolku kuvatiedostolle, joka sisältää animaation.
	 * @param width Yhden animaatioframen leveys (pikseliä).
	 * @param height Yhdein animaatioframen korkeus (pikseliä).
	 * @param speed Animaation nopeus (speed/frame).
	 * @param _mirrored True, mikäli animaation halutaan etenevän päinvastaisessa järjestyksessä (vain jos autoAnim on päällä), muuten false.
	 * @return Luotu Animation-olio.
	 * @throws IOException Mikäli animaatiota ei voitu luoda, tai luotu animaatio ei ole validi.
	 */
	private static Animation getSingleAnimation(String filepath, int width, int height, double speed, boolean _mirrored) throws IOException {
		
		ArrayList<Animation> temp = AnimationFactory.getAnimations(filepath, width, height, speed, false, _mirrored); 
		if (temp == null || temp.size() < 1)
			throw new IOException("Error! Nonplayer-object \"" + filepath + "\" animations don't exist!");
		
		return temp.get(0);
	}
	
	/**
	 * Testipäämetodi, jonka avulla varmistutaan siitä, että luokka toimii kuten sen pitäisi. Tulostaa jokaisen testin sekä sen tuloksen.
	 * @param args Ei huomioida tässä.
	 */
	public static void main(String[] args) {

		try {

			// Alustuksen testaus
			
			System.out.println("Testing the initialization of the objects..");
			if (spikey 					!= null || 
				ground_top_left 		!= null || 
				ground_top_left_inv 	!= null ||
				ground_top 				!= null ||
				ground_top_right_inv	!= null ||
				ground_top_right 		!= null ||
				ground_left 			!= null ||
				ground_center 			!= null ||
				ground_right 			!= null ||
				ground_bottom_left 		!= null ||
				ground_bottom_left_inv	!= null ||
				ground_bottom			!= null ||
				ground_bottom_right_inv	!= null ||
				ground_bottom_right 	!= null ||
				water_platform 			!= null ||
				water_top 				!= null ||
				water 					!= null ||
				waterfall				!= null ||
				waterfall_top			!= null ||
				waterfall_platform		!= null ||
				geyser_top 				!= null ||
				geyser 					!= null ||
				coin 					!= null ||
				crate 					!= null ||
				stone 					!= null ||
				win 					!= null )
				failedTest("One or more objects were not initially null!");
			System.out.println("..OK!");

			// Yksittäisen animaation lataaminen apumetodilla
			
			System.out.println("Testing the getSingleAnimation-helper method..");
			geyser = getSingleAnimation("/resources/player/player.png", 32, 50, 0.5, false);
			if (geyser == null || geyser.getFrames().size() != 5)
				failedTest("getSingleAnimation-method didn't return a valid animation!");
			System.out.println("..OK!");

			// Vihollisen dynaamisen objektin lataaminen apumetodilla
			
			System.out.println("Testing the createEnemy-helper method..");
			spikey = AnimationFactory.getAnimations("/resources/enemies/spikey.png", 60, 60, 0.3, true, false);
			DynamicObject testEnemy = createEnemy(spikey, 5, -1, 80, 90, -15);
			if (testEnemy.getAnimations() != spikey ||
				testEnemy.getType() != 5 ||
				testEnemy.getDirection() != -1 ||
				testEnemy.getX() != 80 ||
				testEnemy.getY() != 90)
				failedTest("createEnemy-method didn't return a valid dynamic object for an enemy object!");
			System.out.println("..OK!");

			// Validin karttatiedoston lataaminen

			System.out.println("Testing the readMap-method..");

			Map testMap = readMap("/resources/maps/testmap.map");
			if (testMap == null)
				failedTest("The readMap-method returned a null!");
			if (spikey 					== null || 
				ground_top_left 		== null || 
				ground_top_left_inv 	== null ||
				ground_top 				== null ||
				ground_top_right_inv	== null ||
				ground_top_right 		== null ||
				ground_left 			== null ||
				ground_center 			== null ||
				ground_right 			== null ||
				ground_bottom_left 		== null ||
				ground_bottom_left_inv	== null ||
				ground_bottom			== null ||
				ground_bottom_right_inv	== null ||
				ground_bottom_right 	== null ||
				water_platform 			== null ||
				water_top 				== null ||
				water 					== null ||
				waterfall				== null ||
				waterfall_top			== null ||
				waterfall_platform		== null ||
				geyser_top 				== null ||
				geyser 					== null ||
				coin 					== null ||
				crate 					== null ||
				stone 					== null ||
				win 					== null )
				failedTest("One or more objects were not initialized correctly!");
			
			// Validin karttatiedoston tarkistaminen
			
			if (testMap.getDynamicObjects().size() != 5)
				failedTest("Testmap contains the wrong number of dynamic objects!");

			if (testMap.getPlayerStartX() != 65)
				failedTest("Player starting X-coordinate invalid!");
			if (testMap.getPlayerStartY() != 545)
				failedTest("Player starting Y-coordinate invalid!");

			if (testMap.getStaticObjects().size() != 20)
				failedTest("Invalid vertical size of the map!");
			if (testMap.getStaticObjects().get(0).size() != 80)
				failedTest("Invalid horizontal size of the map!");
			
			int nulls = 0;
			int floor = 0;
			
			for (int j=0; j<20; j++)
				for (int i=0; i<80; i++) 
					if (testMap.getTileType(i, j) == -1)
						nulls++;
					else if (testMap.getTileType(i, j) == 2)
						floor++;
			
			if (floor != 80)
				failedTest("The number of floor tiles was incorrect!");
			if (nulls != 1520)
				failedTest("The number of null tiles was incorrect!");

			System.out.println("..OK!");

			// Epäkelvon karttatiedoston lataaminen null-stringillä

			System.out.println("Trying to load a map with null-parameter..");
			try {
				testMap = readMap(null);
				failedTest("Loaded an unknown map despite the null path!");
			}
			catch (IOException e) {
				System.out.println("..OK!");
			}

			// Epäkelvon karttatiedoston lataaminen virheellisellä tiedostopolulla
			
			System.out.println("Trying to load a map with an invalid path..");
			try {
				testMap = readMap("kukkelikoo!!!!!!");
				failedTest("Loaded an unknown map despite the null path!");
			}
			catch (IOException e) {
				System.out.println("..OK!");
			}

			System.out.println("Everything OK with the MapFactory!");
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