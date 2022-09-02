package shoparounds.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import util.DatabaseCartHandler;
import util.Session_management;
import Config.BaseURL;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Bundle;

import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG ="tag" ;
    private Session_management sessionManagement;
    RelativeLayout confirm;
    private DatabaseCartHandler db_cart;
    private String getlocation_id = "";
    private String getstore_id = "";
    private String getvalue = "";
    private String get_wallet_ammount = "";
    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    String text;
    private String user_id = "";
    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        // Call the function callInstamojo to start payment here
        sessionManagement = new Session_management(PaymentActivity.this);
        String email = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        String mobile = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        String name = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        String total_rs = getIntent().getStringExtra("total");
        getlocation_id = getIntent().getStringExtra("getlocationid");
        getstore_id = getIntent().getStringExtra("getstoreid");
        gettime = getIntent().getStringExtra("gettime");
        getvalue = getIntent().getStringExtra("getpaymentmethod");
        getdate = getIntent().getStringExtra("getdate");
        get_wallet_ammount = getIntent().getStringExtra("wallet_ammount");
//        callInstamojoPay(email, mobile, total_rs, "official", name);
        sessionManagement = new Session_management(PaymentActivity.this);
        db_cart = new DatabaseCartHandler(PaymentActivity.this);
        startPay(email, mobile, name, total_rs, user_id);
       /* try
        {
            in = new Intent(this,Class.forName("com.rahulhooda.integrationsampleapp_payumoneypnp.MainActivity"));
            startActivity(in);
        }
        catch (ClassNotFoundException e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/
        //callInstamojoPay(email,mobile,total_rs,"official",name);
    }

    private void startPay(String email, String mobile, String name, String total_rs, String user_id) {
        String merchant_id = "6760288";
        String merchant_key = "lf6SBJcW";
        String merchant_salt = "5Aoh9nwnlt";
        String surl = "https://www.payumoney.com/mobileapp/payumoney/success.php";
        String furl = "https://www.payumoney.com/mobileapp/payumoney/failure.php";
        String udf = "";
        boolean debug = false;
        builder.setAmount(total_rs)
                .setFirstName(name)
                .setProductName("book")
                .setEmail(email)
                .setPhone(mobile)
                .setTxnId(user_id)
                .setKey(merchant_key)
                .setMerchantId(merchant_id)
                .setsUrl(surl)
                .setfUrl(furl)
                .setIsDebug(debug)
                .setUdf1(udf)
                .setUdf2(udf)
                .setUdf3(udf)
                .setUdf4(udf)
                .setUdf5(udf)
                .setUdf6(udf)
                .setUdf7(udf)
                .setUdf8(udf)
                .setUdf9(udf)
                .setUdf10(udf);
        try {
            paymentParam = builder.build();
            /* Test debug purpose paymentParam = calculateServerSideHashAndInitiatePayment1(paymentParam,merchant_salt);
            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam,PaymentActivity.this, R.style.AppTheme_default, false);
            */
            getHashKey(paymentParam);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam, String merchant_salt) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");
        stringBuilder.append(merchant_salt);
        String hash = hashCal(stringBuilder.toString());
        Toast.makeText(this, hash, Toast.LENGTH_SHORT).show();
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }
    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    private void getHashKey(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));
        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);

    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PaymentActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("http://yourbooksstore.com/admin/hash/get_hash.php");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /**
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);
            progressDialog.dismiss();
            //payNowButton.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(PaymentActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PaymentActivity.this,merchantHash, Toast.LENGTH_SHORT).show();
                paymentParam.setMerchantHash(merchantHash);
                PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, PaymentActivity.this, R.style.AppTheme_default, false);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                    //Success Transaction
                } else {
                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }
}
    /*private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        Activity activity = PaymentActivity.this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }*/

    //InstapayListener listener;


    /*private void initListener() {
               listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }*/
