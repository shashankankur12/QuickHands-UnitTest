package com.quickhandslogistics.views

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.SplashContract
import com.quickhandslogistics.presenters.SplashPresenter
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity(), SplashContract.View {

    private var isClearSession = false

    private lateinit var splashPresenter: SplashPresenter

    companion object {
        private const val LOGO_ANIMATION_DURATION: Long = 700
        private const val SPLASH_NORMAL_DELAY: Long = 2000
        const val SPLASH_AFTER_LOGOUT_DELAY: Long = 500
        const val ARG_IS_CLEAR_SESSION = "ARG_IS_CLEAR_SESSION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showLogoAnimation()

        intent.extras?.let { bundle ->
            isClearSession = bundle.getBoolean(ARG_IS_CLEAR_SESSION, false)
        }

        splashPresenter = SplashPresenter(this, resources, sharedPref)

        if (isClearSession) {
            splashPresenter.performLogout()
        } else {
            splashPresenter.decideNextActivity(SPLASH_NORMAL_DELAY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        splashPresenter.onDestroy()
    }

    private fun showLogoAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_up)
        anim.duration = LOGO_ANIMATION_DURATION
        imageViewSplashLogo.startAnimation(anim)
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showNextScreen(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            startIntent(DashBoardActivity::class.java)
        } else {
            startIntent(LoginActivity::class.java)
        }
        finishAffinity()
    }
}