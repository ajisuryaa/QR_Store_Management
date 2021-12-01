package com.putrabatam.qrstore.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class PopUpMessage {

    public void login_gagal(String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Gagal masuk")
                .setMessage(message)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void validation_error(String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Kesalahan")
                .setMessage(message)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
