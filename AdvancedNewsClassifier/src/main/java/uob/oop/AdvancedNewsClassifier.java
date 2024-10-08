package uob.oop;

import org.apache.commons.lang3.time.StopWatch;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvancedNewsClassifier {
    public Toolkit myTK = null;
    public static List<NewsArticles> listNews = null;
    public static List<Glove> listGlove = null;
    public List<ArticlesEmbedding> listEmbedding = null;
    public MultiLayerNetwork myNeuralNetwork = null;

    public final int BATCHSIZE = 10;

    public int embeddingSize = 0;
    private static StopWatch mySW = new StopWatch();

    public AdvancedNewsClassifier() throws IOException {
        myTK = new Toolkit();
        myTK.loadGlove();
        listNews = myTK.loadNews();
        listGlove = createGloveList();
        listEmbedding = loadData();
    }

    public static void main(String[] args) throws Exception {
        mySW.start();
        AdvancedNewsClassifier myANC = new AdvancedNewsClassifier();

        myANC.embeddingSize = myANC.calculateEmbeddingSize(myANC.listEmbedding);
        myANC.populateEmbedding();
        myANC.myNeuralNetwork = myANC.buildNeuralNetwork(2);
        myANC.predictResult(myANC.listEmbedding);
        myANC.printResults();
        mySW.stop();
        System.out.println("Total elapsed time: " + mySW.getTime());
    }

    public List<Glove> createGloveList() {
        List<Glove> listResult = new ArrayList<>();
        //TODO Task 6.1 - 5 Marks
        String[] _stopWords = myTK.STOPWORDS; // stores stopwords
        for (int i = 0; i < myTK.listVocabulary.size(); i++) { // loop to check for stopwords
            boolean isStopWords = false;
            String vocabVal = myTK.listVocabulary.get(i);
            for (String stopWord : _stopWords) {
                if (stopWord.equals(vocabVal)) {
                    isStopWords = true;
                    break;
                }
            }
            if (isStopWords == false) { // adds NON stopwords
                Vector vector = new Vector(myTK.listVectors.get(i)); // gets vectors
                Glove glove = new Glove(vocabVal, vector); // creates glove
                listResult.add(glove); // add to list
            }
        }
        return listResult;
    }


    public static List<ArticlesEmbedding> loadData() {
        List<ArticlesEmbedding> listEmbedding = new ArrayList<>();
        for (NewsArticles news : listNews) {
            ArticlesEmbedding myAE = new ArticlesEmbedding(news.getNewsTitle(), news.getNewsContent(), news.getNewsType(), news.getNewsLabel());
            listEmbedding.add(myAE);
        }
        return listEmbedding;
    }

    public int calculateEmbeddingSize(List<ArticlesEmbedding> _listEmbedding) {
        int intMedian = -1;
        //TODO Task 6.2 - 5 Marks

        // Find document length
        List<Integer> documentLengths = new ArrayList<>(); // holds document lengths
        for (int i = 0; i < _listEmbedding.size(); i++) {
            int docLength = 0;
            String content = _listEmbedding.get(i).getNewsContent(); // gets news content to count
            String[] tempArray = content.split(" ");
            for (String word : tempArray) {
                for (String checkingWord : myTK.listVocabulary) {
                    if (word.equalsIgnoreCase(checkingWord)) { // checks if words is in document ignoring case
                        docLength = docLength + 1;
                    }
                }
            }
            documentLengths.add(docLength); // add length to list
        }

        //Sort List
        documentLengths.sort(null); // sorts list

        // Find Median
        int documentLen = documentLengths.size();
        if (documentLen % 2 == 0) { // if length of list is even
            int midValOne = documentLengths.get(documentLen / 2);
            int midValTwo = documentLengths.get(documentLen / 2 + 1);
            intMedian = (midValOne + midValTwo) / 2;
        } else { // length is odd
            intMedian = (documentLengths.get(documentLen + 1)) / 2;
        }
        return intMedian;
    }

    public void populateEmbedding() {
        //TODO Task 6.3 - 10 Marks
        for (ArticlesEmbedding embeddingVal : listEmbedding) {
            try {
                embeddingVal.getEmbedding(); // calls 5.4
            } catch (InvalidSizeException e) { // checks for exceptions
                embeddingVal.setEmbeddingSize(embeddingSize); // sets intsize
            } catch (InvalidTextException e) {
                embeddingVal.getNewsContent(); // get newsContent
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    public DataSetIterator populateRecordReaders(int _numberOfClasses) throws Exception {
        ListDataSetIterator myDataIterator = null;
        List<DataSet> listDS = new ArrayList<>();
        INDArray inputNDArray = null;
        INDArray outputNDArray = null;
        //TODO Task 6.4 - 8 Marks
        for (int i = 0; i < listEmbedding.size(); i++) {
            ArticlesEmbedding tempClass = listEmbedding.get(i); // temp value to store current element of list
            NewsArticles.DataType newsType = tempClass.getNewsType();
            String strLabel = tempClass.getNewsLabel();
            int intLabel = Integer.parseInt(strLabel);
            if (newsType == NewsArticles.DataType.Training) { // check if dt is Training
                inputNDArray = tempClass.getEmbedding();
                outputNDArray = Nd4j.zeros(1, _numberOfClasses); // creates an array full of 0
                outputNDArray.putScalar(0, intLabel - 1, 1); // appends 1 to index of intLabel -1
                DataSet dSet = new DataSet(inputNDArray, outputNDArray);
                listDS.add(dSet);
            }
        }
        return new ListDataSetIterator(listDS, BATCHSIZE);
    }

    public MultiLayerNetwork buildNeuralNetwork(int _numOfClasses) throws Exception {
        DataSetIterator trainIter = populateRecordReaders(_numOfClasses);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(42)
                .trainingWorkspaceMode(WorkspaceMode.ENABLED)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(Adam.builder().learningRate(0.02).beta1(0.9).beta2(0.999).build())
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(embeddingSize).nOut(15)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.HINGE)
                        .activation(Activation.SOFTMAX)
                        .nIn(15).nOut(_numOfClasses).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        for (int n = 0; n < 100; n++) {
            model.fit(trainIter);
            trainIter.reset();
        }
        return model;
    }

    public List<Integer> predictResult(List<ArticlesEmbedding> _listEmbedding) throws Exception {
        List<Integer> listResult = new ArrayList<>();
        //TODO Task 6.5 - 8 Marks
        for (int i = 0; i < _listEmbedding.size(); i++) {
            ArticlesEmbedding tempClass = _listEmbedding.get(i); // temp value to store current element of list
            NewsArticles.DataType newsType = tempClass.getNewsType();
            if (newsType.equals(NewsArticles.DataType.Testing)) { // only if DT is Testing
                int[] predictedLabel = myNeuralNetwork.predict(tempClass.getEmbedding()); // returns predicted val as array
                for (int k : predictedLabel) { // loop to add and predicted label in list and set
                    listResult.add(k);
                    tempClass.setNewsLabel(String.valueOf(k));
                }
            }
        }
        return listResult;
    }

    public void printResults() {
        //TODO Task 6.6 - 6.5 Marks
        List<Integer> labels = new ArrayList<>();
        List<Integer> labels1 = new ArrayList<>();
        for(int i = 0; i<listEmbedding.size();i++){
            ArticlesEmbedding tempClass = listEmbedding.get(i); // temp value to store current element of list
            NewsArticles.DataType newsType = tempClass.getNewsType();
            if (newsType.equals(NewsArticles.DataType.Testing)) { // if DT = Testing add int label to labels list
                int val = Integer.parseInt((tempClass.getNewsLabel()));
                labels.add(val);
            }
            for(Integer element : labels){   // loop to add labels once
                if(!labels1.contains(element)){
                    labels1.add(element);
                }
            }
        }
        labels1.sort(null); // sorts array
        for(int j = 0; j<labels1.size();j++){
            System.out.print("Group " + (labels1.get(j)+1+"\r\n")); //+1 to get correct group no.
            for(ArticlesEmbedding tempClass : listEmbedding){
                int labelVal =  Integer.parseInt(tempClass.getNewsLabel()); // current label
                if(labelVal == labels1.get(j) && tempClass.getNewsType().equals(NewsArticles.DataType.Testing)){  // DT = testing  and label is current label
                    System.out.println(tempClass.getNewsTitle()); // print content
                }

            }
        }

    }
}



