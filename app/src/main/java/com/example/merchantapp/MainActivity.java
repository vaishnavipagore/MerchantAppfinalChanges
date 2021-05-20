package com.example.merchantapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.example.merchantapp.Initializepojo.InitialiseApiResponse;
import com.example.merchantapp.Initializepojo.InitiliseApiRequest;
import com.example.merchantapp.SHFD.AppConstant;
import com.example.merchantapp.SHFD.SHFD;
import com.example.merchantapp.dialoguebox.DialogClass;
import com.example.merchantapp.keyExchangepojo.KeyExchangeRequest;
import com.example.merchantapp.keyExchangepojo.KeyExchangeResponse;
import com.example.merchantapp.loginpojo.LoginRequest;
import com.example.merchantapp.loginpojo.LoginResponse;
import com.example.merchantapp.network.Apiclient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import java.util.StringTokenizer;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.merchantapp.rootutil.Rootutil.isDeviceRooted;

public class MainActivity extends AppCompatActivity {
String yourList="";
    EditText username,password;
    private RequestQueue mQueue;
    String usr,pass;
    Button btn;
    boolean isToken = false;
    String token,mid,tid;
    ProgressDialog dialog;
    String merchantName;
    String struserid, strpassword,stan,invoicenr;
    Executor executor;
    BiometricManager biometricManager;
    String publickey,storedpublickey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executor = ContextCompat.getMainExecutor(this);
        biometricManager = BiometricManager.from(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tmp = "";
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                tmp += key + ": " + value + "\n\n";
            }

        }
        //        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String mToken = instanceIdResult.getToken();
//                Log.e("Token",mToken);
//            }
//        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    token = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    token = task.getResult().getToken();
                    Log.i("FCM TOKEN", token);
                }
            }
        });

        username = (EditText) findViewById(R.id.username);

        password = (EditText) findViewById(R.id.password);

        mQueue = Volley.newRequestQueue(this);
        btn = (Button) findViewById(R.id.submit);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(MainActivity.this, "", "Loading...", true);
                 if(validation()){ login(); }
                 //  jsonParse();


            }

        });

    }

