package com.proton.carepatchtemp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.view.OnItemChildClickListener;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by luochune on 2018/3/21.
 */

public class ProfileListAdapter extends CommonAdapter<ProfileBean> {

    private OnItemChildClickListener<ProfileBean> onItemChildClickListener;

    public ProfileListAdapter(Context context, List<ProfileBean> datas, int layoutId, OnItemChildClickListener<ProfileBean> onItemChildClickListener) {
        super(context, datas, layoutId);
        this.onItemChildClickListener = onItemChildClickListener;
    }

    @Override
    public void convert(CommonViewHolder holder, ProfileBean profileBean) {
        if (profileBean == null) {
            return;
        }
        //头像
        ((SimpleDraweeView) holder.getView(R.id.id_profile_sdv_avatar)).setImageURI(profileBean.getAvatar());
        //用户名
        ((TextView) holder.getView(R.id.id_tv_profile_name)).setText(profileBean.getRealname());
        //年龄
        String birthDay = profileBean.getBirthday();
        if (!TextUtils.isEmpty(birthDay)) {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date birthDate = dateformat.parse(birthDay);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(birthDate);
                int birthYear = calendar.get(Calendar.YEAR);
                //获得当前年份
                calendar.setTime(new Date());
                int currentYear = calendar.get(Calendar.YEAR);
                int age = currentYear - birthYear;
                if (age >= 0) {
                    holder.setText(R.id.id_tv_profile_age, mContext.getResources().getQuantityString(R.plurals.string_sui, age, age));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //性别
        ImageView genderIv = holder.getView(R.id.id_iv_profile_sex);
        int genderInteger = profileBean.getGender();
        if (1 == genderInteger) {
            genderIv.setImageResource(R.drawable.icon_profile_boy);
        } else {
            genderIv.setImageResource(R.drawable.icon_profile_girl);
        }
        /*TODO older.mTvEditProfile.setOnClickListener(view -> {
                    Intent goEdit = new Intent(mContext, ProfileEditActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Extras.PRF_EDIT, mProfiles.get(position));
                    goEdit.putExtras(bundle);
                    mContext.startActivity(goEdit);
                });*/
               /*TODO 报告 holder.mTvProfileReport.setOnClickListener(view -> {
                    Intent goReport = new Intent(mContext, PrfReprotActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Extras.PROFILE, String.valueOf(mProfiles.get(position).getId()));
                    goReport.putExtras(bundle);
                    mContext.startActivity(goReport);

                });*/
        //编辑报告
        holder.getView(R.id.id_lay_profile_edit).setOnClickListener(v ->
        {
            if (onItemChildClickListener != null) {
                onItemChildClickListener.onChildClick(holder, profileBean, v);
            }
        });
        //查看报告
        holder.getView(R.id.id_lay_profile_report).setOnClickListener(v -> {
            if (onItemChildClickListener != null) {
                onItemChildClickListener.onChildClick(holder, profileBean, v);
            }
        });
        //档案删除
        holder.getView(R.id.id_lay_profile_delete).setOnClickListener(v ->
        {
            if (onItemChildClickListener != null) {
                onItemChildClickListener.onChildClick(holder, profileBean, v);
            }
        });
               /*TODO 删除 holder.mTvDeleteProfile.setOnClickListener(view -> {

                    // reuse previous dialog instance, keep widget user state, reset them if you need
// or you can new a SweetAlertDialog to show
*//* sDialog.dismiss();
 new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
         .setTitleText("Cancelled!")
         .setContentText("Your imaginary file is safe :)")
         .setConfirmText("OK")
         .show();*//*
                    new SweetAlertDialog(mContext, Type.WARNING_TYPE)
                            .setTitleText(mContext.getString(R.string.string_confirm_delete))
                            .setContentText(mContext.getString(R.string.string_delete_profile_msg))
                            .setCancelText(mContext.getString(R.string.string_cancel))
                            .setConfirmText(mContext.getString(R.string.string_confirm))
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                for (RealData rti : TestManager.container.values()
                                        ) {
                                    if (rti.getProfile().getId().longValue() == mProfiles.get(position).getId().longValue()) {
                                        ToastUtils.w(R.string.string_is_measureing_can_not_delete);
                                        return;
                                    }
                                }
                                PrfCenter.delete(String.valueOf(mProfiles.get(position).getId()));
                                mProfiles.remove(position);
                                notifyDataSetChanged();
                                sDialog.setTitleText(mContext.getString(R.string.string_deleted))
                                        .setContentText(mContext.getString(R.string.string_select_profile_delete_success))
                                        .setConfirmText(mContext.getString(R.string.string_confirm))
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(Type.SUCCESS_TYPE);
                            })
                            .show();
                });*/
    }
}
