package com.alkhatib.namazvakitleri

    import retrofit2.Call
    import retrofit2.http.GET
    import retrofit2.http.Path

const val website="https://ezanvakti.herokuapp.com/"
    interface RetrofitAPI {
        var selectedCityId: Int
        var selectedCountryId:Int
        // as we are making get request
        // so we are displaying GET as annotation.
        // and inside we are passing
        // last parameter for our url.



        @GET("sehirler/2")
        fun  // as we are calling data from array
        // so we are calling it with json object
      getCity()  : Call<ArrayList<City>?>



        @GET("ilceler/{city_id}")
        fun  // as we are calling data from array
        // so we are calling it with json object
                getDistrict(@Path(value="city_id", encoded = true) id:Int)  : Call<ArrayList<District>?>


        @GET("vakitler/{district_id}")
        fun  // as we are calling data from array
        // so we are calling it with json object
                getPrayersData(@Path(value="district_id", encoded = true) id:Int)  : Call<ArrayList<PrayersData>?>
    }