//    private void jsonParse () {
//
//        final String loginusername = username.getText().toString();
//        String loginpassword = password.getText().toString();
//        Map<String, String> postParam= new HashMap<String, String>();
//        postParam.put("username",loginusername);
//        postParam.put("password", loginpassword);
//        if(username.getText().toString().equals("") || password.getText().toString().equals("")){
//            Toast.makeText(getApplicationContext(),"Username or Password cannot be blank",Toast.LENGTH_LONG).show();
//        }
//
//        String url = Url.BASEURL+Url.login;
//
//
//        Log.e("URLLLLLINK!!!",url);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(postParam), new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("URLAPI",url+" :;  "+ response.toString());
//                try {
//                    Log.d("RESPONEEEE",response.toString());
//                    dialog.dismiss();
//                    String status =  response.getString("msg");
//                    Log.d("msg",status);
//                    if(status.equals("Successful"))
//                    {
//                        token = response.getString("tid");
//                        Log.d("TID",token);
//                        mid = response.getString("mid");
//                        Log.d("MID",mid);
//                        isToken = true;
//                        SHFD.saveToPreferences(MainActivity.this, AppConstant.CUST_TID, token);
//                        SHFD.saveToPreferences(MainActivity.this, AppConstant.Cust_MID,mid);
//                        SHFD.saveToPreferences(MainActivity.this, AppConstant.Cust_LOGIN, true);
//                        Log.e("SHAREDPREFERENCES",token+mid);
//                        if(isDeviceRooted()==true)
//                        {
//                            DialogClass.showWarningDialog("Sorry Cannot Allow Access!!","Your Device Is Rooted ", MainActivity.this);
//                        }
//
//                        else
//                        {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                            builder.setMessage("Login Successful!!")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            redirectToScreen();
//                                        }
//                                    });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//
//                        }
//                    }
//                    else
//                        {
//                        Toast.makeText(getApplication(),"Invalid Credentials",Toast.LENGTH_LONG).show();
//                    }
//
//
////                    if(!status.equals("")){
////                        dialog.dismiss();
////
////                    }
//
////                    merchantName = response.getString("owner_name");
//
////                    if(status.equals("") && token.equals("")){
////                        Toast.makeText(getApplication(),"Invalid Credentials",Toast.LENGTH_LONG).show();
////                    }
////                    else{}
////                     isToken = true;
//
//                } catch (JSONException e) {
//
//                    e.printStackTrace();
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
//                //error.printStackTrace();
//                NetworkResponse networkResponse = error.networkResponse;
//                if (networkResponse != null && networkResponse.statusCode == 401) {
//                    Toast.makeText(getApplicationContext(),"Invalid Username or Password",Toast.LENGTH_LONG).show();
//                }
//                if (networkResponse != null && networkResponse.statusCode == 403) {
//                    Toast.makeText(getApplicationContext(),"Invalid Username or Password",Toast.LENGTH_LONG).show();
//                }
//                if (networkResponse != null && networkResponse.statusCode == 400) {
//                    Toast.makeText(getApplicationContext(),"Invalid Username or Password",Toast.LENGTH_LONG).show();
//                }
//                if (networkResponse != null && networkResponse.statusCode == 502) {
//                    Toast.makeText(getApplicationContext(),"Could not validate UserName/Password. Call HelpDesk",Toast.LENGTH_LONG).show();
//                }
//                if (networkResponse != null && networkResponse.statusCode == 503) {
//                    Toast.makeText(getApplicationContext(),"Could not validate UserName/Password. Call HelpDesk",Toast.LENGTH_LONG).show();
//                }
//                if (networkResponse != null && networkResponse.statusCode == 404) {
//                    Toast.makeText(getApplicationContext(),"Could not validate UserName/Password. Call HelpDesk",Toast.LENGTH_LONG).show();
//                }
//                error.getMessage();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                headers.put("apikey","12345");
//                return headers;
//            }
//        };
//        mQueue.add(request);
//
//    }
















    private boolean validation() {

        struserid = username.getText().toString();
        strpassword =password.getText().toString();
        String strValueStatus = "";
        boolean flag = false;

        if (struserid.isEmpty()) {
            username.setError(" Please Enter Valid Userid");
            strValueStatus = "false ";
        }

        if (strpassword.isEmpty()) {
            password.setError(" Please Enter Valid  Password");
            strValueStatus = " false";
        } else {
            strValueStatus = "true ";
        }

        flag = !strValueStatus.contains("false");
        return flag;

    }
    public  void login()
    {
        Log.d("Login", "loginnnnnnnnnnnnnnnn");
        LoginRequest loginRequest= new LoginRequest();
        loginRequest.setUsername(struserid);
        loginRequest.setPassword(strpassword);

        Call<LoginResponse> responceCall = Apiclient.getApiClient(this).login(loginRequest);

        responceCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.d("loginurl..............", "" + call.request().url().toString());
                if (response.isSuccessful()) {

                    if (response.body().getMsg().equals("Successful")) {

                         dialog.dismiss();

                        Log.d("LOGIN RESPONSE", new Gson().toJson(response.body()));

                      //  Log.d("LOGIN RESPONSE","\nMID:="+response.body().getMid()+"\nTID:="+response.body().getTid()+"\nMSG:="+response.body().getMsg());
                        tid=response.body().getTid();
                        mid=response.body().getMid();
                        SHFD.saveToPreferences(MainActivity.this, AppConstant.CUST_TID, tid);
                        SHFD.saveToPreferences(MainActivity.this, AppConstant.Cust_MID, mid);
                        SHFD.saveToPreferences(MainActivity.this, AppConstant.Cust_LOGIN, true);

                        Log.d("SHAREDPREFERENCES","MID = "+mid+"TID="+tid);
                        if(isDeviceRooted()==true)
                        {
                            DialogClass.showWarningDialog("Sorry Cannot Allow Access!!","Your Device Is Rooted ", MainActivity.this);
                        }

                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Login Successful!!")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            redirectToScreen();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            InitialiseAPI();
                            KEYEXCHANGEAPI();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "ENTER VALID DETAILS", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Enter UserId", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                 DialogClass.showWarningDialog("Network Connection Error", "Network error Please check your mobile connection", MainActivity.this);
                Log.d("loginerror", "" + t.getMessage());

            }
        });

    }

    public  void  authuser(Executor ee)
    {
        BiometricPrompt.PromptInfo promptInfo =new  BiometricPrompt.PromptInfo.Builder()
                // 2
                .setTitle("PayNext Merchant Application")
                // 3

                // 4
                .setDescription("You need to Verify Yourself!! use PIN,Pattern or Password")
                // 5
                .setDeviceCredentialAllowed(true)
                // 6
                .build();

        BiometricPrompt promt =  new  BiometricPrompt(this,ee,new BiometricPrompt.AuthenticationCallback(){

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Intent i = new Intent(MainActivity.this,HomeActivity.class);
//                i.putExtra("token",token);
//                i.putExtra("username",username.getText().toString());
//                i.putExtra("ownername",merchantName);
//                i.putExtra("fcmToken",token);
                username.setText("");
                password.setText("");
                startActivity(i);

            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                finish();
            }
        });

        promt.authenticate(promptInfo);
    }

    private void redirectToScreen() {
        if(executor!=null){
          //  System.out.println("get eroorr   findd 111 ");

            switch (biometricManager.canAuthenticate())
            {
                case  BiometricManager.BIOMETRIC_SUCCESS: authuser(executor); break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE : goToMainPage(); break;

                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE :  goToMainPage(); break;
                case  BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED :goToMainPage(); break;

            }}else{
            //System.out.println("get eroorr   findd 222 ");

            Intent i = new Intent(MainActivity.this,HomeActivity.class);
//            i.putExtra("token",token);
//            i.putExtra("username",username.getText().toString());
//            i.putExtra("ownername",merchantName);
//            i.putExtra("fcmToken",token);
//
            username.setText("");
            password.setText("");
            startActivity(i);

        }



    }

    private void goToMainPage() {

        Intent i = new Intent(MainActivity.this,HomeActivity.class);
        i.putExtra("token",token);
        i.putExtra("username",username.getText().toString());
        i.putExtra("ownername",merchantName);
        i.putExtra("fcmToken",token);
        username.setText("");
        password.setText("");
        startActivity(i);    }

        private void InitialiseAPI() {
       InitiliseApiRequest initialiseRequest = new InitiliseApiRequest();
       initialiseRequest.setMsgType("0300");
       initialiseRequest.setF041(tid);
       initialiseRequest.setF042(mid);
       initialiseRequest.setF047("000056041010BE0510004318");
       initialiseRequest.setF061("1800008389" );
       Gson gson = new Gson();
        yourList = gson.toJson(initialiseRequest);
        Log.d("INITIALISE REQUEST list", "list" + yourList);

        Call<InitialiseApiResponse> responceCall = Apiclient.getApiClient(MainActivity.this).InitialiseAPi(initialiseRequest);
       responceCall.enqueue(new Callback<InitialiseApiResponse>() {
           @Override
           public void onResponse(Call<InitialiseApiResponse> call, Response<InitialiseApiResponse> response) {
               Log.d("INITIALISEAPI URL", "" + call.request().url().toString());
               if (response.isSuccessful()) {
                   Log.d("INITALISE RESPONSE", new Gson().toJson(response.body()));
                  // Log.d("INITALISE RESPONSE","\nMSG="+response.body().getMsgType()+"\nF011="+response.body().getF011()+"\nF039="+response.body().getF039()+"\nF041="+response.body().getF041()+"\nF042="+response.body().getF042()+"\nF048="+response.body().getF048()+"\nF060="+response.body().getF060()+"\nF062="+response.body().getF062());
                   Log.d("INITIALISE API HIT ","API Successful");
                   System.out.println("API Successful  ");
                   System.out.println(response.body().toString());
                   Toast.makeText(MainActivity.this,"API Successful",Toast.LENGTH_SHORT).show();
                   stan=response.body().getF011();
                   invoicenr=response.body().getF062();
                   Log.d("STANANDINVOICE",stan+"\n"+invoicenr);
                   SHFD.saveToPreferences(MainActivity.this, AppConstant.CUST_INVOICE,invoicenr);
                   SHFD.saveToPreferences(MainActivity.this, AppConstant.Cust_STAN,stan);
               } else {
                   System.out.println("API EXCEPTION 1  ");

               }

           }

           @Override
           public void onFailure(Call<InitialiseApiResponse> call, Throwable t) {

           }
       });

    }
    private void KEYEXCHANGEAPI() {
       KeyExchangeRequest keyExchangeRequest = new  KeyExchangeRequest();
        keyExchangeRequest.setMsgType("0800");
        keyExchangeRequest.setF041("10019090");
        keyExchangeRequest.setF042("107113000077460");
        keyExchangeRequest.setF003("940000");

        Gson gson = new Gson();
        yourList = gson.toJson( keyExchangeRequest);
        Log.d("KEYEXCHANGEREQUEST list", "list" + yourList);

        Call<KeyExchangeResponse> responceCall = Apiclient.getApiClient(MainActivity.this).keyExchange(keyExchangeRequest);
        responceCall.enqueue(new Callback<KeyExchangeResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<KeyExchangeResponse> call, Response<KeyExchangeResponse> response) {
                Log.d("KEYEXCHANGE URL", "" + call.request().url().toString());
                if (response.isSuccessful()) {
                    Log.d("KEYEXCHANGE RESPONSE", new Gson().toJson(response.body()));
                    //Log.d("KEYEXCHANGE RESPONSE","\nMSG="+response.body().getMsgType()+"\nF039"+response.body().getF039()+"\nF041="+response.body().getF041()+"\nF048="+response.body().getF048()+"\nF048="+response.body().getF003());
                    Log.d("KEYEXCHANGE API HIT ","API Successful");
                    System.out.println("API Successful  ");
                    System.out.println(response.body().toString());
                    Toast.makeText(MainActivity.this,"API Successful",Toast.LENGTH_SHORT).show();
                    publickey=response.body().getF048();
                    Log.d("PUBLIC KEY",publickey);

                    byte[] data = Base64.decode(publickey, Base64.DEFAULT);
                    try {
                        String text = new String(data, "UTF-8");

                        SHFD.saveToPreferences(MainActivity.this, AppConstant.PUBLICKEY1, text);
                        Log.d("PUBKEY",text);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                } else {
                    System.out.println("KEYEXCHANGE 1  ");

                }

            }

            @Override
            public void onFailure(Call<KeyExchangeResponse> call, Throwable t) {

            }
        });


    }

//    public static PublicKey getKey(String key){
//        try{
//            byte[] byteKey = Base64.encode(key.getBytes(), Base64.DEFAULT);
//            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//
//            return kf.generatePublic(X509publicKey);
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//
//        return null;
//    }
    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(this,MainActivity.class);
//        startActivity(intent);
        finishAffinity();
        
    }


}
