package com.record360.sample;
//
// Copyright (c) 2017 Record360. All rights reserved.
//


import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

public class SampleMultiDexApplication extends MultiDexApplication {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
