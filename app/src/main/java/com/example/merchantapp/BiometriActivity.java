package com.example.merchantapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.Executor;

public class BiometriActivity extends AppCompatActivity {
    Executor executor;
    BiometricManager biometricManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometri);
        executor = ContextCompat.getMainExecutor(this);

        biometricManager = BiometricManager.from(this);
        redirectToScreen();
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
                startActivity(new Intent(BiometriActivity.this, HomeActivity.class));
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
            switch (biometricManager.canAuthenticate())
            {
                case  BiometricManager.BIOMETRIC_SUCCESS: authuser(executor); break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE : break;

                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE :  ;break;
                case  BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED :

            }}



    }
}