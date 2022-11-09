package com.alkhatib.namazvakitleri.CacheStrategy

import androidx.lifecycle.ViewModel
import com.alkhatib.namazvakitleri.PrayersDataRepository
import javax.inject.Inject

// Using Dagger Hilt library to
// inject the data into the view model
//@HiltViewModel
class PrayersDataViewModel @Inject constructor(
    repository: PrayersDataRepository
) : ViewModel() {
   // val prayers = repository.getPrayers().asLiveData()
}