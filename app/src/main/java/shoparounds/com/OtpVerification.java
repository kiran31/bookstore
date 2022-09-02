package shoparounds.com;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class OtpVerification extends AppCompatActivity {
    EditText et_otp ;
    TextView timeout ,back ;
    Button btn_verify ;

   // String otp = "";
    Dialog ProgressDialog;
    private static final long START_TIME_IN_MILLI=120000;
    boolean mmTimerRunning;
    private long mTimeLeftINMILLIS=START_TIME_IN_MILLI;
    CountDownTimer countDownTimer;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_otp_verification );
        et_otp = findViewById( R.id.et_otp );
        timeout = findViewById( R.id.time );
        btn_verify=findViewById( R.id.btn_continue );
        back = findViewById( R.id.txt_back );
        progressDialog=new ProgressDialog(OtpVerification.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

      final String otp = getIntent().getStringExtra( "otp" );
      final String mobile = getIntent().getStringExtra( "mobile" );
      final String type = getIntent().getStringExtra( "type" );
      startTimer();

      back.setOnClickListener( new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              finish();;
          }
      } );

      btn_verify.setOnClickListener( new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              progressDialog.show();

              String getotp = et_otp.getText().toString();
              if (otp.equalsIgnoreCase( getotp ))
              {
                  if (type.equalsIgnoreCase( "f" )) {
                      progressDialog.dismiss();
                      Toast.makeText( OtpVerification.this, "Verified ", Toast.LENGTH_LONG ).show();
                      Intent intent = new Intent( OtpVerification.this, ResetPassword.class );
                      intent.putExtra( "mobile",mobile );
                      startActivity( intent );
                  }
                  else if (type.equalsIgnoreCase( "r" )) {
                      progressDialog.dismiss();
                      Toast.makeText( OtpVerification.this, "Verified ", Toast.LENGTH_LONG ).show();
                      Intent intent = new Intent( OtpVerification.this, RegisterActivity.class );
                      intent.putExtra( "mobile",mobile );
                      startActivity( intent );
                  }

              }
              else
              {
                  Toast.makeText( OtpVerification.this,"wrong otp , try again !", Toast.LENGTH_LONG ).show();
                  finish();
              }

          }
      } );



    }
    public void startTimer()
    {
        countDownTimer=new CountDownTimer(mTimeLeftINMILLIS,1000) {
            @Override
            public void onTick(long l) {

                mTimeLeftINMILLIS=l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
               String otp="";
                timeout.setText("timeout");
                timeout.setTextColor( Color.RED);
                Handler handler = new Handler(  );
                handler.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1500 );
                        }
        }.start();
        mmTimerRunning=true;
    }
    private void updateCountDownText() {
        int minutes=(int)(mTimeLeftINMILLIS/1000)/60;
        int seconds=(int)(mTimeLeftINMILLIS/1000)%60;
        String timeLeftForamatedd= String.format( Locale.getDefault(),"%02d:%02d",minutes,seconds);
       timeout.setText(timeLeftForamatedd);

    }

}
