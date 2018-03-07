package com.example.iman.gtk.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.iman.gtk.R;

/**
 * Created by Iman on 28/02/2018.
 */

public class DialogUtil {
    public static AlertDialog showAlertDialog(Context context, String message, String title) {
        AlertDialog alertDialog = createAlertDialog(context, message, title, false).create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog.Builder createAlertDialog(Context context, String message, String title, boolean linkify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                //.setIconAttribute(R.attr.alert_dialog_icon)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        if (linkify) {
            // Linkify the text in case a URL is included in the message.
            SpannableString messageContent = new SpannableString(message);
            Linkify.addLinks(messageContent, Linkify.WEB_URLS);

            // Assign a custom TextView which allows the hyperlink to work.
            LayoutInflater inflater = LayoutInflater.from(context);
            final TextView messageTextView = (TextView) inflater.inflate(R.layout.dialog_message, null);
            messageTextView.setText(messageContent);
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

            builder.setView(messageTextView);

        } else {
            builder.setMessage(message);

        }

        if (title != null) {
            builder.setTitle(title);
        }

        return builder;
    }
}
