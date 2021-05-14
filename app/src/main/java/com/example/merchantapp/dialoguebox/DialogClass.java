package com.example.merchantapp.dialoguebox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogClass {
    public static void showSuccessDialog(String msg, final Context context) {


        new AlertDialog.Builder(context)
                .setTitle("SUCCESS")
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {


                    }
                }).create().show();


    }

    public static void showAlertDialog(Context context) {


        new AlertDialog.Builder(context)
                .setTitle("Connection Problem")
                .setMessage("Something Went Wrong")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {


                    }
                }).create().show();
    }

    public static void showExistDialog(Context context) {


        new AlertDialog.Builder(context)
                .setTitle("Already Exist")
                .setMessage("This Number is Already Exist")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {


                    }
                }).create().show();
    }

    public static void showNotExistDialog(Context context) {


        new AlertDialog.Builder(context)
                .setTitle("Not Exist")
                .setMessage("This Number is Not Registered")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {


                    }
                }).create().show();

    }

    public static void showWarningDialog(String title, String message, Context context) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {


                    }
                }).create().show();


    }

}
