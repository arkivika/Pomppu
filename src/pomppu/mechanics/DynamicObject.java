/**
 * Pelimoottorin pakkaus, sisältää 2d-pelille ominaiset geneeriset toiminnallisuudet.
 */
package pomppu.mechanics;

import java.util.ArrayList;

import pomppu.game.Map;
import pomppu.graphics.*;

/**
 * Dynaaminen objekti.
 * Sisältää pelissä käytetyn dynaamisen objektin funktionalisuudet, kuten liikuttamisen sekä 
 * törmäyksentarkistuksen. Myös dynaamisen objektin kuvadata sisältyy tähän luokkaan.
 * @author arkivika
 */
public class DynamicObject {
	
	// Final-arvot
	
	public static final int TOP_COLLIDE 	= 0;
	public static final int BOTTOM_COLLIDE 	= 1;
	public static final int LEFT_COLLIDE 	= 2;
	public static final int RIGHT_COLLIDE 	= 3;

	// Fysiikka-arvot
	
	private double gravity, def_gravity;
	private double jumpspeed, def_jumpspeed;
	private double accel, def_accel;
	private double max_speed, def_max_speed;
	private double friction, def_friction;
	private double max_fall_speed, def_max_fall_speed;
	
	// Objektin tila-arvot
	
	private boolean airborne, keyboard_moving, continue_jumping, offmap, run, active, dead;
	private double x, y, vel_x, vel_y, old_x;
	private int state, type, direction;

	// Objektin animaatio
	
	private ArrayList<Animation> animations;
	
	/**
	 * Alustaa dynaamisen objektin animaation sekä vakioarvot. Alustettavat fysiikka-vakioarvot 
	 * määräävät, mihin arvoihin objektin sisäiset fysiikka-arvot palautuvat jokaisen
	 * päivityskerran jälkeen. Fysiikka-vakioarvot ovat muunneltavissa tämän luokan aksessorien
	 * avulla. Vakioarvojen sekä sisäisten arvojen erottelu johtuu pelimekaniikasta:
	 * dynaaminen objekti saattaa pitää jotakin arvoista sisäisesti eri arvoisena kuin annettu
	 * vakioarvo on johtuen esimerkiksi siitä, että sisäinen törmäyksentarkistus kertoo 
	 * objektin olevan vedessä. Näinollen jokaisella päivityskerralla kokeillaan, ollaanko
	 * vedessä, säädetään sisäinen arvo kohdalle, toimitaan ja lopuksi säädetään se normaaliasentoonsa.
	 * @param _animations Dynaamiselle objektille välitettävät animaatiot.
	 */
	public DynamicObject(ArrayList<Animation> _animations) {
	
		animations = _animations;
		
		x = y = old_x = vel_x = vel_y = state = type = direction = 0;

		offmap = active = dead = false;
		airborne = true;
		
		def_gravity = 0.7;
		def_jumpspeed = 15.0;
		def_accel = 1.3;
		def_max_speed = 5.0;
		def_friction = 0.9;
		def_max_fall_speed = 15.0;
		
		resetValues();
	}

	/**
	 * Apumetodi, joka resetoi kaikki tarvittavat muuttujat 
	 * konstruktorissa sekä jokaisen päivityskierroksen jälkeen. 
	 */
	private void resetValues() {
		
		gravity = def_gravity;
		jumpspeed = def_jumpspeed;
		accel = def_accel;
		friction = def_friction;
		max_fall_speed = def_max_fall_speed;

		if (max_speed > def_max_speed && !airborne && run == false)	
			max_speed -= accel/2;

		if (max_speed < def_max_speed)
			max_speed = def_max_speed;
	
		keyboard_moving = false;
		continue_jumping = false;
		run = false;		
	}

	/**
	 * Pitää huolen siitä, ettei dynaaminen objekti pääse ruudun ulkopuolelle.
	 */
	private void validatePosition() {
		if (x < 32) {
			vel_x = 0;
			x = 32;
		}
		if (y < 32) {
			vel_y = 0;
			y = 32;
		}
	}

