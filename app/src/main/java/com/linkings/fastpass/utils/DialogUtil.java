package com.linkings.fastpass.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Lin on 2016/11/22.
 * Time: 15:52
 * Description: TOO
 */

public class DialogUtil {

    public static void showSingleDialog(Context context, String title, String msg, String positive,
                                        DialogInterface.OnClickListener listener) {
        createSingleDialog(context, title, msg, positive, listener).show();
    }

    public static void showDoubleDialog(Context context, String title, String msg, String positive,
                                        String negative, DialogInterface.OnClickListener positivelistener, DialogInterface.OnClickListener negativelistener) {

        createDoubleDialog(context, title, msg, positive, negative, positivelistener, negativelistener).show();
    }

    public static AlertDialog createSingleDialog(Context context, String title, String msg, String positive,
                                                 DialogInterface.OnClickListener listener) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positive, listener)
                .create();
        return dialog;
    }

    public static AlertDialog createDoubleDialog(Context context, String title, String msg, String positive,
                                                 String negative, DialogInterface.OnClickListener positivelistener, DialogInterface.OnClickListener negativelistener) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positive, positivelistener)
                .setNegativeButton(negative, negativelistener)
                .create();
        return dialog;
    }
}
