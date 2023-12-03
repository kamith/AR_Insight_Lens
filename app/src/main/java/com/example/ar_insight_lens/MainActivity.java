/*
    Project Insight Lens: Augmented Reality Optical Character Recognition Assistant for the Visually Impaired

    By: Kamith Mirissage, Alain, Connor
    Copyrights:
        Copyright (c) 2017, Vuzix Corporation

*/

package com.example.ar_insight_lens;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Main activity for speech recognition sample
 */
public class MainActivity extends Activity {
    public final String LOG_TAG = "VoiceSample";
    public final String CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent";
    Button buttonOpenAIApi;
    EditText textEntryField;
    VoiceCmdReceiver mVoiceCmdReceiver;
    private boolean mRecognizerActive = false;

    private String encoddedImage;

    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * when created we setup the layout and the speech recognition
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply the selected theme
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String themeName = prefs.getString("SelectedTheme", "RegularTheme"); // Default to regular theme
        switch (themeName) {
            case "RegularTheme":
                setTheme(R.style.RegularTheme_AR_Insight_Lens);
                break;
            case "BlackWhiteTheme":
                setTheme(R.style.BlackAndWhiteTheme_AR_Insight_Lens);
                break;
            case "DeuteranopiaTheme":
                setTheme(R.style.DeuteranopiaTheme_AR_Insight_Lens);
                break;
            case "ProtanopiaTheme":
                setTheme(R.style.ProtanopiaTheme_AR_Insight_Lens);
                break;
            case "TritanopiaTheme":
                setTheme(R.style.TritanopiaTheme_AR_Insight_Lens);
                break;
        }

        setContentView(R.layout.activity_main);
        buttonOpenAIApi = findViewById(R.id.btn_openai_api);

        encoddedImage = encodeImageToBase64("raw/menu.png");

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setupButtonListeners();

        // Create the voice command receiver class
        mVoiceCmdReceiver = new VoiceCmdReceiver(this);

        // Now register another intent handler to demonstrate intents sent from the service
        myIntentReceiver = new MyIntentReceiver();
        registerReceiver(myIntentReceiver , new IntentFilter(CUSTOM_SDK_INTENT));
    }


    private String encodeImageToBase64(String imagePath) {
        File file = new File(imagePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading a file from file system
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            return Base64.encodeToString(imageData, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in reading the image file", e);
            return "";
        }
    }

    private void setupButtonListeners() {
        buttonOpenAIApi.setOnClickListener(view -> OnOpenAIApiClick());
    }
    /**
     * Sets up a button to change the application's theme.
     *
     * This method assigns an OnClickListener to a button identified by buttonId. When the button is clicked,
     * it saves the specified themeId to SharedPreferences and restarts the activity to apply the new theme.
     *
     * @param buttonId The resource ID of the button that will change the theme.
     * @param themeId The resource ID of the theme to be applied when the button is clicked.
     *
     * Note:
     * - The activity will be recreated when the theme is changed, so ensure to handle any necessary state
     *   saving/restoration.
     * - This method requires the activity to have a valid context for SharedPreferences and must be called
     *   within the lifecycle of an activity (typically in onCreate).
     * - The themes referenced by themeId should be defined in the styles.xml file.
     */
    private void setupThemeChangeButton(int buttonId, int themeId) {
        findViewById(buttonId).setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
            editor.putInt("themeId", themeId);
            editor.apply();

            // Restart the activity to apply the new theme
            recreate();
        });
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

    private void OnOpenAIApiClick() {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String promptText = "Tell me a joke";

        // Ensure that the messages array is correctly formatted
        String jsonBody = "{\"model\": \"gpt-4-1106-preview\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"" + promptText.replace("\"", "\\\"") + "\"}]}";
        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                Log.i(LOG_TAG, "Response: " + responseData);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

//    private void OnOpenAIApiClick(String base64Image) {
//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json");
//
//        String jsonBody = "{"
//                + "\"model\": \"gpt-4-vision-preview\","
//                + "\"messages\": ["
//                + "    {"
//                + "        \"role\": \"user\","
//                + "        \"content\": ["
//                + "            {"
//                + "                \"type\": \"text\","
//                + "                \"text\": \"Summarize the image in 2 sentences\""
//                + "            },"
//                + "            {"
//                + "                \"type\": \"image_url\","
//                + "                \"image_url\": {"
//                + "                    \"url\": \"data:image/png;base64," + base64Image + "\""
//                + "                }"
//                + "            }"
//                + "        ]"
//                + "    }"
//                + "]"
//                + "}";
//
//        RequestBody body = RequestBody.create(mediaType, jsonBody);
//
//        Request request = new Request.Builder()
//                .url(OPENAI_API_URL)
//                .post(body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
//                .build();
//
//        new Thread(() -> {
//            try {
//                Response response = client.newCall(request).execute();
//                String responseData = response.body().string();
//                Log.i(LOG_TAG, "Response: " + responseData);
//            } catch (Exception e) {
//                Log.e(LOG_TAG, "Error: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }).start();
//    }


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
