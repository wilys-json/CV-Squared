// This part of the code was modified from the following tutorial:
// https://heartbeat.fritz.ai/pytorch-mobile-image-classification-on-android-5c0cfb774c5b

package WilysJson.SceneRecogCam;
import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

public class Classifier {

    Module model;
    int resolution;

    public Classifier(String modelPath) {
        // Load Model
        this.model = Module.load(modelPath);
        getResolution(modelPath);

    }

    private void getResolution(String modelPath) {

        String version;

        if (modelPath.contains("efficientnet")) {
            version = "efficientnetb";

            // Infer version of EfficientNet
            String[] arrOfPath = modelPath.split("_", 5);
            for (String chunk : arrOfPath) {
                String lastChar = chunk.substring(chunk.length() - 1);

                if (lastChar.matches("[0-9]")) {
                    version += lastChar;
                    break;
                }
            }
        }

        else {
            version = "default";
        }

        this.resolution = Size.RESOLUTIONS.get(version);
    }

    public Tensor preprocess(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, this.resolution, this.resolution, false);
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB);
    }

    public int argMax(float[] predictions) {

        int maxIndex = -1;
        float maxValue = 0.0f;

        for (int i = 0; i < predictions.length; i ++) {
            if (predictions[i] > maxValue) {
                maxValue = predictions[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public String predict(Bitmap bitmap) {

        Tensor tensor = preprocess(bitmap);

        IValue inputs = IValue.from(tensor);
        Tensor outputs = model.forward(inputs).toTensor();
        float[] predictions = outputs.getDataAsFloatArray();
        int classIndex = argMax(predictions);

        if (classIndex >= 0) {
            // Select class from particular Scene Dataset
            return Scenes.MIT67INDOOR[classIndex];  // from MIT INDOOR 67
        } else {

            return "Not found";
        }
    }

}
