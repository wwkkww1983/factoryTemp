package com.proton.carepatchtemp.fragment.base;

import android.databinding.ViewDataBinding;

/**
 * Created by 王梦思 on 2015/12/18 0018.
 * <p/>
 * Fragment的基类
 */
public abstract class BaseLazyFragment<DB extends ViewDataBinding> extends BaseFragment<DB> {
    @Override
    protected boolean isLazyLoad() {
        return true;
    }
}
