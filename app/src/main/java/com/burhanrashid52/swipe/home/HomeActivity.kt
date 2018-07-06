package com.burhanrashid52.swipe.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.burhanrashid52.swipe.R
import com.burhanrashid52.swipe.swipeLayout.OnItemSwiped
import com.burhanrashid52.swipe.swipeLayout.StackLayoutManager
import com.burhanrashid52.swipe.swipeLayout.StackTouchHelperCallback
import com.burhanrashid52.swipe.swipeLayout.touchelper.ItemTouchHelper
import ja.burhanrashid52.base.BaseActivity
import ja.burhanrashid52.base.getViewModel
import ja.burhanrashid52.base.repo.Status.*
import ja.burhanrashid52.base.toast
import kotlinx.android.synthetic.main.activity_main.*


class HomeActivity : BaseActivity() {


    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        homeViewModel = getViewModel()
        rvSwipe.itemAnimator = DefaultItemAnimator()
        val cardAdapter = CardsAdapter()
        rvSwipe.adapter = cardAdapter

        val stackTouchHelperCallback: StackTouchHelperCallback = object : StackTouchHelperCallback(object : OnItemSwiped {
            override fun onItemSwiped() {
                cardAdapter.removeTopItem()
                viewFlipper.displayedChild = if (cardAdapter.itemCount == 0) 2 else 1
            }

            override fun onItemSwipedLeft() {
                Log.e("SWIPE", "LEFT")
            }

            override fun onItemSwipedRight() {
                Log.e("SWIPE", "RIGHT")
            }

            override fun onItemSwipedUp() {
                Log.e("SWIPE", "UP")
            }

            override fun onItemSwipedDown() {
                Log.e("SWIPE", "DOWN")
            }
        }) {
            override fun getAllowedSwipeDirectionsMovementFlags(viewHolder: RecyclerView.ViewHolder): Int {
                return ItemTouchHelper.RIGHT or
                        ItemTouchHelper.LEFT or
                        ItemTouchHelper.UP or
                        ItemTouchHelper.DOWN
            }
        }
        val itemTouchHelper = ItemTouchHelper(stackTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvSwipe)
        rvSwipe.layoutManager = StackLayoutManager().setAngle(10)
                .setAnimationDuratuion(450)
                .setMaxShowCount(3)
                .setScaleGap(0.1f)
                .setTransYGap(0)
        rvSwipe.adapter = cardAdapter

        homeViewModel.fetchMovies().observe(this, Observer {
            when (it?.status) {
                SUCCESS -> {
                    viewFlipper.displayedChild = 1
                    cardAdapter.moviesList = it.data?.movies?.toMutableList()!!
                }
                ERROR -> {
                    viewFlipper.displayedChild = 2
                    toast("${it.message}")
                }
                LOADING -> viewFlipper.displayedChild = 0
            }
        })
    }
}
