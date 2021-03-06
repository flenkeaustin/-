package com.xiaolian.amigo.data.network;

import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.alipay.AliPayBindInAppReq;
import com.xiaolian.amigo.data.network.model.alipay.AliPayBindQueryReq;
import com.xiaolian.amigo.data.network.model.bathroom.RecordResidenceReqDTO;
import com.xiaolian.amigo.data.network.model.common.ChangeSchoolResDTO;
import com.xiaolian.amigo.data.network.model.common.CheckSchoolRespDTO;
import com.xiaolian.amigo.data.network.model.common.CommitSchoolReqDTO;
import com.xiaolian.amigo.data.network.model.common.EmptyRespDTO;
import com.xiaolian.amigo.data.network.model.common.ApplySchoolCheckRespDTO;
import com.xiaolian.amigo.data.network.model.login.ClearTokenReqDTO;
import com.xiaolian.amigo.data.network.model.login.WeChatBindRespDTO;
import com.xiaolian.amigo.data.network.model.login.WechatLoginReqDTO;
import com.xiaolian.amigo.data.network.model.user.BindResidenceReq;
import com.xiaolian.amigo.data.network.model.user.MobileUpdateReqDTO;
import com.xiaolian.amigo.data.network.model.user.PasswordCheckReqDTO;
import com.xiaolian.amigo.data.network.model.user.PasswordUpdateReqDTO;
import com.xiaolian.amigo.data.network.model.user.PersonalUpdateReqDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleQueryReqDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleReqDTO;
import com.xiaolian.amigo.data.network.model.common.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.user.DeleteResidenceRespDTO;
import com.xiaolian.amigo.data.network.model.login.EntireUserDTO;
import com.xiaolian.amigo.data.network.model.user.PersonalExtraInfoDTO;
import com.xiaolian.amigo.data.network.model.user.QueryAvatarDTO;
import com.xiaolian.amigo.data.network.model.user.QueryUserResidenceListRespDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleRespDTO;
import com.xiaolian.amigo.data.network.model.user.ResidenceUpdateRespDTO;
import com.xiaolian.amigo.data.network.model.user.UserCertifyInfoRespDTO;
import com.xiaolian.amigo.data.network.model.user.UserGradeInfoRespDTO;
import com.xiaolian.amigo.data.network.model.user.UserResidenceDTO;
import com.xiaolian.amigo.data.network.model.user.UserResidenceInListDTO;
import com.xiaolian.amigo.data.network.model.user.UploadUserDeviceInfoReqDTO;
import com.xiaolian.amigo.data.vo.User;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 个人信息模块Api接口
 *
 * @author zcd
 * @date 17/9/15
 */
public interface IUserApi {
    /**
     * 用户默认头像列表
     */
    @POST("user/avatar/list")
    Observable<ApiResult<QueryAvatarDTO>> getAvatarList();

    /**
     * 获取用户个人信息
     */
    @POST("user/one")
    Observable<ApiResult<EntireUserDTO>> getUserInfo();

    /**
     * 用户个人中心额外信息
     */
    @POST("user/extraInfo/one")
    Observable<ApiResult<PersonalExtraInfoDTO>> getUserExtraInfo();

    /**
     * 更新用户个人信息
     */
    @POST("user/update")
    Observable<ApiResult<EntireUserDTO>> updateUserInfo(@Body PersonalUpdateReqDTO body);

    @POST("user/changeSchool/update")
    Observable<ApiResult<BooleanRespDTO>> clearToken(@Body ClearTokenReqDTO body);

    /**
     * 更新用户手机号
     */
    @POST("user/mobile/update")
    Observable<ApiResult<EntireUserDTO>> updateMobile(@Body MobileUpdateReqDTO body);

    /**
     * 更新用户密码
     */
    @POST("user/password/update")
    Observable<ApiResult<SimpleRespDTO>> updatePassword(@Body PasswordUpdateReqDTO body);

    /**
     * 用户绑定的寝室列表
     */
    @Deprecated
    @POST("user/residence/list")
    Observable<ApiResult<QueryUserResidenceListRespDTO>> queryUserResidenceList(@Body SimpleQueryReqDTO body);

