package org.northwinds.amsatstatus.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.northwinds.amsatstatus.AmsatApi
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val mApi: AmsatApi) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}
