/*
package com.proton.carepatchtemp.view.iosdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;

import com.proton.carepatchtemp.R;

import java.util.List;

*/
/**
 * Created by luochune on 2018/3/22.
 *//*


public class IosDialog {

    public IosDialog() {
        super();
    }

    private static Dialog newIntance(IosDialogBean iosDialogBean, Context mContext) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        IosActionSheetHolder holder = new IosActionSheetHolder(mContext);
        dialog.setContentView(holder.rootView);
        holder.assingDatasAndEvents(mContext, iosDialogBean);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.mystyle);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(iosDialogBean.canceledOnTouchOutside);
        iosDialogBean.dialog = dialog;
        return dialog;
    }

    public static void dismiss(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static class Builder {
        public String msg;
        public int type;
        public MyItemDialogListener itemListener;
        public List<String> wordsIos;
        public String bottomTxt = "取消";
        private IosDialogBean iosDialogBean;

        public Builder() {
            iosDialogBean = new IosDialogBean();
        }

        public Builder setMsg(String msg) {
            iosDialogBean.msg = msg;
            return this;
        }

        public Builder setItemDialogListener(MyItemDialogListener itemDialogListener) {
            iosDialogBean.itemListener = itemDialogListener;
            return this;
        }

        public Builder setListWords(List<String> wordsList) {
            iosDialogBean.wordsIos = wordsList;
            return this;
        }

        public Builder setBottomTxt(String bottomTxt) {
            iosDialogBean.bottomTxt = bottomTxt;
            return this;
        }

        public Dialog build(Context mContext) {
            return newIntance(iosDialogBean, mContext);
        }
        //private void
    }

}
*/
