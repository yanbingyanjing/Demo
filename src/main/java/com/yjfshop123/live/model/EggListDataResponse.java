package com.yjfshop123.live.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EggListDataResponse {
    public List<EggListData> list;
    public class EggListData {
        /**
         *  "head_icon": "/XX/XX",//用户图像地址
         *         "name": "xxx",//用户名字

         *         "user_id": "xxx",//用户名字
         *         "phone": "xxxxxxxx",//用户手机号
         *         "order_type": "加成", // 订单类型   加成 解锁 获赏 目标 助推 收币 分红 兑换选品 打赏 提币  手续费
         *         "change_des": "-10金蛋", // 变动数量
         *         "date": "2020-08-10-18:06:06", // 发生时间
         */

        public String order_type;
        public String  change_des;
        public String  user_id;
        public String  name;
        public String date;
    }

    public static   String testData = "{\n" +
            "    \n" +
            "    \"list\": [\n" +
            "      {\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户1\",\n" +
            "        \"phone\": \"18532322451\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }  , \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户2\",\n" +
            "        \"phone\": \"18532322452\",\n" +
            "        \"order_type\": \"选品中\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }  , \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户3\",\n" +
            "        \"phone\": \"18532322453\",\n" +
            "        \"order_type\": \"提币\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }  , \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户4\",\n" +
            "        \"phone\": \"18532322454\",\n" +
            "        \"order_type\": \"收币\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }  , \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户5\",\n" +
            "        \"phone\": \"18532322455\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }, \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户6\",\n" +
            "        \"phone\": \"18532322455\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }, \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户7\",\n" +
            "        \"phone\": \"18532322455\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }, \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户8\",\n" +
            "        \"phone\": \"18532322455\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }, \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户9\",\n" +
            "        \"phone\": \"18532322455\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }, \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户10\",\n" +
            "        \"phone\": \"18532322455\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        }, \n" +
            "{\n" +
            "        \"head_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"name\": \"用户11\",\n" +
            "        \"phone\": \"18532322455\",\n" +
            "        \"order_type\": \"加成\",\n" +
            "        \"change_des\": \"-10金蛋\", \n" +
            "        \"date\": \"2020-08-10-18:06:06\"\n" +
            "        } \n" +
            "        ]\n" +
            "    \n" +
            "}";

}
