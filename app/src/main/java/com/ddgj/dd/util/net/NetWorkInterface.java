package com.ddgj.dd.util.net;

/**
 * 网络参数接口
 * Created by Administrator on 2016/10/4.
 */
public interface NetWorkInterface {
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILED = 1;
    /**
     * 主机地址
     */
    public static final String HOST = "http://117.34.105.120:8080/newdouding";
    /**
     * 注册接口
     */
    public static final String REGISTER = HOST + "/Registered.do";
    /**
     * 找回密码接口
     */
    public static final String FORGET_PASSWORD = HOST + "/forgetpw.do";
    /**
     * 登录接口
     */
    public static final String LOGIN = HOST + "/login.do";
    /**
     * 修改密码接口
     */
    public static final String UPDATE_PASSWORD = HOST + "/updatePW.do";
    /**
     * 修改用户信息接口
     */
    public static final String CHANGE_USER_INFO = HOST + "/Change_info.do";

    /**
     * 获取轮播图接口
     */
    public static final String GET_AD = HOST + "/findChange_picture.do";
    /**
     * 获取全部专利接口
     */
    public static final String GET_ALL_PATENT = HOST + "/findAllPatentTwo.do";
    /**
     * 获取最新专利接口
     */
    public static final String GET_NEW_PATENT = HOST + "/findNewPatent.do";
    /**
     * 获取热门专利接口
     */
    public static final String GET_HOT_PATENT = HOST + "/findHotPatent.do";
    /**
     * 获取我的专利接口
     */
    public static final String GET_MINE_PATENT = HOST + "/finfMyPatent.do";
    /**
     * 获取全部创意接口
     */
    public static final String GET_ALL_ORIGINALITY = HOST + "/findAllOriginalityTwo.do";
    /**
     * 获取最新创意接口
     */
    public static final String GET_NEW_ORIGINALITY = HOST + "/findNewOriginality.do";
    /**
     * 获取最热创意接口
     */
    public static final String GET_HOT_ORIGINALITY = HOST + "/findHotOriginality.do";
    /**
     * 获取我的创意接口
     */
    public static final String GET_MINE_ORIGINALITY = HOST + "/finfMyOriginality.do";
    /**
     * 获取我的创意接口
     */
    public static final String ADD_IDEA = HOST + "/addOriginality.do";

}
