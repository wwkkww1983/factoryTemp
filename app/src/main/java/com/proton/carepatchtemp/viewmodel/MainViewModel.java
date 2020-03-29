package com.proton.carepatchtemp.viewmodel;

import android.databinding.ObservableField;

import com.proton.carepatchtemp.net.bean.ReportListItemBean;

import java.util.List;

/**
 * Created by luochune on 2018/3/23.
 */

public class MainViewModel extends BaseViewModel {
    public ObservableField<Boolean> isStaredRefreshing = new ObservableField<>();//是否开启了列表刷新状态
    public ObservableField<Boolean> isStartedLoadMore = new ObservableField<>();//是否开启了列表加载状态
    public ObservableField<List<ReportListItemBean>> allReportListObF = new ObservableField<>();//所有报告列表
    public ObservableField<List<ReportListItemBean>> collectReportListObF = new ObservableField<>();//收藏报告列表
    public ObservableField<String> topRightOperateStr = new ObservableField<>("");//顶部导航栏右边操作按钮文字
    public ObservableField<Boolean> isEditMeasureReport = new ObservableField<>(false);//是:编辑测量报告  否: 展示报告列表
    public ObservableField<List<String>> allSelectedObserveList = new ObservableField<>();//全部列表选中集合（存储报告id）
    public ObservableField<List<String>> collectSelectedObserveList = new ObservableField<>();//收藏报告列表选中集合
    private int pagesize = 5;//分页加载的每页数目，每页加载5条数据
    private int allReportCurrPageIndex = 1;
    private int collectReportCurrPageIndex = 1;


}
