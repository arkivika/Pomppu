/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import pomppu.mechanics.*;

import java.util.ArrayList;

import pomppu.graphics.Drawable;

/**
 * Sisältää yhteiset toiminnallisuudet kaikille valikkotiloille.
 * @author arkivika
 */
public class Menu {
	
	private GUI gui;
	
	private ArrayList<Drawable> passiveEntries;
	private ArrayList<Drawable> activeEntries;
	private ArrayList<Drawable> title;
	
	private int position;
	
	/**
	 * Konstruktori, joka alustaa valikkotiloissa käytetyt arvot (position) sekä oliot (passiveEntries, activeEntries).
	 * @param _gui Käytettävä GUI-olio.
	 */
	public Menu(GUI _gui) {
		gui = _gui;
	
		position = 0;
		passiveEntries = new ArrayList<Drawable>();
		activeEntries = new ArrayList<Drawable>();
		title = new ArrayList<Drawable>();
	}

	/**
	 * Aksessori, joka tyhjentää GUI-olion valikoissa käytettävät alueet.
	 */
	public void clear() {
		gui.clearSection(1, 1);
		gui.clearSection(1, 0);
	}

	/**
	 * Aksessori, joka tyhjentää menu-olion active- ja passiveEntries listat. 
	 */
	public void clearEntries() {
		activeEntries.clear();
		passiveEntries.clear();
	}

	/**
	 * Aksessori, joka lisää menu-olion otsikko-alueeseen drawable-rajapinnan toteuttavan olion (teksti, kuva, animaatio..).
	 * @param _title Lisättävä Drawable-rajapinnan toteuttava olio.
	 */
	public void addTitle(Drawable _title) {
		title.add(_title);
	}

	/**
	 * Aksessori, joka lisää menu-olion entry-alueeseen drawable-rajapinnan toteuttavan olion (teksti, kuva, animaatio..). Tarvitsee sekä
	 * aktiivisen että passiivisen version (kun hiiri on päällä ja kun hiiri on poissa päältä).
	 * @param active Lisättävä aktiivinen Drawable-rajapinnan toteuttava olio.
	 * @param passive Lisättävä passiivinen Drawable-rajapinnan toteuttava olio.
	 */
	public void addEntry(Drawable active, Drawable passive) {
		activeEntries.add(active);
		passiveEntries.add(passive);
	}

	/**
	 * Aksessori, joka renderoi menun.
	 */
	public void render() {
		gui.clearSection(1, 0);
		gui.clearSection(1, 1);
		
		for (Drawable titleElem : title)
			gui.addToSection(titleElem, 1, 0);
		
		for (int i = 0; i < passiveEntries.size(); i++) {

			if (i == position)
				gui.addToSection(activeEntries.get(i), 1, 1);
			else
				gui.addToSection(passiveEntries.get(i), 1, 1);
		}
	}
	
	/**
	 * Aksessori, joka liikuttaa aktiivista aluetta yhden askeleen ylöspäin. 
	 */
	public void moveUp() {
		position--;
		if (position < 0)
			position += passiveEntries.size();
	}
	
	/**
	 * Aksessori, joka liikuttaa aktiivista aluetta yhden askeleen alaspäin. 
	 */
	public void moveDown() {
		position++;
		if (position >= passiveEntries.size())
			position -= passiveEntries.size();
	}
	
	/**
	 * Aksessori, joka määrittää tämänhetkisen aktiivisen alueen.
	 * @param x Halutun kohdan x-koordinaatti (hiiren kursorin x-koordinaatti).
	 * @param y Halutun kohdan y-koordinaatti (hiiren kursorin y-koordinaatti).
	 */
	public void setPosition(int x, int y) {
		int pos = gui.touchesElement(1, 1, x, y);
		if (pos != -1)
			position = pos;
	}

	/**
	 * Aksessori, joka palauttaa tämänhetkisen aktiivisen elementin indeksin. 
	 * @return Aktiivisen elementin indeksi.
	 */
	public int select() {
		return position;
	}
}
