package com.evg3559programmer.overtimecalendar.composeUI

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdsViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle): ViewModel() {

   private val _visibleAds = MutableLiveData<Int>().apply {
      value = View.VISIBLE
   }
   val visibleAds: LiveData<Int> = _visibleAds
   fun setVisibleAds(visible:Int){
      _visibleAds.postValue(visible)
   }
   private val _visibleBar = MutableLiveData<Boolean>().apply {
      value = true
   }
   val visibleBar: LiveData<Boolean> = _visibleBar
   fun setVisibleBar(visible:Boolean){
      _visibleBar.postValue(visible)
   }
   private val _visibleCross = MutableLiveData<Boolean>().apply {
      value = false
   }
   val visibleCross: LiveData<Boolean> = _visibleCross
   fun setVisibleCross(visible:Boolean){
      _visibleCross.postValue(visible)
   }
   private val _loadedAds = MutableLiveData<Boolean>().apply {
      value = false
   }
   val loadedAds: LiveData<Boolean> = _loadedAds
   fun setLoadedAds(visible:Boolean){
      _loadedAds.postValue(visible)
   }

   private val _errorAds = MutableLiveData<Boolean>(false).apply {
      value = false
   }
   val errorAds: LiveData<Boolean> = _loadedAds
   fun setErrorAds (visible:Boolean){
      _loadedAds.postValue(visible)
   }
}