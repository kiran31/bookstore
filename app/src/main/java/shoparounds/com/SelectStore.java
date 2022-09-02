package shoparounds.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import util.Session_management;

public class SelectStore extends AppCompatActivity{

    ImageView imageView;
    Button chooseBn,uploadBn;
    EditText name;
    private final int IMG_REQUEST=1;
    private Bitmap bitmap;
    Session_management session_management;

    @Override
    protected void attachBaseContext(Context newBase) {
     newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);



    }


}
