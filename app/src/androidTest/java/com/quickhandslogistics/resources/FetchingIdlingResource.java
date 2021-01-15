package com.quickhandslogistics.resources;

import androidx.test.espresso.IdlingResource;

import okhttp3.Dispatcher;

public class FetchingIdlingResource implements IdlingResource {

    private IdlingResource.ResourceCallback resourceCallback;

    public FetchingIdlingResource(Dispatcher okHttpClientDispatcher) {
        okHttpClientDispatcher.setIdleCallback(() -> {
            if (resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
        });
    }

    @Override
    public String getName() {
        return FetchingIdlingResource.class.getSimpleName();
    }

    @Override
    public boolean isIdleNow() {
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        resourceCallback = callback;
    }
}