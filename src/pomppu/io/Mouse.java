/** Sisältää tarvittavat luokat input- ja output-toimintoihin, joiden avulla ohjelmalle
 * välitetään informaatiota ulkoisista lähteistä, kuten näppäimistöltä tai hiireltä. Myös 
 * tiedostoista lukeminen sisältyy I/O-toiminnallisuuksiin.
 */
package pomppu.io;

import java.awt.event.*;
import java.awt.Dimension;

/**
 * Hiiri-luokka. Mahdollistaa informaation välittämisen käyttäjältä ohjelmalle hiiren avulla.
 * @author arkivika
 */
public class Mouse {
	
	private boolean[] keyStates;
	private int x, y;
	private MouseKeys mouseKeys;
	private MouseMotion mouseMotion;
	private Dimension insets;
	
	/**
	 * Sisäinen apuluokka, joka laajentaa MouseAdapter-luokkaa tallentaen hiiren näppäimien 
	 * tilan (painettu = true, ylhäällä = false).  
	 * @author arkivika
	 */
	private class MouseKeys extends MouseAdapter {
	
		public void mousePressed(MouseEvent event) {
			switch(event.getButton()) {
				case MouseEvent.BUTTON1:
					keyStates[0] = true;
					break;
				case MouseEvent.BUTTON2:
					keyStates[1] = true;
					break;
				case MouseEvent.BUTTON3:
					keyStates[2] = true;
			}
		}
		
		public void mouseReleased(MouseEvent event) {
			switch(event.getButton()) {
				case MouseEvent.BUTTON1:
					keyStates[0] = false;
					break;
				case MouseEvent.BUTTON2:
					keyStates[1] = false;
					break;
				case MouseEvent.BUTTON3:
					keyStates[2] = false;
			}
		}
	}

	/**
	 * Sisäinen apuluokka, joka laajentaa MouseMotionAdapter-luokkaa tallentaen hiiren x- ja y-koordinaatit.  
	 * @author arkivika
	 */
	private class MouseMotion extends MouseMotionAdapter {

		public void mouseMoved(MouseEvent event) {
			x = event.getX();
			y = event.getY();
		}
		public void mouseDragged(MouseEvent event) {
			x = event.getX();
			y = event.getY();
			
		}
	}
	
	/**
	 * Konstruktori, joka luo jäsenoliot sekä alustaa muuttujat. Ottaa parametrikseen ikkunan reunojen paksuudet, jotta
	 * saadaan selville hiiren todelliset koordinaatit.
	 * @param _insets Ikkunan reunojen mitat.
	 */
	public Mouse(Dimension _insets) {

		x = y = 0;
		keyStates = new boolean[3];
		
		for (int i=0; i<3; i++)
			keyStates[i] = false;
		
		mouseKeys = new MouseKeys();
		mouseMotion = new MouseMotion();
		
		insets = _insets;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan MouseKeys-olion.
	 * @return MouseKeys-olio.
	 */
	public MouseKeys getMouseKeys() {
		return mouseKeys;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan MouseMotion-olion.
	 * @return MouseMotion-olion.
	 */
	public MouseMotion getMouseMotion() {
		return mouseMotion;
	}

	/**
	 * Aksessori, jonka avulla tarkistetaan, mikäli hiiren nappi on painettuna.
	 * @param button Haluttu nappi (0-2).
	 * @return True, mikäli nappi on painettuna, muuten false.
	 */
	public boolean isPressed(int button) {
		if (button < 0 || button >= keyStates.length) 
			return false;
		
		return keyStates[button];		
	}
	
	/**
	 * Aksessori, jonka avulla tarkistetaan, mikäli hiiri on liikkunut.
	 * @param _x Vanha x-koordinaatti
	 * @param _y Vanha y-koordinaatti
	 * @return True, mikäli hiiri on liikkunut, muuten false.
	 */
	public boolean moved(int _x, int _y) {
		
		if (x != _x || y != _y)
			return true;
		
		return false;
	}

	/**
	 * Aksessoti, joka palauttaa arvonaan hiiren x-koordinaatin.
	 * @return Hiiren x-koordinaatti.
	 */
	public int get_x() {
		return x-insets.width;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan hiiren y-koordinaatin.
	 * @return Hiiren y-koordinaatti.
	 */
	public int get_y() {
		return y-insets.height;
	}
	
	/**
	 * Aksessori, joka mahdollistaa hiiren näppäinten tilan alustamisen asettamalla hiille arvon false.
	 */
	public void clear() {
		for (int i=0; i<keyStates.length; i++)
			keyStates[i] = false;
	}
}