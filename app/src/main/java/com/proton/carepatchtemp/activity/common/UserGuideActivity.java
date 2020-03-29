package com.proton.carepatchtemp.activity.common;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.component.AliyunService;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.databinding.ActivityUserGuideBinding;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

public class UserGuideActivity extends BaseActivity<ActivityUserGuideBinding> {

    List<View> mViewList;

    @Override
    protected void init() {
        super.init();
        mViewList = new ArrayList<>();
        View view1 = getLayoutInflater().inflate(R.layout.layout_guide_1_profile, null);
        View view2 = getLayoutInflater().inflate(R.layout.layout_guide_2_monitor, null);
        View view3 = getLayoutInflater().inflate(R.layout.layout_guide_3_warm, null);
        View view4 = getLayoutInflater().inflate(R.layout.layout_guide_4_share, null);
        view4.setOnClickListener(v -> goToMain());
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        mViewList.add(view4);
    }

    private void goToMain() {
        if (!App.get().isLogined()) {
            IntentUtils.goToLoginFirst(mContext);
        } else {
            IntentUtils.goToMain(mContext);
            //开启服务
            //开启阿里云服务
            IntentUtils.startAliyunService(this);
        }
        SpUtils.saveBoolean(AppConfigs.SP_KEY_SHOW_GUIDE, false);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idViewpager.setAdapter(new ViewPagerAdatper(mViewList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewList.clear();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_user_guide;
    }

    static class ViewPagerAdatper extends PagerAdapter {
        private List<View> mViewList;

        ViewPagerAdatper(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }
}