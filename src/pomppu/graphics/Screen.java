/**
 * Sisältää luokat, jotka ovat vastuussa ikkunan luomisesta sekä kuvien
 * lataamisesta ja niiden käsittelemisestä. Tämä paketti on käytännössä 
 * wrapperi ohjelmassa käytettävälle grafiikkakirjastolle (Swing).
 */
package pomppu.graphics;

import javax.swing.*;

import java.awt.*;
import java.util.*;

/**
 * Toteuttaa Canvas-rajapinnan Java2D kirjastolla.
 * Ottaa tietyn määrän VisibleElement-olioita ja piirtää ne ruudulle Java2D API:n avulla.
 * Myös vastuussa ikkunan luomisesta ja piirtämisestä.
 * @see pomppu.graphics.Canvas
 * @see pomppu.graphics.VisibleElement
 * @see javax.swing.JFrame
 * @author arkivika
 */
public class Screen implements Canvas {

	/**
	 * Sisäinen apuluokka, canvas, jolle peligrafiikka piirretään.
	 * JPanel:in laajentaminen oli lopulta tehokkaampaa kuin BufferStrategy:n käyttäminen.
	 * @author arkivika
	 * @see javax.swing.JPanel
	 */
	private class InternalCanvas extends JPanel {

		/**
		 * Vakio serialVersionUID.
		 * Tukahduttaa varoituksen.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Piirtää taustan sekä jonossa olevat piirrettävät elementit. 
		 */
		public void paintComponent(Graphics graphics) {

			super.paintComponents(graphics);
			Graphics2D g = (Graphics2D)graphics;
			
			g.setColor(clrColor);
			g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

			synchronized(canvas) {
 
				for (VisibleElement elem : drawList) {
				
					Drawable drawable = elem.getDrawable();
					drawable.draw(g, elem.getX(), elem.getY());
				}
			}
			
			g.dispose();
		}
	}

	private JFrame frame;
	
	private Color clrColor; 	
	private LinkedList<VisibleElement> drawList;
	private InternalCanvas canvas;

	/**
	 * Konstruktori, joka luo ikkunan ja piirtää sen ruudulle.
	 * @param width Ikkunan leveys.
	 * @param height Ikkunan korkeus.
	 * @param fullscreen True, mikäli piirretään kokoruututilassa. Muuten false.
	 * @param title Ikkunan otsikko.
	 * @param _frame Käytettävä JFrame-olio.
	 */
	public Screen(int width, int height, boolean fullscreen, String title, JFrame _frame) {
	
		drawList = new LinkedList<VisibleElement>();

		frame = _frame;

		canvas = new InternalCanvas();
		canvas.setIgnoreRepaint(true);
		canvas.setDoubleBuffered(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setTitle(title);

		frame.add(canvas);
		
		frame.setResizable(false);
		frame.setVisible(true);
					
		clrColor = new Color(0, 0, 0);
	}
	
	/**
	 * Aksessori, joka tyhjentää piirrettävien elementtien jonon.
	 */
	public void clear() {

		synchronized(canvas) {
			drawList.clear();
		}
	}
	
	/**
	 * Aksessori, joka joka palauttaa arvonaan frame:n leveyden.	
	 */
	public int getWidth() {
		return frame.getWidth();
	}

	/**
	 * Aksessori, joka joka palauttaa arvonaan frame:n korkeuden.	
	 */
	public int getHeight() {
		return frame.getHeight();
	}
	
	/**
	 * Aksessori, joka piirtää taustan sekä jonossa olevat piirrettävät elementit. 
	 */
	public void draw() {
		canvas.repaint();
	}

	/**
	 * Aksessori, joka lisää elementin piirrettävien elementtien jonoon.
	 * @param elem Piirrettävä elementti.
	 */
	public void addElement(VisibleElement elem) {
		synchronized(canvas) {
			drawList.addLast(elem);
		}
	}
	
	/**
	 * Aksessori, joka poistaa num-määrän elementtejä piirrettävän jonon alusta.
	 * @param num Poistettavien elementtien määrä.
	 */
	public void clearTop(int num) {
		synchronized(canvas) {
			for (int i=0; i<num; i++)
				drawList.removeLast();
		}
	}
	
	/**
	 * Aksessori, joka asettaa ikkunan koon, ts. resoluution.
	 * @param _d Dimension-olio, joka sisältää uuden koon.
	 */
	public void setSize(Dimension _d) {
		frame.setSize(_d);
	}
	
	/**
	 * Aksessori, joka palauttaa arvonaan ikkunan reunojen mitat.
	 * @return Ikkunan reunojen mitat.
	 */
	public Dimension getInsets() {
		
		Dimension insets = new Dimension();
		
		insets.width = (frame.getWidth() - canvas.getWidth()) / 2;
		insets.height = (frame.getHeight() - canvas.getHeight()) - insets.width;
		
		return insets;
	}
}
