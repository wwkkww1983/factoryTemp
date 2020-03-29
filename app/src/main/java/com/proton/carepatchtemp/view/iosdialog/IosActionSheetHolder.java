/*
package com.proton.carepatchtemp.view.iosdialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.utils.DensityUtils;


*/
/**
 * Created by Administrator on 2016/10/9 0009.
 *//*

public class IosActionSheetHolder {
    public ListView lv;
    public View rootView;
    protected Button btnBottom;

    public IosActionSheetHolder(Context context) {
        rootView = View.inflate(context, setLayoutRes(), null);
        findViews();
    }


    protected void findViews() {
        lv = (ListView) rootView.findViewById(R.id.lv);

        lv.setDivider(new ColorDrawable(lv.getResources().getColor(R.color.color_gray_f0)));
        lv.setDividerHeight(2);
        btnBottom = (Button) rootView.findViewById(R.id.btn_bottom);
    }

    protected int setLayoutRes() {
        return R.layout.dialog_ios_alert_bottom;
    }


    public void assingDatasAndEvents(final Context context, final IosDialogBean bean) {
        if (TextUtils.isEmpty(bean.bottomTxt)) {
            btnBottom.setVisibility(View.GONE);
        } else {
            btnBottom.setVisibility(View.VISIBLE);
            btnBottom.setText(bean.bottomTxt);
            btnBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IosDialog.dismiss(bean.dialog);
                    bean.itemListener.onBottomBtnClick();

                }
            });
        }


        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return bean.wordsIos.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                RelativeLayout root = (RelativeLayout) View.inflate(context, R.layout.item_btn_bottomalert, null);
                Button view = (Button) root.findViewById(R.id.btn);
                //第一个位置默认是标题
                if (position == 0) {
                    view.setTextSize(13);
                    view.setTextColor(Color.parseColor("#8c8c8c"));
                } else if (getCount() < 3 && position == 1) {
                    view.setTextColor(Color.parseColor("#FB4342"));
                }
                */
/*if (getCount() >=2){
                    if (position ==0){
                        view.setBackgroundResource(R.drawable.selector_btn_press_all_top);
                    }else if (position == getCount() -1){
                        view.setBackgroundResource(R.drawable.selector_btn_press_all_bottom);
                    }else {
                        view.setBackgroundResource(R.drawable.selector_btn_press_no_corner);
                    }

                }else {
                    view.setBackgroundResource(R.drawable.selector_btn_press_all);
                }*//*


                view.setText(bean.wordsIos.get(position));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IosDialog.dismiss(bean.dialog);
                        bean.itemListener.onItemClick(bean.wordsIos.get(position), position);

                    }
                });

                return root;
            }
        };

        lv.setAdapter(adapter);
    }


}
*/
