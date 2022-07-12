package com.groupphoto.app.util

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.groupphoto.app.R
import com.groupphoto.app.presentation.landing.LandingActivity
import com.groupphoto.app.util.extensions.ButtonSpecs
import com.groupphoto.app.util.extensions.getCompatColor
import com.groupphoto.app.util.extensions.showErrorDialog
import com.pawegio.kandroid.e
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import kotlinx.android.synthetic.main.activity_landing.*

class MainNavigationManager(private val landingActivity: LandingActivity) {

    /**
     * Tab Positions
     */
    companion object MainTab {
        const val ACTIVITY_TAB_POSITION = 0
        const val RINGS_TAB_POSITION = 1
        const val ADD_TAB_POSITION = 2
        const val POOLS_TAB_POSITION = 3
        const val PROFILE_TAB_POSITION = 4
    }

    /**
     * Tabs Start Destinations
     */
    private val startDestinations = listOf(
        R.id.activityFragment,
        R.id.ringsFragment,
        R.id.selectPoolFragment,
        R.id.poolsListFragment,
        R.id.profileFragment
    )

    /**
     * Navigation Controller holder
     */
    var currentController: NavController? = null

    /**
     * Navigation Controllers
     */
    private val activityLandingController: NavController =
        landingActivity.findNavController(R.id.landing_activity).apply {
            graph = navInflater.inflate(R.navigation.nav_landing).apply {
                startDestination = startDestinations[ACTIVITY_TAB_POSITION]
            }
        }
    private val ringsLandingController: NavController =
        landingActivity.findNavController(R.id.landing_rings).apply {
            graph = navInflater.inflate(R.navigation.nav_landing).apply {
                startDestination = startDestinations[RINGS_TAB_POSITION]
            }
        }
    private val addPhotoLandingController: NavController =
        landingActivity.findNavController(R.id.landing_add).apply {
            graph = navInflater.inflate(R.navigation.nav_landing).apply {
                startDestination = startDestinations[ADD_TAB_POSITION]
            }
        }
    private val poolsLandingController: NavController =
        landingActivity.findNavController(R.id.landing_pools).apply {
            graph = navInflater.inflate(R.navigation.nav_landing).apply {
                startDestination = startDestinations[POOLS_TAB_POSITION]
            }
        }
    private val profileLandingController: NavController =
        landingActivity.findNavController(R.id.landing_profile).apply {
            graph = navInflater.inflate(R.navigation.nav_landing).apply {
                startDestination = startDestinations[PROFILE_TAB_POSITION]
            }
        }


    /**
     * Tab View Containers
     */
    private val activityContainer = landingActivity.landing_activity_container
    private val ringsContainer = landingActivity.landing_rings_container
    private val addContainer = landingActivity.landing_add_container
    private val poolsContainer = landingActivity.landing_pools_container
    private val profileContainer = landingActivity.landing_profile_container

    /**
     * Tab Select Listener
     */
    val tabSelectListener by lazy {
        object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                selectTabOnPosition(tab?.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectTabOnPosition(tab?.position)
            }
        }
    }

    /**
     * Tab Selection Handler
     */
    private fun selectTabOnPosition(position: Int?) {
        landingActivity.apply {
//            main_tabs_bottom_nav.getTabAt(position!!)?.icon?.colorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
            main_tabs_bottom_nav.setTabIconTintResource(R.color.colorPrimary)

            main_tabs_bottom_nav.setSelectedTabIndicatorColor(resources.getColor(R.color.colorPrimary))
            switchTab(position ?: ACTIVITY_TAB_POSITION)
        }
    }
    fun onTabSelected(tab: TabLayout.Tab?) {


    }
    /**
     * Switch Tab
     */
    fun switchTab(tabId: Int) {
        e("switch tab $tabId")
        // hide tab views except for the selected one
        fun hideTabContainerExcept(container: View) {
            activityContainer.hide()
            ringsContainer.hide()
            addContainer.hide()
            poolsContainer.hide()
            profileContainer.hide()
            container.show()
        }

        // highlight icon and tab text
        fun highlightTab(shouldHighlight: Boolean) {
            landingActivity.main_tabs_bottom_nav.apply {
                getTabAt(tabId)?.apply {
                    customView?.isSelected = shouldHighlight
                }


            }
        }

        // show tab indicator (bar)
        fun showTabIndicator(show: Boolean) {
            landingActivity.main_tabs_bottom_nav.setSelectedTabIndicatorColor(
                landingActivity.getCompatColor(
                    if (show)
                        R.color.colorPrimary else landingActivity.resources.getColor(R.color.colorPrimary)
                )
            )
        }

        // handle tab and tab views when tab switches
        when (tabId) {
            ACTIVITY_TAB_POSITION -> {
                currentController = activityLandingController
                hideTabContainerExcept(activityContainer)
                highlightTab(true)
                showTabIndicator(true)
            }
            RINGS_TAB_POSITION -> {
                currentController = ringsLandingController
                hideTabContainerExcept(ringsContainer)
                highlightTab(true)
                showTabIndicator(true)
            }
            ADD_TAB_POSITION -> {
                currentController = addPhotoLandingController
                hideTabContainerExcept(addContainer)
                highlightTab(true)
                showTabIndicator(true)
                landingActivity.showAddPhotoDialog()
            }
            POOLS_TAB_POSITION -> {
                currentController = poolsLandingController
                hideTabContainerExcept(poolsContainer)
                highlightTab(true)
                showTabIndicator(true)
            }
            PROFILE_TAB_POSITION -> {
                currentController = profileLandingController
                hideTabContainerExcept(profileContainer)
                highlightTab(true)
                showTabIndicator(true)
            }

        }
    }

    /**
     * Manager Lifecycle Mappings to Activity
     */

    fun onCreate(savedInstanceState: Bundle?) {
        e("On Create")
        // set current controller if null
        if (savedInstanceState == null) {
            currentController = activityLandingController
        }
        // add tab select listener
        landingActivity.main_tabs_bottom_nav.addOnTabSelectedListener(tabSelectListener)
    }

    fun onBackPressed() {
        // back navigate to tab on position
        fun navigateBackToTab(position: Int) {
            switchTab(position)
            landingActivity.main_tabs_bottom_nav.apply {
                // remove tab select listener before selecting a tab
                // to avoid double tab switching
                removeOnTabSelectedListener(tabSelectListener)
                getTabAt(position)?.select()
                // add tab select listener again
                addOnTabSelectedListener(tabSelectListener)
            }
        }
        currentController?.let {
            // handle tab back navigation
            if (it.currentDestination == null || startDestinations.contains(it.currentDestination?.id)) {
                // if controller is in start destination (landing)
                when (it.currentDestination?.id) {
                    // if in home tab -> exit intent dialog
                    startDestinations[ACTIVITY_TAB_POSITION] -> {
                        landingActivity.showErrorDialog(
                            title = "Close App",
                            message = "Do you really want to exit?",
                            positiveButton = ButtonSpecs("YES") { landingActivity.finish() },
                            negativeButton = ButtonSpecs("NO"),
                            isCancelable = true
                        )
                    }

                    // if not in home/chat tab -> go to home tab
                    else -> {
                        navigateBackToTab(ACTIVITY_TAB_POSITION)
                    }
                }

            } else {
                // just pop nav controller if not in start destination
                it.popBackStack()
            }
        }
    }


}