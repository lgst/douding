package com.ddgj.dd.util.net;

/**
 * 网络参数接口
 * Created by Administrator on 2016/10/4.
 */
public interface NetWorkInterface {
    int STATUS_SUCCESS = 0;
    int STATUS_FAILED = 1;
    /**
     * 主机地址
     */
    String HOST = "http://www.51douding.com";
//    String HOST = "http://192.168.2.180:8080/newdouding";

    /**
     * 注册接口
     */
    String REGISTER = HOST + "/Registered.do";
    /**
     * 找回密码接口
     */
    String FORGET_PASSWORD = HOST + "/forgetpw.do";
    /**
     * 登录接口
     */
    String LOGIN = HOST + "/login.do";
    /**
     * 修改密码接口
     */
    String UPDATE_PASSWORD = HOST + "/updatePW.do";
    /**
     * 修改用户信息接口
     */
    String CHANGE_USER_INFO = HOST + "/Change_info.do";

    /**
     * 获取轮播图接口
     */
    String GET_AD = HOST + "/findChange_picture.do";
    /**
     * 获取全部专利接口
     */
    String GET_ALL_PATENT = HOST + "/findAllPatentTwo.do";
    /**
     * 获取最新专利接口
     */
    String GET_NEW_PATENT = HOST + "/findNewPatent.do";
    /**
     * 获取热门专利接口
     */
    String GET_HOT_PATENT = HOST + "/findHotPatent.do";
    /**
     * 获取我的专利接口
     */
    String GET_MINE_PATENT = HOST + "/finfMyPatent.do";
    /**
     * 根据id查询订制 代工
     */
    String GET_ORDER_BY_ID = HOST + "/findCustom_made.do";
    /**
     * 获取全部创意接口
     */
    String GET_ALL_ORIGINALITY = HOST + "/findAllOriginalityTwo.do";
    /**
     * 获取最新创意接口
     */
    String GET_NEW_ORIGINALITY = HOST + "/findNewOriginality.do";
    /**
     * 获取最热创意接口
     */
    String GET_HOT_ORIGINALITY = HOST + "/findHotOriginality.do";
    /**
     * 获取我的创意接口
     */
    String GET_MINE_ORIGINALITY = HOST + "/finfMyOriginality.do";
    /**
     * 获取我的全部项目统计数量
     */
    String GET_MINE = HOST + "/get_mine_count.do";
    /**
     * 获取订制接口
     */
    String GET_ORDER = HOST + "/findCustom_madeTwo.do";
    /**
     * 获取个人创意接口
     */
    String ADD_IDEA = HOST + "/addOriginality.do";
    /**
     * 获取个人专利接口
     */
    String ADD_Patent = HOST + "/addPatent.do";
    /**
     * 获取发布定制接口
     */
    String ADD_Order = HOST + "/addCustom_made.do";

    /**
     * 根据类别获取订制接口
     */
    String GET_ORDER_FOR_TYPE = HOST + "/findCustom_madeType.do";
    /**
     * 上传用户图像
     */
    String UPDATE_USER_ICON = HOST + "/addHeadPicture.do";
    /**
     * 获取订制工厂 / 代工工厂
     */
    String GET_ORDER_FACTORY = HOST + "/findFacilitatorMade.do";
    /**
     * 获取创意详情页
     */
    String GET_ORIGINALITY_DETAILS = HOST + "/originalityDet.do";
    /**
     * 删除创意
     */
    String DELETE_ORIGINALITY = HOST + "/delOriginality.do";
    /**
     * 获取专利详情页
     */
    String GET_PATENT_DETAILS = HOST + "/patentDet.do";
    /**
     * 删除专利
     */
    String DELETE_PATENT = HOST + "/delPatent.do";
    /**
     * 获取订制详情页
     */
    String GET_ORDER_DETAILS = HOST + "/Custom_madeDetails.do";
    String DELETE_ORDER = HOST + "/delCustom_made.do";
    /**
     * 获取订制工厂详情
     */
    String GET_ORDER_FACTORY_DETAILS = HOST + "/factorydetails.do";
    /**
     * 获取代工产品
     */
    String GET_ORDER_PRODUCT_DETAILS = HOST + "/foundryDetails.do";
    /***
     * 添加评论
     * OriginalityDetails.do
     */
    String ADD_COMMENT_BBS = HOST + "/addFollowCard.do";
    /***
     * 获取所有评论
     * OriginalityDetails.do
     */
    String GET_ALL_COMMENT_BBS = HOST + "/findFollowCard.do";

    /**
     * 获取自己的订制接口
     */
    /**
     * 添加评论接口<br>
     * topic_id 主题id<br>
     * topic_type 主题类型 0为个人创意评论 1为个人创意产品评论 2为私人订制产品评论 3为代工产品评论<br>
     * c_content 评论内容<br>
     * from_u_id 评论用户id
     */
    String ADD_COMMENT = HOST + "/addComments.do";
    /**
     * 查看评论
     */
    String GET_COMMENT = HOST + "/findComments.do";

