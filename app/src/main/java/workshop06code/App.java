package workshop06code;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (!line.matches("[a-z]{4}")) {
                    logger.severe("Invalid word in data.txt (ignored): " + line);
                } else {
                    logger.info("Valid word added from data.txt: " + line);
                    wordleDatabaseConnection.addValidWord(i, line);
                    i++;
                }
            }

        } catch (IOException e) {
            System.out.println("Not able to load. Sorry!");
            logger.log(Level.SEVERE, "Error reading data.txt", e);
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();
            while (!guess.equals("q")) {
                if (!guess.matches("[a-z]{4}")) {
                    System.out.println("Invalid input. Please enter a 4-letter word with lowercase letters a-z only.\n");
                    logger.warning("User entered invalid guess: " + guess);
                }else{
                    System.out.println("You've guessed '" + guess + "'.");

                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        System.out.println("Success! It is in the the list.\n");
                    }else{
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }
                }
                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "Scanner input error", e);
        }

    }
}