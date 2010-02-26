/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import pomppu.mechanics.*;
import pomppu.graphics.*;
import pomppu.io.*;

import java.awt.event.*;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Pelitila. Sisältää pelilogiikan.
 * @see pomppu.game.GameState
 * @author arkivika
 */
public class Game extends GameState {
	
	private Player player;
	private ArrayList<DynamicObject> nonplayerObjects;
	private ArrayList<NonPlayerObject> nonplayers;
	
	private Drawable heart_on, heart_off;
	private Drawable hearts[];
	
	private Map map;
	
	private double time_left;
	
	/**
	 * Konstruktori, joka alustaa pelitilan. Kutsuu ensin yliluokkansa (GameState) konstruktoria. Alustaa
	 * sydämet, kartan, pelaajan sekä ei-pelaajat. Asettaa kameran oikeaan kohtaan ja lataa taustakuvan.
	 * Asettaa lisäksi maksimiajan kentän läpipääsyyn.
	 * @param _cam Käytettävä camera-olio.
	 * @param _gui Käytettävä GUI-olio.
	 * @param _keyboard Käytettävä Keyboard-olio.
	 * @param _mouse Käytettävä Mouse-olio.
	 * @param time_limit Aikaraja kentän läpäisemiseksi (sekuntia).
	 */	
	public Game(Camera _cam, GUI _gui, Keyboard _keyboard, Mouse _mouse, String _map, int time_limit) throws IOException {

		super(_cam, _gui, _keyboard, _mouse);

		heart_on = ImageFactory.getImage("/resources/objects/heart_on.png");
		heart_off = ImageFactory.getImage("/resources/objects/heart_off.png");
		hearts = new Drawable[5];
		
		for (int i=0; i<5; i++) 
			hearts[i] = heart_on;
		
		map = MapLoader.readMap("/resources/maps/" + _map);
		
		ArrayList<Animation> playerAnimations = AnimationFactory.getAnimations("/resources/player/player.png", 36, 50, 0.3, true, false);

		if (playerAnimations == null || playerAnimations.size() < 1)
			throw new IOException("Error! Player animations don't exist!");
		
		player = new Player(playerAnimations, map.getPlayerStartX(), map.getPlayerStartY(), map);
	
		nonplayerObjects = map.getDynamicObjects();
		nonplayers = new ArrayList<NonPlayerObject>();
		
		for ( DynamicObject obj : nonplayerObjects ) 
			nonplayers.add( new NonPlayerObject(obj, map, obj.getDirection()) ); 
	
		camera.setPos(map.getPlayerStartX(), map.getPlayerStartY());
		camera.setBackground("/resources/backgrounds/Background.jpg");
		
		time_left = time_limit;
	}
	
	/**
	 * Aksessori, joka laskee pelaajan pisteet suhteessa kuluneeseen aikaan.
	 * @return Pelaajan lopulliset pisteet.
	 */
	public int calculateScores() {
		return (int)(time_left * player.getScore());
	}
	
