package com.alfanthariq.moviedb.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import java.security.MessageDigest

object UserInterfaceUtil {
    fun animatePagerTransition(forward: Boolean, pager: ViewPager) {
        val animator = ValueAnimator.ofInt(0, pager.width - if (forward) pager.paddingLeft else pager.paddingRight)
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                pager.endFakeDrag()
            }

            override fun onAnimationCancel(animation: Animator) {
                pager.endFakeDrag()
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {

            private var oldDragPosition = 0

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val dragPosition = animation.animatedValue as Int
                val dragOffset = dragPosition - oldDragPosition
                oldDragPosition = dragPosition
                pager.fakeDragBy((dragOffset * if (forward) -1 else 1).toFloat())
            }
        })

        animator.duration = 250
        pager.beginFakeDrag()
        animator.start()
    }

    fun hideKeyboardFrom(context: Context) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(View(context).windowToken, 0)
    }

    fun md5(str : String): String {
        val md = MessageDigest.getInstance("MD5")
        val digested = md.digest(str.toByteArray())
        return digested.joinToString("") {
            String.format("%02x", it)
        }
    }

    fun confirmDialog(context: Context, message: String, title: String): AlertDialog.Builder {
        val dialogBuilder = AlertDialog.Builder(context)

        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            .setTitle(title)

        // create dialog box
        return dialogBuilder
    }
}