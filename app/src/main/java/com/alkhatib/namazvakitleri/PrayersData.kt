package com.alkhatib.namazvakitleri

import com.google.gson.annotations.SerializedName


data class  PrayersData (

    @SerializedName("Aksam"                  ) var Aksam                  : String? = null,
    @SerializedName("Gunes"                  ) var Gunes                  : String? = null,
    @SerializedName("HicriTarihKisa"         ) var HicriTarihKisa         : String? = null,
    @SerializedName("Ikindi"                 ) var Ikindi                 : String? = null,
    @SerializedName("Imsak"                  ) var Imsak                  : String? = null,
    @SerializedName("KibleSaati"             ) var KibleSaati             : String? = null,
    @SerializedName("MiladiTarihUzun"        ) var MiladiTarihUzun        : String? = null,
    @SerializedName("Ogle"                   ) var Ogle                   : String? = null,
    @SerializedName("Yatsi"                  ) var Yatsi                  : String? = null
    )
