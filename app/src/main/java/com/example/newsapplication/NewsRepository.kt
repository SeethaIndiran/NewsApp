package com.example.newsapplication

import com.example.newsapplication.db.ArticleDatabase
import com.example.newsapplication.ui.api.RetrofitInstance
import com.example.newsapplication.ui.models.Article

class NewsRepository(private val db:ArticleDatabase) {

    suspend fun getBreakingNews(countryCode:String, pageNumber:Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String, pageNumber:Int)=
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article:Article) = db.getArticleDao().insertArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}