package com.example.newsapplication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.newsapplication.NewsViewModel
import com.example.newsapplication.R
import com.example.newsapplication.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*


class  ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel:NewsViewModel
    val args : ArticleFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        val article = args.article
        webView.apply{
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
        }
    }
}