package com.proton.carepatchtemp.fragment.measure;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.databinding.FragmentMeasureContainerBinding;
import com.proton.carepatchtemp.factory.activity.SelectErrorActivity;
import com.proton.carepatchtemp.factory.constants.Constant;
import com.proton.carepatchtemp.factory.fragment.CalibrateFragment;
import com.proton.carepatchtemp.factory.fragment.FactoryMeasureFragment;
import com.proton.carepatchtemp.factory.fragment.FactoryScanFragment;
import com.proton.carepatchtemp.factory.fragment.QualificationFragment;
import com.proton.carepatchtemp.factory.utils.CalibrateBeanDao;
import com.proton.carepatchtemp.factory.view.SelectTempDialogFragment;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.fragment.base.BaseLazyFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangmengsi on 2018/2/28.
 * 测量界面容器
 */

public class MeasureContainerFragment extends BaseLazyFragment<FragmentMeasureContainerBinding> {

    private MeasureChooseProfileFragment mChooseProfileFragment;
    private MeasureScanDeviceFragment mScanDeviceFragment;
    private BaseFragment mCurrentFragment;
    private MeasureCardsFragment mMeasuringFragment;
    private OnMeasureContainerListener onMeasureContainerListener;
    private boolean isAddDevice;
    private boolean isUnbindInto;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 体温贴校准搜索页面
     */
    private FactoryScanFragment factoryScanFragment;
    private FactoryMeasureFragment factoryMeasureFragment;
    private CalibrateFragment calibrateFragment;
    private QualificationFragment qualificationFragment;
    /**
     * true：表示标题可以点击  false：表示不可点击
     */
    private boolean isTitleClickable = false;
    /**
     * fragment的标识，主要用于区别title的显示
     */
    private String fragmentTag = "factory_scan";
    /**
     * 水槽测温点1
     */
    private float firstSinkTemp = Constant.firstSinkTemp;
    /**
     * 水槽测温点2
     */
    private float secondSinkTemp = Constant.secondSinkTemp;
    /**
     * 第一次测温允许误差
     */
    private float firstAllowableError = 0.30f;
    /**
     * 第二次温度允许误差
     */
    private float secondAllowableError = 0.30f;

    public float getFirstSinkTemp() {
        return firstSinkTemp;
    }

    public float getSecondSinkTemp() {
        return secondSinkTemp;
    }

    public float getFirstAllowableError() {
        return firstAllowableError;
    }

    public float getSecondAllowableError() {
        return secondAllowableError;
    }

    public static MeasureContainerFragment newInstance(boolean isAddDevice) {
        return newInstance(isAddDevice, false, null);
    }

