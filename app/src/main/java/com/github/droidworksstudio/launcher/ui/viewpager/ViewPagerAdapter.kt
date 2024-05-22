package com.github.droidworksstudio.launcher.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.droidworksstudio.launcher.ui.drawer.DrawFragment
import com.github.droidworksstudio.launcher.ui.home.HomeFragment
import com.github.droidworksstudio.launcher.ui.widgetmanager.WidgetManagerFragment


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments: ArrayList<Fragment> = arrayListOf(
        WidgetManagerFragment(),
        HomeFragment(),
        DrawFragment(),
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
