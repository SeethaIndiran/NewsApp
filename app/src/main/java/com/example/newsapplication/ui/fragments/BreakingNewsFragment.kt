package com.example.newsapplication.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.NewsViewModel
import com.example.newsapplication.R
import com.example.newsapplication.Resource
import com.example.newsapplication.adapters.NewsAdapter
import com.example.newsapplication.ui.MainActivity
import com.example.newsapplication.ui.api.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsapplication.ui.models.Article
import kotlinx.android.synthetic.main.fragment_breaking_news.*


class BreakingNewsFragment : Fragment(),NewsAdapter.setOnItemClickListener {

      lateinit var newsViewModel:NewsViewModel
     lateinit var newsAdapter: NewsAdapter

    val TAG = "BreakingNewsFragment"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_breaking_news,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()




        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer {response->

            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let{ newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }

                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let{ message->
                       Toast.makeText(activity,"An error occured: $message",Toast.LENGTH_LONG).show()

                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }

        })
    }

    private  fun hideProgressBar(){
        paginationProgressBarBreaking.visibility = View.INVISIBLE
        isLoading  = false
    }

    private  fun showProgressBar(){
        paginationProgressBarBreaking.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object:RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning= firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible &&  isScrolling
            if(shouldPaginate){
                newsViewModel.getBreakingNews("us")
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
        rvBreakingNews.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }

    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article", article)

        }
        findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment,bundle )
    }
    }





