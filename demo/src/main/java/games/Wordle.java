package games;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Wordle extends JFrame {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final ArrayList<String> llista = getLlista();
    private String targetWord;
    private int attempts;
    private JTextField guessField;
    private JTextArea feedbackArea;
    private JPanel gridPanel;

    public Wordle() {
        setTitle("Wordle Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        targetWord = chooseRandomWord();
        attempts = 0;

        // Create the guess field and button
        guessField = new JTextField(10); // Adjust width of the text field
        JButton guessButton = new JButton("Guess");

        // Feedback area
        feedbackArea = new JTextArea(10, 30); // Adjust size of the feedback area
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Button action listener
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeGuess();
            }
        });

        // Panel for guess input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(guessButton);
        inputPanel.add(guessField);

        // Grid panel for any additional UI elements (currently not used for the grid but can be expanded)
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(MAX_ATTEMPTS, 1));

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(feedbackArea), BorderLayout.CENTER); // Feedback area in the center
        add(gridPanel, BorderLayout.EAST);

        setVisible(true);
    }
    public static ArrayList<String> getLlista() {
        ArrayList<String> wordsList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.wordle-catala.cat/wp-content/uploads/2023/09/paraules-de-5-lletres-en-catala.txt").get();
            String text = doc.body().text();
            wordsList.addAll(Arrays.asList(text.split("\\s+")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordsList;
    }
    private String chooseRandomWord() {
        Random random = new Random();
        int randomNumber = random.nextInt(10500);
        return llista.get(randomNumber);
    }

    private void makeGuess() {
        String guess = guessField.getText().toLowerCase();
        guessField.setText("");

        if (isValidGuess(guess)) {
            feedbackArea.append("Attempt " + (attempts + 1) + ": " + guess + "\n");
            String feedback = getFeedback(guess);
            feedbackArea.append(feedback + "\n");
            attempts++;

            if (guess.equals(targetWord)) {
                feedbackArea.append("Congratulations! You've guessed the word: " + targetWord + "\n");
                guessField.setEnabled(false);
            } else if (attempts >= MAX_ATTEMPTS) {
                feedbackArea.append("Sorry! You've run out of attempts. The word was: " + targetWord + "\n");
                guessField.setEnabled(false);
            }
        } else {
            feedbackArea.append("Invalid guess. Please enter a valid 5-letter word.\n");
        }
    }

    private boolean isValidGuess(String guess) {
        return guess.length() == 5 && llista.contains(guess);
    }

    private String getFeedback(String guess) {
        StringBuilder feedback = new StringBuilder();
        boolean[] checked = new boolean[5]; // To avoid double counting letters

        // First pass for correct letters in the correct position
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == targetWord.charAt(i)) {
                feedback.append("[").append(guess.charAt(i)).append("] "); // Correct position
                checked[i] = true; // Mark as checked
            } else {
                feedback.append("_ "); // Placeholder for feedback
            }
        }

        // Second pass for correct letters in the wrong position
        for (int i = 0; i < 5; i++) {
            if (!checked[i]) {
                char currentChar = guess.charAt(i);
                if (targetWord.indexOf(currentChar) != -1 && !feedback.toString().contains("[" + currentChar + "]")) {
                    feedback.setCharAt(i * 2, '{'); // Correct letter but wrong position
                    feedback.setCharAt(i * 2 + 1, currentChar);
                    feedback.setCharAt(i * 2 + 2, '}'); // Adding brackets for feedback
                }
            }
        }

        return feedback.toString();
    }
}



