package com.proton.carepatchtemp.viewmodel.report;

import android.databinding.ObservableField;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ReportListItemBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.StringUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import org.litepal.LitePal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;
import okhttp3.internal.Util;

import static com.proton.carepatchtemp.net.center.DataCenter.parseResult;

/**
 * Created by luochune on 2018/3/15.
 * 测量报告viewmodel
 */

public class ReportViewModel extends BaseViewModel {
    public ObservableField<Boolean> isStaredRefreshing = new ObservableField<>();//是否开启了列表刷新状态
    public ObservableField<Boolean> isStartedLoadMore = new ObservableField<>();//是否开启了列表加载状态
    public ObservableField<List<ReportListItemBean>> allReportListObF = new ObservableField<>();//所有报告列表
    public ObservableField<List<ReportListItemBean>> collectReportListObF = new ObservableField<>();//收藏报告列表
    //public ObservableField<String> topRightOperateStr = new ObservableField<>("");//顶部导航栏右边操作按钮文字
    public ObservableField<Boolean> isEditMeasureReport = new ObservableField<>(false);//是:编辑测量报告  否: 展示报告列表
    public ObservableField<List<String>> allSelectedObserveList = new ObservableField<>();//全部列表选中集合（存储报告id）
    public ObservableField<List<String>> collectSelectedObserveList = new ObservableField<>();//收藏报告列表选中集合
    private int pagesize = 5;//分页加载的每页数目，每页加载5条数据
    private int allReportCurrPageIndex = 1;
    private int collectReportCurrPageIndex = 1;
    /**
     * 是否存在本地未上传的文件
     */
    private boolean isHaveLocalReport = false;

    private static List<ReportListItemBean> parseReportList(String json) throws Exception {
        Logger.json(json);
        ResultPair resultPair = parseResult(json);
        if (resultPair.isSuccess()) {
            Type type = new TypeToken<List<ReportListItemBean>>() {
            }.getType();
            return JSONUtils.getObj(resultPair.getData(), type);
        } else {
            throw new ParseResultException(resultPair.getData());
        }
    }

    /**
     * 获取所有报告列表
     *
     * @param isRefresh 是否需要刷新
     *                  收藏collectlist==1 取全部:0
     */
    public void getAllReportList(boolean isRefresh) {
        ReportNetCallBack reportNetCallBack = new ReportNetCallBack(isRefresh, "0");
        HashMap<String, String> paramMap = new HashMap<String, String>();
        if (isRefresh) {
            allReportCurrPageIndex = 1;
        } else {
            allReportCurrPageIndex++;
        }
        paramMap.put("page", allReportCurrPageIndex + "");//页码
        paramMap.put("endtime", "0");//本地最新报告时间
        paramMap.put("type", "1");//1,体温 2，心电 3，医生端 4，微信
        /*if (isRefresh) {
            paramMap.put("poll", "1");//    0 是上拉 1是 下拉
        } else {
            paramMap.put("poll", "0");
        }*/
        paramMap.put("collectlist", "0");//收藏collectlist==1 取全部:0
        paramMap.put("pagesize", pagesize + "");//每页数目
        MeasureReportCenter.getReportList(paramMap, reportNetCallBack);
    }

    /**
     * 获取一个宝贝的所有报告
     *
     * @param isRefresh 是否需要刷新
     */
    public void getOneBabayAllReportList(long profileid, boolean isRefresh) {
        ReportNetCallBack reportNetCallBack = new ReportNetCallBack(isRefresh, "0");
        if (isRefresh) {
            allReportCurrPageIndex = 1;
        } else {
            allReportCurrPageIndex++;
        }
        MeasureReportCenter.getOneBabyReportList(profileid, allReportCurrPageIndex, 0, 1, 0, pagesize, reportNetCallBack);
    }

