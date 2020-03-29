package com.proton.carepatchtemp.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.utils.BlackToast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Created by luochune on 2018/4/16.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.get().wxApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        finish();
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                BlackToast.show(R.string.string_share_success);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                BlackToast.show(R.string.string_share_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                BlackToast.show(R.string.string_share_deny);
            break;
        }
    }
}

