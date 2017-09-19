package com.xiaolian.amigo.data.network;

import com.xiaolian.amigo.data.network.model.ApiResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 文件接口
 * <p>
 * Created by zcd on 9/19/17.
 */

public interface IFileApi {
    // 上传文件
    @Multipart
    @POST("/file/upload")
    Observable<ApiResult<String>> uploadFile(@Part("file") RequestBody images);
}
