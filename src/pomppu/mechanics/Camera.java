/**
 * Pelimoottorin pakkaus, sisältää 2d-pelille ominaiset geneeriset toiminnallisuudet.
 */
package pomppu.mechanics;

import java.util.ArrayList;
import pomppu.graphics.*;

/**
 * Camera-luokka. Vastaa pelilogiikan tulkitsemisesta ja sen välittämisestä Canvas-rajapinnan toteuttavalle 
 * oliolle (tässä tapauksessa Screen) renderoimista varten.
 * @author arkivika
 */
public class Camera {
	
	static final double HORIZONTAL_BORDER_OFFSET = 1.35;
	static final double FOLLOW_BORDER_SIZE = 0.4;
	static final double FOLLOW_SPEED = 6.0;

	private Canvas screen;
	private GUI gui;
	private ArrayList<DynamicObject> dynamicObjects;
	private ArrayList<ArrayList<StaticObject>> staticObjects;
	private int x, y;
	private boolean show_background;
	private static Drawable background;
	
	/**
	 * Konstruktori, joka ottaa parametrikseen käytettävän Canvas-rajapinnan toteuttavan olion (Screen), sekä GUI-olion.
	 * Alustaa Camera-olion listat (ArrayList) sekä muuttujat.
	 * @param _screen
	 * @param _gui
	 */
	public Camera(Canvas _screen, GUI _gui) {
		
		dynamicObjects = new ArrayList<DynamicObject>();
		staticObjects = new ArrayList<ArrayList<StaticObject>>(); 
		screen = _screen;
		gui = _gui;
		x = y = 0;
		show_background = false;
	}
	
	/**
	 * Aksessori, jonka avulla saadan määriteltyä Camera-olion käyttämä taustakuva.
	 * @param path Tiedostopolku, jossa haluttu kuva sijaitsee.
	 */
	public void setBackground(String path) {
		
		if (path != null) {
			show_background = true;
			background = ImageFactory.getImage(path); 
		}
		show_background = true;
	}
	
	/**
	 * Aksessori, joka lisää dynaamisen objektin piirrettävien objektien listaan.
	 * @param obj Haluttu dynaaminen objekti.
	 */
	public void addDynamicObject(DynamicObject obj) {
		dynamicObjects.add(obj);
	}

	/**
	 * Aksessori, jolla lisätään staattiset objektit piirrettävien objektien joukkoon.
	 * @param obj Haluttu lista, joka sisältää listat piirrettävistä staattisista objekteista. Sisäkkäiset listat, 
	 * koska staattiset objektit muodostavat keskenään 2-ulotteisen taulun (pelikenttä).
	 */
	public void addStaticObjects(ArrayList<ArrayList<StaticObject>> obj) {
		staticObjects = obj;
	}

	/**
	 * Aksessori, joka tyhjentää dynaamisten objektien listan.
	 */
	public void clearObjects() {
		dynamicObjects.clear();
	}
	
	/**
	 * Aksessori, joka lisää GUI:n elementit piirrettävien elementtien listaan.
	 */
	public void renderGUI() {
		
		ArrayList<VisibleElement> guiElements = gui.render();

		for (VisibleElement elem : guiElements)
			screen.addElement(elem);
		
		screen.draw();
	}
	
	/**
	 * Aksessori, joka tyhjentää GUI-olion, jotta se voidaan piirtää uudelleen.
	 */
	public void clearGUI() {
		screen.clearTop(gui.size());
	}
	
	/**
	 * Aksessori, joka tyhjentää Screen-olion, lisää taustakuvan piirrettävien elementtien joukkoon (mikäli
	 * halutaan), piirtää taustalla olevat objektit (staattiset), dynaamiset objektit, sekä edustalla olevat 
	 * objektit (staattiset).
	 */
	public void render() {
		
		screen.clear();
		
		if (show_background)
			screen.addElement(new VisibleElement( background, 0, 0 ));
		
		renderBehind();
		
		renderDynamics();
		
		renderFront();			
	}
	
	/**
	 * Aksessori, jonka avulla määritetään, piirretäänkö taustakuvaa.
	 * @param show True, mikäli taustakuva halutaan piirtää, muuten false.
	 */
	public void showBackground(boolean show) {
		show_background = show;
	}
	
	/**
	 * Aksessori, jonka avulla kamera voidaan sijoittaa tiettyyn x,y-koordinaattiin.
	 * @param _x Haluttu x-koordinaatti.
	 * @param _y Haluttu y-koordinaatti.
	 */
	public void setPos(int _x, int _y) {
		x = _x-(screen.getWidth()/2);
		y = _y-(screen.getHeight()/2);
	}