    /**
     * 发表帖子
     */
    String PUBLISH_BBS = HOST + "/addSendCard.do";
    /**
     * 获取全部帖子
     */
    String GET_ALL_POST = HOST + "/getAllPost.do";
    /**
     * 获取全部帖子
     */
    String GET_HOT_POST = HOST + "/findHotPost.do";
    /**
     * 获取全部帖子
     */
    String GET_POST_DETAIL = HOST + "/findPostDetail.do";
    /**
     * 个人创意点赞
     * originality_id
     */
    String ORIGINALITY_SUPPORT = HOST + "/modifyOriginalityPraise.do";
    /**
     * 创意点赞
     */
    String PATENT_SUPPORT = HOST + "/modifyPantentPraise.do";
    /**
     * 获取工厂接口
     */
    String GET_FACILITATORMADE = HOST + "/facilitatorsSearch.do";
    /**
     * 获取产品接口
     */
    String GET_PRODUCTMADE = HOST + "/findAllOriginalityTwo.do";
    /***
     * 获取工厂详情接口
     */
    String GET_FAC_DETAILS = HOST + "/factorydetails.do";
    /***
     * 获取创意产品详情接口
     * OriginalityDetails.do
     */
    String GET__PRODUCT_DETAILS = HOST + "/OriginalityDetails.do";

    /**
     * 版本更新检查
     */
    String CHECK_UPDATE = HOST + "/updateVersions.do";

    /**
     * 获取自己的订制接口
     */
    String GET_MINE_ORDER = HOST + "/findMyCustom_made.do";
    /**
     * 添加收藏接口
     */
    String ADD_FAVORITE = HOST + "/addCollections.do";
    /**
     * 删除收藏接口
     */
    String DELETE_FAVORITE = HOST + "/delCollections.do";
    /**
     * 获取我的收藏接口
     */
    String GET_FAVORITE = HOST + "/findMyCollections.do";

    /**
     * 上传异常信息接口
     */
    String UPLOAD_ERROR = HOST + "/addError_infor.do";

    /**
     * 查询手机号码是否已经注册
     */
    String CHECK_PHONE_IS_USED = HOST + "/findPhone.do";
    /**
     * 获得订制详情
     */
    String GET_ORDER_DETAIL = HOST + "/findCustom_madeNum.do";
    /**
     * 获得下单用户信息
     */
    String GET_ORDER_USERS = HOST + "/findCustom_madeAndOrder.do";
    /**
     * 订制下单
     */
    String ADD_ORDER = HOST + "/addOrder.do";
    /**
     * 查询订单
     */
    String QUERY_ORDER = HOST + "/findOrder.do";
    /**
     * 拒绝订单合作
     * order_id
     */
    String REGECT_ORDER = HOST + "/refuseCooperation.do";
    /**
     * 同意合作
     */
    String AGREE_ORDER = HOST + "/agreedCooperation.do";

    /*
    * 申请验收
    * */
    String ACCEPTANCE_CHECK = HOST + "/successOrder.do";
    /**
     * 同意验收
     */
    String AGREE_CHECK = HOST + "/agreedAcceptance.do";
    /**
     * 拒绝验收
     */
    String REGECT_CHECK = HOST + "/refuseAcceptance.do";
    /**
     * 发布悬赏
     */
    String ADD_REWARD = HOST + "/addReward.do";
    /**
     * 参加悬赏
     */
    String ADD_REWARD_ORDER = HOST + "/addRewardTask.do";
    /**
     * 获取悬赏列表
     */
    String GET_REWARD = HOST + "/findRewardList.do";
    /**
     * 发布招标
     */
    String ADD_TENDER = HOST + "/addInviteTenders.do";
    /**
     * 获取招标
     */
    String GET_TENDER = HOST + "/findInviteTendersList.do";
    /**
     * 投标
     */
    String ADD_TENDER_ORDER = HOST + "/addTender.do";
    /**
     * 提交投标
     */
    String COMMIT_TENDER_ORDER = HOST + "/modifyTender.do";
    /**
     * 提交悬赏任务作品
     */
    String COMMIT_REWARD_ORDER = HOST + "/modifyRewardTask.do";
    /**
     * 获取我接的悬赏
     */
    String GET_MINE_REWARD_ORDER = HOST + "/findMyRewardTask.do";
    /**
     * 查询我的悬赏
     */
    String GET_MINE_REWARD = HOST + "/findMyReward.do";
    /**
     * 获取我的招标
     */
    String GET_MINE_TENDER = HOST + "/findMyInviteTenders.do";
    /**
     * 获取我的投标
     */
    String GET_MINE_TENDER_ORDER = HOST + "/findMyTender.do";
    /**
     * 查询创意详情
     */
    String GET_ORIGINALITY_DETAIL = HOST + "/findByOriginalityId.do";
    /**
     * 查询专利详情
     */
    String GET_PATENT_DETAIL = HOST + "/findByPatentId.do";
    /**
     * 查询工厂详情
     */
    String GET_FACTORY_DETAIL = HOST + "/findbyId_fcilitator.do";
    /**
     * 查询代工产品详情
     */
    String GET_DAIGONF_PRODUCT_DETAIL = HOST + "/findCustom_made.do";
    /**
     * 取消收藏接口
     */
    String QUXIAO_FAVORITE = HOST + "/cancelCollection.do";
    /**
     * 获取采购列表
     * /procurementByType.do
     */
    String GET_PROCUREMENT = HOST + "/procurementByType.do";
    /**
     * 发布采购
     */
    String ADD_PROCUREMENT = HOST + "/insertProcurement.do";
    /**
     * 承接/updateContinue.do
     */
    String CONTINUE = HOST + "/updateContinue.do";
}
