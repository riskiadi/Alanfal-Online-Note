package com.alkalynx.alanfal.alanfal;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;


/**
 * Created by SaputraPC on 20/11/2017.
 */

public class Alert {

    public Alert(Boolean cancelable, String title, String msg, Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setCancelable(cancelable);
        builder.setTitle(title);
        builder.setMessage(msg);

        if (cancelable) {

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

        } else {

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

        }

        builder.show();

    }


}
