package com.genius.example

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class ViewPagerFragment : Fragment(R.layout.viewpager_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(this)
    }

    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            return if (position % 2 == 0) {
                ViewPagerActiveFragment()
            } else {
                ViewPagerEmptyFragment()
            }
        }

        override fun getItemCount(): Int = 5
    }
}