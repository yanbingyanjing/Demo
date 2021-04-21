package com.yjfshop123.live.live.response;

import java.util.List;

public class BigTurntableListResponse {
    /**
     * product_price : 0
     * list : [{"id":1,"product_name":"谢谢参与","product_icon":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574679560465&di=b350fa4b05bd280b29e7f91398223a06&imgtype=0&src=http%3A%2F%2Fossweb-img.qq.com%2Fimages%2Fgamevip%2Fact%2Fa20131126hyrzsc%2Flucklyimg8.jpg","product_number":97},{"id":2,"product_name":"罗浮山门票","product_icon":"https://www.vbill.cn/front/images/newImages/logo.png","product_number":38},{"id":3,"product_name":"罗浮山嘉宝田温泉体验券","product_icon":"https://www.chinaums.com/chinaums/images/logo.png","product_number":39},{"id":4,"product_name":"精美旅游书籍《山水酿惠州》","product_icon":"http://yunshanfu.unionpay.com/pc/images/logo.png","product_number":39},{"id":5,"product_name":"谢谢参与","product_icon":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574679560465&di=b350fa4b05bd280b29e7f91398223a06&imgtype=0&src=http%3A%2F%2Fossweb-img.qq.com%2Fimages%2Fgamevip%2Fact%2Fa20131126hyrzsc%2Flucklyimg8.jpg","product_number":97},{"id":6,"product_name":"碧海湾漂流门票","product_icon":"http://cn.unionpay.com/images/images2017/logo.png?v=20161102","product_number":40},{"id":7,"product_name":"谢谢参与","product_icon":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574679560465&di=b350fa4b05bd280b29e7f91398223a06&imgtype=0&src=http%3A%2F%2Fossweb-img.qq.com%2Fimages%2Fgamevip%2Fact%2Fa20131126hyrzsc%2Flucklyimg8.jpg","product_number":95},{"id":8,"product_name":"南昆山门票","product_icon":"https://m.freemypay.com/twcms/view/default/img/logo.jpg","product_number":39}]
     */

    private int product_price;
    private List<ListBean> list;

    public int getProduct_price() {
        return product_price;
    }

    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 1
         * product_name : 谢谢参与
         * product_icon : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574679560465&di=b350fa4b05bd280b29e7f91398223a06&imgtype=0&src=http%3A%2F%2Fossweb-img.qq.com%2Fimages%2Fgamevip%2Fact%2Fa20131126hyrzsc%2Flucklyimg8.jpg
         * product_number : 97
         */

        private int id;
        private String product_name;
        private String product_icon;
        private int product_number;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getProduct_icon() {
            return product_icon;
        }

        public void setProduct_icon(String product_icon) {
            this.product_icon = product_icon;
        }

        public int getProduct_number() {
            return product_number;
        }

        public void setProduct_number(int product_number) {
            this.product_number = product_number;
        }
    }
}
