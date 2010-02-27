/**
 * Sisältää ohjelman pääluokan, pelilogiikan sekä pelivalikot.
 */
package pomppu.game;

import pomppu.mechanics.*;
import pomppu.graphics.*;
import pomppu.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.awt.*;
import java.util.Scanner;

/**
 * Parhaat tulokset. Tässä tilassa näkee parhaat tulokset.
 * 
 * @see pomppu.game.GameState
 * @author arkivika
 */
public class Highscore extends GameState {

	// Näytettävien parhaiden tulosten maksimimäärä
	private static final int MAX_SCORES = 6;

	/**
	 * Sisäinen apuluokka, jota käytetään parhaan tuloksen säilyttämiseen.
	 * Toteuttaa Comparable-luokan. Sisältää compareTo-metodin, jolla voidaan
	 * vertailla parhaita tuloksia keskenään.
	 * @author arkivika
	 */
	private class Score implements Comparable<Score> {

		String name;
		int score;

		public int compareTo(Score other) {

			if (score > other.score)
				return -1;
			else if (score < other.score)
				return 1;
			else
				return 0;
		}

		public String toString() {
			return name + " " + score;
		}
	}

	private ArrayList<Score> scores;
	private Menu menu;
	private String path;

	/**
	 * Konstruktori Highscore-pelitilalle, joka tarvitsee pelitiloille ominaiset
	 * parametrit (Camera-olio, GUI-olio, Keyboard-olio sekä Mouse-olio).
	 * Tarvitsee lisäksi highscore-tiedoston sijainnin. Kutsuu yliluokkansa
	 * (GameState) konstruktoria ja alustaa valikoissa käytettävän menu-olion.
	 * Lukee parhaat tulokset annetusta tiedostosta readScores()-apumetodin
	 * avulla. Lisää lopuksi tilan ominaiset osat GUI:hin (otsikot).
	 * @param _cam Käytettävä Camera-olio.
	 * @param _gui Käytettävä GUI-olio.
	 * @param _keyboard Käytettävä Keyboard-olio.
	 * @param _mouse Käytettävä Mouse-olio.
	 * @param filepath Käytettävän tiedoston sijainti.
	 */
	public Highscore(Camera _cam, GUI _gui, Keyboard _keyboard, Mouse _mouse, String filepath) {

		super(_cam, _gui, _keyboard, _mouse);
		menu = new Menu(_gui);

		scores = new ArrayList<Score>();
		path = filepath;

		readScores();

		menu.addTitle(new Text("Pomppu 1.0", "Tahoma", Font.BOLD, 32,
				Color.GREEN));
		menu.addTitle(new Text("Highscores", "Tahoma", Font.BOLD, 28,
				Color.YELLOW));
	}

	/**
	 * Apumetodi, jonka avulla saadaan parhaat tulokset luettua tiedostosta
	 * scores-listaan.
	 */
	private void readScores() {

		scores.clear();
		Scanner input;

		try {
			input = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			System.out.println("Highscores not found in " + path
					+ "\nIgnoring...");
			return;
		}

		while (input.hasNext()) {

			String line = input.nextLine();
			Scanner lineScanner = new Scanner(line);

			Score lineScore = new Score();
			lineScore.score = 0;
			lineScore.name = "";

			while (lineScanner.hasNext()) {
				if (lineScanner.hasNextInt()) {
					lineScore.score = lineScanner.nextInt();
				} else
					lineScore.name += lineScanner.next() + " ";
			}
			scores.add(lineScore);
		}
	}

	/**
	 * Aksessori, jonka avulla saadaan lisättyä uusi tulos parhaisiin tuloksiin.
	 * Järjestää tulokset sekä käyttää writeScores-apumetodia niiden
	 * kirjoittamiseksi tiedostoon.
	 * @param name Pelaajan nimi (String).
	 * @param score Pelaajan tulos (int).
	 */
	public void addScore(String name, int score) {

		Score newScore = new Score();

		if (name != null && name.length() > 0) {
			newScore.name = name;
			newScore.name.trim();
		} else
			newScore.name = "John Doe";

		newScore.score = score;
		scores.add(newScore);

		Collections.sort(scores);
		writeScores();
	}

	/**
	 * Apumetodi, joka kirjoittaa tulokset tiedostoon.
	 */
	private void writeScores() {

		PrintWriter scoreFile;

		try {
			scoreFile = new PrintWriter(new File(path));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot write file " + path);
			return;
		}
		for (Score s : scores) {
			scoreFile.println(s);
		}

		scoreFile.close();
	}

	/**
	 * Aksessori, joka aloittaa parhaat tulokset -tilan. Sisältää tilaluupin
	 * sekä alustaa siinä käytettävät arvot.
	 * @return Palauttaa arvonaan muuttujan, joka kertoo Pomppu-luokalle, mihin tilaan siirrytään seuraavaksi. Vakioarvo on 0.
	 */
	public int doState() {

		int m_x = 0;
		int m_y = 0;

		for (int i = 0; i < MAX_SCORES; i++) {

			if (i >= scores.size())
				break;

			Score s = scores.get(i);
			Text scoreEntry = new Text(s.toString(), "Tahoma", Font.BOLD, 32,
					Color.WHITE);
			menu.addEntry(scoreEntry, scoreEntry);
		}

		menu.addEntry(new Text("Back", "Tahoma", Font.BOLD, 32, Color.RED),
				new Text("Back", "Tahoma", Font.BOLD, 32, Color.WHITE));

		while (true) {

			render();

			try {
				Thread.sleep(Pomppu.FRAME_DELAY);
			} 
			catch (Exception e) { 
				System.out.println("Thread.sleep failed!"); 
			}

			if (mouse.moved(m_x, m_y)) {

				m_x = mouse.get_x();
				m_y = mouse.get_y();
				menu.setPosition(m_x, m_y);
			}

			if (mouse.isPressed(0)) {

				try {
					Thread.sleep(Pomppu.MOUSE_SLEEP_DELAY);
				} 
				catch (Exception e) { 
					System.out.println("Thread.sleep failed!"); 
				}

				if (menu.select() == ((scores.size() < MAX_SCORES) ? scores
						.size() : MAX_SCORES))
					break;
			}
		}

		camera.clearGUI();
		menu.clear();
		menu.clearEntries();

		return Pomppu.MAIN_MENU;
	}

