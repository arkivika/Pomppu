/**
 * Pelimoottorin pakkaus, sisältää 2d-pelille ominaiset geneeriset toiminnallisuudet.
 */
package pomppu.mechanics;

import pomppu.graphics.*;

/**
 * Staattinen objekti.
 * Sisältää pelissä käytetyn staattisen objektin funktionalisuudet, kuten törmäyksentarkistuksen. 
 * Myös staattisen objektin kuvadata sisältyy tähän luokkaan.
 * @author arkivika
 */
public class StaticObject {
	
	public static final int TOP_COLLIDE = 0;
	public static final int BOTTOM_COLLIDE = 1;
	public static final int LEFT_COLLIDE = 2;
	public static final int RIGHT_COLLIDE = 3;
		
	protected Drawable drawable;
	protected int x, y, type;
	protected boolean active;

	/**
	 * Konstruktori, joka tarvitsee parametreikseen Drawable-rajapinnan toteuttavan olion (kuva, teksti, animaatio..), objektin x- ja
	 * y-koordinaatit, tyypin sekä tiedon siitä, onko objekti aktiivinen.
	 * @param _drawable Drawable-rajapinnan toteuttava olio.
	 * @param _x Objektin x-koordinaatti.
	 * @param _y Objektin y-koordinaatti.
	 * @param _type Objektin tyyppi.
	 * @param _active Objektin aktiivisuus.
	 */
	public StaticObject(Drawable _drawable, int _x, int _y, int _type, boolean _active) {
		
		drawable = _drawable;
		x = _x*32; 
		y = _y*32;
		active = _active;
		type = _type;
	}
	
	/**
	 * Aksessori, joka mahdollistaa törmäyksentarkistuken dynaamisen objektin suhteen. Ottaa parametrikseen myös dynaamisen
	 * objektin osan, jonka suhteen törmäys tarkistetaan.<br>TOP_COLLIDE (0), BOTTOM_COLLIDE (1), LEFT_COLLIDE (2), RIGHT_COLLIDE (3) 
	 * @param obj Dynaaminen objekti.
	 * @param part Dynaamisen objektin osa (0,1,2,3).
	 * @return True, mikäli törmäys on tapahtunut, muuten false.
	 */
	public boolean collide(DynamicObject obj, int part) {
		
		double end_x	= obj.getAnimation().getWidth();
		double real_y	= obj.getAnimation().getHeight();
		double start_y	= obj.getAnimation().getHeight()*0.3;
		double end_y	= obj.getAnimation().getHeight()*0.8;
		double an_width = obj.getAnimation().getWidth();
		
		/* Tarkistaa kahden törmäyslaatikon väliset etäisyydet ja kertoo mikäli törmäys on tapahtunut. Perustuu objektin kuvakokoon, eli
		 * törmäyksentarkistus on dynaaminen operaatio suhteessa animaatioon.
		 */
		switch(part) {
		
			case TOP_COLLIDE:
				if ( ( ( obj.getX()			>= x && obj.getX()			<= x+drawable.getWidth() ) || 
					   ( obj.getX()+end_x	>= x && obj.getX()+end_x	<= x+drawable.getWidth() ) ||
					   ( obj.getX()			<= x && obj.getX()+end_x	>= x+drawable.getWidth() ) ) &&  
					   ( obj.getY()			>= y && obj.getY()			<= y+drawable.getHeight() ) )
					return true;
		
			case BOTTOM_COLLIDE:
				if ( ( ( obj.getX()			>= x && obj.getX()			<= x+drawable.getWidth() ) || 
					   ( obj.getX()+end_x	>= x && obj.getX()+end_x	<= x+drawable.getWidth() ) ||
					   ( obj.getX()			<= x && obj.getX()+end_x	>= x+drawable.getWidth() ) ) &&  
					   ( obj.getY()+real_y	>= y && obj.getY()+real_y	<= y+drawable.getHeight() ) ) 
					return true;
				
			case LEFT_COLLIDE:
				if ( ( obj.getX() 			>= x && obj.getX() 			<= x+drawable.getWidth()  ) &&  
				   ( ( obj.getY()+start_y 	>= y && obj.getY()+start_y	<= y+drawable.getHeight() ) || 
					 ( obj.getY()+end_y 	>= y && obj.getY()+end_y 	<= y+drawable.getHeight() ) || 
					 ( obj.getY()+start_y 	<= y && obj.getY()+end_y 	>= y+drawable.getHeight() ) ) )
					return true;
			
			case RIGHT_COLLIDE:
				if ( ( obj.getX()+an_width	>= x && obj.getX()+an_width <= x+drawable.getWidth()  ) &&  
				   ( ( obj.getY()+start_y	>= y && obj.getY()+start_y 	<= y+drawable.getHeight() ) || 
					 ( obj.getY()+end_y		>= y && obj.getY()+end_y 	<= y+drawable.getHeight() ) || 
					 ( obj.getY()+start_y	<= y && obj.getY()+end_y 	>= y+drawable.getHeight() ) ) )
					return true;
		}

		return false;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan staattisen objektin x-koordinaatin.
	 * @return Staattisen objektin x-koordinaatti.
	 */
	public int get_x() {
		return x;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan staattisen objektin y-koordinaatin.
	 * @return Staattisen objektin y-koordinaatti.
	 */
	public int get_y() {
		return y;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan staattisen objektin tyypin.
	 * @return Staattisen objektin tyyppi.
	 */
	public int get_type() {
		return type;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan staattisen objektin Drawable-rajapinnan toteuttavan olion.
	 * @return Staattisen objektin Drawable-rajapinnan toteuttava olio.
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan staattisen objektin aktiivisuuden.
	 * @return True, mikäli objekti on aktiivinen, muuten false.
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * Aksessori, jonka avulla voidaan säätää staattisen objektin aktiivisuuden tilaa.
	 * @param _active True, mikäli objektin halutaan olevan aktiivinen, muuten false.
	 */
	public void setActive(boolean _active) {
		active = _active;
	}
}