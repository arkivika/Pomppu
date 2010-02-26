/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import java.io.IOException;

import pomppu.graphics.Screen;
import pomppu.mechanics.*;
import pomppu.io.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Ohjelman pääluokka, joka sisältää eri pelitilat.
 * @author arkivika
 */
public class Pomppu {
	
	public static final int SENTINEL = -1, MAIN_MENU = 0, NEW_GAME = 1, HIGHSCORES = 2, SETTINGS = 3, RESUME_GAME = 4, GAME_OVER = 5;
	public static final int FRAME_DELAY = 1000/50; 
	public static final long MOUSE_SLEEP_DELAY = 100;
	
	/**
	 * Main-metodi, joka toteuttaa eri pelitilat yksi kerrallaan.
	 * @param args Komentoriviparametrit. Ei käytössä.
	 */
	public static void main(String[] args) {

		JFrame frame = new JFrame();
		
		// Pelin eri tilat		
		GameState[] state = new GameState[4];

		Screen screen = new Screen(640, 480, false, "Pomppu 1.0", frame);
		GUI gui = new GUI(screen);
		
		// Asetetaan paddin-arvot GUI:lle
		gui.setPadding(25);
		
		// Luodaan uusi kamera (Camera-olio)
		Camera camera = new Camera(screen, gui);

		// Luodaan näppäimistö (Keyboard-olio)
		Keyboard keyboard = new Keyboard();
		frame.addKeyListener(keyboard);
		
		// Luodaan hiiri (Mouse-olio)
		Mouse mouse = new Mouse(screen.getInsets());
		frame.addMouseListener(mouse.getMouseKeys());
		frame.addMouseMotionListener(mouse.getMouseMotion());

		// Alustetaan pelitilat
		Main main = new Main(camera, gui, keyboard, mouse);
		Game game = null;
		Highscore highscore = new Highscore(camera, gui, keyboard, mouse, "highscores.scr");
		Settings settings = new Settings(camera, gui, keyboard, mouse, screen);

		// Asetetaan pelitilat taulukkoon
		state[MAIN_MENU] = main; 
		state[NEW_GAME] = game;
		state[HIGHSCORES] = highscore;
		state[SETTINGS] = settings;
	
		// Aloitustila
		int curState = MAIN_MENU; 
		int returnState = MAIN_MENU;
		
		// Suoritetaan haluttuja tiloja, kunnes käyttäjä haluaa lopettaa ohjelman suorituksen
		while (curState != SENTINEL) {

			returnState = state[curState].doState();

			mouse.clear();
			keyboard.clear();
	
			switch(returnState) {
	
				case SENTINEL:
					curState = SENTINEL;
					break;
			
				case MAIN_MENU:
					curState = MAIN_MENU;
					break;
					
				case NEW_GAME: 
					try {					
						state[NEW_GAME] = game = new Game(camera, gui, keyboard, mouse, "level_1.map", 500);
						curState = NEW_GAME;
					}
					catch (IOException e) {
						System.out.println("Error! Couldn't create game: " + e);
						curState = MAIN_MENU;
					}
					break;
					
				case HIGHSCORES:
					curState = HIGHSCORES;
					break;
					
				case SETTINGS: 
					curState = SETTINGS;
					break;

				case RESUME_GAME: 
					if (state[NEW_GAME] == null)
						try {					
							state[NEW_GAME] = game = new Game(camera, gui, keyboard, mouse, "level_1.map", 500);
							curState = NEW_GAME;
						}
						catch (IOException e) {
							System.out.println("Error! Couldn't create game: " + e);
							curState = MAIN_MENU;
						}
						else
							curState = NEW_GAME;
					break;
					
				case GAME_OVER:
					main.setValidResume(false);
					String name = JOptionPane.showInputDialog(null, "Game Over!", "Enter your name:", JOptionPane.QUESTION_MESSAGE);
					int score = game.calculateScores();
					highscore.addScore(name, score);
					curState = HIGHSCORES;
			}
		}

		System.exit(0);		
	}
}
