package com.saveetha.cineselect

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * A utility object to simplify navigation between activities.
 */
object NavigationHelper {

    /**
     * Navigates to a specified activity.
     *
     * @param context The current context or activity.
     * @param destination The class of the activity to navigate to.
     */
    fun navigateTo(context: Context, destination: Class<*>) {
        val intent = Intent(context, destination)
        context.startActivity(intent)
    }

    /**
     * Finishes the current activity to go back to the previous one in the stack.
     *
     * @param activity The activity to finish.
     */
    fun navigateBack(activity: Activity) {
        activity.finish()
    }
}