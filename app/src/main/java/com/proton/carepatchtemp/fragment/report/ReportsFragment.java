package com.proton.carepatchtemp.fragment.report;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.FragmentReportsBinding;
import com.proton.carepatchtemp.fragment.base.BaseViewModelFragment;
import com.proton.carepatchtemp.fragment.home.ReportFragment;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.report.ReportViewModel;

/**
 * Created by luochune on 2018/3/19.
 * 报告列表"全部"和"已收藏"tab对应的fragment
 */

public class ReportsFragment extends BaseViewModelFragment<FragmentReportsBinding, ReportViewModel> {

    private ReportFragment allReportFragment;//全部报告片段
    private ReportFragment collcetReportFragment;//收藏报告片
    private OnReportsContainerListener onReportsContainerListener;

    public static ReportsFragment newInstance() {
        return new ReportsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mInflatedView != null) {
            ViewGroup parent = (ViewGroup) mInflatedView.getParent();
            if (parent != null) {
                parent.removeView(mInflatedView);
            }
            setLoadSuccess();
            return mInflatedView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_reports;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        allReportFragment = new ReportFragment();//全部报告片段
        Bundle allReportBdle = new Bundle();
        allReportBdle.putString("type", "0");
        allReportFragment.setArguments(allReportBdle);
        collcetReportFragment = new ReportFragment();//收藏报告片段
        Bundle collectBdle = new Bundle();
        collectBdle.putString("type", "1");
        collcetReportFragment.setArguments(collectBdle);
        binding.idReportViewpager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
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
    protected ReportViewModel getViewModel() {
        return ViewModelProviders.of(this).get(ReportViewModel.class);
    }

    public void changeReportFragment(int tabNum) {
        if (tabNum == 0) {
            binding.idReportViewpager.setCurrentItem(0);
        } else if (tabNum == 1) {
            binding.idReportViewpager.setCurrentItem(1);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        //测量报告标题文字设定
        binding.idIncludeTop.idTabchangeview.setSegmentText(getString(R.string.string_all), 0);
        binding.idIncludeTop.idTabchangeview.setSegmentText(getString(R.string.string_report_collect_tip), 1);
        binding.idIncludeTop.idTopRight.setText(getActivity().getResources().getString(R.string.string_edit));
        binding.idIncludeTop.idTopRight.setVisibility(View.VISIBLE);
        Utils.notLoginViewHide(binding.idIncludeTop.idTopRight);
        setListener();
    }

    private void setListener() {
        //报告列表头部tab点击切换监听
        binding.idIncludeTop.idTabchangeview.setOnSegmentViewClickListener((v, position) -> {
            switch (position) {
                case 0:
                    binding.idReportViewpager.setCurrentItem(0);
                    break;
                case 1:
                    binding.idReportViewpager.setCurrentItem(1);
                    break;
            }
        });
        binding.idIncludeTop.idTopRight.setOnClickListener(v -> {
            String operateStr = ((TextView) v).getText().toString();
            if (operateStr.equals(App.get().getResources().getString(R.string.string_edit))) {
                //编辑报告
                binding.idIncludeTop.idTopRight.setText(getActivity().getResources().getString(R.string.string_cancel));
                //测量报告编辑
                viewmodel.isEditMeasureReport.set(true);
                allReportFragment.editOrCancel(true);
                collcetReportFragment.editOrCancel(true);
            } else {
                //取消编辑
                binding.idIncludeTop.idTopRight.setText(getActivity().getResources().getString(R.string.string_edit));
                //测量报告取消编辑
                viewmodel.isEditMeasureReport.set(false);
                allReportFragment.editOrCancel(false);
                collcetReportFragment.editOrCancel(false);
            }
        });
        //监听左上角开关
        binding.idIncludeTop.idToogleDrawer.setOnClickListener(v -> {
            if (onReportsContainerListener != null) {
                onReportsContainerListener.onToggleDrawer();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        binding.idReportViewpager.setCurrentItem(0);
    }

    //设置回调到MainActiivty操作
    public void setOnReportsContainerListener(OnReportsContainerListener onReportsContainerListener) {
        this.onReportsContainerListener = onReportsContainerListener;
    }

    public interface OnReportsContainerListener {

        /**
         * 打开关闭drawer
         */
        void onToggleDrawer();
    }
}
