package com.proton.carepatchtemp.factory.activity;

import android.support.v4.app.FragmentTransaction;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityNewSearchBinding;
import com.proton.carepatchtemp.factory.fragment.FactoryScanFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;

public class NewSearchActivity extends BaseActivity<ActivityNewSearchBinding> {

    private FactoryScanFragment factoryScanFragment;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_new_search;
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idTopNavigation.title.setText("点击相应设备进行连接");
        showFactoryScanDevice();
        factoryScanFragment.setNewDeviceConnectCallBack(new FactoryScanFragment.ConnectCallBack() {
            @Override
            public void connectSuccess() {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_ADD));
                finish();
            }
        });
    }

    @Override
    protected boolean showBackBtn() {
        return true;
    }

    /**
     * 体温贴校准搜索页面
     */
    public void showFactoryScanDevice() {
        if (factoryScanFragment == null) {
            factoryScanFragment = FactoryScanFragment.newInstance(true);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_right
            );
            if (factoryScanFragment.isAdded()) {
                //fragment已经添加了
                transaction.show(factoryScanFragment);
            } else {
                transaction.add(R.id.id_container, factoryScanFragment);
            }
            transaction.commitAllowingStateLoss();
        }
    }
}
