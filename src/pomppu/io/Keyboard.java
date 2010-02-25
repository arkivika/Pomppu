/** Sisältää tarvittavat luokat input- ja output-toimintoihin, joiden avulla ohjelmalle
 * välitetään informaatiota ulkoisista lähteistä, kuten näppäimistöltä tai hiireltä. Myös 
 * tiedostoista lukeminen sisältyy I/O-toiminnallisuuksiin.
 */
package pomppu.io;

import java.awt.event.*;
import java.util.HashMap;

/**
 * Näppäimistö-luokka. Mahdollistaa informaation välittämisen käyttäjältä ohjelmalle näppäimistön avulla.
 * @author arkivika
 */
public class Keyboard extends KeyAdapter {
	
	private HashMap<Integer,Boolean> keyStates;
	
	/**
	 * Konstruktori, joka alustaa näppäinten tilat. 
	 */
	public Keyboard() {
		keyStates = new HashMap<Integer,Boolean>();
	}

	/**
	 * Aksessori, jolla kokeillaan, mikäli haluttu näppäin on painettuna.
	 * @param keyCode Näppäimen virtuaalinen näppäinkoodi (VK_XXXX).
	 * @return True, mikäli näppäin on painettuna alas, muuten false.
	 */
	public boolean isPressed(int keyCode) {
		Boolean state = keyStates.get(keyCode);
		return (state == null) ? false : state.booleanValue();
	}
	
	/**
	 * Aksessori, joka korvaa abstraktin KeyAdapter-luokan metodin keyPressed.		
	 */
	public void keyPressed(KeyEvent event) {
		keyStates.put(event.getKeyCode(), true);
	}
	
	/**
	 * Aksessori, joka korvaa abstraktin KeyAdapter-luokan metodin keyReleased.
	 */
	public void keyReleased(KeyEvent event) {
		keyStates.put(event.getKeyCode(), false);
	}
	
	/**
	 * Aksessori, joka alustaa näppäimistön tilan tyhjentämällä keyStates-HashMapin.
	 */
	public void clear() {
		keyStates.clear();
	}
}