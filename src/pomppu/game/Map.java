/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import java.util.ArrayList;

import pomppu.mechanics.*;

/**
 * Map-luokka, joka sisältää pelissä käytetyt sekä staattiset että dynaamiset objektit pelaajaa lukuunottamatta. Sisältää myös
 * pelaajan aloituskoordinaatit, sekä törmäyksentarkistusrutiinit dynaamisen ja staattisen objektin välillä. 
 * @author arkivika
 */
public class Map {
	
	private int p_x, p_y;

	public static final int TOP_COLLISION = 0;
	public static final int BOTTOM_COLLISION = 1;
	public static final int LEFT_COLLISION = 2;
	public static final int RIGHT_COLLISION = 3;
	
	ArrayList<ArrayList<StaticObject>> staticObjects;
	ArrayList<DynamicObject> dynamicObjects; 
			
	/**
	 * Konstruktori, joka ottaa parametreikseen listan staattisista objekteista (lista listoista, koska pelikartta on 2-ulotteinen) sekä dynaamisista objekteista.
	 * Tarvitsee myös pelaajahahmon aloituskoordinaatit.
	 * @param _staticObjects Lista staattisista objekteista.
	 * @param _dynamicObjects List dynaamisista objekteista.
	 * @param _p_x Pelaajan x-aloituskoordinaatti.
	 * @param _p_y Pelaajan y-aloituskoordinaatti.
	 */
	public Map(ArrayList<ArrayList<StaticObject>> _staticObjects, ArrayList<DynamicObject> _dynamicObjects, int _p_x, int _p_y) {
		staticObjects = _staticObjects;	
		dynamicObjects = _dynamicObjects;
		p_x = _p_x;
		p_y = _p_y;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan pelaajan x-aloituskoordinaatin.
	 * @return Pelaajan x-aloituskoordinaatti.
	 */
	public int getPlayerStartX() {
		return (p_x*32)+1;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan pelaajan y-aloituskoordinaatin.
	 * @return Pelaajan y-aloituskoordinaatti.
	 */
	public int getPlayerStartY() {
		return (p_y*32)+1;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan staattisen objektin tyypin halutusta (i,j)-indeksistä.
	 * @param i Haluttu vaakarivin indeksi.
	 * @param j Haluttu pystyrivin indeksi.
	 * @return Staattisen objektin tyyppi.
	 */
	public int getTileType(int i, int j) {
		if (staticObjects.get(j) != null)
			if (staticObjects.get(j).get(i) != null)
				return staticObjects.get(j).get(i).get_type();
		return -1;
	}

	/**
	 * Aksessori, joka palauttaa arvonaan Map-olion sisältämän staattiset objektit.
	 * @return Map-olion sisältämät staattiset objektit.
	 */
	public ArrayList<ArrayList<StaticObject>> getStaticObjects() {
		return staticObjects;
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan Map-olion sisältämän dynaamiset objektit.
	 * @return Map-olion sisältämät dynaamiset objektit.
	 */
	public ArrayList<DynamicObject> getDynamicObjects() {
		return dynamicObjects;
	}

	/**
	 * Aksessori, joka tarkistaa törmayksen dynaamisen objektin sekä staattisen objektin välillä.
	 * @param obj Dynaaminen objekti.
	 * @param part Dynaamisen objektin osa, jonka suhteen törmäys tarkistetaan: TOP_COLLISION(0), BOTTOM_COLLISION(1), LEFT_COLLISION(2), RIGHT_COLLISION(3).
	 * @return Kolmialkioinen taulukko (1 alkio / staattinen objekti, eli "tile"), joka sisältää staattisen objektin tyypin,
	 * johon törmäys on tapahtunut. Mikäli törmäystä ei ole tapahtunut, palautetaan -1. 
	 */
	public int[] collide(DynamicObject obj, int part) {
		
		int retValue[] = {-1,-1,-1};
		int index_x, index_y = 0; 
		
		index_x = (int)((obj.getX()+obj.getAnimation().getWidth()/2)/32);
		index_y = (int)((obj.getY()+(obj.getAnimation().getHeight()/2))/32);					

		switch(part) {
		
			case TOP_COLLISION: 
				index_y = (int)((obj.getY())/32);
				retValue = verticalCollide(obj, index_x, index_y, TOP_COLLISION);
				break;
	
			case BOTTOM_COLLISION:
				index_y = (int)((obj.getY()+(obj.getAnimation().getHeight()))/32);
				retValue = verticalCollide(obj, index_x, index_y, BOTTOM_COLLISION);
				break;
	
			case LEFT_COLLISION:
				index_x = (int)((obj.getX()-1)/32);
				retValue = horizontalCollide(obj, index_x, index_y, LEFT_COLLISION);
				break;
	
			case RIGHT_COLLISION:
				index_x = (int)((obj.getX()+obj.getAnimation().getWidth())/32);
				retValue = horizontalCollide(obj, index_x, index_y, RIGHT_COLLISION); 
		}
		
		return retValue;
	}

	/**
	 * Apumetodi, joka tarkistaa pystysuuntaiset törmäykset. Käytetään apuna collide-metodissa.
	 * @param obj Dynaaminen objekti.
	 * @param index_x Staattisen objektin x-suuntainen indeksi.
	 * @param index_y Staattisen objektin y-suuntainen indeksi.
	 * @param part Dynaamisen objektin osa, jonka suhteen törmäys tarkistetaan: TOP_COLLISION(0), BOTTOM_COLLISION(1), LEFT_COLLISION(2), RIGHT_COLLISION(3).
	 * @return Kolmialkioinen taulukko (1 alkio / staattinen objekti, eli "tile"), joka sisältää staattisen objektin tyypin,
	 * johon törmäys on tapahtunut. Mikäli törmäystä ei ole tapahtunut, palautetaan -1.
	 */
	private int[] verticalCollide(DynamicObject obj, int index_x, int index_y, int part) {
		
		int[] retValue = {-1,-1,-1};

		if ( index_y < staticObjects.size() )
			if ( index_x < staticObjects.get(index_y).size()-1 && index_x > 0 && index_y > -1 ) 		
				for (int i=-1; i<2; i++)
					if (staticObjects.get(index_y).get(index_x+i) != null)
						if (staticObjects.get(index_y).get(index_x+i).collide(obj, part) && staticObjects.get(index_y).get(index_x+i).getActive() ) {
							
							retValue[i+1] = staticObjects.get(index_y).get(index_x+i).get_type();
							if (retValue[i+1] == 56 && obj.getType() == 1)
								staticObjects.get(index_y).get(index_x+i).setActive(false);
						}

		if (index_y > staticObjects.size() + 5)				// Annetaan pudota vähän :)
			retValue[0] = retValue[1] = retValue[2] = -2;
		
		return retValue;
	}
	
	/**
	 * Apumetodi, joka tarkistaa vaakatasossa tapahtuvat törmäykset. Käytetään apuna collide-metodissa.
	 * @param obj Dynaaminen objekti.
	 * @param index_x Staattisen objektin x-suuntainen indeksi.
	 * @param index_y Staattisen objektin y-suuntainen indeksi.
	 * @param part Dynaamisen objektin osa, jonka suhteen törmäys tarkistetaan: TOP_COLLISION(0), BOTTOM_COLLISION(1), LEFT_COLLISION(2), RIGHT_COLLISION(3).
	 * @return Kolmialkioinen taulukko (1 alkio / staattinen objekti, eli "tile"), joka sisältää staattisen objektin tyypin,
	 * johon törmäys on tapahtunut. Mikäli törmäystä ei ole tapahtunut, palautetaan -1.
	 */
	private int[] horizontalCollide(DynamicObject obj, int index_x, int index_y, int part) {
		
		int[] retValue = {-1,-1,-1};
		
		if ( index_y < staticObjects.size()-1 )
			if ( index_x < staticObjects.get(index_y).size() && index_x > -1 && index_y > 0 ) 		
				for (int i=-1; i<2; i++)
					if (staticObjects.get(index_y+i).get(index_x) != null)
						if (staticObjects.get(index_y+i).get(index_x).collide(obj, part) && staticObjects.get(index_y+i).get(index_x).getActive() ) {
							
							retValue[i+1] = staticObjects.get(index_y+i).get(index_x).get_type();
							if (retValue[i+1] == 56 && obj.getType() == 1)
								staticObjects.get(index_y+i).get(index_x).setActive(false);
						}

		if (index_x > staticObjects.get(0).size()-2)
			retValue[0] = retValue[1] = retValue[2] = -3;
		
		return retValue;
	}
}
