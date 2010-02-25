/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import java.util.ArrayList;
import pomppu.mechanics.DynamicObject;

/**
 * Ei-pelaaja-luokka. Sisältää funktionalisuudet objekteille, jotka eivät ole pelaajan kontrolloitavissa:
 * törmäyksentarkistusrutiinit sekä dynaamisten että staattisten objektien suhteen. Sisältää
 * myös alkeellisen tekoälyn.
 * @author arkivika
 */
public class NonPlayerObject {
		
	private DynamicObject obj;
	private int staticCol[][];
	private Map map;	
	private int direction;

	/**
	 * Konstruktori, joka alustaa ei-pelaaja-objektin. Tarvitsee parametreiksi oman dynaamisen objektin
	 * ja kartan, joka sisältää staattiset objektit. Tarvitsee myös objektin aloitussuunnan.
	 * @param _obj Käytettävä dynaaminen objekti.
	 * @param _map Kartta, joka sisältää staattiset objektit.
	 * @param _direction Aloitussuunta (-1 tai 1).
	 */		
	public NonPlayerObject(DynamicObject _obj, Map _map, int _direction) {
		
		direction = _direction;
		map = _map;
		
		obj = _obj;
		obj.setAutoAnimation(false);
	}
	
	/**
	 * Aksessori, joka päivittää ei-pelaaja-objektin tilaa. Käytännössä tämä metodi sisältää objektin
	 * tekoälyn: kuinka reagoida törmäyksiin ja miten käyttäytyä yleisesti. Tarvitsee parametreiksi 
	 * pelaajan dynaamisen objektin sekä listan muista dynamaisista objekteista törmäyksentarkistusta varten.
	 * @param player Pelaajan dynaaminen objekti.
	 * @param others Lista kaikista ei-pelaaja-objekteista.
	 * @return Palauttaa vakiona 0. Voidaan käyttää objektin tilainformaation välittämiseksi game-luokalle.
	 */
	public int update(DynamicObject player, ArrayList<DynamicObject> others) {
	
		if (obj.getActive()) {
					
			if (obj.getType() == 2) {
				jump(); 
			}
			 
			staticCol = obj.staticCollision(map);

			int top_col = staticCol[DynamicObject.TOP_COLLIDE][1];
			int bottom_col = staticCol[DynamicObject.BOTTOM_COLLIDE][1];
			int left_col = staticCol[DynamicObject.LEFT_COLLIDE][1];
			int right_col = staticCol[DynamicObject.RIGHT_COLLIDE][1];
			
			if (top_col != -1) {}
			if (bottom_col != -1) {}
			if (left_col != -1 && left_col < 100) { direction = 1; }
			if (right_col != -1 && right_col < 100) { direction = -1; }
				
			for (DynamicObject other : others) {
				if (other != this.getObject()) {
					if (obj.dynamicCollision(other, DynamicObject.TOP_COLLIDE)) {}
					if (obj.dynamicCollision(other, DynamicObject.BOTTOM_COLLIDE)) {}
					if (obj.dynamicCollision(other, DynamicObject.LEFT_COLLIDE)) {}
					if (obj.dynamicCollision(other, DynamicObject.RIGHT_COLLIDE)) {}
				}
			}
				
			if (obj.dynamicCollision(player, DynamicObject.TOP_COLLIDE)) {}
			if (obj.dynamicCollision(player, DynamicObject.BOTTOM_COLLIDE)) {}
			if (obj.dynamicCollision(player, DynamicObject.LEFT_COLLIDE)) {}
			if (obj.dynamicCollision(player, DynamicObject.RIGHT_COLLIDE)) {}
			
			obj.update();

			move(direction);
		}
		
		return 0;
	}

	/**
	 * Aksessori, jolla voidaan liikuttaa objektia. Liikuttaa samalla objektin animaatiota.
	 * @param _direction Objektin suunta (-1 tai 1).
	 */
	public void move(int _direction) {
		obj.advanceFrame();
		obj.move(_direction);
	}
	
	/**
	 * Aksessori, jolla saadaan objekti hyppäämään. Liikuttaa samalla objektin animaatiota.
	 */
	public void jump() {
		obj.jump(false);
		obj.continueJumping();
	}
	
	/**
	 * Aksessori, jolla saadaan objekti juoksemaan (liikkumaan nopeutetulla vauhdilla).
	 */
	public void run() {
		obj.run();
	}

	/**
	 * Aksessori, joka palauttaa arvonaan ei-pelaaja-objektin dynaamisen objektin.
	 * @return Ei-pelaaja-objektin dynaaminen objekti.
	 */
	public DynamicObject getObject() {
		return obj;
	}
}