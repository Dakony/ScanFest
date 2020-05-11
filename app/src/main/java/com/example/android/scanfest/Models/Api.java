package com.example.android.scanfest.Models;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("sms")
    Call<ResponseBody> sendSms(
            @Field("phoneNumber") String phoneNumber,
            @Field("message") String message
    );
}
