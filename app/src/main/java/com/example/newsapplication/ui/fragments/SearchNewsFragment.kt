package com.example.newsapplication.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.NewsViewModel
import com.example.newsapplication.R
import com.example.newsapplication.Resource
import com.example.newsapplication.adapters.NewsAdapter
import com.example.newsapplication.ui.MainActivity
import com.example.newsapplication.ui.api.Constants
import com.example.newsapplication.ui.api.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newsapplication.ui.models.Article
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment(),NewsAdapter.setOnItemClickListener {

   private lateinit var viewModel:NewsViewModel
   private lateinit var newsAdapter: NewsAdapter
     private  val TAG = "SearchNewsFragment"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel


        setUpRecyclerView()



        var job: Job? = null
        etSearch.addTextChangedListener { editable->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let{
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {response->

            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let{ newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles)
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            rvSearchNews.setPadding(0,0,0,0)
                        }

                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let{ message->
                        Toast.makeText(activity,"An error occured:$message",Toast.LENGTH_LONG).show()

                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }

        })
    }

    private  fun hideProgressBar(){
        paginationProgressBarSearch.visibility = View.INVISIBLE
        isLoading = false
    }

    private  fun showProgressBar(){
        paginationProgressBarSearch.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning= firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible &&  isScrolling
            if(shouldPaginate){
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }

    private  fun setUpRecyclerView(){

        newsAdapter = NewsAdapter(this)
        rvSearchNews.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener((this@SearchNewsFragment.scrollListener))
        }
    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article", article)

        }
        findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,bundle )
    }
    }





