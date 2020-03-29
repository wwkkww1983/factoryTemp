package com.proton.carepatchtemp.net.center;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.bean.ArticleBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangmengsi on 2018/02/03.
 */

public class ArticleCenter extends DataCenter {

    /**
     * 获取健康提示
     *
     * @param poll 0 = 获取endTime以前的最新pageSize条数据 下拉
     *             1 = 获取endTime以后的最新pageSize条数据 上拉
     */
    public static void getHealthTips(int page, long endTime, int poll, NetCallBack<List<ArticleBean>> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("pagesize", "10");
        params.put("endtime", String.valueOf(endTime));
        params.put("poll", String.valueOf(poll));

        RetrofitHelper.getArticleApi().getHealthTips(params)
                .map(ArticleCenter::parseArticle)
                .compose(threadTrans())
                .subscribe(new NetSubscriber<List<ArticleBean>>(callBack) {
                    @Override
                    public void onNext(List<ArticleBean> value) {
                        callBack.onSucceed(value);
                    }
                });
    }

    private static List<ArticleBean> parseArticle(String json) throws Exception {
        Logger.json(json);
        ResultPair resultPair = parseResult(json);
        if (resultPair.isSuccess()) {
            Type type = new TypeToken<ArrayList<ArticleBean>>() {
            }.getType();
            return JSONUtils.getObj(resultPair.getData(), type);
        } else {
            throw new ParseResultException(resultPair.getData());
        }
    }
}
