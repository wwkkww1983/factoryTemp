package com.proton.carepatchtemp.activity.measure;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.bean.ShareTempBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.LayoutDeviceShareBinding;
import com.proton.carepatchtemp.net.bean.ShareHistoryBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.MeasureCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.MQTTShareManager;
import com.proton.carepatchtemp.utils.PermissionsChecker;
import com.proton.carepatchtemp.utils.Utils;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备共享
 * <in-extra>
 * profileId String 档案id
 * deviceId String 设备id
 * </in-extra>
 */
public class DeviceShareActivity extends BaseActivity<LayoutDeviceShareBinding> {

    private final int PERMISSION_REQUEST_READ_CONTACTS = 1;
    /**
     * 请求打开通讯录权限
     */
    private final int REQUEST_CODE_OPEN_CONTACTS = 2;
    /**
     * 档案id
     */
    protected String profileId;
    /**
     * 设备id
     */
    protected String deviceId;

    protected List<ShareHistoryBean> mShareList;
    private CommonAdapter<ShareHistoryBean> shareHistoryAdapter;
    private String macaddress;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限允许
                    readContacts();
                } else {
                    //权限拒绝
                    BlackToast.show(R.string.string_open_contact_permission);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPEN_CONTACTS:
                if (resultCode == RESULT_OK) {
                    ContentResolver reContentResolverol = getContentResolver();
                    Uri contactData = data.getData();
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();
                    String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null);
                    while (phone.moveToNext()) {
                        String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!TextUtils.isEmpty(username)) {
                            binding.idEtPhoneNum.setText(usernumber);
                        }
                    }

                }
                break;
        }
    }

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        profileId = mIntent.getStringExtra("profileId");
        deviceId = mIntent.getStringExtra("deviceId");
        macaddress = mIntent.getStringExtra("macaddress");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShareList = null;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.layout_device_share;
    }

    @Override
    protected void initData() {
        if (!App.get().isLogined()) {
            setLoadError();
            return;
        }
        getShareHistory();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idRvShareHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mShareList = new ArrayList<>();
        shareHistoryAdapter = new CommonAdapter<ShareHistoryBean>(this, mShareList, R.layout.item_layout_share_history) {
            @Override
            public void convert(CommonViewHolder holder, ShareHistoryBean sharelistBean) {
                String phoneNum = sharelistBean.getMobile();
                ((TextView) holder.getView(R.id.id_tv_sharePhoneNum)).setText(phoneNum);
                holder.getView(R.id.id_v_delete).setOnClickListener(v -> {
                    //取消一条分享历史记录
                    MeasureCenter.cancelShare(phoneNum, profileId, new NetCallBack<ResultPair>() {
                        @Override
                        public void onSucceed(ResultPair data) {
                            BlackToast.show(R.string.string_cancel_share_success);
                            getShareHistory();
                        }
                    });
                    MQTTShareManager.getInstance()
                            .publish(Utils.getShareTopic(macaddress)
                                    , new ShareTempBean(204, sharelistBean.getSharedUid()));
                });
            }
        };
        binding.idRvShareHistory.setAdapter(shareHistoryAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idEtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    binding.idIvPhoneNumDel.setVisibility(View.VISIBLE);
                } else {
                    binding.idIvPhoneNumDel.setVisibility(View.GONE);
                }
            }
        });
        binding.idIvPhoneNumDel.setOnClickListener(v -> binding.idEtPhoneNum.setText(""));
        //分享设备信息
        binding.idBtnShareDevice.setOnClickListener(v -> {
            String phoneNum = binding.idEtPhoneNum.getText().toString();
            if (!TextUtils.isEmpty(phoneNum) && Utils.isMobilePhone(phoneNum)) {
                //通过App共享设备信息
                if (!CommonUtils.listIsEmpty(mShareList)) {
                    for (ShareHistoryBean share : mShareList) {
                        if (share.getMobile().equalsIgnoreCase(phoneNum)) {
                            BlackToast.show(R.string.string_device_has_shared);
                            return;
                        }
                    }
                    shareDevice(deviceId, phoneNum, profileId);
                } else {
                    shareDevice(deviceId, phoneNum, profileId);
                }
            } else {
                BlackToast.show(R.string.string_input_valiablePhoneNum);
            }

        });
        //从通讯录读取手机号码
        binding.idIvContact.setOnClickListener(v -> {
            //检查是否允许读取通讯录
            if (!PermissionsChecker.lacksPermission(this, Manifest.permission.READ_CONTACTS)) {
                readContacts();
            } else {
                //动态申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
            }
        });
    }

    /**
     * 分享设备
     *
     * @param deviceId  设备id
     * @param phoneNum  手机号码
     * @param profileId 档案id
     */
    private void shareDevice(String deviceId, String phoneNum, String profileId) {
        MeasureCenter.shareDevice(deviceId, phoneNum, profileId, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //分享成功
                BlackToast.show(R.string.string_share_successs);
                binding.idEtPhoneNum.setText("");
                getShareHistory();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && !TextUtils.isEmpty(resultPair.getData())) {
                    BlackToast.show(resultPair.getData());
                } else {
                    BlackToast.show(R.string.string_share_fail);
                }
            }
        });
    }

    /**
     * 获取分享历史
     */
    private void getShareHistory() {
        MeasureCenter.getShareHistory(profileId, new NetCallBack<List<ShareHistoryBean>>() {
            @Override
            public void onSucceed(List<ShareHistoryBean> sharelistBeanList) {
                if (sharelistBeanList != null && sharelistBeanList.size() > 0) {
                    mShareList.clear();
                    mShareList.addAll(sharelistBeanList);
                    shareHistoryAdapter.setDatas(mShareList);
                } else {
                    mShareList.clear();
                    shareHistoryAdapter.setDatas(mShareList);
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
            }
        });
    }

    /**
     * 读取通讯录
     */
    private void readContacts() {
        startActivityForResult(new Intent(
                Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_OPEN_CONTACTS);
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idLoadingLayout;
    }

    @Override
    protected int getNotLoginTips() {
        return R.string.string_not_login_can_not_share;
    }

    @Override
    public String getTopCenterText() {
        return getResString(R.string.string_device_share);
    }
}
