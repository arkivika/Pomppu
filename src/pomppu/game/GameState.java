/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import pomppu.mechanics.*;
import pomppu.io.*;

/**
 * Abstrakti luokka ohjelmassa käytetyille tiloille, joka sisältää tarvittavat kytkökset eri 
 * tilojen tarvitsemiin olioihin.
 * @author arkivika
 */
public abstract class GameState {
	
	protected GUI gui;
	protected Camera camera;
	protected Keyboard keyboard;
	protected Mouse mouse;
	
	/**
	 * Luo GameState-luokan perivälle oliolle sen tarvitsemat kytkökset. 	
	 * @param _camera Kamera-olio.
	 * @param _gui GUI-olio.
	 */
	public GameState(Camera _camera, GUI _gui, Keyboard _keyboard, Mouse _mouse) {
		keyboard = _keyboard;
		camera = _camera;
		gui = _gui;
		mouse = _mouse;
	}
	
	/**
	 * Suorittaa tilalle ominaisen operaation.
	 * @return Seuraavan sovellettavan tilan indeksin.
	 */
	public abstract int doState();
	
}
