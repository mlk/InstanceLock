package com.github.mlk.instancelock;

class StoreApplicationStartupListener implements ApplicationStartupListener {
    String message = null;
    @Override
    public void applicationStartup(String message) {
        this.message = message;
    }
}