	/**
	 * Päivittää dynaamisen objektin tilan.
	 */
	public void update() {
		
		old_x = x;
		
		if (airborne) {
			
			if (state < 2)
				setState(state+2);
			if (vel_x > 0)
				setState(2);
			if (vel_x < 0)
				setState(3); 
			
			if (type == 1)
				setAnimationSpeed(0.1);
				
			y += vel_y;
			vel_y += gravity;
			
			if (vel_y < 0 && !continue_jumping)
				vel_y += gravity*4;
				
			if (vel_y > max_fall_speed)
				vel_y = max_fall_speed;

		}
		else { 

			if (state > 1)
				setState(state-2);
			
			if (vel_x > 0)
				setState(0);
			if (vel_x < 0)
				setState(1);
				
			setAnimationSpeed(0.3);
						
		}
		
		if (!keyboard_moving && vel_x != 0) {
		
			if (!airborne) {
				vel_x += (vel_x > 0) ? -friction : friction;
				if (vel_x > -friction && vel_x < friction)
					vel_x = 0;
			}
			else {
				vel_x += (vel_x > 0) ? -friction*0.05 : friction*0.05;
				if (vel_x > -friction && vel_x < friction)
					vel_x = 0;
			}
				
		}
		
		if (!dead)
			x += vel_x;
		
		if (vel_x < 0)
			direction = -1;
		else if (vel_x > 0)
			direction = 1;
		else
			direction = 0;

		validatePosition();

		resetValues();
	}

	/**
	 * Dynaamisen objektin törmäyksentarkistusrutiini toisen dynaamisen objektin suhteen.
	 * @param obj Dynaaminen objekti, jonka suhteen törmäys tarkistetaan.
	 * @param part Dynaamisen objektin osa, joka halutaan tarkistaa.<br>TOP_COLLIDE (0), BOTTOM_COLLIDE (1), LEFT_COLLIDE (2), RIGHT_COLLIDE (3)
	 * @return Palauttaa arvon true, mikäli törmäys on tapahtunut. Muuten palauttaa arvon false.
	 */
	public boolean dynamicCollision(DynamicObject obj, int part) {
		
		double end_x	= obj.getAnimation().getWidth();
		double real_y	= obj.getAnimation().getHeight();
		double start_y	= obj.getAnimation().getHeight()*0.3;
		double end_y	= obj.getAnimation().getHeight()*0.7;
		double an_width = obj.getAnimation().getWidth();

		switch(part) {
		
			case TOP_COLLIDE:
				if ( ( ( obj.getX()			>= x && obj.getX()			<= x+animations.get(state).getWidth() ) || 
					   ( obj.getX()+end_x	>= x && obj.getX()+end_x	<= x+animations.get(state).getWidth() ) ||
					   ( obj.getX()			<= x && obj.getX()+end_x	>= x+animations.get(state).getWidth() ) ) &&  
					   ( obj.getY()			>= y && obj.getY()			<= y+animations.get(state).getHeight() ) ) {
				//	vel_x = 0;
					return true;
				}
		
			case BOTTOM_COLLIDE:
				if ( ( ( obj.getX()			>= x && obj.getX()			<= x+animations.get(state).getWidth() ) || 
					   ( obj.getX()+end_x	>= x && obj.getX()+end_x	<= x+animations.get(state).getWidth() ) ||
					   ( obj.getX()			<= x && obj.getX()+end_x	>= x+animations.get(state).getWidth() ) ) &&  
					   ( obj.getY()+real_y	>= y && obj.getY()+real_y	<= y+animations.get(state).getHeight() ) ) { 
				//	vel_x = 0;
					return true;
			}
				
			case LEFT_COLLIDE:
				if ( ( obj.getX() 			>= x && obj.getX() 			<= x+animations.get(state).getWidth()  ) &&  
				   ( ( obj.getY()+start_y 	>= y && obj.getY()+start_y	<= y+animations.get(state).getHeight() ) || 
					 ( obj.getY()+end_y 	>= y && obj.getY()+end_y 	<= y+animations.get(state).getHeight() ) || 
					 ( obj.getY()+start_y 	<= y && obj.getY()+end_y 	>= y+animations.get(state).getHeight() ) ) ) {
					//x = old_x;
					//vel_x = 0;
					return true;
				}
			
			case RIGHT_COLLIDE:
				if ( ( obj.getX()+an_width	>= x && obj.getX()+an_width <= x+animations.get(state).getWidth()  ) &&  
				   ( ( obj.getY()+start_y	>= y && obj.getY()+start_y 	<= y+animations.get(state).getHeight() ) || 
					 ( obj.getY()+end_y		>= y && obj.getY()+end_y 	<= y+animations.get(state).getHeight() ) || 
					 ( obj.getY()+start_y	<= y && obj.getY()+end_y 	>= y+animations.get(state).getHeight() ) ) ) {
					//x = old_x;
					//vel_x = 0;
					return true;
				}
			
		}

		return false;
		
	}

