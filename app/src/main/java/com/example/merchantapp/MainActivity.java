package com.example.merchantapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.merchantapp.dialoguebox.DialogClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.example.merchantapp.rootutil.Rootutil.isDeviceRooted;

public class MainActivity extends AppCompatActivity {

    EditText username,password;
    private RequestQueue mQueue;
    String usr,pass;
    Button btn;
    boolean isToken = false;
    String token;
    ProgressDialog dialog;
    String merchantName;

    Executor executor;
    BiometricManager biometricManager;


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
                //Toast.makeText(getApplicationContext(),"Http call",Toast.LENGTH_LONG).show();

                dialog = ProgressDialog.show(MainActivity.this, "", "Loading...", true);

                jsonParse();


            }

        });

    }

    private void jsonParse () {

        final String loginusername = username.getText().toString();
        String loginpassword = password.getText().toString();
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("username",loginusername);
        postParam.put("password", loginpassword);
        if(username.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Username or Password cannot be blank",Toast.LENGTH_LONG).show();
        }

        String url = "https://europa-sandbox.perseuspay.com/mportal/v1/login";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(postParam), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("URLAPI",url+" :;  "+ response.toString());
                try {

                    Log.d("RESPONEEEE",response.toString());

                    String status =  response.getString("status");
                    if(!status.equals("")){
                        dialog.dismiss();

                    }
                    Log.d("Status",status);
                    token = response.getString("token");

                    Log.d("Token",token);

                    isToken = true;
                    merchantName = response.getString("owner_name");

                    if(status.equals("") && token.equals("")){
                        Toast.makeText(getApplication(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                    }
                    else
                    { isToken = true;

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

                            }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

                //error.printStackTrace();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    Toast.makeText(getApplicationContext(),"Invalid Username or Password",Toast.LENGTH_LONG).show();
                }
                if (networkResponse != null && networkResponse.statusCode == 400) {
                    Toast.makeText(getApplicationContext(),"Invalid Username or Password",Toast.LENGTH_LONG).show();
                }
                if (networkResponse != null && networkResponse.statusCode == 502) {
                    Toast.makeText(getApplicationContext(),"Could not validate UserName/Password. Call HelpDesk",Toast.LENGTH_LONG).show();
                }
                if (networkResponse != null && networkResponse.statusCode == 503) {
                    Toast.makeText(getApplicationContext(),"Could not validate UserName/Password. Call HelpDesk",Toast.LENGTH_LONG).show();
                }
                if (networkResponse != null && networkResponse.statusCode == 404) {
                    Toast.makeText(getApplicationContext(),"Could not validate UserName/Password. Call HelpDesk",Toast.LENGTH_LONG).show();
                }
                error.getMessage();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("apikey","12345");
                return headers;
            }
        };
        mQueue.add(request);

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
                i.putExtra("token",token);
                i.putExtra("username",username.getText().toString());
                i.putExtra("ownername",merchantName);
                i.putExtra("fcmToken",token);
                username.setText("");
                password.setText("");
                startActivity(i);            }

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
            System.out.println("get eroorr   findd 111 ");

            switch (biometricManager.canAuthenticate())
            {
                case  BiometricManager.BIOMETRIC_SUCCESS: authuser(executor); break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE : goToMainPage(); break;

                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE :  goToMainPage(); break;
                case  BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED :goToMainPage(); break;

            }}else{
            System.out.println("get eroorr   findd 222 ");

            Intent i = new Intent(MainActivity.this,HomeActivity.class);
            i.putExtra("token",token);
            i.putExtra("username",username.getText().toString());
            i.putExtra("ownername",merchantName);
            i.putExtra("fcmToken",token);

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


    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(this,MainActivity.class);
//        startActivity(intent);
        finishAffinity();
        
    }


}
