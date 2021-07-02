package WilysJson.SceneRecogCam;
import java.util.Map;

public class Size {

    public static Map<String, Integer> RESOLUTIONS = Map.of(
            "default", 224,
            "efficientnetb0", 224,
            "efficientnetb1", 240,
            "efficientnetb2", 260,
            "efficientnetb3", 300,
            "efficientnetb4", 380,
            "efficientnetb5", 456,
            "efficientnetb6", 528,
            "efficientnetb7", 600

    );
}
