package uob.oop;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.Properties;


public class ArticlesEmbedding extends NewsArticles {
    private int intSize = -1;
    private String processedText = "";

    private INDArray newsEmbedding = Nd4j.create(0);

    public ArticlesEmbedding(String _title, String _content, NewsArticles.DataType _type, String _label) {
        //TODO Task 5.1 - 1 Mark
        super(_title,_content,_type,_label); // constructor for parent class


    }

    public void setEmbeddingSize(int _size) {
        //TODO Task 5.2 - 0.5 Marks
        this.intSize = _size; // sets intSize

    }

    public int getEmbeddingSize(){
        return intSize;
    }

    @Override
    public String getNewsContent() {
        //TODO Task 5.3 - 10 Marks
        if(!processedText.isEmpty()){
            return processedText;
        }
        // TextCleaning
        String cleanedContent = textCleaning(super.getNewsContent()); // textCleaning on newsContent

        //Lemmatization the text
        Properties props = new Properties();
        props.setProperty("annotators","tokenize,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = pipeline.processToCoreDocument(cleanedContent);

        //Remove STOPWORDS
        StringBuilder notStopWords = new StringBuilder();
        for(CoreLabel currentCleanedContent : document.tokens()){
            String lemma = currentCleanedContent.lemma();
            boolean isStopWord = false;
            for(String stopWord: Toolkit.STOPWORDS){
                if(stopWord.equals(lemma)){
                    isStopWord = true;
                    break;
                }
            }
            if (isStopWord == false){
                notStopWords.append(lemma.toLowerCase()).append(" "); // adds content to SB if not STOPWORD
            }
            processedText = notStopWords.toString();
        }
        return processedText.trim();
    }

    public INDArray getEmbedding() throws Exception {
        //TODO Task 5.4 - 20 Marks

        //ERRORS
        if (intSize == -1) {
            throw new InvalidSizeException("Invalid size");
        }
        if (processedText.isEmpty()) {
            throw new InvalidTextException("Invalid text");
        }
        if(!newsEmbedding.isEmpty()){ // if newEmbedding not Empty returns the correct value, therefore stops looping
            return newsEmbedding;
        }
        // Set Dimensions
        String[] contentArray = processedText.split(" ");
        int listLimit = Math.min(contentArray.length, intSize); // checks which is smaller to show whether Zero need to added or limit val
        int yCoordinate = AdvancedNewsClassifier.listGlove.get(0).getVector().getVectorSize(); // gets Vector size of Arrray
        newsEmbedding = Nd4j.zeros(intSize, yCoordinate);

        //Gets Word Embedding
        int row = 0;
        for (String word : contentArray) {
            Glove sameGlove = null; // sets to null
            for (Glove tempGlove : AdvancedNewsClassifier.listGlove) {
                if (tempGlove.getVocabulary().equalsIgnoreCase(word)) {
                    sameGlove = tempGlove; // changes sameGLove val so it can initiate if statement below
                    break;
                }
            }
                if (sameGlove != null) {
                    newsEmbedding.putRow(row++, Nd4j.create(sameGlove.getVector().getAllElements())); // creates an array with all vector elements and puts it in other array where row is next row
                    if (row == listLimit) { // limiting row size
                        break;
                    }
                }

        }

    return Nd4j.vstack(newsEmbedding.mean(1));

    }
    /***
     * Clean the given (_content) text by removing all the characters that are not 'a'-'z', '0'-'9' and white space.
     * @param _content Text that need to be cleaned.
     * @return The cleaned text.
     */
    private static String textCleaning(String _content) {
        StringBuilder sbContent = new StringBuilder();

        for (char c : _content.toLowerCase().toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || Character.isWhitespace(c)) {
                sbContent.append(c);
            }
        }

        return sbContent.toString().trim();
    }
}
