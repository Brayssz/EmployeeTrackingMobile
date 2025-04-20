package com.example.employeetracking.utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Messenger {

    public static MaterialAlertDialogBuilder showAlertDialog(
            Context context,
            String title,
            String message,
            String positiveButtonTitle
    ) {
        return new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, (dialog, which) -> {
                });
    }

    public static void showAlertDialogAndRedirect(
            Activity activity,
            String title,
            String message,
            String positiveButtonTitle,
            Class<?> targetActivity // Pass the class of the target activity
    ) {
        new MaterialAlertDialogBuilder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, (dialog, which) -> {
                    // Redirect to the target activity
                    Intent intent = new Intent(activity, targetActivity);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                })
                .show();
    }

    public static MaterialAlertDialogBuilder showAlertDialog(
            Context context,
            String title,
            String message,
            String positive,
            String negative,
            DialogInterface.OnClickListener positiveAction,
            DialogInterface.OnClickListener negativeAction
    ) {
        return new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive, positiveAction)
                .setNegativeButton(negative, negativeAction);
    }
}