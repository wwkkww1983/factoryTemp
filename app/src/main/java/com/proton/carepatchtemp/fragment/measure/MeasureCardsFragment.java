package com.proton.carepatchtemp.fragment.measure;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.databinding.FragmentMeasureCardsBinding;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.wms.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmengsi on 2018/2/28.
 * 测量卡片
 */

public class MeasureCardsFragment extends BaseFragment<FragmentMeasureCardsBinding> {

    /**
     * 正在测量的界面
     */
    private List<Fragment> mMeasuringFragment = new ArrayList<>();
    private OnMeasureFragmentListener onMeasureFragmentListener;

    public static MeasureCardsFragment newInstance() {
        return new MeasureCardsFragment();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_cards;
    }

    @Override
    protected void fragmentInit() {
        initViewPager();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (onMeasureFragmentListener != null) {
                    if (mMeasuringFragment.get(position) instanceof MeasureFragment) {
                        onMeasureFragmentListener.onMeasureStatusChanged(((MeasureFragment) mMeasuringFragment.get(position)).isBeforeMeasure());
                    } else {
                        onMeasureFragmentListener.onMeasureStatusChanged(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initViewPager() {
        if (binding == null) return;
        for (Fragment fragment : mMeasuringFragment) {
            if (fragment instanceof MeasureFragment) {
                //之前添加的全部置为已创建
                ((MeasureFragment) fragment).setIsBeforeMeasure(((MeasureFragment) fragment).isBeforeMeasure());
            }
        }
        FragmentStatePagerAdapter mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mMeasuringFragment.get(position);
            }

            @Override
            public int getCount() {
                return mMeasuringFragment.size();
            }
        };
        binding.idViewpager.setAdapter(mAdapter);
        binding.idViewpager.setOffscreenPageLimit(Integer.MAX_VALUE);
    }

    /**
     * 添加测量界面
     */
    public void addItem(MeasureBean measureBean) {
        Logger.w("添加测量界面isShare:" + measureBean.isShare());
        clearWarmPrefrence(measureBean.getMacaddress());

        //防止重复添加
        for (Fragment fragment : mMeasuringFragment) {
            MeasureBean measureInfo = null;
            if (fragment instanceof MeasureFragment) {
                measureInfo = ((MeasureFragment) fragment).mMeasureInfo;
            }

            if (fragment instanceof BaseMeasureFragment) {
                measureInfo = ((BaseMeasureFragment) fragment).mMeasureInfo;
            }

            if (measureInfo != null && measureInfo.getMacaddress().equalsIgnoreCase(measureBean.getMacaddress())
                    && measureInfo.getProfile().getProfileId() == measureBean.getProfile().getProfileId()) {
                Logger.w("已经添加了该测量:" + measureInfo.getMacaddress() + "," + measureInfo.getProfile().getRealname());
                return;
            }
        }

        if (measureBean.isShare()) {
            MeasureShareItemFragment fragment = MeasureShareItemFragment.newInstance(measureBean);
            fragment.setOnMeasureItemListener(this::removeMeasureItem);
            mMeasuringFragment.add(0, fragment);
            if (onMeasureFragmentListener != null) {
                onMeasureFragmentListener.onMeasureStatusChanged(false);
            }
        } else {
            MeasureFragment fragment = MeasureFragment.newInstance(measureBean);
            fragment.setOnMeasureListener(new MeasureFragment.OnMeasureListener() {
                @Override
                public void closeCard() {
                    removeMeasureItem(fragment);
                }

                @Override
                public void onMeasureStatusChanged(MeasureFragment fragment, boolean isBeforeMeasure) {
                    if (onMeasureFragmentListener != null) {
                        onMeasureFragmentListener.onMeasureStatusChanged(isBeforeMeasure);
                    }
                }
            });
            mMeasuringFragment.add(0, fragment);
        }

        initViewPager();
    }

    private void removeMeasureItem(BaseFragment fragment) {
        if (mMeasuringFragment.contains(fragment)) {
            mMeasuringFragment.remove(fragment);
            if (mMeasuringFragment.size() <= 0) {
                if (onMeasureFragmentListener != null) {
                    onMeasureFragmentListener.onCloseAllMeasure();
                    onMeasureFragmentListener.onMeasureStatusChanged(false);
                }
            }
            initViewPager();
        }
    }

    public void setOnMeasureFragmentListener(OnMeasureFragmentListener onMeasureFragmentListener) {
        this.onMeasureFragmentListener = onMeasureFragmentListener;
    }

    public boolean hasMeasureItem() {
        return mMeasuringFragment.size() > 0;
    }



    protected void clearWarmPrefrence(String macaddress) {
        //低电量提醒
        Logger.w("清除提醒标志:" + macaddress);
        SpUtils.saveLong(Utils.getLowPowerSharedPreferencesKey(macaddress), 0);
        SpUtils.saveLong(Utils.getHighTempWarmSharedPreferencesKey(macaddress), 0);
        SpUtils.saveLong(Utils.getLowTempWarmSharedPreferencesKey(macaddress), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMeasuringFragment.clear();
    }

    @Override
    protected boolean openStat() {
        return false;
    }

    public interface OnMeasureFragmentListener {
        /**
         * 关闭所有的测量
         */
        void onCloseAllMeasure();

        /**
         * 测量状态切换
         */
        void onMeasureStatusChanged(boolean isBeforeMeasure);
    }
}
