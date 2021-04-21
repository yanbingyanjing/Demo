package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.IncomeRankingResponse;
import com.yjfshop123.live.ui.viewholders.H4Holder;

import java.util.ArrayList;
import java.util.List;

public class H4Adapter extends RecyclerView.Adapter<H4Holder> {

    private List<IncomeRankingResponse.ListBean> mList = new ArrayList<>();
    private View header;
    private final static int VIEW_HEADER = 0;
    private final static int VIEW_ITEM = 1;

    private LayoutInflater layoutInflater;
    private Context context;

    public H4Adapter(Context context, View header){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.header = header;
    }

    @Override
    public H4Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == VIEW_HEADER) {
            itemView = header;
        }else if (viewType == VIEW_ITEM){
            itemView = layoutInflater.inflate(R.layout.layout_h4_item, parent, false);
        }
        return new H4Holder(context, itemView, viewType, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(H4Holder holder, int position) {
        if (getItemViewType(position) == VIEW_HEADER) {
            //banner
        } else if (getItemViewType(position) == VIEW_ITEM) {
            holder.bind(mList.get(position + 2), getItemViewType(position), mList.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_HEADER;
        } else {
            return VIEW_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        //其实应该-3但是要留一个位置显示顶部view -2
        //这样在取item时候要+2
        int size = mList.size() - 2;
        if (size < 1){
            return 1;
        }else {
            return size;
        }
    }
private String test="[{\"num\":1,\"user_id\":5,\"user_nickname\":\"我要好好学习\",\"sex\":1,\"age\":18,\"city_name\":\"北京市\",\"show_photo\":\"https:\\/\\/zb-1302869529.cos.ap-shanghai.myqcloud.com\\/upload\\/20200927\\/f23cc453ec5f44ca8716dbe7dbf537db.png?imageMogr2\\/thumbnail\\/600x\",\"is_vip\":0,\"coin\":170,\"is_follow\":1},{\"num\":2,\"user_id\":4,\"user_nickname\":\"不回家\",\"sex\":1,\"age\":19,\"city_name\":\"北京市\",\"show_photo\":\"https:\\/\\/zb-1302869529.cos.ap-shanghai.myqcloud.com\\/upload\\/20200820\\/B0ED3343DA90F8A08A7D9CB870F54790.jpg?imageMogr2\\/thumbnail\\/600x\",\"is_vip\":0,\"coin\":139,\"is_follow\":1},{\"num\":3,\"user_id\":11,\"user_nickname\":\"规划好\",\"sex\":1,\"age\":18,\"city_name\":\"\",\"show_photo\":\"https:\\/\\/zb-1302869529.cos.ap-shanghai.myqcloud.com\\/upload\\/20200827\\/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2\\/thumbnail\\/600x\",\"is_vip\":0,\"coin\":70,\"is_follow\":0},\n" +
        "{\n" +
        "\"num\":2,\n" +
        "\"user_id\":4,\n" +
        "\"user_nickname\":\"不回家\",\n" +
        "\"sex\":1,\n" +
        "\"age\":19,\n" +
        "\"city_name\":\"北京市\",\n" +
        "\"show_photo\":\"https:\\/\\/zb-1302869529.cos.ap-shanghai.myqcloud.com\\/upload\\/20200820\\/B0ED3343DA90F8A08A7D9CB870F54790.jpg?imageMogr2\\/thumbnail\\/600x\",\n" +
        "\"is_vip\":0,\n" +
        "\"coin\":139,\n" +
        "\"is_follow\":1\n" +
        "},\n" +
        "{\n" +
        "\"num\":3,\n" +
        "\"user_id\":11,\n" +
        "\"user_nickname\":\"规划好\",\n" +
        "\"sex\":1,\n" +
        "\"age\":18,\n" +
        "\"city_name\":\"\",\n" +
        "\"show_photo\":\"https:\\/\\/zb-1302869529.cos.ap-shanghai.myqcloud.com\\/upload\\/20200827\\/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2\\/thumbnail\\/600x\",\n" +
        "\"is_vip\":0,\n" +
        "\"coin\":70,\n" +
        "\"is_follow\":0\n" +
        "},\n" +
        "{\n" +
        "\"num\":2,\n" +
        "\"user_id\":4,\n" +
        "\"user_nickname\":\"不回家\",\n" +
        "\"sex\":1,\n" +
        "\"age\":19,\n" +
        "\"city_name\":\"北京市\",\n" +
        "\"show_photo\":\"https:\\/\\/zb-1302869529.cos.ap-shanghai.myqcloud.com\\/upload\\/20200820\\/B0ED3343DA90F8A08A7D9CB870F54790.jpg?imageMogr2\\/thumbnail\\/600x\",\n" +
        "\"is_vip\":0,\n" +
        "\"coin\":139,\n" +
        "\"is_follow\":1\n" +
        "},\n" +
        "{\n" +
        "\"num\":3,\n" +
        "\"user_id\":11,\n" +
        "\"user_nickname\":\"规划好\",\n" +
        "\"sex\":1,\n" +
        "\"age\":18,\n" +
        "\"city_name\":\"\",\n" +
        "\"show_photo\":\"https:\\/\\/zb-1302869529.cos.ap-shanghai.myqcloud.com\\/upload\\/20200827\\/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2\\/thumbnail\\/600x\",\n" +
        "\"is_vip\":0,\n" +
        "\"coin\":70,\n" +
        "\"is_follow\":0\n" +
        "}]";
    public void setCards(List<IncomeRankingResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList=list;
      //  mList=new Gson().fromJson(test,new TypeToken<List<IncomeRankingResponse.ListBean>>(){}.getType());
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}