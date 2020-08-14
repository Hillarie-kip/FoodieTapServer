package com.techkip.foodietapserver.Remote;

import com.techkip.foodietapserver.model.MyResponse;
import com.techkip.foodietapserver.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAAdAn95KA:APA91bHIp7AtoaMumP0NECaQgY68sVW3_x14qjVDuII2iVgWzEBz4PfPDuj7emPQf1DymMDKYj0tAPW5Htote-aJZNkMw4hErC657-OkxRFyTksNeVC-Q5YFDuVdINaM6IbipdBJ1291" //key from project setting >cloud messaging >server key.firebase

    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}