    /**
     * 用户删除绑定寝室
     */
    @Deprecated
    @POST("user/residence/delete")
    Observable<ApiResult<DeleteResidenceRespDTO>> deleteResidence(@Body SimpleReqDTO body);

    /**
     * 用户绑定编辑寝室
     */
    @POST("user/residence/bind")
    @Deprecated
    Observable<ApiResult<UserResidenceInListDTO>> bindResidence(@Body BindResidenceReq body);

    /**
     * 用户密码校验
     */
    @POST("user/password/check")
    Observable<ApiResult<BooleanRespDTO>> checkPasswordValid(@Body PasswordCheckReqDTO reqDTO);

    /**
     * 检查是否有申请过更换学校
     */
    @POST("user/change/school/info")
    Observable<ApiResult<ApplySchoolCheckRespDTO>> applySchoolCheck();

    /**
     * 检查是否可以更换学校
     */
    @POST("user/changeSchool/check/new")
    Observable<ApiResult<CheckSchoolRespDTO>> changeSchoolCheck();

    /**
     *提交审核前的预处理
     */
    @POST("user/change/school/check/precondition")
    Observable<ApiResult<CheckSchoolRespDTO>>  PreApplySchoolCheck();

    /**
     *提交审核
     */
    @POST("user/change/school")
    Observable<ApiResult<ChangeSchoolResDTO>>  applyChangeSchool(@Body CommitSchoolReqDTO reqDTO);

    /**
     *支付宝是否绑定
     */
    @POST("user/alipay/bind/info")
    Observable<ApiResult<User.AlipayBindBean>> isAlipayBind();

    /**
     *
     */
    @POST("user/alipay/bind")
    Observable<ApiResult<User.AlipayBindBean>> bindAlipayInApp(@Body AliPayBindInAppReq reqDTO);

    @POST("user/change/school/cancel")
    Observable<ApiResult<CheckSchoolRespDTO>> cancelApplyChangeSchool(@Body SimpleReqDTO reqDTO);

    @POST("wechat/user/account/native/bind")
    Observable<ApiResult<WeChatBindRespDTO>> bindWechatApp(@Body WechatLoginReqDTO reqDTO);
    /**
     * 用户绑定宿舍详情
     */
    @POST("user/residence/one")
    Observable<ApiResult<UserResidenceDTO>> queryResidenceDetail(@Body SimpleReqDTO reqDTO);

    /**
     * 提交设备信息
     */
    @POST("user/deviceInfo/upload")
    Observable<ApiResult<BooleanRespDTO>> uploadDeviceInfo(@Body UploadUserDeviceInfoReqDTO reqDTO);

    /**
     * 用户绑定洗澡地址
     */
    @POST("user/residence/bath/record")
    Observable<ApiResult<UserResidenceInListDTO>> recordBath(@Body RecordResidenceReqDTO reqDTO);

    /**
     * 公共浴室列表
     */
    @POST("user/residence/bath/record/list")
    Observable<ApiResult<QueryUserResidenceListRespDTO>> bathList(@Body EmptyRespDTO dto);

    /**
     * 删除洗澡地址记录
     * @param dto
     * @return
     */
    @POST("user/residence/bath/record/delete")
    Observable<ApiResult<DeleteResidenceRespDTO>>  deleteBathRecord(@Body SimpleReqDTO dto);


    /**
     * 更新默认洗澡地址
     * @param dto
     * @return
     */
    @POST("user/residence/bath/record/default/update")
    Observable<ApiResult<ResidenceUpdateRespDTO>>  updateNormalBathroom(@Body SimpleReqDTO dto);


    /**
     * 学生认证
     * @param
     * @return
     */
    @POST("user/auth/certify")
    Observable<ApiResult<BooleanRespDTO>> certify(@Body RequestBody body);


    /**
     * 年级信息
     * @return
     */
    @POST("user/grade/info")
    Observable<ApiResult<UserGradeInfoRespDTO>> gradeInfo();

    /**
     * 查询认证信息
     * @return
     */
    @POST("user/auth/certify/info")
    Observable<ApiResult<UserCertifyInfoRespDTO>> certifyInfo();

}
