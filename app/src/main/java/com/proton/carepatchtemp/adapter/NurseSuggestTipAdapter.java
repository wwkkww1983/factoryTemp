package com.proton.carepatchtemp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.utils.UIUtils;

import java.util.List;

/**
 * Created by luochune on 2018/4/21.
 */

public class NurseSuggestTipAdapter extends PagerAdapter {

    //体温小常识
    private String tempCommon = "http://www.protontek.com/app/fever_tip1.html";
    //常用退烧药
    private String tempLowFever = "http://www.protontek.com/app/fever_tip2.html";
    //发热惊厥史
    private String fever = "http://www.protontek.com/app/fever_tip3.html";
    //疫苗接种史
    private String vacine = "http://www.protontek.com/app/fever_tip4.html";
    private List<String> nurseSuggestTipList;
    private Context mContext;

    public NurseSuggestTipAdapter(List<String> suggestList, Context mContext) {
        this.nurseSuggestTipList = suggestList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return nurseSuggestTipList == null ? 0 : nurseSuggestTipList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout_nursedetail_tip, null);
        ImageView imageView = view.findViewById(R.id.id_nursesuggest_pic);
        String nurseTip = nurseSuggestTipList.get(position);
        if (nurseTip.equals(UIUtils.getString(R.string.string_nursedes_fever))) {
            //发热惊厥
            imageView.setImageResource(R.drawable.img_nursedes_fever);
            //imageView.setImageDrawable(getDrawableResId(R.drawable.img_nursedes_fever));
            view.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, GlobalWebActivity.class).putExtra("url", fever)));
        } else if (nurseTip.equals(UIUtils.getString(R.string.string_nursedes_vacine))) {
            //疫苗接种史
            imageView.setImageResource((R.drawable.img_nursedes_vacin));
            // imageView.setImageDrawable(getDrawableResId(R.drawable.img_nursedes_vacin));
            view.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, GlobalWebActivity.class).putExtra("url", vacine)));
        } else if (nurseTip.equals(UIUtils.getString(R.string.string_nursedes_commonDownFever))) {
            //退烧药
            imageView.setImageResource(R.drawable.img_nursedes_downfever);
            //imageView.setImageDrawable(getDrawableResId(R.drawable.img_nursedes_downfever));
            view.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, GlobalWebActivity.class).putExtra("url", tempLowFever)));
        } else if (nurseTip.equals(UIUtils.getString(R.string.string_nursedes_tempCommon))) {
            //体温常识
            imageView.setImageResource(R.drawable.img_nursedes_common);
            //mageView.setImageDrawable(getDrawableResId(R.drawable.img_nursedes_common));
            view.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, GlobalWebActivity.class).putExtra("url", tempCommon)));
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        container.removeView(container.getChildAt(position));
    }

    /* private Drawable getDrawableResId(int resId)
     {
         return ContextCompat.getDrawable(mContext,resId);
     }*/
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

   /* @Override
    public float getPageWidth(int position) {
        return DensityUtils.dip2px(mContext,155);
    }*/
}
