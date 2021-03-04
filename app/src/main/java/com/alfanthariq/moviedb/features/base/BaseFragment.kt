package com.alfanthariq.moviedb.features.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.alfanthariq.moviedb.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<in V : BaseMvpView, T : BasePresenter<V>> : DialogFragment(), BaseMvpView {
    private var pDialog: Dialog? = null
    private lateinit var context_ : Context
    private lateinit var view_: View

    override fun getContext(): Context = context_

    override fun onAttach(context: Context) {
        super.onAttach(context)

        context_ = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view_ = inflater.inflate(layoutId(), container, false)
        mPresenter.attachView(this as V)
        return view_
    }

    protected abstract var mPresenter: T

    @LayoutRes
    abstract fun layoutId(): Int

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.detachView()
    }

    override fun showLoadingDialog(message: String?) {
        if (pDialog == null) pDialog = Dialog(context_)

        pDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pDialog?.setContentView(R.layout.dialog_loading)
        pDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //val progressBar = pDialog?.findViewById(R.id.progressBar) as ProgressBar
        val msg = pDialog?.findViewById(R.id.txt_message) as TextView
        if (message != null) msg.text = message
        else msg.text = getString(R.string.loading)
        pDialog?.setCancelable(false)
        pDialog?.show()
    }

    override fun hideLoadingDialog() {
        if (pDialog != null && pDialog?.isShowing!!) {
            pDialog?.dismiss(); pDialog = null
        }
    }

    override fun showError(error: String?) {
        Toast.makeText(context_, error, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(context_, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackError(error: String?) : Snackbar {
        val error2 = if (error.isNullOrEmpty()) "" else error
        val view:View = view_.findViewById(android.R.id.content)
        val snackbar : Snackbar = Snackbar.make(view, error2, Snackbar.LENGTH_INDEFINITE); return snackbar
    }

    override fun showSnackMessage(message: String?) : Snackbar {
        val message2 = if (message.isNullOrEmpty()) "" else message
        val view:View = view_.findViewById(android.R.id.content)
        val snackbar : Snackbar = Snackbar.make(view, message2, Snackbar.LENGTH_LONG); return snackbar
    }
}