	/**
	 * Aksessori, joka aloittaa pelitilan. Sisältää peliluupin ja alustaa siinä käytettävät 
	 * arvot ja välittää dynaamiset sekä staattiset objektit kameralle. Luo pelitilassa käytettävät 
	 * tekstit sekä kuvat (sydämet) ja välittää ne GUI:lle.
	 * Peliluuppi sisältää pelilogiikan. Tyhjentää lopuksi käyttämänsä GUI-kentät ja kameran. 
	 * Palauttaa arvonaan informaatiota Pomppu-luokalle siitä, miten pelitila keskeytyi/loppui 
	 * (game over, pause jne.).
	 * @return Informaatiota siitä, miksi peliluuppi keskeytyi. Tämä arvo kertoo Pomppu-luokalle, mihin
	 * tilaan seuraavaksi siirrytään. Vakioarvo on 0 (Resume Game mahdollinen). Mikäli pelaaja on kuollut,
	 * palautetaan arvona 5 (Resume Game ei mahdollinen). 
	 */
	public int doState() {

		boolean spaceReleased = true;
		int spaceReleasedTimer = 0;
		int m_x = 0;
		int m_y = 0;
		int retValue = Pomppu.MAIN_MENU;
		long last_ms, ms, d_ms;
		last_ms = ms = Pomppu.FRAME_DELAY;
		d_ms = 0;
		
		camera.addStaticObjects(map.getStaticObjects());

		if (nonplayers != null)
			for ( NonPlayerObject obj : nonplayers ) 
				camera.addDynamicObject(obj.getObject());

		camera.addDynamicObject(player.getObject());

		Text score = new Text("Coins: 0 Score: 0", "Arial", 0, 24, Color.white);
		Text time = new Text("Time left: 0", "Arial", 0, 24, Color.white);
		
		gui.addToSection(score, 0, 0);
		gui.addToSection(time, 0, 0);

		for (int i=0; i<5; i++)
			gui.addToSection(hearts[i], 2, 0);
		
		// Peliluuppi. Luokan pihvi! :)
		
		Game:
			while(true) {
					
				// Alustetaan / päivitetään fps:n laskemiseen ja tasaamiseen tarvittava arvo
				last_ms = System.currentTimeMillis();
	
				if (d_ms > Pomppu.FRAME_DELAY)
					d_ms = Pomppu.FRAME_DELAY;
				
				// Renderöidään pelitila.
				render();
							
				// Huomioidaan HID-laitteilta saatu informaatio
				if (mouse.moved(m_x, m_y)) {}
				if (keyboard.isPressed(KeyEvent.VK_SHIFT)) {player.run();}
				if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
					
					if (spaceReleased) {
						player.jump(false);
						spaceReleased = false;
					}
	
					player.jump(true);
					spaceReleasedTimer = 1;
				}
				if (keyboard.isPressed(KeyEvent.VK_LEFT)) {player.moveLeft();}
				if (keyboard.isPressed(KeyEvent.VK_RIGHT)) {player.moveRight();}
				if (keyboard.isPressed(KeyEvent.VK_ESCAPE)) { break; }
				
				// Välitetään pelaajan tila väliaikaiselle muuttujalle, johon voidaan reagoida myöhemmin
				int playerStatus = player.update(nonplayerObjects);
				
				switch (playerStatus) {

					case -1:
						retValue = Pomppu.GAME_OVER;
						break Game;
					case -2:
						retValue = Pomppu.GAME_OVER;
						player.addToScore(100);
						break Game;
				}
				
				// Päivitetään ei-pelaaja-objektit
				if (nonplayers != null)
					for ( NonPlayerObject obj : nonplayers ) 
						obj.update(player.getObject(), nonplayerObjects);
	
				// Piirretään sydämet
				gui.clearSection(2,0);
				for (int i=0; i<5; i++)
					if (i<player.getHealth())
						gui.addToSection(heart_on, 2, 0);
					else
						gui.addToSection(heart_off, 2, 0);
				
				// Päivitetään kamera pelaajan kohdalle
				camera.follow(player.getObject());
				
				// Lasketaan, kauanko pitää nukkua fps:n tasaamiseksi
				d_ms = System.currentTimeMillis()-last_ms;
				ms = 60-d_ms;
	
				try { Thread.sleep(Pomppu.FRAME_DELAY-d_ms); } catch (Exception e) {}
				
				if (ms > 60)
					System.out.println(ms);
	
				if (spaceReleasedTimer > 0)
					spaceReleasedTimer--;
				else
					spaceReleased = true;
				
				// Päivitetään GUI-informaatio
				time_left -= (double)(d_ms+Pomppu.FRAME_DELAY)/1000;
				score.updateText("Coins: " + player.getScore() + " Score: " + calculateScores());
				time.updateText("Time left: " + (int)time_left);
			}
	
		gui.clearSection(0, 0);
		gui.clearSection(2, 0);
		camera.clearObjects();
		
		return retValue;
	}
	
	/**
	 * Apumetodi, joka renderöi kameran sisällön ja sen päälle GUI:n sisällön.
	 */
	private void render() {
	
		camera.render();
		camera.renderGUI();	
	}
}