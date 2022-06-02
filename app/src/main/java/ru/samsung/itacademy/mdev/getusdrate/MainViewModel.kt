package ru.samsung.itacademy.mdev.getusdrate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val ethRate = MutableLiveData<String>()
    val rateCheckInteractor = RateCheckInteractor()

    fun onCreate() {
        //refreshRate()
    }

    fun onRefreshClicked() {
        refreshRate()
    }

    private fun refreshRate() {
        GlobalScope.launch(Dispatchers.Main) {
            val rate = rateCheckInteractor.requestRate()
            ethRate.value = rate
        }
    }

    companion object {
        const val TAG = "MainViewModel"
        val ETH_RATE_URL = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=RUB"
    }
}