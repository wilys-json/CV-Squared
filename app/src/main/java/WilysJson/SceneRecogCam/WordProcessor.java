package WilysJson.SceneRecogCam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class WordProcessor extends PorterStemmer {

    private final String[] stopWords = {"a", "an", "the"};
//    private POSTaggerME posTagger;
//    private TokenizerME tokenizer;

//    WordProcessor(POSModel posModel, TokenizerModel tokenizerModel) {
    WordProcessor(){
//        posTagger = new POSTaggerME(posModel);
//        tokenizer= new TokenizerME(tokenizerModel);

    }


    public String removeStopWords(String word) {

        String stopwordsRegex = Arrays.stream(stopWords).collect(Collectors.joining("|",
                "\\b(", ")\\b\\s?"));

        String cleanWord = word.toLowerCase().replaceAll(stopwordsRegex, "");
        return cleanWord;
    }

    public WordItem processWord(String word) {

        String cleanWord = removeStopWords(word);
        WordItem processedWord = new WordItem(stem(cleanWord), cleanWord);
        return processedWord;
    }

}