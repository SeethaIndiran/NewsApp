package com.example.newsapplication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.NewsViewModel
import com.example.newsapplication.R
import com.example.newsapplication.adapters.NewsAdapter
import com.example.newsapplication.ui.MainActivity
import com.example.newsapplication.ui.models.Article
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*


class SavedNewsFragment : Fragment(R.layout.fragment_saved_news),NewsAdapter.setOnItemClickListener {

    lateinit var viewModel :NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        setUpRecyclerView()



        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully deleted article",Snackbar.LENGTH_LONG).apply{
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles->
            newsAdapter.differ.submitList(articles)
        })
    }

    private  fun setUpRecyclerView(){

        newsAdapter = NewsAdapter(this)
        rvSavedNews.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article", article)

        }
        findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment,bundle )
    }
    }

