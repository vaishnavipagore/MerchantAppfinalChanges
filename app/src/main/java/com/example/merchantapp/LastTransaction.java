package com.example.merchantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.merchantapp.Initializepojo.InitialiseApiResponse;
import com.example.merchantapp.Initializepojo.InitiliseApiRequest;
import com.example.merchantapp.SHFD.AppConstant;
import com.example.merchantapp.SHFD.SHFD;
import com.example.merchantapp.lastransactionpojo.LastTransactionRequest;
import com.example.merchantapp.lastransactionpojo.LastTransactionResponse;
import com.example.merchantapp.network.Apiclient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Float.parseFloat;

public class LastTransaction extends AppCompatActivity {

    private TextView mTextViewResults, tw, mTextView, Amount, Authcode, Cardtype, Batch, Card, MID, PAN, Product, RRN, TID, TransactionID;
    private Button button;
    private RequestQueue mQueue;
    String MyToken,UserId;
    ProgressDialog dialog;
    String strmid,strtid,strinvoicenumb;
    String yourList="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_transaction);
        //        Intent intent = getIntent();
//        MyToken = intent.getStringExtra("token1");
//        UserId = intent.getStringExtra("userid1");
//
//        Log.d("Username is",UserId);
//        Log.d("Token is ",MyToken);
        dialog = ProgressDialog.show(LastTransaction.this, "", "Loading...", true);
        mQueue = Volley.newRequestQueue(this);
        Amount = findViewById(R.id.amount);

        Cardtype = findViewById(R.id.cardtype);
        Authcode = findViewById(R.id.authcode);
        Batch = findViewById(R.id.batchno);
        Card = findViewById(R.id.card);
        MID = findViewById(R.id.mid);
        PAN = findViewById(R.id.pan);
        Product = findViewById(R.id.product);
        RRN = findViewById(R.id.rrn);
        TID = findViewById(R.id.tid);
        TransactionID = findViewById(R.id.transaction);
// button = findViewById(R.id.btn);
// button.setText("<");
// jsonParse();



        strmid = SHFD.readFromPreferences(LastTransaction.this, AppConstant.Cust_MID, "");
        strtid = SHFD.readFromPreferences(LastTransaction.this, AppConstant.CUST_TID, "");
        strinvoicenumb=SHFD.readFromPreferences(LastTransaction.this, AppConstant.CUST_INVOICE, "");
        LastTransaction();

    }


//    private void jsonParse () {
//        String url = "https://europa-sandbox.perseuspay.com/mportal/v1/lasttransactionstatus";
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//
//                try {
//
//                    JSONArray LTS = response.getJSONArray("ltsdata");
//                    Log.d("LTS", String.valueOf(LTS));
//
//                    System.out.println("getResponse :: 1"+ LTS);
//
//
//                    dialog.dismiss();
//                    //  Log.d("Length is ", String.valueOf(n));
//
//                    String s = response.getJSONArray("ltsdata").getString(0);
//                    String auth = new JSONObject(s).getString("authcode");
//                    String cardtype = new JSONObject(s).getString("cardtype");
//                    Cardtype.append(cardtype);
//                    String amount = new JSONObject(s).getString("amount");
//                    Float amount1 = parseFloat(amount) /100;
//
//                    Amount.append(amount1.toString());
//
//                    String authcode = new JSONObject(s).getString("authcode");
//                    if(authcode.equals("")){
//                        authcode = "-";
//                    }
//                    Authcode.append(authcode);
//                    String batch = new JSONObject(s).getString("batchno");
//                    Batch.append(batch);
//
//                    Card.append(cardtype);
//                    String mid = new JSONObject(s).getString("mid");
//                    MID.append(mid);
//
//                    String pan = new JSONObject(s).getString("pan");
//                    PAN.append(pan);
//                    String product = new JSONObject(s).getString("producttype");
//                    Product.append(product);
//                    String rrn = new JSONObject(s).getString("rrn");
//                    RRN.append(rrn);
//                    String tid = new JSONObject(s).getString("tid");
//                    TID.append(tid);
//                    String transaction = new JSONObject(s).getString("transactionid");
//                    TransactionID.append(transaction);
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    System.out.println("getResponse :: 2"+ e.getMessage());
//
//
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//
//            public void onErrorResponse(VolleyError error) {
//
//                System.out.println("getResponse :: 3"+ error.getLocalizedMessage());
//                System.out.println("getResponse :: 3"+ error);
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                headers.put("token", MyToken);
//                headers.put("apikey", "perseus");
//                headers.put("userid", UserId);
//                System.out.println("getResponsew Params Last: "+ headers);
//                return headers;
//            }
//        };
//        mQueue.add(request);
//
//    }



    private void LastTransaction() {

       LastTransactionRequest lastTransactionRequest = new LastTransactionRequest();
        lastTransactionRequest.setMsgType("0620");
        lastTransactionRequest.setF041(strtid);
        lastTransactionRequest.setF042(strmid);
        lastTransactionRequest.setF003("990000");
        lastTransactionRequest.setF011("000002");
        lastTransactionRequest.setF062(strinvoicenumb);
        Gson gson = new Gson();
        yourList = gson.toJson(lastTransactionRequest);
        Log.d("LastTransactionREQUEST", "list" + yourList);

        Call<LastTransactionResponse> responceCall = Apiclient.getApiClient(LastTransaction.this).lastransaction(lastTransactionRequest);
        responceCall.enqueue(new Callback<LastTransactionResponse>() {
            @Override
            public void onResponse(Call<LastTransactionResponse> call, Response<LastTransactionResponse> response) {
                Log.d("LastTransaction URL", "" + call.request().url().toString());
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Log.d("LastTransactionRESPONSE", new Gson().toJson(response.body()));
                    Log.d("LastTransactionAPIHIT ","API Successful");
                    System.out.println("API Successful");
                    System.out.println(response.body().toString());
                    if(response.body().getF039().equals("25"))
                    {
                        System.out.println("GOT 25 in F039");
                    }
                    else
                        {
                        TID.setText(response.body().getF041());
                        MID.setText(response.body().getF042());
                       }

                    Toast.makeText(LastTransaction.this,"API Successful",Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("API EXCEPTION 1  ");
                }

            }

            @Override
            public void onFailure(Call<LastTransactionResponse> call, Throwable t) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(LastTransaction.this,HomeActivity.class);
        startActivity(i);
    }
}
