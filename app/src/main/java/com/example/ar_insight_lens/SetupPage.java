package com.example.ar_insight_lens;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class SetupPage extends Activity {

    Button b_Regular, b_BlackWhite, b_Deuteranopia, b_Protanopia, b_Tritanopia;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_page);
        b_Regular = findViewById(R.id.buttonRegular);
        b_BlackWhite = findViewById(R.id.buttonBlackWhite);
        b_Deuteranopia = findViewById(R.id.buttonDeuteranopia);
        b_Protanopia = findViewById(R.id.buttonProtanopia);
        b_Tritanopia = findViewById(R.id.buttonTritanopia);
        setupButtonListeners();

    }
    private void setupButtonListeners() {
        b_Regular.setOnClickListener(view -> setRegularTheme());
        b_BlackWhite.setOnClickListener(view -> setBlackWhiteTheme());
        b_Deuteranopia.setOnClickListener(view -> setDeuteranopiaTheme());
        b_Protanopia.setOnClickListener(view -> setProtanopiaTheme());
        b_Tritanopia.setOnClickListener(view -> setTritanopiaTheme());
    }

    private void setRegularTheme(){
        popupToast("Regular Theme");
        regularThemeChange();
        openMainActivity();
    }
    private void setBlackWhiteTheme(){
        popupToast("BlackWhite Theme");
        blackWhiteThemeChange();
        openMainActivity();
    }
    private void setDeuteranopiaTheme(){
        popupToast("Deuteranopia Theme");
        deuteranopiaThemeChange();
        openMainActivity();
    }
    private void setProtanopiaTheme(){
        popupToast("Protanopia Theme");
        protanopiaThemeChange();
        openMainActivity();
    }
    private void setTritanopiaTheme(){
        popupToast("Tritanopia Theme");
        tritanopiaThemeChange();
        openMainActivity();
    }

    private void popupToast(String iStr) {
        Toast myToast = Toast.makeText(SetupPage.this, iStr, Toast.LENGTH_LONG);
        myToast.show();
    }

    private void openMainActivity() {
        Intent intent = new Intent(SetupPage.this, MainActivity.class);
        startActivity(intent);
    }

    private void regularThemeChange() {
        editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putString("SelectedTheme", "RegularTheme");
        editor.apply();

        // Apply the theme immediately if needed
        //recreate(); // Call this to recreate the activity with the new theme
    }

    private void blackWhiteThemeChange() {
        editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putString("SelectedTheme", "BlackWhiteTheme");
        editor.apply();

        // Apply the theme immediately if needed
        //recreate(); // Call this to recreate the activity with the new theme
    }

    private void deuteranopiaThemeChange() {
        editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putString("SelectedTheme", "DeuteranopiaTheme");
        editor.apply();

        // Apply the theme immediately if needed
        //recreate(); // Call this to recreate the activity with the new theme
    }

    private void protanopiaThemeChange() {
        editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putString("SelectedTheme", "ProtanopiaTheme");
        editor.apply();

        // Apply the theme immediately if needed
        //recreate(); // Call this to recreate the activity with the new theme
    }

    private void tritanopiaThemeChange() {
        editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putString("SelectedTheme", "TritanopiaTheme");
        editor.apply();

        // Apply the theme immediately if needed
        //recreate(); // Call this to recreate the activity with the new theme
    }


}
