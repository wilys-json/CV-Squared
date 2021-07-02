package WilysJson.SceneRecogCam;

import android.util.Base64;

public class Keys {

    static {
        System.loadLibrary("keys");
    }

    private native String getOpenSymbolShareSecret();
    private native String getAWSCognitoPoolID();

    public static String OPENSYMBOL_SHARESECRET = new String(Base64.decode(new Keys().getOpenSymbolShareSecret(),
            Base64.DEFAULT));
    public static String AWSCOGNITO_POOLID = new String(Base64.decode(new Keys().getAWSCognitoPoolID(), Base64.DEFAULT));

}
