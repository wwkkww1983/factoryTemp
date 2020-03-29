package com.proton.carepatchtemp.activity.user;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.Observable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityNewProfileStep2Binding;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.StatusBarUtil;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.proton.carepatchtemp.viewmodel.AddProfileViewModel;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <in-extra>
 * realname String 姓名
 * gender int 性别
 * birthday String 生日
 * </>
 */
public class NewProfileStep2Activity extends BaseViewModelActivity<ActivityNewProfileStep2Binding, AddProfileViewModel> {

    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    protected Uri cropUri = Uri.fromFile(new File(FileUtils.getAvatar()));
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
            }
        }
    }

    @SuppressLint("CheckResult")
    private void uploadPic() {
        io.reactivex.Observable.just(FileUtils.getAvatar())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(s -> OSSUtils.uploadImg(FileUtils.getAvatar()))
                .subscribe(s -> {
                    viewmodel.ossAvatorUri.set(s);
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
        binding.setViewModel(viewmodel);
        Intent mIntent = getIntent();
        String realname = mIntent.getStringExtra("realname");
        if (!TextUtils.isEmpty(realname)) {
            viewmodel.name.set(realname);
        }
        int gender = mIntent.getIntExtra("gender", 1);
        viewmodel.gender.set(gender);
        String birthday = mIntent.getStringExtra("birthday");
        if (!TextUtils.isEmpty(birthday)) {
            viewmodel.birthday.set(birthday);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        //头像
        viewmodel.ossAvatorUri.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                binding.idSdvProfileAddavator.setImageURI(viewmodel.ossAvatorUri.get());
            }
        });
        binding.idSdvProfileAddavator.setOnClickListener(v -> addPicClick());
        viewmodel.stepNum.set(2);
    }

    /**
     * 点击添加头像
     */
    public void addPicClick() {
        final String[] stringItems = {getResString(R.string.string_choose_localpic), getResString(R.string.string_take_picture)};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.title(getResString(R.string.string_set_avatar));
        dialog.titleTextSize_SP(14F);
        dialog.show();
        dialog.setOnOperItemClickL((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    openGallery();
                    break;
                case 1:
                    openCamera();
                    break;
            }
            dialog.dismiss();

        });
    }

    @Override
    protected void setStatusBarColor() {
        StatusBarUtil.setColor(this, getResColor(R.color.color_main_bg));
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_new_profile_step2;
    }

    @Override
    protected AddProfileViewModel getViewModel() {
        return ViewModelProviders.of(this).get(AddProfileViewModel.class);
    }
}
