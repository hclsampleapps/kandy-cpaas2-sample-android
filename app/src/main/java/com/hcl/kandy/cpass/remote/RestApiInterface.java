package com.hcl.kandy.cpass.remote;


import com.hcl.kandy.cpass.remote.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ashish Goel on 2/1/2019.
 */
public interface RestApiInterface {
    @FormUrlEncoded
    @POST(API.LOGIN_URL)
    Call<LoginResponse> loginAPI(@Field("username") String username,
                                 @Field("password") String password,
                                 @Field("client_id") String client_id,
                                 @Field("grant_type") String grant_type,
                                 @Field("scope") String scope);
}
