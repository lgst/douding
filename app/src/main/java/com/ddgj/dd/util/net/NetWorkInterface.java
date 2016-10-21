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
    /**根据id查询订制 代工*/
    public static final String GET_ORDER_BY_ID = HOST + "/findMyCustom_made.do";
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
    /**获取我的全部项目统计数量*/
    public static final String GET_MINE = HOST + "/get_mine_count.do";
    /**
     * 获取订制接口
     */
    public static final String GET_ORDER = HOST + "/findCustom_madeTwo.do";
    /**
     * 获取个人创意接口
     */
    public static final String ADD_IDEA = HOST + "/addOriginality.do";
    /**
     * 获取个人专利接口
     */
    public static final String ADD_Patent = HOST + "/addPatent.do";
    /**
     * 获取发布定制接口
     */
    public static final String ADD_Order = HOST + "/addCustom_made.do";

    /**
     * 根据类别获取订制接口
     */
    public static final String GET_ORDER_FOR_TYPE = HOST + "/findCustom_madeType.do";
    /**
     * 上传用户图像
     */
    public static final String UPDATE_USER_ICON = HOST + "/addHeadPicture.do";
    /**
     * 获取订制工厂 / 代工工厂
     */
    public static final String GET_ORDER_FACTORY = HOST + "/findFacilitatorMade.do";
    /**
     * 获取创意详情页
     */
    public static final String GET_ORIGINALITY_DETAILS = HOST + "/originalityDet.do";
    /**获取专利详情页*/
    public static final String GET_PATENT_DETAILS = HOST + "/patentDet.do";
    /**
     * 获取订制详情页
     */
    public static final String GET_ORDER_DETAILS = HOST + "/Custom_madeDetails.do";
    /**获取订制工厂详情*/
    public static final String GET_ORDER_FACTORY_DETAILS = HOST + "/factorydetails.do";
    /**获取代工产品*/
    public static final String GET_ORDER_PRODUCT_DETAILS = HOST + "/foundryDetails.do ";


}
