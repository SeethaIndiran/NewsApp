package com.example.newsapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapplication.ui.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertArticle(article:Article):Long

    @Query("SELECT * FROM articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
     fun deleteArticle(article:Article)
}