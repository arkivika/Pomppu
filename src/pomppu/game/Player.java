/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import java.util.ArrayList;
import pomppu.mechanics.DynamicObject;
import pomppu.graphics.*;

/**
 * Pelaaja-luokka. Sisältää tarpeelliset funktionalisuudet pelaajalle.
 * @author arkivika
 */
public class Player {
		
	private DynamicObject obj;
	private int staticCol[][];
	private Map map;	
	private int score, health, health_delay, health_counter;
	
	boolean keyboard_moving;
	
	/**
	 * Konstruktori, joka ottaa parametreikseen pelaajahahmon käyttämät animaation, pelaajan x- sekä y-aloituskoordinaatit sekä Map-olion.
	 * @param _drawables Pelaajan animaatiot.
	 * @param x Pelaajan x-aloituskoordinaatti.
	 * @param y Pelaajan y-aloituskoordinaatti.
	 * @param _map Map-olio.
	 */
	public Player(ArrayList<Animation> _drawables, int x, int y, Map _map) {
		
		if (_drawables == null || _drawables.size() == 0)
			return;

		score = 0;
		health_delay = 100;
		health = 5;
		health_counter = 100;
		
		map = _map;
		
		obj = new DynamicObject(_drawables);
		obj.setPos(x, y);
		obj.setType(1);
		obj.setAutoAnimation(false);
		obj.setActive(true);
	
		keyboard_moving = false;
	}

	/**
	 * Aksessori, joka päivittää pelaajan tilan. Tarvitsee parametrikseen listan muista dynaamisista objekteista,
	 * käytännössä lista niistä objekteista, joiden suhteen törmäyksiä tulee tarkistaa (ei-pelaajahahmot).
	 * @param others Lista ei-pelaajahahmojen dynaamisista objekteista.
	 * @return Muuttuja (int), joka kertoo Game-pelitilalle pelaajan tilasta. 0, mikäli kaikki on hyvin, -1, mikäli pelaaja on kuollut.
	 */
	public int update(ArrayList<DynamicObject> others) {
		
		int retValue = 0;
		
		if (health_counter < health_delay)
			health_counter++;
		
		obj.update();
		
		staticCol = obj.staticCollision(map);
	
		if (!obj.isDead()) {

			for (int i=0; i<4; i++)
				for (int j=0; j<3; j++) {
					switch(staticCol[i][j]) {
						case 56:
							score++;
							break;
						case 200:
							retValue = -2;
							break;				
					}
				}

			for (DynamicObject other : others) {
				
				if (other.getType() == 2 || other.getType() == 3) {
				
					if (obj.dynamicCollision(other, DynamicObject.TOP_COLLIDE) || obj.dynamicCollision(other, DynamicObject.BOTTOM_COLLIDE) ||
						obj.dynamicCollision(other, DynamicObject.LEFT_COLLIDE) || obj.dynamicCollision(other, DynamicObject.RIGHT_COLLIDE)) {
					
						if (health_counter == health_delay) {
							health--;
							obj.bump(other);
							health_counter = 0;
						}
					}
				}
			}

			if (!keyboard_moving)
				obj.resetAnimation();
		
			keyboard_moving = false;
			
			if (health <= 0) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { System.out.println("Couldn't sleep!"); }
				obj.kill();
			}
		}
		
		if (obj.offScreen())
			retValue = -1;
		
		return retValue;
	}

	/**
	 * Aksessori, jonka avulla pelaajan dynaamista objektia liikutetaan vasemmalle. Edistää myös pelaajan aktiivista animaatiota.
	 */
	public void moveLeft() {
		obj.advanceFrame();
		obj.move(-1);
		keyboard_moving = true;
	}

	/**
	 * Aksessori, jonka avulla pelaajan dynaamista objektia liikutetaan oikealle. Edistää myös pelaajan aktiivista animaatiota.
	 */
	public void moveRight() {
		obj.advanceFrame();
		obj.move(1);
		keyboard_moving = true;
	}
	
	/**
	 * Aksessori, jonka avulla pelaajan dynaaminen objekti saadaan hyppäämään.
	 */
	public void jump(boolean _continue) {
		if (!_continue) {
			obj.jump(false);
			keyboard_moving = true;
		}
		else
			obj.continueJumping();
	}
	
	/**
	 * Aksessori, jonka avulla pelaajan dynaaminen objekti saadaan liikkumaan nopeammin (juoksemaan).
	 */
	public void run() {
		obj.run();
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan pelaajan dynaamisen objektin.
	 * @return Pelaajan dynaaminen objekti.
	 */
	public DynamicObject getObject() {
		return obj;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan pelaajan keräämien kolikoiden määrän.
	 * @return Pelaajan keräämien kolikoiden määrä.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Aksessori, jonka avulla voidaan lisätä pelaajan pisteitä.
	 * @param amount Lisättävä määrä.
	 */
	public void addToScore(int amount) {
		score += amount;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan pelaajan jäljellä olevien sydämien määrän.
	 * @return Pelaajan jäljellä olevien sydämien määrä.
	 */
	public int getHealth() {
		return health;
	}
}