package com.alkhatib.namazvakitleri.RetrofitApi

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "prayers")
data class  PrayersData (

    @SerializedName("Aksam"                  ) var Aksam                  : String? = null,
    @SerializedName("Gunes"                  ) var Gunes                  : String? = null,
    @SerializedName("HicriTarihKisa"         ) var HicriTarihKisa         : String? = null,
    @SerializedName("Ikindi"                 ) var Ikindi                 : String? = null,
    @SerializedName("Imsak"                  ) var Imsak                  : String? = null,
    @SerializedName("KibleSaati"             ) var KibleSaati             : String? = null,
   @PrimaryKey
     @SerializedName("MiladiTarihUzun"        ) var MiladiTarihUzun        : String,
    @SerializedName("Ogle"                   ) var Ogle                   : String? = null,
    @SerializedName("Yatsi"                  ) var Yatsi                  : String? = null
    )
