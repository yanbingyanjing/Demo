package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.videolist.model.ImpressBean;
import com.yjfshop123.live.ui.widget.MyTextView;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyTagActivity1 extends BaseActivity {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.group)
    LinearLayout mGroup;

    private String tags = "";
    private ArrayList<ImpressBean> dataList;
    private LayoutInflater mInflater;
    private LinkedList<String> mLinkedList;
    private View.OnClickListener mOnClickListener;
    private boolean mChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_mytag1);
        ButterKnife.bind(this);
        setHeadLayout();

        mLinkedList = new LinkedList<>();
        mInflater = LayoutInflater.from(mContext);

        initView();
        initData();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.tag_my);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText(R.string.de_save);

        String content = getIntent().getStringExtra("content");
        if (!TextUtils.isEmpty(content)){
            tags = content;
        }

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTextView mtv = (MyTextView) v;
                if (!mtv.isChecked()) {
                    if (mLinkedList.size() < 4) {
                        mtv.setChecked(true);
                        addId(mtv.getBean().getName());
                        mChanged = true;
                    } else {
                        NToast.shortToast(mContext, R.string.tag_tip);
                    }
                } else {
                    removeId(mtv.getBean().getName());
                    mtv.setChecked(false);
                    mChanged = true;
                }
            }
        };

        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLinkedList.size() == 0) {
                    NToast.shortToast(mContext, "请选择标签");
                    return;
                }
                if (!mChanged) {
                    NToast.shortToast(mContext, "未修改标签");
                    return;
                }
                String tags = "";
                for (String str : mLinkedList) {
                    tags += str + ",";
                }
                if (tags.endsWith(",")) {
                    tags = tags.substring(0, tags.length() - 1);
                }

                setResult(12, new Intent().putExtra("result", tags));
                finish();
            }
        });
    }


    private void initData() {
        dataList = new ArrayList<>();
        ImpressBean impressBean1 = new ImpressBean(getString(R.string.tag_1), "#ff4433", 0);
        ImpressBean impressBean2 = new ImpressBean(getString(R.string.tag_2), "#ff7733", 0);
        ImpressBean impressBean3 = new ImpressBean(getString(R.string.tag_3), "#ffbb33", 0);
        ImpressBean impressBean4 = new ImpressBean(getString(R.string.tag_4), "#daff33", 0);
        ImpressBean impressBean5 = new ImpressBean(getString(R.string.tag_5), "#8fff33", 0);
        ImpressBean impressBean6 = new ImpressBean(getString(R.string.tag_6), "#33ff33", 0);
        ImpressBean impressBean7 = new ImpressBean(getString(R.string.tag_7), "#33ff92", 0);
        ImpressBean impressBean8 = new ImpressBean(getString(R.string.tag_8), "#33ffda", 0);
        ImpressBean impressBean9 = new ImpressBean(getString(R.string.tag_9), "#33d6ff", 0);
        ImpressBean impressBean10 = new ImpressBean(getString(R.string.tag_10), "#33aaff", 0);
        ImpressBean impressBean11 = new ImpressBean(getString(R.string.tag_11), "#337aff", 0);
        ImpressBean impressBean12 = new ImpressBean(getString(R.string.tag_12), "#334eff", 0);
        ImpressBean impressBean13 = new ImpressBean(getString(R.string.tag_13), "#4733ff", 0);
        ImpressBean impressBean14 = new ImpressBean(getString(R.string.tag_14), "#7033ff", 0);
        ImpressBean impressBean15 = new ImpressBean(getString(R.string.tag_15), "#a733ff", 0);
        ImpressBean impressBean16 = new ImpressBean(getString(R.string.tag_16), "#c933ff", 0);
        ImpressBean impressBean17 = new ImpressBean(getString(R.string.tag_17), "#f533ff", 0);
        ImpressBean impressBean18 = new ImpressBean(getString(R.string.tag_18), "#ff33c9", 0);
        ImpressBean impressBean19 = new ImpressBean(getString(R.string.tag_19), "#ff3399", 0);
        ImpressBean impressBean20 = new ImpressBean(getString(R.string.tag_20), "#ff3363", 0);
        ImpressBean impressBean21 = new ImpressBean(getString(R.string.tag_21), "#334eff", 0);
        ImpressBean impressBean22 = new ImpressBean(getString(R.string.tag_22), "#3dff33", 0);
        dataList.add(impressBean1);
        dataList.add(impressBean2);
        dataList.add(impressBean3);
        dataList.add(impressBean4);
        dataList.add(impressBean5);
        dataList.add(impressBean6);
        dataList.add(impressBean7);
        dataList.add(impressBean8);
        dataList.add(impressBean9);
        dataList.add(impressBean10);
        dataList.add(impressBean11);
        dataList.add(impressBean12);
        dataList.add(impressBean13);
        dataList.add(impressBean14);
        dataList.add(impressBean15);
        dataList.add(impressBean16);
        dataList.add(impressBean17);
        dataList.add(impressBean18);
        dataList.add(impressBean19);
        dataList.add(impressBean20);
        dataList.add(impressBean21);
        dataList.add(impressBean22);
        for (int i = 0; i < dataList.size(); i++) {
            if (tags.contains(dataList.get(i).getName())){
                dataList.get(i).setCheck(1);
            }else {
                dataList.get(i).setCheck(0);
            }
        }

        int line = 0;
        int fromIndex = 0;
        boolean hasNext = true;
        while (hasNext) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.view_impress_line, mGroup, false);
            int endIndex = line % 2 == 0 ? fromIndex + 4 : fromIndex + 3;
            if (endIndex >= dataList.size()) {
                endIndex = dataList.size();
                hasNext = false;
            }
            for (int i = fromIndex; i < endIndex; i++) {
                MyTextView item = (MyTextView) mInflater.inflate(R.layout.view_impress_item, linearLayout, false);
                ImpressBean impressBean = dataList.get(i);
                if (impressBean.isChecked()) {
                    addId(impressBean.getName());
                }
                item.setBean(impressBean);
                linearLayout.addView(item);
                item.setOnClickListener(mOnClickListener);
            }
            fromIndex = endIndex;
            line++;
            mGroup.addView(linearLayout);
        }
    }

    private void addId(String tag) {
        mLinkedList.add(tag);
    }

    private void removeId(String tag) {
        int index = -1;
        for (int i = 0, size = mLinkedList.size(); i < size; i++) {
            if (tag.equals(mLinkedList.get(i))) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            mLinkedList.remove(index);
        }
    }

}
