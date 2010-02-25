/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import pomppu.mechanics.*;
import pomppu.graphics.*;
import pomppu.io.*;

import java.awt.*;

/**
 * Asetukset.
 * Tässä tilassa on mahdollista muuttaa pelin asetuksia.
 * @see pomppu.game.GameState
 * @author arkivika
 */
public class Settings extends GameState {
	
	private Menu menu;
	private Screen screen;
	private Dimension dimensions[] = {new Dimension(640,480), 
									  new Dimension(800,600),
									  new Dimension(1024,600),
									  new Dimension(1024,768)};

	private Text resolution_on, resolution_off;
	private int size;
	
	/**
	 * Konstruktori Settings-pelitilalle, joka tarvitsee pelitiloille ominaiset parametrit (Camera-olio,
	 * GUI-olio, Keyboard-olio sekä Mouse-olio). Tarvitsee lisäksi Screen-olion resoluution muuttamiseksi. Kutsuu
	 * yliluokkansa (GameState) konstruktoria ja alustaa valikoissa käytettävän menu-olion. Lisää lopuksi tilalle 
	 * ominaiset osat GUI:hin.
	 * @param _cam Käytettävä Camera-olio.
	 * @param _gui Käytettävä GUI-olio.
	 * @param _keyboard Käytettävä Keyboard-olio.
	 * @param _mouse Käytettävä Mouse-olio.
	 * @param _screen Käytettävä Screen-olio.
	 */
	public Settings(Camera _cam, GUI _gui, Keyboard _keyboard, Mouse _mouse, Screen _screen) {

		super(_cam, _gui, _keyboard, _mouse);
		menu = new Menu(_gui);
		
		resolution_on = new Text("Resolution: 640x480", "Arial", 0, 32, Color.red);
		resolution_off = new Text("Resolution: 640x480", "Arial", 0, 32, Color.white);
		
		menu.addTitle(new Text("Pomppu 1.0", "Tahoma", Font.BOLD, 32, Color.GREEN));
		menu.addTitle(new Text("Settings", "Tahoma", Font.BOLD, 28, Color.YELLOW));
		menu.addEntry(resolution_on, resolution_off);
		menu.addEntry(new Text("Back", "Tahoma", Font.BOLD, 32, Color.RED), 
				  	  new Text("Back", "Tahoma", Font.BOLD, 32, Color.WHITE));

		screen = _screen;
		size = 0;
	}
	
	/**
	 * Aksessori, joka aloittaa settings-tilan. Sisältää tilaluupin sekä alustaa siinä käytettävät arvot.
	 * @return Palauttaa arvonaan muuttujan, joka kertoo Pomppu-luokalle, mihin tilaan siirrytään seuraavaksi. Vakioarvo on 0.
	 */
	public int doState() {

		int m_x = 0;
		int m_y = 0;
		
		while(true) {
				
			render();
			
			try { Thread.sleep(66); } catch (Exception e) {}
			
			if (mouse.moved(m_x, m_y)) {
				
				m_x = mouse.get_x();
				m_y = mouse.get_y();
				menu.setPosition(m_x, m_y);
			}
			
			if (mouse.isPressed(0)) {

				try { Thread.sleep(Pomppu.MOUSE_SLEEP_DELAY); } catch(Exception e) {}
				
				if (menu.select() == 0) {
					size++;
						if (size > 3)
							size = 0;
						
					screen.setSize(dimensions[size]);

					resolution_on.updateText("Resolution: " + screen.getWidth() + "x" + screen.getHeight());
					resolution_off.updateText("Resolution: " + screen.getWidth() + "x" + screen.getHeight());
				}

				if (menu.select() == 1)
					break;
			}
		}
	
		camera.clearGUI();
		menu.clear();
		
		return Pomppu.MAIN_MENU;
	}
	
	/**
	 * Apumetodi, joka päivittää GUI:n sekä renderöi menu:n.
	 */	
	private void render() {
	
		camera.clearGUI();
		menu.render();
		camera.renderGUI();	
	}
}