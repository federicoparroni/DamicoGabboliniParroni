package com.example.gabdampar.travlendar.Controller.ViewController;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by federico on 12/12/17.
 */

public class PopupImageView extends AppCompatImageView {

    public PopupImageView(Context context) {
        super(context);
    }

    public PopupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PopupImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void ShowPopup() {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(this, "scaleX", 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(this, "scaleY", 1f);
        scaleUpX.setDuration(500);
        scaleUpY.setDuration(500);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", 0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 0f);
        scaleDownX.setDuration(350);
        scaleDownY.setDuration(350);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleUpX).with(scaleUpY);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.setStartDelay(800);

        AnimatorSet anim = new AnimatorSet();
        anim.playSequentially(scaleUp, scaleDown);
        anim.start();
    }

}