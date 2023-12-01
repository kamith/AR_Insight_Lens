package com.example.ar_insight_lens;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class SetupPage extends Activity {

    Button b_Regular, b_BlackWhite, b_Deuteranopia, b_Protanopia, b_Tritanopia;
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
    }
    private void setBlackWhiteTheme(){
        popupToast("BlackWhite Theme");
    }
    private void setDeuteranopiaTheme(){
        popupToast("Deuteranopia Theme");
    }
    private void setProtanopiaTheme(){
        popupToast("Protanopia Theme");
    }
    private void setTritanopiaTheme(){
        popupToast("Tritanopia Theme");
    }

    private void popupToast(String iStr) {
        Toast myToast = Toast.makeText(SetupPage.this, iStr, Toast.LENGTH_LONG);
        myToast.show();
    }
}
