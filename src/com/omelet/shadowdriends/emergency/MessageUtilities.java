package com.omelet.shadowdriends.emergency;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public abstract class MessageUtilities {
    
    public static void confirmUser( Context context, String msg,
                                    DialogInterface.OnClickListener yesClick, 
                                    DialogInterface.OnClickListener noClick){
        if (context==null || msg==null || yesClick==null || noClick==null) return;
        
        Builder alert = new AlertDialog.Builder(context);
        alert.setIcon(android.R.drawable.ic_dialog_alert)
             .setTitle("Confirmation")
             .setMessage(msg)
             .setPositiveButton(    "Yes", yesClick)
             .setNegativeButton("No", noClick)
             .show();
    }
}
