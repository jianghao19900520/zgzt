package cn.com.test;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by zixing
 * Date 2018/10/18.
 * desc ：
 */

public interface MainService {

    @GET("/business_xyml/services/pointofsales/pointofsaleslogin")
    Call<JSONObject> getCall();

    @FormUrlEncoded
    @POST("/business_xyml/services/pointofsales/pointofsaleslogin")
    Call<JSONObject> getNewsData(@Field("pointofsalesCode") String key);

}
