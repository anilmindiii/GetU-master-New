package getu.app.com.getu.hepler;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import getu.app.com.getu.R;

/**
 * Created by abc on 30/12/2017.
 */

public class CusDialogProg {

    public static void myDialog(Context context, Dialog pDialog) {
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setCancelable(false);
        pDialog.setContentView(R.layout.dialogue_layout);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //pDialog.show();
    }

}
