package com.example.employeetracking.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.employeetracking.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Loader {

    private Dialog dialog;

    public void showLoader(Context context) {
        View view = View.inflate(context, R.layout.loader, null);
        ImageView imageView = view.findViewById(R.id.img_loader);

        Glide.with(context).load(R.drawable.loader_3).into(imageView);

        dialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(false)
                .create();

        dialog.show();
    }


    public void dismissLoader() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
