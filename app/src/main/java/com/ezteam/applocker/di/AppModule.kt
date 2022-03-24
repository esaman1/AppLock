package com.ezteam.applocker.di

import com.ezteam.applocker.viewmodel.*
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single { AppLockViewModel(androidApplication()) }
    single { TakePhotoViewModel(androidApplication()) }
    single { HideImageViewModel(androidApplication()) }
    single { HideVideoModel(androidApplication()) }
    single { RestoreImageViewModel(androidApplication()) }
    single { RestoreAudioViewModel(androidApplication()) }
    single { RestoreVideoViewModel(androidApplication()) }
    single { NotifyViewModel(androidApplication()) }
    single { CleanerViewModel(androidApplication()) }
    single { ThemeViewModel(androidApplication()) }
    single { SameImageViewModel(androidApplication()) }
}