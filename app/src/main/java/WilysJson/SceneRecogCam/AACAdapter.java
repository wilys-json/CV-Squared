package WilysJson.SceneRecogCam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AACAdapter extends RecyclerView.Adapter<AACAdapter.AACViewHolder> {

    // Requests
    private List<aacButton> aacButtons = new ArrayList<>();
    private RequestQueue requestQueue;


//    static {
//        System.loadLibrary("keys");
//    }
//
//    public native String getOpenSymbolShareSecret();
//    public native String getAWSCognitoPoolID();

    AACAdapter(Context context, String place) {
        StringBuilder apiKey = new StringBuilder();
        requestQueue = Volley.newRequestQueue(context);
        String shareSecret = Keys.OPENSYMBOL_SHARESECRET;
        getPublicKey(shareSecret, new OpenSymbolKeyGetter() {
            @Override
            public void onSuccess(String token) {
                apiKey.append(token);
                loadButtons(place, apiKey.toString());
            }
        });
    }

    public static class AACViewHolder extends RecyclerView.ViewHolder {


        public CardView containerView;
        public TextView textView;
        public ImageView imageView;

        AACViewHolder(View view) {
            super(view);
            containerView = view.findViewById(R.id.aac_button);
            textView = view.findViewById(R.id.aac_text);
            imageView = view.findViewById(R.id.aac_image);
        }

    }

    public interface WordItemCallBack {
        void onSuccess(List<String> wordTexts);
    }

    public interface OpenSymbolKeyGetter {
        void onSuccess(String token);
    }

    public interface OpenSymbolImageGetter {
        void onSuccess(String imageUrl);
    }


    public void getPublicKey(String s, final OpenSymbolKeyGetter getter) {

        String tokenUrl = GlobalParams.SYMBOL_TOKEN_API;
        Map<String, String> params = new HashMap<>();
        params.put("secret", s);

        JsonObjectRequest tokenRequest = new JsonObjectRequest(Request.Method.POST,
                tokenUrl, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String token = response.getString("access_token");
                    getter.onSuccess(token);
                } catch (JSONException e) {

                    Log.e("SceneRecogCam/getPublicKey", "Access Token JSON error",
                            e);
                }
            }
        }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("SceneRecogCam/getPublicKey", "Access Token error", error);

                }
            });

     requestQueue.add(tokenRequest);

    }

    public void loadButtons(String place, String apiKey) {

        loadWordItems(place, new WordItemCallBack() {
            @Override
            public void onSuccess(List<String> wordTexts) {

                for (int i = 0; i < wordTexts.size(); i++) {
                    String aacText = wordTexts.get(i);

                    StringBuilder imageAPI = new StringBuilder(GlobalParams.SYMBOL_API);
                    imageAPI.append("?access_token=" + apiKey);
                    imageAPI.append("&q=" + aacText.replace(" ", "%20"));
                    imageAPI.append("%20favor:" + GlobalParams.SYMBOL_PREFERRED_LIBRARY);
                    loadImageUrl(imageAPI.toString(), new OpenSymbolImageGetter() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            Map<String, String> button = new HashMap<>();
                            button.put("text", aacText);
                            button.put("imageUrl", imageUrl);
                            new GetImageUrlTask().execute(button);
                        }
                    });
                }
            }
        });

    }

    public void loadImageUrl(String ImageAPI, final OpenSymbolImageGetter imageGetter) {

        String preferredLibrary = GlobalParams.SYMBOL_PREFERRED_LIBRARY;
        List<String> preferredLibraries = Arrays.asList(GlobalParams.SYMBOL_PREFERRED_LIBRARIES);
        JsonArrayRequest imageRequest = new JsonArrayRequest(Request.Method.GET,
                ImageAPI, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject imageItem = response.getJSONObject(i);
                        String repo = imageItem.getString("repo_key");
                        String imageUrl = imageItem.getString("image_url");
                        if (repo.equals(preferredLibrary)) {
                            imageGetter.onSuccess(imageUrl);
                            break;
                        }
                        else if (preferredLibraries.contains(repo)) {
                            imageGetter.onSuccess(imageUrl);
                            break;
                        }
                        else {
                            imageGetter.onSuccess(imageUrl);
                            break;
                        }
                    }

                } catch (JSONException e) {
                    Log.e("SceneRecogCam/loadimageURL", "Image JSON error.", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("SceneRecogCam/loadingimageURL", "Image item error.", error);

            }
        });

        requestQueue.add(imageRequest);

    }


    public void loadWordItems(String place, final WordItemCallBack callback) {

        WordProcessor wordProcessor = new WordProcessor();
        List<String> targetLabels = Arrays.asList(GlobalParams.KG_RELATIONS);
        List<String> wordStems = new ArrayList<>();
        List<String> wordTexts = new ArrayList<>();

        String url = GlobalParams.KG_URL + place.replace(" ", "_");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
;
                try {

                    JSONArray concepts = response.getJSONArray("edges");
                    for (int i = 0; i < concepts.length(); i++) {
                        JSONObject concept = concepts.getJSONObject(i);
                        String label = concept.getJSONObject("rel").getString("label");

                        if (targetLabels.contains(label)) {
                            String retrievedWord = concept.getJSONObject("start")
                                    .getString("label");
                            float weight = BigDecimal.valueOf(concept.getDouble("weight"))
                                    .floatValue();
                            WordItem processedText = wordProcessor.processWord(retrievedWord);
                            if (Float.compare(weight, GlobalParams.KG_WEIGHT) >= 0 &&
                                    !wordStems.contains(processedText.getStem()) &&
                                    !processedText.getText().equals(place)) {
                                    wordTexts.add(processedText.getText());
                                    wordStems.add(processedText.getStem());
                                }
                            }
                        }
                    callback.onSuccess(wordTexts);
                }
                catch (JSONException e) {
                    Log.e("SceneRecogCam", "JSON ERROR", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("SceneRecogCam", "AAC ITEM ERROR", error);
            }
        });
        requestQueue.add(request);
    }

    @NonNull
    @Override
    public AACViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.aac_button, parent, false);

        return new AACViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AACViewHolder holder, int position) {
        aacButton current = aacButtons.get(position);
        new DownloadImageTask(holder.imageView).execute(current.getAACImage());
        holder.textView.setText(current.getText());
        holder.containerView.setTag(current);
/************UNDER CONSTRUCTION: TTS Play Sound Function**********************/
//        holder.containerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent playSound = new Intent(holder.containerView.getContext(),
//                        PlaySoundActivity.class);
//                playSound.putExtra("aacText", current.getText());
//                v.getContext().startActivity(playSound);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return aacButtons.size();
    }

    private class GetImageUrlTask extends AsyncTask<Map<String, String>, Void, aacButton> {

        @Override
        protected aacButton doInBackground(Map<String, String>... maps) {
            aacButton button = new aacButton(maps[0].get("text"), maps[0].get("imageUrl"));
            return button;
        }

        @Override
        protected void onPostExecute(aacButton aacButton) {
            aacButtons.add(aacButton);
            notifyDataSetChanged();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            } catch (IOException e) {
                Log.e("SceneRecogCam/DownloadImageTask", "Image download error", e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

}
