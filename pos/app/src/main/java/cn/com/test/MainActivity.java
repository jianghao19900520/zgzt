package cn.com.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initData();
//        postDataWithParame();
        HttpAPi.getInstance().requestRedTypeList(new OnHttpCallBack<Object>(){


            @Override
            public void onSuccess(Object o) {
                System.out.println("test2:onSuccess");
                System.out.println("test2:onSuccess"+String.valueOf(o));
            }

            @Override
            public void onFail(int httpCode, int statusCode, String msg) {
                System.out.println("test:onFailure"+msg);
            }
        });
    }

    public void getDatasync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://api.2025123.com.cn/business_xyml/services/pointofsales/pointofsaleslogin")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    okhttp3.Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.d("@@@kwwl", "response.code()==" + response.code());
                        Log.d("@@@kwwl", "response.message()==" + response.message());
                        Log.d("@@@kwwl", "res==" + response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void postDataWithParame() {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("pointofsalesCode", "1801CA889956");//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url("http://api.2025123.com.cn/business_xyml/services/pointofsales/pointofsaleslogin")
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("@@@kwwl", "onFailure + response.body().string()");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.d("@@@kwwl", "res==" + response.body().string());
                Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
            }
        });//回调方法的使用与get异步请求相同，此时略。
    }



    private void initData() {
        // 创建一个非常简单的REST适配器，它指向 GitHub 的 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.2025123.com.cn")
//                .baseUrl("http://wwww.baidu.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 创建一个我们的 GitHub API 接口的实例。
        MainService service = retrofit.create(MainService.class);

        // 创建一个调用实例来查找都有哪些大神对 Retrofit 做出了贡献。
//        HashMap hashMap = new HashMap();
//        hashMap.put("pointofsalesCode", "1801CA889956");
        Call<JSONObject> call = service.getCall();
        Log.d(TAG, "main: " + call);

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + "isSuccessful");

                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
