package com.example.mayurpractical.service;


import com.example.mayurpractical.model.CountryModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetCountryDataService {

    @GET("country/get/all")
    Call<CountryModel> getJSON();
}
