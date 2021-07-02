package WilysJson.SceneRecogCam;

import com.amazonaws.regions.Regions;

public class GlobalParams {

    public static String KG_URL = "https://api.conceptnet.io/c/en/";

    public static String[] KG_RELATIONS = new String[] {
            "AtLocation"
    };

    public static float KG_WEIGHT = 1.0f;

    public static String SYMBOL_TOKEN_API = "https://www.opensymbols.org/api/v2/token";

    public static String SYMBOL_API = "https://www.opensymbols.org/api/v2/symbols";

    public static String SYMBOL_PREFERRED_LIBRARY = "arasaac";

    public static String[] SYMBOL_PREFERRED_LIBRARIES = new String[] {

            "arasaac", "mulberry"
    };

    public static Regions AWS_REGION = Regions.US_EAST_2;

}
