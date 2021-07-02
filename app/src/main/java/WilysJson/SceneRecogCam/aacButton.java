package WilysJson.SceneRecogCam;


public class aacButton {

    private String AACText;
    private String AACImage;

    aacButton (String aacText, String aacImage) {
        this.AACText = aacText;
        this.AACImage = aacImage;

    }

    public String getText() { return AACText; }

    public String getAACImage () { return AACImage; }

}