	/**
	 * Dynaamisen objektin törmäyksentarkistusrutiini staattisten objektien suhteen. 
	 * Tämä metodi reagoi suoraan törmäyksiin dynaamisen objektin tilan suhteen eikä odota update()-kutsua.
	 * @param _map Map-olio, joka sisältää staattiset objektit. 
	 * @return Palauttaa int-tyyppisen kaksiulotteisen taulukon. Ulompi alkio sisältää aina neljä alkiota, ja sisemmät taulukot
	 * sisältävät kukin 3 alkiota. Tämä johtuu törmäyksentarkistusruutiinin luonteesta: Ulompia alkioita on neljä; yksi jokaista
	 * dynaamisen objektin osaa varten (top, bottom, left, right). Niistä kukin sisältää kolme alkiota, koska törmäyksentarkistusrutiini
	 * tarkistaa törmäykset kolmen vierekkäisen staattisen objektin kohdalta riippuen dynaamisen objektin tarkistettavasta osasta. 
	 * Nämä kolme alkiota sisältävät törmäyksen tapahtuessa staattisten objektien tyypit. Mikäli törmäystä ei tapahtunut ko. staattisen
	 * objektin kohdalla, palautetaan vastaavassa alkiossa -1.
	 */
	public int[][] staticCollision(Map _map) {
		
		int retValue[][] = {{-1,-1,-1},{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}};

		// Vasen törmäys
		
		if (vel_x <= 0.0 && !dead) { 
			 retValue[LEFT_COLLIDE] = _map.collide(this, LEFT_COLLIDE);
			 for (int i=0; i<3; i++) 
				if (retValue[LEFT_COLLIDE][i] >= 0 && retValue[LEFT_COLLIDE][i] < 50 && 
					retValue[LEFT_COLLIDE][i] != 5 && retValue[LEFT_COLLIDE][i] != 2 && 
					retValue[LEFT_COLLIDE][i] != 1 && retValue[LEFT_COLLIDE][i] != 4) {
					x = old_x;
					vel_x = 0;
					break;
				}
		}

		// Oikea törmäys

		if (vel_x >= 0.0 && !dead) { 
			retValue[RIGHT_COLLIDE] = _map.collide(this, RIGHT_COLLIDE);
			for (int i=0; i<3; i++) 
				if ((retValue[RIGHT_COLLIDE][i] >= 0 && retValue[RIGHT_COLLIDE][i] < 50 && 
					 retValue[RIGHT_COLLIDE][i] != 5 && retValue[RIGHT_COLLIDE][i] != 2 && 
					 retValue[RIGHT_COLLIDE][i] != 3 && retValue[RIGHT_COLLIDE][i] != 6) || 
					 retValue[RIGHT_COLLIDE][i] == -3) {
					x = old_x;
					vel_x = 0;
					break;
				}
		
		}

		// Pohjan törmäys

		if (vel_y >= 0.0) {
			retValue[BOTTOM_COLLIDE] = _map.collide(this, BOTTOM_COLLIDE);
			for (int i=0; i<3; i++) {
				if (((retValue[BOTTOM_COLLIDE][i] > -1 && retValue[BOTTOM_COLLIDE][i] < 50) || 
					  retValue[BOTTOM_COLLIDE][i] == 102 || 
					  retValue[BOTTOM_COLLIDE][i] == 105) && 
					 (retValue[BOTTOM_COLLIDE][i] != 4 && retValue[BOTTOM_COLLIDE][i] != 5 && retValue[BOTTOM_COLLIDE][i] != 6)) {
					if (!dead) {
						y = (int)((((int)y + animations.get(state).getHeight())/32)*32 - animations.get(state).getHeight());
						vel_y = 0;
						airborne = false;
					}
					break;
				}
				else 
					airborne = true;
			
				if (retValue[BOTTOM_COLLIDE][i] == -2)
						offmap = true;
			}
		}
		
		// Huipun törmäys

		if (vel_y < 0.0 && !dead) {
			retValue[TOP_COLLIDE] = _map.collide(this, TOP_COLLIDE);
			for (int i=0; i<3; i++) 
				if (retValue[TOP_COLLIDE][i] == 0 ||
				   (retValue[TOP_COLLIDE][i] > 6  && 
					retValue[TOP_COLLIDE][i] < 50)) {
					y = (int)(((int)y/32+1)*32);
					vel_y = 0;
					break;
				}
		}
		
		// Vedessä
		
		for (int i=0; i<3; i++)
			if (((retValue[TOP_COLLIDE][i] 	 	> 100 && retValue[TOP_COLLIDE][i]		< 105) ||
				 (retValue[BOTTOM_COLLIDE][i] 	> 100 && retValue[BOTTOM_COLLIDE][i] 	< 105) ||
				 (retValue[RIGHT_COLLIDE][i]  	> 100 && retValue[RIGHT_COLLIDE][i]  	< 105) ||
				 (retValue[LEFT_COLLIDE][i]		> 100 && retValue[LEFT_COLLIDE][i]   	< 105)) && !dead) {
				if (retValue[TOP_COLLIDE][i] == 103 || retValue[BOTTOM_COLLIDE][i] == 103 || retValue[RIGHT_COLLIDE][i] == 103 || retValue[LEFT_COLLIDE][i] == 103)
					gravity = def_gravity * 3;
				else if (retValue[TOP_COLLIDE][i] == 104 || retValue[BOTTOM_COLLIDE][i] == 104 || retValue[RIGHT_COLLIDE][i] == 104 || retValue[LEFT_COLLIDE][i] == 104) {
					vel_y = -30.0;
					gravity = 0;
					airborne = true;
				}
				else {
					gravity = def_gravity / 2;
					max_fall_speed = def_max_fall_speed / 3;
				}
				max_speed = def_max_speed / 2;
				accel = def_accel / 2;
			}
		
		validatePosition();

		return retValue;
	}
	
