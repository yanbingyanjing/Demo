package com.yjfshop123.live.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.utils.TimeUtil;

public class SystemVideoHolder extends RecyclerView.ViewHolder {

    private TextView system_message_item_time;
    private TextView system_message_item_content;

    public SystemVideoHolder (View itemView) {
        super(itemView);

        system_message_item_time = itemView.findViewById(R.id.system_message_item_time);
        system_message_item_content = itemView.findViewById(R.id.system_message_item_content);
    }

    public void bind(MessageDB messageDB) {

       // if (messageDB.getIsShowTime() == 1){
            system_message_item_time.setVisibility(View.VISIBLE);
            system_message_item_time.setText(TimeUtil.getTimeStr(messageDB.getTimestamp()));
     //  }else {
      //      system_message_item_time.setVisibility(View.GONE);
      //  }
        system_message_item_content.setText(messageDB.getText());
    }

}

