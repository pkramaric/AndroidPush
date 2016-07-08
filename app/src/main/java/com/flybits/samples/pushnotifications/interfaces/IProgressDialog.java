package com.flybits.samples.pushnotifications.interfaces;

public interface IProgressDialog {

    void onProgressStart(String text, boolean isCancelable);

    void onProgressEnd();

}
