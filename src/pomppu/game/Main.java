/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import pomppu.mechanics.*;
import pomppu.graphics.*;
import pomppu.io.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

/**
 * Päävalikko.
 * Ohjelmaa suoritettaessa ensimmäinen tila. Tästä tilasta
 * on mahdollista päästä muihin tiloihin.
 * @see pomppu.game.GameState
 * @author arkivika
 */
public class Main extends GameState {
	
	private Menu menu;
	private boolean validResume;
	
	/**
	 * Konstruktori Main-pelitilalle, joka tarvitsee pelitiloille ominaiset parametrit (Camera-olio,
	 * GUI-olio, Keyboard-olio sekä Mouse-olio). Kutsuu yliluokkansa (GameState) konstruktoria ja 
	 * alustaa valikoissa käytettävän menu-olion. Lisää lopuksi pelitilalle ominaiset osat GUI:hin.  
	 * @param _cam Käytettävä Camera-olio.
	 * @param _gui Käytettävä GUI-olio.
	 * @param _keyboard Käytettävä Keyboard-olio.
	 * @param _mouse Käytettävä Mouse-olio.
	 */	
	public Main(Camera _cam, GUI _gui, Keyboard _keyboard, Mouse _mouse) {

		super(_cam, _gui, _keyboard, _mouse);
	
		validResume = true;
		
		menu = new Menu(_gui);
		
		menu.addTitle(new Text("Pomppu 1.0", "Tahoma", Font.BOLD, 32, Color.GREEN));
		menu.addTitle(new Text("Main Menu", "Tahoma", Font.BOLD, 28, Color.YELLOW));
		menu.addEntry(new Text("New Game", "Tahoma", Font.BOLD, 32, Color.RED), 
					  new Text("New Game", "Tahoma", Font.BOLD, 32, Color.WHITE));
		menu.addEntry(new Text("Resume Game", "Tahoma", Font.BOLD, 32, Color.RED), 
					  new Text("Resume Game", "Tahoma", Font.BOLD, 32, Color.WHITE));
		menu.addEntry(new Text("Highscores", "Tahoma", Font.BOLD, 32, Color.RED), 
					  new Text("Highscores", "Tahoma", Font.BOLD, 32, Color.WHITE));
		menu.addEntry(new Text("Settings", "Tahoma", Font.BOLD, 32, Color.RED), 
				  	  new Text("Settings", "Tahoma", Font.BOLD, 32, Color.WHITE));
		menu.addEntry(new Text("Quit", "Tahoma", Font.BOLD, 32, Color.RED), 
				  	  new Text("Quit", "Tahoma", Font.BOLD, 32, Color.WHITE));
	}

	/**
	 * Aksessori, joka aloittaa päävalikko-pelitilan. Sisältää tilaluupin sekä alustaa siinä käytettävät arvot.
	 * @return Palauttaa arvonaan muuttujan, joka kertoo Pomppu-luokalle, mihin tilaan siirrytään seuraavaksi. 
	 * Vakioarvo on -1, mikä tarkoittaa pelin lopettamista.
	 */
	public int doState() {

		int m_x = 0;
		int m_y = 0;
		
		while(true) {

			render();

			try { Thread.sleep(Pomppu.FRAME_DELAY); } catch (Exception e) {}
			
			if (mouse.moved(m_x, m_y)) {
				
				m_x = mouse.get_x();
				m_y = mouse.get_y();
				menu.setPosition(m_x, m_y);
			}
			
			if (mouse.isPressed(0) || keyboard.isPressed(KeyEvent.VK_ENTER)) {
				
				try { Thread.sleep(Pomppu.MOUSE_SLEEP_DELAY); } catch(Exception e) {}
				
				camera.clearGUI();
				menu.clear();

				if (menu.select() == 0)
					return Pomppu.NEW_GAME;			
				if (menu.select() == 1)
					if (validResume) 
						return Pomppu.RESUME_GAME;
					else {
						validResume = true;
						return Pomppu.NEW_GAME;
						}
				if (menu.select() == 2)
					return Pomppu.HIGHSCORES;
				if (menu.select() == 3)
					return Pomppu.SETTINGS;
				if (menu.select() == 4)
					break;
			}
		}

		return Pomppu.SENTINEL;
	}
	
	/**
	 * Aksessori, joka määrittää, mikäli "Resume Game"-valinta on pätevä.	
	 * @param _valid True, jos "Resume Game" on pätevä, muuten false.
	 */
	public void setValidResume(boolean _valid) {
		validResume = _valid;
	}

	/**
	 * Apumetodi, joka päivittää GUI:n ja renderöi menu:n.
	 */
	private void render() {

		camera.clearGUI();
		menu.render();
		camera.renderGUI();
	}
}