	/**
	 * Pienimuotoinen törmäysmetodi toisen dynaamisen objektin suhteen, jota voidaan halutessa kutsua myös dynaamisen objektin ulkopuolelta.
	 * @param obj Dynaaminen objekti, johon törmätään.
	 */
	public void bump(DynamicObject obj) {
	
		if (direction != obj.direction || direction == 0)
			vel_x = 2*obj.vel_x;
		else
			vel_x = -2*obj.vel_x;
		
		vel_y = -jumpspeed/2;
		airborne = true;
	}
	
	/**
	 * Dynaamisen objektin hyppymetodi. 
	 */
	public void jump(boolean force) {
		
		if (!airborne || force) {
			vel_y = -jumpspeed;
			airborne = true;
		}
	}

	/**
	 * Dynaamisen objektin "jatka hyppyä"-metodi. Tätä kutsutaan jatkuvasti, mikäli hypyn halutaan
	 * jatkuvan maksimikorkeuteen. Jos hyppy halutaan jättää matalaksi, ko. metodin kutsuminen tulee lopettaa.
	 */
	public void continueJumping() {
		continue_jumping = true;			
	}
	
	/**
	 * Dynaamisen objektin "juokse"-metodi. Tätä kutsutaan jatkuvasti, mikäli halutaan dynaamisen objektin liikkuvan
	 * vakiota suuremmalla nopeudella. Mikäli halutaan palata normaalinopeuteen (def_max_speed), tulee metodin kutsuminen lopettaa.
	 */
	public void run() {
		if (!airborne && max_speed < 10.0)
			max_speed += accel/5;
		run = true;
	}

	/**
	 * Dynaamisen objektin liikuttamismetodi x-akselilla.
	 * @param direction Suunta, johon liikutetaan (1 = oikealle, -1 = vasemmalle).
	 */
	public void move(int direction) {
		
		if (direction >= 0)
			direction = 1;
		if (direction < 0)
			direction = -1;
		
		keyboard_moving = true;
		
		if (!airborne) {
			vel_x += direction*accel;

			if (vel_x > max_speed)
				vel_x = max_speed;
			if (vel_x < -max_speed)
				vel_x = -max_speed;
		} else {
			vel_x += direction*accel*0.3;

			if (vel_x > max_speed)
				vel_x = max_speed;
			if (vel_x < -max_speed)
				vel_x = -max_speed;
		}
	}

	/**
	 * Aksessori, jonka avulla asetetaan hyppyvauhti.
	 * @param speed Haluttu vauhti.
	 */
	public void setJumpSpeed(double speed) {
		def_jumpspeed = speed;
	}
	
	/**
	 * Aksessori joka palauttaa objektin tyypin. Viittaa siihen, onko kyseessä pelaajan objekti (1) vai tekoälyllä tai muullaa toiminnallisuudella
	 * varustettu objekti (>1).
	 * @return Objektin tyyppi.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Aksessori, jonka avulla objektille on mahdollista asettaa sen tyyppi.
	 * @param _type Haluttu tyyppi.
	 */
	public void setType(int _type) {
		type = _type;
	}
	
