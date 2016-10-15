package com.ddgj.dd.bean;

/**
 * Created by Administrator on 2016/10/4.
 */
public class ResponseInfo {

    /**
     * data :
     * count :
     * status : 1
     * sum :
     * msg : 注册失败，账户已被使用
     */

    private String data;
    private String count;
    private int status;
    private String sum;
    private String msg;

    public ResponseInfo() {
    }

    public ResponseInfo(String data, String count, int status, String sum, String msg) {
        this.data = data;
        this.count = count;
        this.status = status;
        this.sum = sum;
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
