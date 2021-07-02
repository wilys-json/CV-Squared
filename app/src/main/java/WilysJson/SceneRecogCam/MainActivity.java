package WilysJson.SceneRecogCam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private int rotation;

    PreviewView mPreviewView;
    ImageView captureImage;
    Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviewView = findViewById(R.id.viewFinder);
        captureImage = findViewById(R.id.captureImg);
        classifier = new Classifier(Utils.assetFilePath(this, "efficientnet_lite2_5c2d038a.pt"));

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }


    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis.Builder imageAnalysisBuilder = new ImageAnalysis.Builder();

        ImageCapture.Builder imageCaptureBuilder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(imageCaptureBuilder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        ImageCapture imageCapture = imageCaptureBuilder.setTargetRotation(getDisplay().getRotation()).build();
        ImageAnalysis imageAnalysis = imageAnalysisBuilder.setTargetRotation(getDisplay().getRotation()).build();

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);



        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OrientationEventListener orientationEventListener = new OrientationEventListener(getApplicationContext()) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        if (orientation == ORIENTATION_UNKNOWN) {
                            return;
                        }
                        else if (orientation >= 45 && orientation < 135) {
                            rotation = Surface.ROTATION_270;
                        }
                        else if (orientation >= 135 && orientation < 225) {
                            rotation = Surface.ROTATION_180;
                        }
                        else if (orientation >=225 && orientation < 315) {
                            rotation = Surface.ROTATION_90;
                        }
                        else {
                            rotation = Surface.ROTATION_0;
                        }
                    }
                };

                orientationEventListener.enable();
                imageCapture.setTargetRotation(rotation);
                imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {


                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);


                        Bitmap bitmap = Utils.rotateImage(imageProxytoBitmap(image), rotation);
                        String prediction = classifier.predict(bitmap);
                        Bitmap processedBitmap = Utils.scaleImage(bitmap);
                        Intent aacBoardView = new Intent(getApplicationContext(), AACBoard.class);

                        aacBoardView.putExtra("capturedScene", processedBitmap);
                        aacBoardView.putExtra("pred", prediction);
                        image.close();

                        startActivity(aacBoardView);

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                });
            }
        });
    }

    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private Bitmap imageProxytoBitmap(ImageProxy image) {

        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}