package com.proton.carepatchtemp.activity.report;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivitySomOneMeasureReportBinding;
import com.proton.carepatchtemp.fragment.home.ReportFragment;
import com.proton.carepatchtemp.view.TabCheckedChangeView;
import com.proton.carepatchtemp.viewmodel.MainViewModel;

/**
 * 某份档案下面的测量报告
 * <传入extra>
 * profileId int 档案id
 * </>
 */
public class SomOneMeasureReportActivity extends BaseViewModelActivity<ActivitySomOneMeasureReportBinding, MainViewModel> {


    private ReportFragment allReportFragment;//全部报告片段
    private ReportFragment collcetReportFragment;//收藏报告片

    @Override
    protected void init() {
        super.init();
        long profileId = getIntent().getLongExtra("profileId", -1);
        allReportFragment = new ReportFragment();//全部报告片段
        Bundle allReportBdle = new Bundle();
        allReportBdle.putString("type", "0");
        allReportBdle.putLong("profileId", profileId);
        allReportFragment.setArguments(allReportBdle);
        collcetReportFragment = new ReportFragment();//收藏报告片段
        Bundle collectBdle = new Bundle();
        collectBdle.putString("type", "1");
        collectBdle.putLong("profileId", profileId);
        collcetReportFragment.setArguments(collectBdle);
        binding.idReportViewpager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return allReportFragment;
                    case 1:
                        return collcetReportFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        binding.idReportViewpager.setOffscreenPageLimit(2);
    }

    @Override
    protected void initView() {
        super.initView();
        //测量报告标题文字设定
        binding.idIncludeTopTab.idTabchangeview.setSegmentText(getString(R.string.string_all), 0);
        binding.idIncludeTopTab.idTabchangeview.setSegmentText(getString(R.string.string_report_collect_tip), 1);
        binding.idIncludeTopTab.idTvRightOperate.setText(getString(R.string.string_edit));
        binding.idIncludeTopTab.idTvRightOperate.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        super.setListener();
        //报告列表头部tab点击切换监听
        binding.idIncludeTopTab.idTabchangeview.setOnSegmentViewClickListener(new TabCheckedChangeView.onSegmentViewClickListener() {
            @Override
            public void onSegmentViewClick(View v, int position) {
                switch (position) {
                    case 0:
                        binding.idReportViewpager.setCurrentItem(0);
                        break;
                    case 1:
                        binding.idReportViewpager.setCurrentItem(1);
                        break;
                }
            }
        });
        binding.idIncludeTopTab.idTvRightOperate.setOnClickListener(v -> {
            String operateStr = ((TextView) v).getText().toString();
            if (operateStr.equals(App.get().getResources().getString(R.string.string_edit))) {
                //编辑报告
                binding.idIncludeTopTab.idTvRightOperate.setText(R.string.string_cancel);
                //测量报告编辑
                viewmodel.isEditMeasureReport.set(true);
                allReportFragment.editOrCancel(true);
                collcetReportFragment.editOrCancel(true);
            } else {
                //取消编辑
                binding.idIncludeTopTab.idTvRightOperate.setText(R.string.string_edit);
                //测量报告取消编辑
                viewmodel.isEditMeasureReport.set(false);
                allReportFragment.editOrCancel(false);
                collcetReportFragment.editOrCancel(false);
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        binding.idReportViewpager.setCurrentItem(0);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_som_one_measure_report;
    }

    @Override
    protected MainViewModel getViewModel() {
        return ViewModelProviders.of(this).get(MainViewModel.class);
    }
}
