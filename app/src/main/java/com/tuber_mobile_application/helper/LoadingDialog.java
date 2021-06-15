package com.tuber_mobile_application.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.tuber_mobile_application.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startAnimationLoader() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loader, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void stopAnimationLoader(){
        alertDialog.dismiss();
    }

}
