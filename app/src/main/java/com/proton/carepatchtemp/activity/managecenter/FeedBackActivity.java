package com.proton.carepatchtemp.activity.managecenter;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.Observable;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityFeedBackBinding;
import com.proton.carepatchtemp.viewmodel.managecenter.FeedBackViewModel;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseViewModelActivity<ActivityFeedBackBinding, FeedBackViewModel> implements View.OnClickListener {

    @Override
    protected int inflateContentView() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void init() {
        super.init();
        binding.setViewModel(viewmodel);
        binding.setViewClickListener(this);
    }

    @Override
    protected void setListener() {
        super.setListener();
        viewmodel.isBeautyChecked.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (viewmodel.isBeautyChecked.get()) {
                    //选中
                    binding.idIvNoBeauty.setImageResource(R.drawable.icon_rb_checked);
                } else {
                    binding.idIvNoBeauty.setImageResource(R.drawable.icon_rb_unchecked);
                }
            }
        });
        viewmodel.isHaveAppBug.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (viewmodel.isHaveAppBug.get()) {
                    //选中
                    binding.idIvHaveBug.setImageResource(R.drawable.icon_rb_checked);
                } else {
                    binding.idIvHaveBug.setImageResource(R.drawable.icon_rb_unchecked);
                }
            }
        });
        viewmodel.isSatisfyProduct.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (viewmodel.isSatisfyProduct.get()) {
                    //选中
                    binding.idIvUnsatisfyProduct.setImageResource(R.drawable.icon_rb_checked);
                } else {
                    binding.idIvUnsatisfyProduct.setImageResource(R.drawable.icon_rb_unchecked
                    );
                }
            }
        });
    }

    @Override
    protected FeedBackViewModel getViewModel() {
        return ViewModelProviders.of(this).get(FeedBackViewModel.class);
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_feedback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_lay_beauty:
                viewmodel.isBeautyChecked.set(viewmodel.isBeautyChecked.get() != true);
                break;
            case R.id.id_lay_app_bug:
                viewmodel.isHaveAppBug.set(viewmodel.isHaveAppBug.get() != true);
                break;
            case R.id.id_lay_unsatisfy_product:
                viewmodel.isSatisfyProduct.set(viewmodel.isSatisfyProduct.get() != true);
                break;
        }
    }
}
