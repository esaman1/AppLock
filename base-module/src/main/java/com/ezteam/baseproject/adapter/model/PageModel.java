package com.ezteam.baseproject.adapter.model;

import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;

@Keep
public class PageModel {
    private Fragment fragment;
    private String title;
    private int resIconId;

    public PageModel(Fragment fragment, String title, int resIconId) {
        this.fragment = fragment;
        this.title = title;
        this.resIconId = resIconId;
    }

    public PageModel(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResIconId() {
        return resIconId;
    }

    public void setResIconId(int resIconId) {
        this.resIconId = resIconId;
    }
}
