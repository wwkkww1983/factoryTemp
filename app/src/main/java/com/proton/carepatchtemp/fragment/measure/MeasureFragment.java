package com.proton.carepatchtemp.fragment.measure;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.databinding.FragmentMeasureBinding;
import com.proton.carepatchtemp.fragment.base.BaseFragment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangmengsi on 2018/2/28.
 * 单个测量卡片，包含测量准备和测量界面
 */

public class MeasureFragment extends BaseFragment<FragmentMeasureBinding> {
    public MeasureBean mMeasureInfo;
    private BeforeMeasureFragment mBeforeMeasureFragment;
    private MeasureItemFragment mMeasuringItemFragment;
    private boolean isBeforeMeasure = true;
    private BaseMeasureFragment mCurrentFragment;
    private OnMeasureListener onMeasureListener;

    public static MeasureFragment newInstance(MeasureBean measureBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("measureInfo", measureBean);
        MeasureFragment fragment = new MeasureFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure;
    }

    @Override
    protected void fragmentInit() {
        mMeasureInfo = (MeasureBean) getArguments().getSerializable("measureInfo");
        if (isBeforeMeasure) {
            showBeforeMeasure(mMeasureInfo);
        } else {
            showMeasuring(mMeasureInfo);
        }

    }

    private void showBeforeMeasure(MeasureBean measureBean) {
        if (mBeforeMeasureFragment == null) {
            mBeforeMeasureFragment = BeforeMeasureFragment.newInstance(measureBean);
        }
        if (measureBean != null) {
            mBeforeMeasureFragment.setMeasureInfo(measureBean);
            mBeforeMeasureFragment.setOnBeforeMeasureListener(new BeforeMeasureFragment.OnBeforeMeasureListener() {
                @Override
                public void onGoToMeasure(MeasureBean measureBean) {
                    showMeasuring(measureBean);
                }

                @Override
                public void closeCard() {
                    if (onMeasureListener != null) {
                        onMeasureListener.closeCard();
                    }
                }
            });
        }
        showFragment(mBeforeMeasureFragment);
    }

    /**
     * 显示正在测量界面
     */
    public void showMeasuring(MeasureBean measureBean) {
        if (mMeasuringItemFragment == null) {
            mMeasuringItemFragment = MeasureItemFragment.newInstance(measureBean);
            mMeasuringItemFragment.setOnMeasureItemListener(new MeasureItemFragment.OnMeasureItemListener() {
                @Override
                public void closeCard(MeasureItemFragment fragment) {
                    if (onMeasureListener != null) {
                        onMeasureListener.closeCard();
                    }
                }

                @Override
                public void remeasure(MeasureBean measureBean) {
                    showBeforeMeasure(measureBean);
                }
            });
        }
        showFragment(mMeasuringItemFragment);
    }

    /**
     * 显示fragment
     */
    private void showFragment(BaseMeasureFragment fragment) {
        if (fragment == null || fragment.isDetached()) return;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.card_in,
                R.anim.card_out,
                R.anim.card_in,
                R.anim.card_out
        );
        transaction.replace(R.id.id_measure_container, fragment);
        transaction.commitAllowingStateLoss();
        mCurrentFragment = fragment;

        if (isVisible) {
            if (onMeasureListener != null) {
                onMeasureListener.onMeasureStatusChanged(this, isBeforeMeasure());
            }
        }
    }

    public void setIsBeforeMeasure(boolean isBeforeMeasure) {
        this.isBeforeMeasure = isBeforeMeasure;
    }

    public void closeCard() {
        if (mCurrentFragment != null) {
            mCurrentFragment.closeCardOnly();
        }
    }

    /**
     * 当前是否是测量准备
     */
    public boolean isBeforeMeasure() {
        return mCurrentFragment == null || mCurrentFragment instanceof BeforeMeasureFragment;
    }

    public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    @Override
    protected boolean openStat() {
        return false;
    }

    public interface OnMeasureListener {
        /**
         * 停止当前测量
         */
        void closeCard();

        /**
         * 测量状态切换
         */
        void onMeasureStatusChanged(MeasureFragment fragment, boolean isBeforeMeasure);
    }
}
