package com.quickhandslogistics.modified.views

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.SplashContract
import com.quickhandslogistics.modified.presenters.SplashPresenter
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity(), SplashContract.View {

    private lateinit var splashPresenter: SplashPresenter

    companion object {
        private const val LOGO_ANIMATION_DURATION: Long = 700
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        showLogoAnimation()

        splashPresenter = SplashPresenter(this, sharedPref)
        splashPresenter.decideNextActivity()
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
    override fun showNextScreen(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            startIntent(DashBoardActivity::class.java, isFinish = true)
        } else {
            startIntent(LoginActivity::class.java, isFinish = true)
        }
    }
}