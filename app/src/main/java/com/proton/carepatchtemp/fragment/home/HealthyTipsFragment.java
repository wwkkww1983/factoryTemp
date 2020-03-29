package com.proton.carepatchtemp.fragment.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.databinding.FragmentHealthyTipsBinding;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.net.bean.ArticleBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.ArticleCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.FormatUtils;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.adapter.recyclerview.OnItemClickListener;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmengsi on 2018/2/28.
 */

public class HealthyTipsFragment extends BaseFragment<FragmentHealthyTipsBinding> {

    private List<ArticleBean> mHealthyTips = new ArrayList<>();
    private CommonAdapter<ArticleBean> mHealthyTipsAdapter;

    public static HealthyTipsFragment newInstance() {
        return new HealthyTipsFragment();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_healthy_tips;
    }

    @Override
    protected void fragmentInit() {
        binding.idIncludeRefresh.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        initRefreshLayout(binding.idIncludeRefresh.idRefreshLayout, refreshlayout -> getHealthyTips(true), refreshlayout -> getHealthyTips(false));
        mHealthyTipsAdapter = new CommonAdapter<ArticleBean>(mContext, mHealthyTips, R.layout.item_health_tip) {
            @Override
            public void convert(CommonViewHolder holder, ArticleBean articleBean) {
                holder.setText(R.id.id_title, articleBean.getTitle())
                        .setText(R.id.id_time, FormatUtils.getTimeYMDHM(articleBean.getTime()))
                        .setText(R.id.id_content, articleBean.getSummary());
                SimpleDraweeView imageView = holder.getView(R.id.id_img);
                imageView.setImageURI(articleBean.getImage());
            }
        };

        binding.idIncludeRefresh.idRecyclerview.setAdapter(mHealthyTipsAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        binding.idIncludeRefresh.idRefreshLayout.autoRefresh(1);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idTopLayout.idTitle.setText(getActivity().getResources().getString(R.string.string_healthy_tips));
        setListener();
    }

    private void setListener() {
        binding.idTopLayout.idToogleDrawer.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity instanceof HomeActivity) {
                ((HomeActivity) activity).toogleDrawer();
            }
        });
        mHealthyTipsAdapter.setOnItemClickListener(new OnItemClickListener<ArticleBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, ArticleBean articleBean, int position) {
                String url = articleBean.getUrl();
                if (!TextUtils.isEmpty(url)) {
                    startActivity(new Intent(getActivity(), GlobalWebActivity.class).putExtra("url", url));
                } else {
                    BlackToast.show(R.string.string_no_detail_content);
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, ArticleBean articleBean, int position) {
                return false;
            }
        });
    }

    private void getHealthyTips(boolean isRefresh) {
        long endTime;
        if (mHealthyTips.size() <= 0) {
            endTime = 0L;
        } else {
            endTime = mHealthyTips.get(mHealthyTips.size() - 1).getTime();
        }
        ArticleCenter.getHealthTips(1, endTime, isRefresh ? 0 : 1, new NetCallBack<List<ArticleBean>>() {

            @Override
            public void noNet() {
                super.noNet();
                if (isRefresh) {
                    setLoadError();
                    binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
                } else {
                    binding.idIncludeRefresh.idRefreshLayout.finishLoadmore();
                }
            }

            @Override
            public void onSucceed(List<ArticleBean> data) {
                if (isRefresh) {
                    mHealthyTips.clear();
                    binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
                } else {
                    binding.idIncludeRefresh.idRefreshLayout.finishLoadmore();
                }

                mHealthyTips.addAll(data);
                mHealthyTipsAdapter.notifyDataSetChanged();
                if (CommonUtils.listIsEmpty(mHealthyTips)) {
                    setLoadEmpty();
                } else {
                    setLoadSuccess();
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                setLoadError();
                if (isRefresh) {
                    binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
                } else {
                    binding.idIncludeRefresh.idRefreshLayout.finishLoadmore();
                }
            }
        });
    }

    @Override
    protected boolean isNeedLoginLoad() {
        return false;
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idIncludeRefresh.idRefreshLayout;
    }
}
