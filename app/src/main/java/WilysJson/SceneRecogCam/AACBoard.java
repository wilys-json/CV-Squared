package WilysJson.SceneRecogCam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AACBoard extends AppCompatActivity {

    private AACAdapter aacAdapter;
    private GridLayoutManager gridLayoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);

        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("capturedScene");
        String place = getIntent().getStringExtra("pred").replace("_", " ");

        TextView textView = findViewById(R.id.label);

        ImageView imageView = findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);

        if (place.equals("Not found")) {
            textView.setText("Sorry I can't identify this place.\nPlease try again.".toUpperCase());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 9);
        } else {
            textView.setText(place.toUpperCase());
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            aacAdapter = new AACAdapter(getApplicationContext(), place);
            gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setAdapter(aacAdapter);
            recyclerView.setLayoutManager(gridLayoutManager);
        }


    }
}
