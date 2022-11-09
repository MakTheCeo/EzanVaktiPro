package com.alkhatib.namazvakitleri.RetrofitApi

import com.google.gson.annotations.SerializedName

data class District (


        @SerializedName("IlceAdi"   ) var IlceAdi   : String? = null,
        @SerializedName("IlceID"    ) var IlceID    : String? = null

)