package shoparounds.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import shoparounds.com.networkconnectivity.NetworkConnection;
import shoparounds.com.networkconnectivity.NoInternetConnection;

public class NoConnectionActivity extends AppCompatActivity {
    ImageView mCheckConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        mCheckConnection = (ImageView) findViewById(R.id.no_internet_connection);
        mCheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.connectionChecking(getApplicationContext())) {
                    Intent intent = new Intent(NoConnectionActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(NoConnectionActivity.this, NoInternetConnection.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
