package com.alkhatib.namazvakitleri.RetrofitApi

import com.google.gson.annotations.SerializedName

data class City (

    @SerializedName("SehirAdi"   ) var SehirAdi   : String? = null,
    @SerializedName("SehirID"    ) var SehirID    : String? = null

)