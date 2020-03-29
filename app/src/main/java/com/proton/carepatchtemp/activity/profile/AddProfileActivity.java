package com.proton.carepatchtemp.activity.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.databinding.ActivityAddProfileBinding;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.net.center.ProfileCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.DateUtils;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.logger.Logger;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 增加档案类
 * <传入extra>
 * from String  从哪个页面进来 [firstLogin: 首次注册登录进来]、[chooseProfile: 实时测量选择宝宝]、[measureSave: 实时测量页保存报告]
 * </>
 * <resultCode>
 * 1——>MainActiviy
 * </>
 */
public class AddProfileActivity extends BaseActivity<ActivityAddProfileBinding> implements OnDateSetListener {
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    protected Uri cropUri = Uri.fromFile(new File(FileUtils.getAvatar()));
    private String ossAvatorUri;
    private TimePickerDialog mDialogYearMonthDay;
    private boolean isAddingProfile;
    private Uri tempUri;
    private File mCameraFile = new File(FileUtils.getDataCache(), "image.jpg");
    private boolean canSkip;
    private int isAttachAddNew;

    @Override
    protected void init() {
        super.init();
        isAttachAddNew=getIntent().getIntExtra("isAttachAddNew",0);
        long minMillSeconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//时间格式化类
        try {
            Date date = sdf.parse("1900-1-1");//解析到一个时间
            minMillSeconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mDialogYearMonthDay = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId(getResources().getString(R.string.string_cancel))
                .setSureStringId(getResources().getString(R.string.string_finish))
                .setTitleStringId(getResources().getString(R.string.string_choose_birthday_title))
                .setYearText(getResources().getString(R.string.string_year))
                .setMonthText(getResources().getString(R.string.string_month))
                .setDayText(getResources().getString(R.string.day))
                .setHourText(getResources().getString(R.string.hour))
                .setMinuteText(getResources().getString(R.string.minute))
                .setMinMillseconds(minMillSeconds)
                .setMaxMillseconds(System.currentTimeMillis())
                .setType(Type.YEAR_MONTH_DAY)
                .setThemeColor(getResources().getColor(R.color.color_gray_b3))
                .setToolBarTextColor(getResources().getColor(R.color.color_main))
                .build();
        canSkip = getIntent().getBooleanExtra("canSkip", true);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_add_profile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case UCrop.REQUEST_CROP:
                    uploadPic();
                    break;
            }
        }
    }

    @SuppressLint("CheckResult")
    private void uploadPic() {
        Observable.just(FileUtils.getAvatar())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(s -> OSSUtils.uploadImg(FileUtils.getAvatar()))
                .subscribe(s -> {
                    ossAvatorUri = s;
                    binding.idSdvProfileAddavator.setImageURI(ossAvatorUri);
                }, throwable -> com.wms.logger.Logger.w(throwable.toString()));
    }

    /**
     * 裁剪图片方法实现
     */
    protected void startPhotoZoom(Uri uri) {
        UCrop.of(uri, cropUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(300, 300)
                .start(this);
    }

    private void openGallery() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    private void openCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".FileProvider",
                    mCameraFile);
        } else {
            tempUri = Uri.fromFile(mCameraFile);
        }
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(takePhotoIntent, TAKE_PICTURE);
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idSdvProfileAddavator.setOnClickListener(v -> {
            //添加头像
            showChoosePicDialog();
        });
        //选择出生日期
        binding.idEtProfileAddbirthdate.setOnClickListener(v -> mDialogYearMonthDay.show(getSupportFragmentManager(), ""));
        //添加档案
        binding.idBtnFinish.setOnClickListener(v -> addProfileRequest());
        binding.getRoot().setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.getRoot()));
    }

    private void addProfileRequest() {
        HashMap<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(ossAvatorUri)) {
            ossAvatorUri = AppConfigs.DEFAULT_AVATOR_URL;
        } else {
            ossAvatorUri = OSSUtils.getSaveUrl(ossAvatorUri);
        }
        map.put("avatar", ossAvatorUri);
        //姓名
        String name = binding.idEtProfileaAddname.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            BlackToast.show(R.string.string_name_profile_tip);
            return;
        } else {
            map.put("realname", name);
            map.put("title", name);
        }
        //性别
        if (binding.idRbProfileBoy.isChecked()) {
            map.put("gender", "1");// 1男
        } else if (binding.idRbProfileGirl.isChecked()) {
            map.put("gender", "2");//2 女
        }
        //生日
        String birthday = binding.idEtProfileAddbirthdate.getText().toString().trim();
        if (TextUtils.isEmpty(birthday)) {
            BlackToast.show(R.string.string_choose_birthday);
            return;
        } else {
            map.put("birthday", birthday);
        }
        showDialog();
        requestAddProfile(map);

    }

    /**
     * 从哪个页面进来添加档案
     */
    private void requestAddProfile(HashMap<String, String> map) {
        if (isAddingProfile) return;
        isAddingProfile = true;
        ProfileCenter.addProfile(new NetCallBack<ProfileBean>() {
            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                isAddingProfile = false;
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ProfileBean data) {
                dismissDialog();
                BlackToast.show(R.string.string_profile_add);
                data.setIsAttachAddNew(isAttachAddNew);
                if (!TextUtils.isEmpty(App.get().getLastScanDeviceId())) {
                    //上次扫描了直接绑定
                    bindDevice(data.getProfileId());
                } else {
                    if (getIntent().getBooleanExtra("needScanQRCode", false)) {
                        IntentUtils.goToScanQRCode(mContext, data, true);
                    }
                }
                isAddingProfile = false;
                finish();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
                isAddingProfile = false;
            }
        }, map);
    }

    private void bindDevice(long profileId) {
        DeviceCenter.editShareProfile(String.valueOf(profileId), App.get().getLastScanDeviceId(), false, new NetCallBack<Boolean>() {

            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(Boolean data) {
                dismissDialog();
                finish();
                Logger.w("更新分享设备成功");
                App.get().setLastScanDeviceId("");
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                BlackToast.show(R.string.string_bind_fail);
            }
        });
    }

    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        final String[] stringItems = {getResString(R.string.string_choose_localpic), getResString(R.string.string_take_picture)};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.title(getResString(R.string.string_set_avatar));
        dialog.titleTextSize_SP(14F);
        dialog.cancelText(getString(R.string.string_cancel));
        dialog.show();
        dialog.setOnOperItemClickL((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    openGallery();
                    break;
                case 1:
                    openCamera();
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });
    }

    @Override
    protected boolean showBackBtn() {
        return canSkip;
    }

    @Override
    public void onBackPressed() {
        if (canSkip) {
            super.onBackPressed();
        }
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_add_profile);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        binding.idEtProfileAddbirthdate.setText(DateUtils.dateStrToYMD(millseconds));
    }
}
