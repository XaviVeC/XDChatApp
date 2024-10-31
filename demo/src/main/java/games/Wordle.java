package games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Wordle {

    public String getParaula() {
        ArrayList<String> wordsList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.wordle-catala.cat/wp-content/uploads/2023/09/paraules-de-5-lletres-en-catala.txt").get();
            String text = doc.body().text();
            wordsList.addAll(Arrays.asList(text.split("\\s+")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int randomNumber = random.nextInt(10500);
        return wordsList.get(randomNumber);
        
    }
}