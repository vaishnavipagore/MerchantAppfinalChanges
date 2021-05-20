package com.example.merchantapp.network;


import com.example.merchantapp.Initializepojo.InitialiseApiResponse;
import com.example.merchantapp.Initializepojo.InitiliseApiRequest;
import com.example.merchantapp.keyExchangepojo.KeyExchangeRequest;
import com.example.merchantapp.keyExchangepojo.KeyExchangeResponse;
import com.example.merchantapp.lastransactionpojo.LastTransactionRequest;
import com.example.merchantapp.lastransactionpojo.LastTransactionResponse;
import com.example.merchantapp.loginpojo.LoginRequest;
import com.example.merchantapp.loginpojo.LoginResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    @Headers({"Content-Type: application/json","apikey: 12345"})
    @POST("login")
    Call<LoginResponse>login(@Body LoginRequest loginRequest );

    @Headers({"Content-Type: application/json","apikey: 12345"})
    @POST("h2hpayments")
    Call<InitialiseApiResponse>InitialiseAPi(@Body InitiliseApiRequest registrationRequest );


    @Headers({"Content-Type: application/json","apikey: 12345"})
    @POST("h2hpayments")
    Call<KeyExchangeResponse>keyExchange(@Body KeyExchangeRequest keyExchangeRequest );

    @Headers({"Content-Type: application/json","apikey: 12345"})
    @POST("h2hpayments")
    Call<LastTransactionResponse> lastransaction(@Body LastTransactionRequest lastTransactionRequest );

}
