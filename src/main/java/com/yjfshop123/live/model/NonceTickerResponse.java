package com.yjfshop123.live.model;

public class NonceTickerResponse {
    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Result[] getTickets() {
        return tickets;
    }

    public void setTickets(Result[] tickets) {
        this.tickets = tickets;
    }

    private String transactionTime;
    private Result[] tickets;

    public class Result {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getExpire_time() {
            return expire_time;
        }

        public void setExpire_time(String expire_time) {
            this.expire_time = expire_time;
        }

        public String getExpire_in() {
            return expire_in;
        }

        public void setExpire_in(String expire_in) {
            this.expire_in = expire_in;
        }

        private String expire_time;
        private String expire_in;
    }

}