    public static MeasureContainerFragment newInstance(boolean isAddDevice, boolean directToScan, ProfileBean profile) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAddDevice", isAddDevice);
        bundle.putBoolean("directToScan", directToScan);
        bundle.putSerializable("profile", profile);
        MeasureContainerFragment fragment = new MeasureContainerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_container;
    }

    @Override
    protected void fragmentInit() {
        isAddDevice = getArguments().getBoolean("isAddDevice");
        if (isAddDevice) {
            binding.idTopLayout.idToogleDrawer.setImageResource(R.drawable.btn_back_img);
        } else {
            binding.idTopLayout.idToogleDrawer.setImageResource(R.drawable.icon_toolbar_left);
        }
        showFactoryScanDevice();
    }

    @Override
    protected void initView() {
        super.initView();
        if (!isAddDevice) {
            binding.idTopLayout.idTitle.setText(getActivity().getResources().getString(R.string.string_measure));
        } else {
            binding.idTopLayout.idTitle.setText(getActivity().getResources().getString(R.string.string_add_new_device));
        }

        binding.idTopLayout.idToogleDrawer.setOnClickListener(v -> {
            if (onMeasureContainerListener != null) {
                onMeasureContainerListener.onToggleDrawer();
            }
        });

        /**
         * 水槽测温点的选择
         */
        binding.idTopLayout.idTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTitleClickable) {
                    return;
                }
                if (fragmentTag.equalsIgnoreCase("factory_calibrate")) {
                    startActivity(new Intent(mContext, SelectErrorActivity.class)
                            .putExtra("firstAllowableError", firstAllowableError)
                            .putExtra("secondAllowableError", secondAllowableError));//选择误差范围
                } else {
                    setWaterTemp();
                }
            }
        });

        /**
         * 标题栏右侧文字点击事件
         */
        binding.idTopLayout.idTopRight.setOnClickListener(v -> {

            if (fragmentTag.equalsIgnoreCase("factory_scan")) {//扫描页面点击下一步连接信号最好的20个贴
                factoryScanFragment.connectAllDevice();
            } else if (fragmentTag.equalsIgnoreCase("factory_measure1")) {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_measure2"));
            } else if (fragmentTag.equalsIgnoreCase("factory_measure2")) {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_calibrate"));
            } else if (fragmentTag.equalsIgnoreCase("factory_calibrate")) {
                //保存校准误差
                saveAllowableError();
                //开始校准所有未校准的贴
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.CALIBRATE_ALL_PATCH));
            }
        });
    }

    /**
     * 体温贴校准搜索页面
     */
    public void showFactoryScanDevice() {
        binding.idTopLayout.idTopRight.setVisibility(View.VISIBLE);
        binding.idTopLayout.idTopRight.setText("下一步");
        if (factoryScanFragment == null) {
            factoryScanFragment = FactoryScanFragment.newInstance(false);
        }
        showFragment(factoryScanFragment);
        factoryScanFragment.setNewDeviceConnectCallBack(null);
    }

    /**
     * 1.测温页面
     * 2.记录温度页面
     */
    public void showFactoryMeasureFragment() {
        if (factoryMeasureFragment == null) {
            factoryMeasureFragment = FactoryMeasureFragment.newInstance();
        }
        showFragment(factoryMeasureFragment);
        factoryMeasureFragment.refresh();//刷新页面
        factoryMeasureFragment.setFragment_tag("factory_measure1");//重置测温页面
        binding.idTopLayout.idTitle.setText(String.format("测温点1: %s °C", firstSinkTemp));
    }

    /**
     * 校准页面
     */
    public void showCalibrateFragment() {
        if (calibrateFragment == null) {
            calibrateFragment = CalibrateFragment.newInstance();
        }
        showFragment(calibrateFragment);
        calibrateFragment.refresh();//刷新页面
    }

    /**
     * 合格验证页面
     */
    public void showQualificationFragment() {
        if (qualificationFragment == null) {
            qualificationFragment = QualificationFragment.newInstance();
        }
        showFragment(qualificationFragment);
        qualificationFragment.refresh();
    }

    /**
     * 显示fragment
     */
    private void showFragment(BaseFragment fragment) {
        if (fragment == null || fragment == mCurrentFragment) return;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_right
        );
        if (mCurrentFragment != null) {
            if (fragment.isAdded()) {
                //fragment已经添加了
                transaction.hide(mCurrentFragment).show(fragment);
            } else {
                transaction.hide(mCurrentFragment).add(R.id.id_container, fragment);
            }
        } else {
            if (fragment.isAdded()) {
                //fragment已经添加了
                transaction.show(fragment);
            } else {
                transaction.add(R.id.id_container, fragment);
            }
        }
        mCurrentFragment = fragment;
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT) {//fragment切换
            fragmentTag = event.getMsg();

            if (fragmentTag.equalsIgnoreCase("factory_scan")) {//返回最初扫描页面
                showFactoryScanDevice();
            }

            if (fragmentTag.equalsIgnoreCase("factory_measure1")) {//测温1
                binding.idTopLayout.idTitle.setText(String.format("测温点1:%s℃", firstSinkTemp));
                binding.idTopLayout.idTopRight.setVisibility(View.VISIBLE);
                binding.idTopLayout.idTopRight.setText("下一步");
                isTitleClickable = true;
                setNextViewAvailable(false);
                showFactoryMeasureFragment();
            } else if (fragmentTag.equalsIgnoreCase("factory_measure2")) {//测温2
                binding.idTopLayout.idTitle.setText(String.format("测温点2:%s℃", secondSinkTemp));
                binding.idTopLayout.idTopRight.setVisibility(View.VISIBLE);
                binding.idTopLayout.idTopRight.setText("下一步");
                isTitleClickable = true;
                setNextViewAvailable(false);
                factoryMeasureFragment.enterTemp2Measure();
            } else if (fragmentTag.equalsIgnoreCase("factory_calibrate")) {//校准
                showCalibrateFragment();
                binding.idTopLayout.idTopRight.setVisibility(View.VISIBLE);
                binding.idTopLayout.idTopRight.setText("开始校准");
                isTitleClickable = true;
                binding.idTopLayout.idTitle.setText(String.format("o1:±%s，o2:±%s", firstAllowableError, secondAllowableError));
            } else if (fragmentTag.equalsIgnoreCase("factory_qualification")) {//合格验证
                binding.idTopLayout.idTitle.setText("合格验证");
                binding.idTopLayout.idTopRight.setVisibility(View.GONE);
                isTitleClickable = false;
                showQualificationFragment();
            }
        } else if (event.getEventType() == MessageEvent.EventType.STABLE_CHANGE) {//稳定状态发生改变时候更新
            setNextViewAvailable(checkNextAvailable());
        } else if (event.getEventType() == MessageEvent.EventType.ERROR_CHANGE) {
            firstAllowableError = (float) event.getObject();
            secondAllowableError = (float) event.getObject2();
            binding.idTopLayout.idTitle.setText(String.format("o1:±%s，o2:±%s", firstAllowableError, secondAllowableError));
        }
    }

    /**
     * 检测是否能够点击下一步按钮
     * 满足条件：所有的体温贴都是稳定状态
     *
     * @return
     */
    private boolean checkNextAvailable() {
        Map<String, ViewModel> allMeasureViewModel = Utils.getAllMeasureViewModel();
        Set<Map.Entry<String, ViewModel>> entries = allMeasureViewModel.entrySet();
        Iterator<Map.Entry<String, ViewModel>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            MeasureViewModel measureViewModel = (MeasureViewModel) iterator.next().getValue();
            if (fragmentTag.equalsIgnoreCase("factory_measure1")) {
                if (measureViewModel.firstStableState.get() != 1) {
                    return false;
                }
            } else {
                if (measureViewModel.secondStableState.get() != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 设置“下一步”是否能点击
     *
     * @param isNexAvailable
     */
    private void setNextViewAvailable(boolean isNexAvailable) {
        binding.idTopLayout.idTopRight.setClickable(isNexAvailable);
        binding.idTopLayout.idTopRight.setEnabled(isNexAvailable);
        binding.idTopLayout.idTopRight.setTextColor(isNexAvailable ? ContextCompat.getColor(mContext, R.color.color_main) : ContextCompat.getColor(mContext, R.color.color_gray_6c));
    }

    /**
     * 设置水槽温度
     */
    private void setWaterTemp() {
        SelectTempDialogFragment selectTempDialogFragment = SelectTempDialogFragment.newInstance();
        if (fragmentTag.equalsIgnoreCase("factory_measure1")) {
            selectTempDialogFragment.setReferenceTemp(firstSinkTemp);
        } else {
            selectTempDialogFragment.setReferenceTemp(secondSinkTemp);
        }
        selectTempDialogFragment.setOnDialogFragmentDismissListener(new SelectTempDialogFragment.OnDialogFragmentDismissListener() {
            @Override
            public void onDiologDismiss() {

            }

            @Override
            public void onDialogSelect(Float waterTemp) {
                if (fragmentTag.equalsIgnoreCase("factory_measure1")) {
                    firstSinkTemp = waterTemp;
                    binding.idTopLayout.idTitle.setText(String.format("测温点1 : %s℃", waterTemp));
                } else {
                    binding.idTopLayout.idTitle.setText(String.format("测温点2 : %s℃", waterTemp));
                    secondSinkTemp = waterTemp;
                }
            }
        });
        selectTempDialogFragment.show(getChildFragmentManager(), "");
    }

    /**
     * 保存误差范围
     */
    private void saveAllowableError() {
        Map<String, ViewModel> allMeasureViewModel = Utils.getAllMeasureViewModel();
        Set<Map.Entry<String, ViewModel>> entries = allMeasureViewModel.entrySet();
        Iterator<Map.Entry<String, ViewModel>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            MeasureViewModel measureViewModel = (MeasureViewModel) iterator.next().getValue();
            measureViewModel.firstAllowableError.set(getFirstAllowableError());
            measureViewModel.secondAllowableError.set(getSecondAllowableError());
            CalibrateBeanDao.saveAllowableError(measureViewModel.patchMacaddress.get(), getFirstAllowableError(), getSecondAllowableError());
        }
    }


    public void setOnMeasureContainerListener(OnMeasureContainerListener onMeasureContainerListener) {
        this.onMeasureContainerListener = onMeasureContainerListener;
    }

    @Override
    protected boolean openStat() {
        return false;
    }

    public interface OnMeasureContainerListener {
        /**
         * 添加测量卡片
         */
        void onAddMeasureItem(MeasureBean measureInfo);

        /**
         * 打开关闭drawer
         */
        void onToggleDrawer();

        /**
         * 当前正在显示测量界面
         */
        void onShowMeasuring();
    }
}
