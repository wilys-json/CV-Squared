// This code is modified from this tutorial:
// https://heartbeat.fritz.ai/pytorch-mobile-image-classification-on-android-5c0cfb774c5b

package WilysJson.SceneRecogCam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Utils {

    final static int RESOLUTION = 200;

    // From PyTorch Android Sample
    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }

        catch (IOException e) {
            Log.e("Utils.assetFilePath", "Error process asset " + assetName + " to file path");
        }
        return null;
    }


    public static Bitmap scaleImage(Bitmap bitmap) {

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, RESOLUTION, RESOLUTION, false);
        return scaledBitmap;

    }

    public static Bitmap rotateImage(Bitmap originalBitmap, int rotation) {

        Matrix rotationMatrix = new Matrix();

        switch (rotation) {

            case Surface.ROTATION_0:
                rotationMatrix.setRotate(90);
                break;
            case Surface.ROTATION_90:
                rotationMatrix.setRotate(0);
                break;
            case Surface.ROTATION_180:
                rotationMatrix.setRotate(270);
                break;
            case Surface.ROTATION_270:
                rotationMatrix.setRotate(180);
                break;
        }


        Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalBitmap.getWidth(), originalBitmap.getHeight(), rotationMatrix, true);

        return rotatedBitmap;

    }

    public static Bitmap processImage(Bitmap bitmap, int rotation) {

        return scaleImage(rotateImage(bitmap, rotation));

    }

}
