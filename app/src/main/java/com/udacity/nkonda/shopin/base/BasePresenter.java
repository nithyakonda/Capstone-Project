package com.udacity.nkonda.shopin.base;

public interface BasePresenter<S extends BaseState> {

    void start(S state);

    S getState();

}