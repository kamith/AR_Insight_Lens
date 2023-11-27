/*
Copyright (c) 2017, Vuzix Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

*  Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

*  Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

*  Neither the name of Vuzix Corporation nor the names of
   its contributors may be used to endorse or promote products derived
   from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.example.ar_insight_lens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Main activity for speech recognition sample
 */
public class MainActivity extends Activity {
    public final String LOG_TAG = "VoiceSample";
    public final String CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent";
    Button buttonListen, buttonPopup, buttonClear, buttonRestore;
    EditText textEntryField;
    VoiceCmdReceiver mVoiceCmdReceiver;
    private boolean mRecognizerActive = false;

    /**
     * when created we setup the layout and the speech recognition
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonListen = (Button) findViewById(R.id.btn_listen);
        buttonPopup = (Button) findViewById(R.id.btn_popup);
        buttonClear = (Button) findViewById(R.id.btn_clear);
        buttonRestore = (Button) findViewById(R.id.btn_restore);
        textEntryField = (EditText) findViewById(R.id.edit_textBox);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        // It is a best practice to explicitly request focus to a button to make navigation with the
        // Vuzix buttons/touchpad more consistent to the user
        buttonListen.requestFocusFromTouch();

        buttonListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnListenClick();
            }
        });
        buttonPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnPopupClick();
            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClearClick();
            }
        });
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                OnRestoreClick();
            }
        });

        // Create the voice command receiver class
        mVoiceCmdReceiver = new VoiceCmdReceiver(this);

        // Now register another intent handler to demonstrate intents sent from the service
        myIntentReceiver = new MyIntentReceiver();
        registerReceiver(myIntentReceiver , new IntentFilter(CUSTOM_SDK_INTENT));
    }


    /**
     * Unregister from the speech SDK
     */
    @Override
    protected void onDestroy() {
        mVoiceCmdReceiver.unregister();
        unregisterReceiver(myIntentReceiver);
        super.onDestroy();
    }


    /**
     * Utility to get the name of the current method for logging
     * @return String name of the current method
     */
    public String getMethodName() {
        return LOG_TAG + ":" + this.getClass().getSimpleName() + "." + new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * Helper to show a toast
     * @param iStr String message to place in toast
     */
    private void popupToast(String iStr) {
        Toast myToast = Toast.makeText(MainActivity.this, iStr, Toast.LENGTH_LONG);
        myToast.show();
    }

    /**
     * Update the button from "Listen" to "Stop" based on our cached state
     */
    private void updateListenButtonText() {
        int newText = R.string.btn_text_listen;
        if ( mRecognizerActive ) {
            newText = R.string.btn_text_stop;
        }
        buttonListen.setText(newText);
    }

    /**
     * Handler called when "Listen" button is clicked. Activates the speech recognizer identically to
     * saying "Hello Vuzix".  Also handles "Stop" button clicks to terminate the recognizer identically
     * to a time-out
     */
    private void OnListenClick() {
        Log.e(LOG_TAG, getMethodName());
        // Trigger the speech recognizer to start/stop listening.  Listening has a time-out
        // specified in the Vuzix Smart Glasses settings menu, so it may terminate without us
        // requesting it.
        //
        // We want this to toggle to state opposite our current one.
        mRecognizerActive = !mRecognizerActive;
        // Manually calling this syncrhonizes our UI state to the recognizer state in case we're
        // requesting the current state, in which we won't be notified of a change.
        updateListenButtonText();
        // Request the new state
        mVoiceCmdReceiver.TriggerRecognizerToListen(mRecognizerActive);
    }

    /**
     * Sample handler that will be called from the "popup message" button, or a voice command
     */
    public void OnPopupClick() {
        Log.e(LOG_TAG, getMethodName());
        popupToast(textEntryField.getText().toString());
    }

    /**
     * Sample handler that will be called from the "clear" button, or a voice command
     */
    public void OnClearClick() {
        Log.e(LOG_TAG, getMethodName());
        textEntryField.setText("");
    }

    /**
     * Sample handler that will be called from the "restore" button, or a voice command
     */
    public void OnRestoreClick() {
        Log.e(LOG_TAG, getMethodName());
        textEntryField.setText(getResources().getString(R.string.default_text));
    }

    /**
     * Sample handler that will be called from the secret "Edit Text" voice command (defined in VoiceCmdReceiver.java)
     */
    public void SelectTextBox() {
        Log.e(LOG_TAG, getMethodName());
        textEntryField.requestFocus();
        // Show soft keyboard for the user to enter the value.
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textEntryField, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * A callback for the SDK to notify us if the recognizer starts or stop listening
     *
     * @param isRecognizerActive boolean - true when listening
     */
    public void RecognizerChangeCallback(boolean isRecognizerActive) {
        Log.d(LOG_TAG, getMethodName());
        mRecognizerActive = isRecognizerActive;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateListenButtonText();
            }
        });
    }

    /**
     * You may prefer using explicit intents for each recognized phrase. This receiver demonstrates that.
     */
    private MyIntentReceiver  myIntentReceiver;

    public class MyIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(LOG_TAG, getMethodName());
            Toast.makeText(context, "Custom Intent Detected", Toast.LENGTH_LONG).show();
        }
    }
}
