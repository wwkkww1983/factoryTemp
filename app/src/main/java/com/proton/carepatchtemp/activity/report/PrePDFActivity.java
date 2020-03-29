package com.proton.carepatchtemp.activity.report;

import android.content.Intent;

import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityPrePdfBinding;
import com.wms.logger.Logger;

import java.io.File;

/**
 * 预览pdf报告activity
 * <extra>
 * filePath String pdf报告路径
 * </>
 */
public class PrePDFActivity extends BaseActivity<ActivityPrePdfBinding> {
    String pdfFilePath;

    @Override
    protected void init() {
        super.initData();
        Intent intent = getIntent();
        pdfFilePath = intent.getStringExtra("filePath");
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.title.setText(R.string.string_preview);
        binding.idPreviewPdf.fromFile(new File(pdfFilePath)).defaultPage(1).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                Logger.i("load_success");
            }
        }).load();

      /*  pdfView.fromAsset(pdfName)
                .pages(0, 2, 1, 3, 3, 3)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onDraw(onDrawListener)
                .onLoad(onLoadCompleteListener)
                .onPageChange(onPageChangeListener)
                .load();*/
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_pre_pdf;
    }
}
