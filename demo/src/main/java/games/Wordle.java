package games;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Wordle extends JFrame {
    private static final int MAX_GUESSES = 5;
    private static final ArrayList<String> LLISTA = getLlista();
    private String targetWord;
    private int guessCount = 0;

    private JTextField guessField;
    private JPanel guessesPanel;

    public Wordle() {
        setTitle("Wordle Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLayout(new BorderLayout());

        targetWord = getRandomWord().toLowerCase();

        JLabel titleLabel = new JLabel("Endevina el Mot!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        guessesPanel = new JPanel();
        guessesPanel.setLayout(new GridLayout(MAX_GUESSES, 1));
        add(guessesPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        guessField = new JTextField(5);
        guessField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(guessField);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.addActionListener(new SubmitGuessListener());
        inputPanel.add(submitButton);

        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String getRandomWord() {
        Random rand = new Random();
        return LLISTA.get(rand.nextInt(LLISTA.size()));
    }

    private void checkGuess(String guess) {
        JPanel guessPanel = new JPanel();
        guessPanel.setLayout(new GridLayout(1, 5));

        for (int i = 0; i < 5; i++) { 
            JLabel letterLabel = new JLabel(String.valueOf(guess.charAt(i)), SwingConstants.CENTER);
            letterLabel.setOpaque(true);
            letterLabel.setFont(new Font("Arial", Font.BOLD, 20));
            letterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            if (guess.charAt(i) == targetWord.charAt(i)) {
                letterLabel.setBackground(Color.GREEN); // correct letter, correct position
            } else if (targetWord.contains(String.valueOf(guess.charAt(i)))) {
                letterLabel.setBackground(Color.YELLOW); // correct letter, wrong position
            } else {
                letterLabel.setBackground(Color.LIGHT_GRAY); // incorrect letter
            }
            guessPanel.add(letterLabel);
        }

        guessesPanel.add(guessPanel);
        guessesPanel.revalidate();
        guessesPanel.repaint();

        if (guess.equals(targetWord)) {
            JOptionPane.showMessageDialog(this, "Felicitats! L'has encertat!!");
            resetGame();
        } else if (++guessCount >= MAX_GUESSES) {
            JOptionPane.showMessageDialog(this, "Game Over! La paraula era: " + targetWord);
            resetGame();
        }
    }

    private void resetGame() {
        targetWord = getRandomWord();
        guessCount = 0;
        guessesPanel.removeAll();
        guessesPanel.revalidate();
        guessesPanel.repaint();
    }

    private class SubmitGuessListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String guess = guessField.getText().toLowerCase();
            if (guess.length() != 5 || !LLISTA.contains(guess)) {
                JOptionPane.showMessageDialog(null, "Siusplau, usa una paraula vàlida de 5 lletres.");
                return;
            }
            checkGuess(guess);
            guessField.setText("");
        }
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

}





