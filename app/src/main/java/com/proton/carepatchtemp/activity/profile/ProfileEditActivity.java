package com.proton.carepatchtemp.activity.profile;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.databinding.ActivityProfileEditBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.ProfileCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.DateUtils;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
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
 * 档案编辑页面
 * <enter_extra>
 * Serializable  profileBean ProfitBean
 * </>
 * <back_extra>
 * String editName  从编辑姓名页返回
 * </>
 * <result>
 * resultcode为3是编辑姓名页回来
 * </result>
 */
public class ProfileEditActivity extends BaseActivity<ActivityProfileEditBinding> implements OnDateSetListener {

    private static final int PROFILE_EDIT_REQUEST_CODE = 3;//请求姓名编辑
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    protected Uri cropUri = Uri.fromFile(new File(FileUtils.getAvatar()));
    private String ossAvatorUri = "";//全局头像uri
    private TimePickerDialog mDialogYearMonthDay;
    private long profileId = -1;
    private ProfileBean profileBean;
    private Uri tempUri;
    private File mCameraFile = new File(FileUtils.getDataCache(), "image.jpg");

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
                case PROFILE_EDIT_REQUEST_CODE:
                    //从姓名编辑页返回
                    if (data != null) {
                        String editName = data.getStringExtra("editName");
                        if (!TextUtils.isEmpty(editName)) {
                            binding.idTvRealName.setText(editName);
                        }
                    }
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
                    binding.idSdvProfileAvatar.setImageURI(ossAvatorUri);
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
    protected void init() {
        super.init();
        Intent getIntent = getIntent();
        profileBean = (ProfileBean) getIntent.getSerializableExtra("profileBean");
        if (profileBean == null) {
            return;
        }
        //档案id
        profileId = profileBean.getProfileId();
        ossAvatorUri = profileBean.getAvatar();
        //设置头像
        if (!TextUtils.isEmpty(ossAvatorUri)) {
            binding.idSdvProfileAvatar.setImageURI(ossAvatorUri);
        }
        //设置姓名
        binding.idTvRealName.setText(profileBean.getRealname() + "");
        //设置性别
        if (1 == profileBean.getGender()) {
            binding.idTvSex.setText(getResources().getString(R.string.string_boy));
        } else {
            binding.idTvSex.setText(getResources().getString(R.string.string_girl));
        }
        //设置生日
        binding.idTvBirthday.setText(profileBean.getBirthday() + "");
        //档案id 国际版本不显示档案编码
        binding.idTvProfileCode.setText(Utils.getShareId(profileBean.getProfileId()));
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
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.idTvRightOperate.setText(getResources().getString(R.string.string_save));
        binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_profile_edit;
    }

    @Override
    protected void setListener() {
        super.setListener();
        //编辑头像
        binding.idLayProfileAvatar.setOnClickListener(v -> showChoosePicDialog());
        //编辑姓名
        binding.idLayProfileName.setOnClickListener(v -> startActivityForResult(new Intent(this, ProfileNameEditActivity.class).putExtra("name", binding.idTvRealName.getText().toString()), PROFILE_EDIT_REQUEST_CODE));
        binding.idLayProfileBirthday.setOnClickListener(v -> mDialogYearMonthDay.show(getSupportFragmentManager(), ""));
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(view -> submitEditProfile());
        //编辑性别
        binding.idLayProfileSex.setOnClickListener(v -> {
            final String[] stringItems = {getString(R.string.string_boy), getString(R.string.string_girl)};
            final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
            dialog.title(getString(R.string.string_sex));
            dialog.titleTextSize_SP(14F);
            dialog.cancelText(getString(R.string.string_cancel));
            dialog.show();
            dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                switch (position) {
                    case 0:
                        //男
                        binding.idTvSex.setText(getString(R.string.string_boy));
                        break;
                    case 1:
                        //女
                        binding.idTvSex.setText(getString(R.string.string_girl));
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            });
        });
        //档案id
        binding.idLayProfileCode.setOnClickListener(v -> {
            final String[] stringItems = {getString(R.string.string_copy)};
            final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
            dialog.title(getString(R.string.string_profile_codeid));
            dialog.titleTextSize_SP(14F);
            dialog.cancelText(getString(R.string.string_cancel));
            dialog.show();
            dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                switch (position) {
                    case 0:
                        //复制档案编码
                        ClipboardManager clipboardManager = (ClipboardManager) ProfileEditActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        //创建ClipData对象
                        ClipData clipData = ClipData.newPlainText(getResString(R.string.string_profile_codeid), binding.idTvProfileCode.getText().toString());
                        //添加ClipData对象到剪切板中
                        clipboardManager.setPrimaryClip(clipData);
                        BlackToast.show(getResources().getString(R.string.string_copy_success));
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            });
        });
    }

    /**
     * 提交编辑档案数据
     */
    private void submitEditProfile() {
        showDialog(getResources().getString(R.string.string_submit_data));
        if (TextUtils.isEmpty(ossAvatorUri)) {
            ossAvatorUri = AppConfigs.DEFAULT_AVATOR_URL;
        } else {
            ossAvatorUri = OSSUtils.getSaveUrl(ossAvatorUri);
        }
        String realName = binding.idTvRealName.getText().toString();
        if (TextUtils.isEmpty(realName)) {
            BlackToast.show(getString(R.string.string_input_name_tip));
            dismissDialog();
            return;
        }
        String sexStr = binding.idTvSex.getText().toString();
        String birthday = binding.idTvBirthday.getText().toString();
        //上传档案
        HashMap<String, String> map = new HashMap<>();
        map.put("title", realName);
        map.put("realname", realName);
        if (sexStr.equals(getString(R.string.string_boy))) {
            map.put("gender", "1");
        } else if (sexStr.equals(getString(R.string.string_girl))) {
            map.put("gender", "2");
        }
        map.put("birthday", birthday);
        map.put("avatar", ossAvatorUri);
        map.put("profileid", profileId + "");
        map.put("created", String.valueOf(profileBean.getCreated()));
        requestEditProfile(map);
    }

    /**
     * 提交编辑档案
     *
     * @param map 编辑参数
     */
    private void requestEditProfile(HashMap<String, String> map) {
        ProfileCenter.editProfile(map, new NetCallBack<ProfileBean>() {
            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(getResources().getString(R.string.string_no_net));
            }

            @Override
            public void onSucceed(ProfileBean data) {
                //档案编辑成功
                dismissDialog();
                //回调到主页面
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PROFILE_CHANGE,"isEdit",data));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                finish();
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
    public String getTopCenterText() {
        return getResources().getString(R.string.string_profile_edit);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        binding.idTvBirthday.setText(DateUtils.dateStrToYMD(millseconds));
    }
}