	/**
	 * Apumetodi, joka päivittää GUI:n sekä renderöi menu:n.
	 */
	private void render() {

		camera.clearGUI();
		menu.render();
		camera.renderGUI();
	}

	/**
	 * Testipäämetodi, jonka avulla varmistutaan siitä, että luokka toimii kuten sen pitäisi. Tulostaa jokaisen testin sekä sen tuloksen.
	 * @param args Ei huomioida tässä.
	 */
	public static void main(String[] args) {

		try {

			// Alustusvaiheen testaus
			
			System.out.println("Testing construction phase..");

			Highscore failScore = new Highscore(null, null, null, null, "invalidtestfile");
			if (failScore == null)
				failedTest("Couldn't create invalid Highscore object.");
			System.out.print("..");

			if (failScore.scores == null)
				failedTest("Scores-ArrayList null in valid Highsore object");
			System.out.print("..");

			if (failScore.scores.size() != 0)
				failedTest("Score count not 0 in invalid Highscore object (not possible)");
			System.out.print("..");

			// Virheellisen Highscore-olion lataaminen
			
			failScore = new Highscore(null, null, null, null, "/&&@!MEJAHÖIDJIÖK/\\~åäö><|^ÖGFFFÖÖ");
			if (failScore == null)
				failedTest("Couldn't create invalid Highscore object.");
			System.out.print("..");
			
			// Virheellisen Highscore-olion validointi
			
			if (failScore.scores == null)
				failedTest("Scores-ArrayList null in valid Highsore object");
			System.out.print("..");

			if (failScore.scores.size() != 0)
				failedTest("Score count not 0 in invalid Highscore object (not possible)");
			System.out.print("..");

			// Validin Highscore-olion lataaminen tiedostosta
			
			Highscore validScore = new Highscore(null, null, null, null, "testscores.scr");
			if (validScore == null)
				failedTest("Couldn't create valid Highscore object.");
			System.out.print("..");

			// Ladatun Highscore-olion validointi
			
			if (validScore.scores == null)
				failedTest("Scores-ArrayList null in valid Highsore object");
			System.out.print("..");

			if (validScore.scores.size() < 3)
				failedTest("Score count less than 3 in test-file (not possible)");
			System.out.print("..");

			for (int i = 0; i < validScore.scores.size() - 1; i++)
				if (validScore.scores.get(i).compareTo(
						validScore.scores.get(i + 1)) == 1)
					failedTest("Valid scores out of order.");
			System.out.println("..OK!");

			// Uuden tuloksen lisääminen

			System.out.println("Testing addScore..");
			validScore.addScore("Pahkuna", 500);
			if (validScore.scores.size() < 3)
				failedTest("Score count less than 3 in test-file after add score");
			System.out.print("..");

			// Lisätyn tuloksen validointi
			
			for (int i = 0; i < validScore.scores.size() - 1; i++)
				if (validScore.scores.get(i).compareTo(validScore.scores.get(i + 1)) == 1)
					failedTest("Valid scores out of order after add score.");
			System.out.print("..");

			// Ladataan tiedosto, johon juuri lisättiin tulos ja validoidaan se 
			
			Highscore cmpScore = new Highscore(null, null, null, null, "testscores.scr");
			System.out.print("..");

			for (int i = 0; i < cmpScore.scores.size(); i++) {
				if (cmpScore.scores.get(i).compareTo(validScore.scores.get(i)) != 0)
					failedTest("Valid scores not matching with the scores file.");
				System.out.print("..");
			}

			// Lisätään virheellisesti ladattuun HighScore-olioon tulos
			
			failScore.addScore("Pahkuna", 500);
			if (failScore.scores.size() != 1 && failScore.scores.size() != 0)
				failedTest("Score count invalid after add score");

			// Varmistetaan, että uusi virheellisesti ladattu HighScore-olio ei täsmää edellisen virheellisen olion kanssa 
			
			cmpScore = new Highscore(null, null, null, null, "/&&@!MEJAHÖIDJIÖK/\\~åäö><|^ÖGFFFÖÖ");
			System.out.print("..");

			for (int i = 0; i < cmpScore.scores.size(); i++) {
				if (cmpScore.scores.get(i).compareTo(validScore.scores.get(i)) == 0)
					failedTest("FailScores matching with a non-existent file (?)");
				System.out.print("..");
			}
			
			System.out.println("..OK!");
		} 
		catch (Exception e) {
			failedTest("Unknown exception: " + e);
		}

		System.out.println("Everything OK with the Highscores!");
	}

	/**
	 * Apumetodi, joka tulostaa pieleen menneen testin sekä lopettaa ohjelman suorittamisen.
	 * @param test Pieleen mennyt testi.
	 */
	private static void failedTest(String test) {
		System.out.println("TEST FAILED: " + test);
		System.exit(0);
	}
}