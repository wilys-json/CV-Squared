/*****************
UNDER CONSTRUCTION
*****************/
package WilysJson.SceneRecogCam;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PlaySoundActivity extends AppCompatActivity {

    // Backend resources
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonPollyPresigningClient presigningClient;
    private List<Voice> voices;

    //Media player
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String aacText = getIntent().getStringExtra("aacText");
        credentialsProvider = new CognitoCachingCredentialsProvider(getApplicationContext(),
                Keys.AWSCOGNITO_POOLID, Regions.US_EAST_2);
        presigningClient = new AmazonPollyPresigningClient(credentialsProvider);
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
        DescribeVoicesResult describeVoicesResult = presigningClient
                .describeVoices(describeVoicesRequest);
        voices = describeVoicesResult.getVoices();

        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        .withText(aacText)
                        .withVoiceId(voices.get(0).getId())
                        .withOutputFormat(OutputFormat.Mp3);
        URL presignedSynthesizeSpeechUrl = presigningClient
                .getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
        } catch (IOException e) {
            Log.e("SceneRecogCam/MediaPlayer", "Failed to set data source", e);
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

    }
}
