package com.example.newsapplication

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NewsViewModelFactory(val app:Application,val newsRepository: NewsRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsRepository ) as T
    }

}