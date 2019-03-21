package com.record360.sample;
//
// Copyright (c) 2017 Record360. All rights reserved.
//


import androidx.multidex.MultiDexApplication;
import androidx.appcompat.app.AppCompatDelegate;

public class SampleMultiDexApplication extends MultiDexApplication {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
