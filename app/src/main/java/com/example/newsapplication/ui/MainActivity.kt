package com.example.newsapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapplication.NewsRepository
import com.example.newsapplication.NewsViewModel
import com.example.newsapplication.NewsViewModelFactory
import com.example.newsapplication.R
import com.example.newsapplication.db.ArticleDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel:NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelFactory(application,newsRepository)

        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment= supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController= navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

    }
}