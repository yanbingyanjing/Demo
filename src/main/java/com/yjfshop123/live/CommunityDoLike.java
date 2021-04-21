package com.yjfshop123.live;

import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;

import org.json.JSONException;


public class CommunityDoLike{

    private static CommunityDoLike instance;

    public static CommunityDoLike getInstance() {
        if (instance == null) {
            instance = new CommunityDoLike();
        }
        return instance;
    }

    private CommunityDoLike() {
    }

    /**
     * 社区点赞
     *
     * @param object_id
     */
    public void dynamicDoLike(int object_id, boolean isReply) {
        String body = "";
        String type;
        if (isReply){
            type = "forum_reply";
        }else {
            type = "forum_dynamic";
        }
        try {
            body = new JsonBuilder()
                    .put("object_id", object_id)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/doLike", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    /**
     * 取消社区点赞
     *
     * @param object_id
     */
    public void dynamicUndoLike(int object_id, boolean isReply) {
        String body = "";
        String type;
        if (isReply){
            type = "forum_reply";
        }else {
            type = "forum_dynamic";
        }
        try {
            body = new JsonBuilder()
                    .put("object_id", object_id)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/undoLike", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    /**
     * 短视频点赞
     *
     * @param object_id
     */
    public void shortVideoDoLike(int object_id, boolean isReply) {
        String body = "";
        String type;
        if (isReply){
            type = "shortvideo_reply";
        }else {
            type = "shortvideo_dynamic";
        }
        try {
            body = new JsonBuilder()
                    .put("object_id", object_id)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortvideo/doLike", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    /**
     * 取消短视频点赞
     *
     * @param object_id
     */
    public void shortVideoUndoLike(int object_id, boolean isReply) {
        String body = "";
        String type;
        if (isReply){
            type = "shortvideo_reply";
        }else {
            type = "shortvideo_dynamic";
        }
        try {
            body = new JsonBuilder()
                    .put("object_id", object_id)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortvideo/undoLike", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }
}