	/**
	 * Aksessori, jonka avulla kamera saadaan dynaamisesti seuraamaan dynaamista objektia.
	 * @param obj Haluttu dynaaminen objekti.
	 */
	public void follow(DynamicObject obj) {
		double d_x = obj.getX()+(obj.getAnimation().getWidth()/2) - x;
		double d_y = obj.getY()+(obj.getAnimation().getHeight()/2) - y;
		
		if(d_x < screen.getWidth() * FOLLOW_BORDER_SIZE * HORIZONTAL_BORDER_OFFSET)
			x -= (screen.getWidth() * FOLLOW_BORDER_SIZE - d_x)/FOLLOW_SPEED;
		 
		else if(d_x > screen.getWidth() - screen.getWidth() * FOLLOW_BORDER_SIZE / HORIZONTAL_BORDER_OFFSET)
			x += (d_x - (screen.getWidth() - screen.getWidth() * FOLLOW_BORDER_SIZE))/FOLLOW_SPEED;
			  
		if(d_y < screen.getHeight() * FOLLOW_BORDER_SIZE)
			y -= (screen.getHeight() * FOLLOW_BORDER_SIZE - d_y)/FOLLOW_SPEED;
		
		else if(d_y > screen.getHeight() - screen.getHeight() * FOLLOW_BORDER_SIZE)
			y += (d_y - (screen.getHeight() - screen.getHeight() * FOLLOW_BORDER_SIZE))/FOLLOW_SPEED;
		
		validatePosition();
	}

	/**
	 * Apumetodi, joka piirtää dynaamiset objektit, mikäli ne ovat peliruudun alueella.
	 */
	private void renderDynamics() {
		
		for (DynamicObject obj : dynamicObjects) {
			
			if (obj.getX() > x-obj.getAnimation().getWidth() && obj.getX() < x+screen.getWidth() &&
				obj.getY() > y-obj.getAnimation().getHeight() && obj.getY() < y+screen.getHeight() ) {
				obj.setActive(true);
				screen.addElement(new VisibleElement(obj.getAnimation(), obj.getX()-x, obj.getY()-y));
			}
		}
	}
	
	/**
	 * Apumetodi, joka piirtää taustalla olevat staattiset objektit, mikäli ne ovat peliruudun alueella.
	 */
	private void renderBehind() {
		
		for(int list_y = y/32; list_y <= (y + screen.getHeight())/32; list_y++) {
			
			ArrayList<StaticObject> list = null;	
			if(list_y >= 0 && list_y < staticObjects.size())
				list = staticObjects.get(list_y);
			else
				continue;
			
			for(int list_x = x/32; list_x <= (x + screen.getWidth())/32; list_x++) {
				if(list_x >= 0 && list_x < list.size()) {
					StaticObject obj = list.get(list_x);
					if (obj != null && obj.active && obj.get_type() <= 99)
						screen.addElement(new VisibleElement(obj.getDrawable(), obj.get_x()-x, obj.get_y()-y));
				}
			}
		}
	}
	
	/**
	 * Apumetodi, joka piirtää edustalla olevat staattiset objektit, mikäli ne ovat peliruudun alueella.
	 */
	private void renderFront() {
		
		for(int list_y = y/32; list_y <= (y + screen.getHeight())/32; list_y++) {
			
			ArrayList<StaticObject> list = null;	
			if(list_y >= 0 && list_y < staticObjects.size())
				list = staticObjects.get(list_y);
			else
				continue;
			
			for(int list_x = x/32; list_x <= (x + screen.getWidth())/32; list_x++) {
				if(list_x >= 0 && list_x < list.size()) {
					StaticObject obj = list.get(list_x);
					if (obj != null && obj.active && obj.get_type() > 99)
						screen.addElement(new VisibleElement(obj.getDrawable(), obj.get_x()-x, obj.get_y()-y));
				}
			}
		}
	}

	/**
	 * Apumetodi, jonka avulla varmistetaan, että kamera pysyy pelin rajojen sisäpuolella.
	 */
	private void validatePosition() {
		int max_x = (staticObjects.get(0).size()*32) - screen.getWidth();
		int max_y = (staticObjects.size()*32) - screen.getHeight();
		
		if (x > max_x)
			x = max_x;
		if (y > max_y)
			y = max_y;
				
		if (x < 0) 
			x = 0;
		if (y < 0)
			y = 0;
	}
}
