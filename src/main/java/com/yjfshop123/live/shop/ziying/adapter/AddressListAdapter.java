package com.yjfshop123.live.shop.ziying.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.model.AddCartResponse;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopList;
import com.yjfshop123.live.shop.ziying.ui.AddressAddActivity;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AddressListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private DefaultAddress.AddressData[] mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public AddressListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_ziying_address, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList[position]);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.length : 0;
    }

    public void setCards(DefaultAddress.AddressData[] list) {

        mList = list;
        notifyDataSetChanged();
    }


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {

        TextView name, moren, address;
        ImageView edittext;

        public Vh(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            moren = itemView.findViewById(R.id.moren);
            address = itemView.findViewById(R.id.address);
            edittext = itemView.findViewById(R.id.edittext);
        }

        void setData(final DefaultAddress.AddressData data) {
            if (data == null) return;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMorenData(data.address_id);
                }
            });
            edittext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AddressAddActivity.class);
                    intent.putExtra("data", new Gson().toJson(data));
                    context.startActivity(intent);
                }
            });
            address.setText(data.region + " " + data.address);
            name.setText(data.name + " " + data.telephone);
            if (data.is_default == 1) moren.setVisibility(View.VISIBLE);
            else moren.setVisibility(View.GONE);
        }

    }


    public void setMorenData(String id) {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("id", id);
        LoadDialog.show(context);
        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取的数据", response);
                LoadDialog.dismiss(context);
                AddCartResponse addCartResponse = new Gson().fromJson(response, AddCartResponse.class);
                if (!TextUtils.isEmpty(addCartResponse.code) && addCartResponse.code.equals("success")) {
                    ((Activity) context).finish();
                } else NToast.shortToast(context, context.getString(R.string.shezhi_morenshibai));


            }

            @Override
            public void onFailure(int what, String error) {
                Log.d("获取的数据", error);
                LoadDialog.dismiss(context);
                NToast.shortToast(context, error);
            }
        }, HttpUtil.ziying_shop_set_moren_address_url, paraMap);
    }

}

