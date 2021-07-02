package WilysJson.SceneRecogCam;

public class WordItem {

    private String wordStem;
    private String wordText;

    WordItem(String stem, String word) {
        wordStem = stem;
        wordText = word;
    }

    public String getStem() {
        return this.wordStem;
    }

    public String getText() {
        return this.wordText;
    }

}
