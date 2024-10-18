package com.abrebo.tabletennishub.ui.viewmodel.Profile

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (var repository: Repository,
                                            application: Application): AndroidViewModel(application){
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    var map=MutableLiveData<HashMap<String,Any>>()

    fun getUserInfo(userEmail: String){
        viewModelScope.launch {
            map.value= repository.getUserInfo(userEmail) as HashMap<String, Any>
        }
    }


}