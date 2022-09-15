package com.lhl.screen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class FramworkFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {
    private static final String TAG = "FramworkFragmentLifecycleCallbacks";
    ScreenManager mFramworkManager;
    FramworkActivityLifecycleCallbacks callbacks;

    FramworkFragmentLifecycleCallbacks(ScreenManager framworkManager, FramworkActivityLifecycleCallbacks framworkActivityLifecycleCallbacks) {
        mFramworkManager = framworkManager;
        callbacks = framworkActivityLifecycleCallbacks;
    }

    @Override
    public void onFragmentPreAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
        super.onFragmentPreAttached(fm, f, context);
//        Log.e(TAG,"onFragmentPreAttached "+f.getClass().getSimpleName());
    }


    @Override
    public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
//        Log.e(TAG,"onFragmentViewCreated "+f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentStarted(fm, f);
//        Log.e(TAG,"onFragmentStarted "+f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentResumed(fm, f);
        mFramworkManager.setScreen(f);
//        Log.e(TAG,"onFragmentResumed "+f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentDetached(fm, f);
//        Log.e(TAG,"onFragmentDetached "+f.getClass().getSimpleName());
    }
}

