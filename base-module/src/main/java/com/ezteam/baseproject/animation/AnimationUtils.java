package com.ezteam.baseproject.animation;

import android.os.Build;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;


public class AnimationUtils {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void fadeIn(View view, ViewGroup parent, int duration, Transition.TransitionListener listener) {
        AnimationUtils.animation(view, parent, duration, new Fade(Fade.IN), listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void fadeOut(View view, ViewGroup parent, int duration, Transition.TransitionListener listener) {
        AnimationUtils.animation(view, parent, duration, new Fade(Fade.OUT), listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void slideIn(View view, ViewGroup parent, int duration, Transition.TransitionListener listener) {
        AnimationUtils.animation(view, parent, duration, new Slide(Gravity.START), listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void slideOut(View view, ViewGroup parent, int duration, Transition.TransitionListener listener) {
        AnimationUtils.animation(view, parent, duration, new Slide(Gravity.BOTTOM), listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void animation(View view, ViewGroup parent, int duration, Transition transition, Transition.TransitionListener listener) {
        transition.setDuration(duration);
        transition.addTarget(view);
        if (listener != null) {
            transition.addListener(listener);
        }
        TransitionManager.beginDelayedTransition(parent, transition);
    }
}
