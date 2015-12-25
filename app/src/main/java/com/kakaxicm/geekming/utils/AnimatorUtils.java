package com.kakaxicm.geekming.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Administrator on 2015/7/7.
 */
public class AnimatorUtils {
    /**
     * Z轴旋转动画
     *
     * @param view        做动画的View
     * @param repeatCount 重复次数
     * @param duration    时间
     * @param listener    动画监听
     * @param angles      角度变化
     */
    public static void startRotationZAnim(View view, int repeatCount, int duration, Animator.AnimatorListener listener, float... angles) {
        PropertyValuesHolder phRotation = PropertyValuesHolder.ofFloat("rotation", angles);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, phRotation);
        animator.setRepeatCount(repeatCount);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.setDuration(duration).start();
    }

    public static void startStarAnimator(View view, int duration, boolean isShow, Animator.AnimatorListener listener) {
        PropertyValuesHolder phTranslationY = null;
        PropertyValuesHolder phAlpha = null;
        PropertyValuesHolder phScaleX = null;
        PropertyValuesHolder phScaleY = null;
        if (isShow) {
            phTranslationY = PropertyValuesHolder.ofFloat("translationY", -view.getMeasuredHeight() * 1.6f, 0);
            phAlpha = PropertyValuesHolder.ofFloat("alpha", 0.2f, 1.0f);
            phScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.2f, 1.0f);
            phScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1.0f);
        } else {
            phTranslationY = PropertyValuesHolder.ofFloat("translationY", 0, -view.getMeasuredHeight() * 1.6f);
            phAlpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
            phScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f);
            phScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f);
        }

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, phTranslationY, phAlpha, phScaleX, phScaleY);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.setDuration(duration).start();
    }

    /**
     * 心跳效果
     *
     * @param targetView
     */
    public static void startHeartBeat(View targetView) {
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.4f, 1.0f);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.4f, 1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(targetView, holderX, holderY);
        animator.setDuration(500);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static ObjectAnimator creatRotateAnimator(View targetView, float fromRotateZ, float endRotateZ, int repeatCount) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "rotation", fromRotateZ, endRotateZ);
//        animator.setAutoCancel(true);
        animator.setRepeatCount(repeatCount);
        animator.setDuration(500);
        return animator;
    }

    public static ObjectAnimator createAlphaAnimator(View targetView, float fromAlpha, float endAlpha) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "alpha", fromAlpha, endAlpha);
        return animator;
    }

    public static ObjectAnimator createDelayAlphaSplashAnimator(View targetView, long delay,long duration) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.3f, 1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(targetView, alpha);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setRepeatCount(Animation.INFINITE);
        animator.setRepeatMode(Animation.REVERSE);
        return animator;
    }
}