    public void getCollectReportList(boolean isRefresh) {
        ReportNetCallBack reportNetCallBack = new ReportNetCallBack(isRefresh, "1");
        HashMap<String, String> paramMap = new HashMap<>();
        if (isRefresh) {
            collectReportCurrPageIndex = 1;
        } else {
            collectReportCurrPageIndex++;
        }
        paramMap.put("page", collectReportCurrPageIndex + "");//页码
        paramMap.put("endtime", "0");//本地最新报告时间
        paramMap.put("type", "1");//1,体温 2，心电 3，医生端 4，微信
        paramMap.put("collectlist", "1");//收藏collectlist==1 取全部:0
        paramMap.put("pagesize", pagesize + "");//每页数目
        MeasureReportCenter.getReportList(paramMap, reportNetCallBack);
    }

    /**
     * 获取一个档案下面的所有收藏报告列表
     *
     * @param profileId 档案id
     * @param isRefresh 是否刷新
     */
    public void getOneBabyCollectList(long profileId, boolean isRefresh) {
        ReportNetCallBack reportNetCallBack = new ReportNetCallBack(isRefresh, "1");
        if (isRefresh) {
            collectReportCurrPageIndex = 1;
        } else {
            collectReportCurrPageIndex++;
        }
        MeasureReportCenter.getOneBabyCollcetReportList(profileId, collectReportCurrPageIndex, 0, 1, 1, pagesize, reportNetCallBack);
    }

    //报告加载状态改变
    private void modifydataLoadingSatus(boolean isRefresh) {
        if (isRefresh) {
            isStaredRefreshing.set(false);
        } else {
            isStartedLoadMore.set(false);
        }
    }

    /**
     * 报告列表选中某一项
     */
    public void itemClick(String type, int position) {
        if (type.equals("0")) {
            //全部列表
            List<ReportListItemBean> allReportList = allReportListObF.get();
            ReportListItemBean reportListItemBean = allReportList.get(position);
            boolean isOldChecked = reportListItemBean.isChecked();
            List<String> reportListItemBeanList = allSelectedObserveList.get();
            if (CommonUtils.listIsEmpty(reportListItemBeanList)) {
                reportListItemBeanList = new ArrayList<String>();
            }
            if (isOldChecked) {
                //清除当前报告Id选择
                reportListItemBeanList.remove(reportListItemBean.getId());
                //需要重新赋值给变量才能收到变化
                List<String> newReportList = new ArrayList<String>();
                newReportList.addAll(reportListItemBeanList);
                allSelectedObserveList.set(newReportList);
            } else {
                //添加当前报告id
                reportListItemBeanList.add(reportListItemBean.getId());
                //需要重新赋值给变量才能收到变化
                List<String> newReportList = new ArrayList<String>();
                newReportList.addAll(reportListItemBeanList);
                allSelectedObserveList.set(newReportList);
            }
            reportListItemBean.setChecked(!isOldChecked);
            allReportList.set(position, reportListItemBean);
            allReportListObF.set(allReportList);
        } else if (type.equals("1")) {
            //收藏列表
            List<ReportListItemBean> collectReportList = collectReportListObF.get();
            ReportListItemBean reportListItemBean = collectReportList.get(position);
            boolean isOldChecked = reportListItemBean.isChecked();
            List<String> reportListItemBeanList = collectSelectedObserveList.get();
            if (CommonUtils.listIsEmpty(reportListItemBeanList)) {
                reportListItemBeanList = new ArrayList<String>();
            }
            if (isOldChecked) {
                //清除当前报告Id选择
                reportListItemBeanList.remove(reportListItemBean.getId());
                List<String> collectList = new ArrayList<>();
                collectList.addAll(reportListItemBeanList);
                collectSelectedObserveList.set(collectList);
            } else {
                //添加当前报告id
                reportListItemBeanList.add(reportListItemBean.getId());
                List<String> collectList = new ArrayList<>();
                collectList.addAll(reportListItemBeanList);
                collectSelectedObserveList.set(collectList);
            }
            reportListItemBean.setChecked(!isOldChecked);
            collectReportList.set(position, reportListItemBean);
            collectReportListObF.set(collectReportList);
        }
    }

