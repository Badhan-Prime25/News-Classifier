package uob.oop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Toolkit {
    public static List<String> listVocabulary = null;
    public static List<double[]> listVectors = null;
    private static final String FILENAME_GLOVE = "glove.6B.50d_Reduced.csv";

    public static final String[] STOPWORDS = {"a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"};

    public void loadGlove() throws IOException {
        //TODO Task 4.1 - 5 marks
        listVocabulary = new ArrayList<>();
        listVectors = new ArrayList<>();
        try {
            File file = Toolkit.getFileFromResource(FILENAME_GLOVE);
            BufferedReader myReader = new BufferedReader(new FileReader(file));
            String line;
            while((line= myReader.readLine()) != null){
                String[] lineSplit = line.split(",");
                listVocabulary.add(lineSplit[0]);
                double[] vectorArray = new double[(lineSplit.length-1)];
                for(int i = 1;i<lineSplit.length;i++){
                    vectorArray[i-1] = Double.parseDouble(lineSplit[i]);
                }
                listVectors.add(vectorArray);

            }
        }catch (Exception IOException) {
            System.out.println( IOException.getMessage());
            }

        }



    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Toolkit.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public List<NewsArticles> loadNews() {
        List<NewsArticles> listNews = new ArrayList<>();
        //TODO Task 4.2 - 5 Marks
        try {
            File folder = getFileFromResource("News"); // gets folder path
            if (folder.exists()) {
                File[] newsFiles = folder.listFiles(); // creates list of all files in folder
                if (newsFiles != null) {
                    for (File current : newsFiles) {
                        if (current.isFile() && current.getName().endsWith(".htm")) { // check if file & end with .htm
                            String newsArticles = Files.readString(current.toPath());  // read the content in the File
                            String currentTitle = HtmlParser.getNewsTitle(newsArticles);
                            String currentContent = HtmlParser.getNewsContent(newsArticles);
                            NewsArticles.DataType currentType = HtmlParser.getDataType(newsArticles);
                            String currentLabel = HtmlParser.getLabel(newsArticles);
                            NewsArticles currentArticle = new NewsArticles(currentTitle, currentContent, currentType, currentLabel); // uses colleted data to create an instance of NewsArticles
                            listNews.add(currentArticle); //  add to the  List of NewsArticle
                        }
                    }

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listNews;
    }

    public static List<String> getListVocabulary() {
        return listVocabulary;
    }

    public static List<double[]> getlistVectors() {
        return listVectors;
    }
}