	/**
	 * Aksessori, joka palauttaa objektin x-koordinaatin.
	 * @return Objektin x-koordinaatti.
	 */
	public int getX() {
		return (int)x;
	}
	
	/**
	 * Aksessori, joka palauttaa objektin y-koordinaatin.
	 * @return Objektin y-koordinaatti.
	 */
	public int getY() {
		return (int)y;
	}

	/**
	 * Aksessori, joka asettaa objektin tiettyyn paikkaan (x,y)-koordinaatistossa.
	 * @param _x Haluttu x-koordinaatti.
	 * @param _y Haluttu y-koordinaatti.
	 */
	public void setPos(int _x, int _y) {
		x = _x;
		y = _y;
	}

	/**
	 * Aksessori, joka palauttaa objektin x-koordinaatin suuntaisen nopeuden.	
	 * @return Objektin x-koordinaatin suuntainen nopeus.
	 */
	public double getVelX() {
		return vel_x;
	}

	/**
	 * Aksessori, joka palauttaa objektin y-koordinaatin suuntaisen nopeuden.	
	 * @return Objektin y-koordinaatin suuntainen nopeus.
	 */
	public double getVelY() {
		return vel_y;
	}

	/**
	 * Aksessori, jonka avulla on mahdollista säätää objektin animaation tilan, eli käytännössä sen,
	 * mitä animaatiota on tarkoitus käyttää.
	 * @param _state Haluttu tila.
	 */
	public void setState(int _state) {
		
		if (state < 0 || state >= animations.size())
			return;
		if (state != _state) {
			animations.get(state).reset();
			state = _state;
		}
	}

	/**
	 * Aksessori, joka palauttaa objektin tämänhetkisen animaation tilan, eli käytettävän animaation.
	 * @return Tämänhetkinen animaatio.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Aksessori, jonka avulla on mahdollista säätää objektin tämänhetkisen animaation nopeutta.
	 * @param _speed Haluttu nopeus (x framea / ruudunpäivitys [50 krt. / sek.]).
	 */
	public void setAnimationSpeed(double _speed) {
		animations.get(state).setSpeed(_speed);
	}

	/**
	 * Aksessori, jota kutsuttaessa tämänhetkinen animaatio etenee yhdellä framella.
	 */
	public void advanceFrame() {
		animations.get(state).advanceFrame();
	}
	
	/**
	 * Aksessori, joka resetoi tämänhetkisen animaation.
	 */
	public void resetAnimation() {
		animations.get(state).reset();
	}

	/**
	 * Aksessori, jolla asetetaan käytettävä animaation etenemään itsenäisesti.	
	 * @param _auto True, mikäli halutaan animaation etenevän itsenäisesti. Muuten false.
	 */
	public void setAutoAnimation(boolean _auto) {
		for (Animation anim : animations)
			anim.setAutoAnimation(_auto);
	}

	/**
	 * Aksessori, joka palauttaa arvonaan tämänhetkisen animaation. 
	 * @return Tämänhetkinen animaatio.
	 */
	public Animation getAnimation() {
		return animations.get(state);
	}

	/**
	 * Aksessori, joka kertoo onko objekti ruudun ulkopuolella.
	 * @return Palauttaa arvon true, mikäli objekti on ruudun ulkopuolella, muuten palauttaa arvon false.
	 */
	public boolean offScreen() {
		return offmap;
	}
	
	/**
	 * Aksessori, jonka avulla voidaan asettaa objektin aktiivisuuden tila.
	 * @param act True, mikäli aktiivinen. Muuten false.
	 */
	public void setActive(boolean act) {
		active = act;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan objektin aktiivisuuden tilan.
	 * @return Objektin aktiivisuuden tila.
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * Aksessori, jonka avulla voidaan muuttaa objektin sisäistä suuntaa. 
	 * @param dir Sisäinen suunta, joko 1 tai -1.
	 */
	public void setDirection(int dir) {
		if (dir == -1 || dir == 1)
			direction = dir;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan objektin sisäisen suunnan.
	 * @return Objektin sisäinen suunta.
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * Aksessori, joka "tappaa" dynaamisen objektin pakottaen sen tippumaan kaiken läpi.
	 */
	public void kill() {
		dead = true;
		vel_y = -40;
		airborne = true;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan true, mikäli dynaaminen objekti on elossa. Muuten false.
	 * @return True, mikäli objekti on kuollut. Muuten false.
	 */
	public boolean isDead() {
		return dead;
	}
}