    /**
     * 删除所选报告
     */
    public void deleteSelectedReport(long profitId, String type) {
        if (type.equals("0")) {
            //全部报告
            List<String> allReportIdList = allSelectedObserveList.get();
            if (allReportIdList != null && allReportIdList.size() > 0) {
                String allReportProfitId = StringUtils.join(allReportIdList, ",");
                Logger.i("all_selected_id " + allReportProfitId);
                deleteReports(profitId, allReportProfitId, type);
            } else {
                BlackToast.show(R.string.string_choose_atLeast);
            }
        } else if (type.equals("1")) {
            //收藏报告
            List<String> collectReportIdList = collectSelectedObserveList.get();
            if (collectReportIdList != null && collectReportIdList.size() > 0) {
                String collectReportedProfitId = StringUtils.join(collectReportIdList, ",");
                Logger.i("collect_selected_id " + collectReportedProfitId);
                deleteReports(profitId, collectReportedProfitId, type);
            } else {
                BlackToast.show(R.string.string_choose_atLeast);
            }
        }
    }

    /**
     * 删除测量报告
     *
     * @param profitId  档案id，不是某个档案下的测量报告传 -1
     * @param reportIds 报告id，多个用","分隔
     * @param type      0:全部  1:收藏
     */
    private void deleteReports(long profitId, String reportIds, String type) {
        /**
         * 删除本地报告
         */
        String[] split = reportIds.split(",");
        if (split != null) {
            for (int i = 0; i < split.length; i++) {
                String reportId = split[i];
                LitePal.deleteAll(ReportBean.class, "reportId = ?", reportId);
                Logger.w("删除本地报告");
            }
        }

        MeasureReportCenter.deleteMeasureReport(reportIds, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                status.set(Status.NO_NET);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //报告删除成功
                BlackToast.show(R.string.string_delete_success);
                //TODO 目前通过刷新列表方式展示最后数据
                if (profitId == -1) {
                    //账号下的测量报告
                    if (type.equals("0")) {
                        allSelectedObserveList.set(new ArrayList<>());
                        getAllReportList(true);
                        //更新收藏
                        notifyUpdateCollcetReport();
                    } else if (type.equals("1")) {
                        collectSelectedObserveList.set(new ArrayList<>());
                        getCollectReportList(true);
                        //更新全部报告列表
                        notifyUpdateAllReport();
                    }
                } else {
                    //某个档案下的测量报告
                    if (type.equals("0")) {
                        allSelectedObserveList.set(new ArrayList<>());
                        getOneBabayAllReportList(profitId, true);
                        //更新收藏
                        notifyUpdateCollcetReport();
                    } else if (type.equals("1")) {
                        collectSelectedObserveList.set(new ArrayList<>());
                        getOneBabyCollectList(profitId, true);
                        //更新全部报告列表
                        notifyUpdateAllReport();
                    }
                }

            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData() + "");
                } else {
                    BlackToast.show(R.string.string_delete_failed);
                }
            }
        });
    }

    /**
     * 收藏某个报告
     *
     * @param id   报告id
     * @param type 收藏报告还是全部报告
     */
    public void collectReport(long profileId, String id, String type) {
        HashMap<String, String> collcetReportMap = new HashMap<>();
        collcetReportMap.put("reportid", id);
        MeasureReportCenter.collectReport(collcetReportMap, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //收藏成功
                BlackToast.show(R.string.string_report_collect_success);
                if (profileId == -1) {
                    //账户下所有报告
                    if (type.equals("0")) {
                        getAllReportList(true);
                        //更新收藏
                        notifyUpdateCollcetReport();
                    } else {
                        getCollectReportList(true);
                        //更新全部
                        notifyUpdateAllReport();
                    }
                } else {
                    //某个档案下所有报告
                    if (type.equals("0")) {
                        getOneBabayAllReportList(profileId, true);
                        //更新收藏
                        notifyUpdateCollcetReport();
                    } else {
                        getOneBabyCollectList(profileId, true);
                        //更新全部
                        notifyUpdateAllReport();
                    }
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                } else {
                    BlackToast.show(R.string.string_report_collect_failed);
                }
            }
        });
    }

    /**
     * 取消收藏报告
     *
     * @param type 报告类型
     */
    public void cancelReport(long profileId, String id, String type) {
        HashMap<String, String> collcetReportMap = new HashMap<>();
        collcetReportMap.put("reportid", id);
        MeasureReportCenter.cancelReport(collcetReportMap, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //取消收藏成功
                BlackToast.show(R.string.string_report_cancelCollect_success);
                if (profileId == -1) {
                    if (type.equals("0")) {
                        getAllReportList(true);
                        //更新收藏报告
                        notifyUpdateCollcetReport();
                    } else {
                        getCollectReportList(true);
                        //更新全部报告
                        notifyUpdateAllReport();
                    }
                } else {
                    if (type.equals("0")) {
                        getOneBabayAllReportList(profileId, true);
                        //更新收藏报告
                        notifyUpdateCollcetReport();
                    } else {
                        getOneBabyCollectList(profileId, true);
                        //更新全部报告列表
                        notifyUpdateAllReport();
                    }
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                //取消收藏失败
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                } else {
                    BlackToast.show(R.string.string_report_cancelCollect_failed);
                }
            }
        });
    }

    //通知更新全部报告列表
    private void notifyUpdateAllReport() {
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.UPDATE_ALL_REPORT));
    }

    //通知更新收藏报告列表
    private void notifyUpdateCollcetReport() {
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.UPDATE_COLLECT_REPORT));
    }

    /**
     * 收藏报告类型或者是全部报告类型
     *
     * @param profitId 档案id
     */
    public void clickAllReportDelete(long profitId, String type) {
        final String[] stringItems = {UIUtils.getString(R.string.string_confirm)};
        final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
        dialog.title(UIUtils.getString(R.string.string_delete_report_tip));
        dialog.titleTextSize_SP(14F);
        dialog.cancelText(getResString(R.string.string_cancel));
        dialog.show();
        dialog.setOnOperItemClickL((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    deleteSelectedReport(profitId, type);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });
    }

    /**
     * 删除报告
     *
     * @param id   报告id
     * @param type 0:全部  1:收藏
     */
    public void deleteReport(long profileId, String id, String type) {
        final String[] stringItems = {UIUtils.getString(R.string.string_confirm)};
        final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
        dialog.title(UIUtils.getString(R.string.string_delete_report_tip));
        dialog.titleTextSize_SP(14F);
        dialog.cancelText(getResString(R.string.string_cancel));
        dialog.show();
        dialog.setOnOperItemClickL((parent, view1, position, itemId) -> {
            switch (position) {
                case 0:
                    //确定删除
                    deleteReports(profileId, id, type);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });
    }

    //请求报告列表数据回调
    private class ReportNetCallBack extends NetCallBack<List<ReportListItemBean>> {
        private boolean isRefresh = false;//是否是刷新数据
        private String reportType = "0";//0:全部报告 1:收藏报告

        public ReportNetCallBack(boolean isRefresh, String reportType) {
            this.isRefresh = isRefresh;
            this.reportType = reportType;
        }

        @Override
        public void noNet() {
            super.noNet();
            status.set(Status.NO_NET);
            modifydataLoadingSatus(isRefresh);
        }


        @Override
        public void onSucceed(List<ReportListItemBean> data) {
            status.set(Status.Success);
            modifydataLoadingSatus(isRefresh);
            if (data == null) {
                return;
            }

            //获取本地报告
            fetchLocalReport(data);
            if (isHaveLocalReport) {//如果有本地报告则需要对data按开始时间进行排序
                Collections.sort(data, (o1, o2) -> {
                    long starttime1 = o1.getStarttime();
                    long starttime2 = o2.getStarttime();
                    if (starttime1 - starttime2 < 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                });
            }

            switch (reportType) {
                case "0":
                    //全部报告列表
                    if (isRefresh) {
                        allReportListObF.set(data);
                    } else {
                        //加载
                        if (data.size() > 0) {
                            //拼装数据
                            List<ReportListItemBean> reportListItemBeanList = new ArrayList<ReportListItemBean>();
                            reportListItemBeanList.addAll(allReportListObF.get());
                            reportListItemBeanList.addAll(data);
                            allReportListObF.set(reportListItemBeanList);
                        } else {
                            //加载的下一页数据为空
                            BlackToast.show(R.string.string_no_moredata);
                        }
                    }
                    break;
                case "1":
                    //收藏报告列表
                    if (isRefresh) {
                        collectReportListObF.set(data);
                    } else {
                        //加载
                        if (data.size() > 0) {
                            //拼装数据
                            List<ReportListItemBean> reportListItemBeanList = new ArrayList<ReportListItemBean>();
                            reportListItemBeanList.addAll(collectReportListObF.get());
                            reportListItemBeanList.addAll(data);
                            collectReportListObF.set(reportListItemBeanList);
                        } else {
                            //加载的下一页数据为空
                            BlackToast.show(App.get().getResources().getString(R.string.string_no_moredata));
                        }
                    }
                    break;
            }
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            super.onFailed(resultPair);
            status.set(Status.Fail);
            modifydataLoadingSatus(isRefresh);
        }
    }

    private void fetchLocalReport(List<ReportListItemBean> data) {
        isHaveLocalReport = false;
        List<ReportBean> reportBeans = LitePal.where("userId = ?", App.get().getApiUid()).find(ReportBean.class);
        for (ReportBean report : reportBeans) {
            //上传报告
            if (TextUtils.isEmpty(report.getFilePath())) {
                Logger.w("离线报告本地路径为空删除");
                report.delete();
                continue;
            }

            if (!report.getFilePath().startsWith("http") && !FileUtils.isFileExist(report.getFilePath())) {
                Logger.w("离线报告本地路径文件不存在删除");
                report.delete();
                continue;
            }

            //有文件路径,未上传的有效数据
            if (!report.getFilePath().startsWith("http")) {
                ReportListItemBean itemBean = reportBean2ReportListItemBean(report);
                if (itemBean != null && !Utils.checkProfileIsMeasuring(itemBean.getProfileid())) {
                    data.add(itemBean);
                    isHaveLocalReport = true;
                }
            }
        }
    }

    /**
     * 数据转换
     *
     * @param reportBean
     * @return
     */
    private ReportListItemBean reportBean2ReportListItemBean(ReportBean reportBean) {
        ReportListItemBean itemBean = new ReportListItemBean();
        itemBean.setId(reportBean.getReportId());
        itemBean.setDeviceid(reportBean.getDeviceId());
        itemBean.setProfileid(Long.parseLong(reportBean.getProfileId()));
        itemBean.setType(reportBean.getType());
        itemBean.setProfilename(reportBean.getProfileName());
        itemBean.setProfileavatar(reportBean.getUserAvatar());
        itemBean.setDevicetype(reportBean.getDeviceType());
        itemBean.setStarttime(reportBean.getStartTime());
        itemBean.setEndtime(reportBean.getEndTime());
        itemBean.setFilepath(reportBean.getFilePath());
        ReportListItemBean.DataBean dataBean = new ReportListItemBean.DataBean();
        dataBean.setTemp_max(String.valueOf(reportBean.getMaxTemp()));
        itemBean.setData(dataBean);
        itemBean.setCollect(false);
        return itemBean;
    }
}
