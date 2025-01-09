package com.example.prima_pagina.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.prima_pagina.R;
import com.example.prima_pagina.databinding.ActivityLoginBinding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText VenitEditText;
    private SharedPreferences sharedPreferences;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 100; // pt permisiune notificari
    private void handleSuccessfulLogin() {
        // Stochez isFirstLaunch ca adevarat
        sharedPreferences = getSharedPreferences("isFirstLaunch", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstLaunch", true);
        editor.apply(); // Aplic modificarile la SharedPreferences asincron

        // Trec la activitatea principala
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        nameEditText = findViewById(R.id.editnume);
        final EditText usernameEditText = binding.username;
        passwordEditText = binding.password;
        VenitEditText = findViewById(R.id.IntroducereVenit);
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                // Finalizare si distrugere activitate de conectare odata cu succes
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verfific daca venitul a fost introdus corect
                String venitString = VenitEditText.getText().toString().trim(); // trim() elimina orice spatii albe

                // Verific daca butonul de tip EditText nu contine nimic sau contine caractere nenumerice
                if (venitString.isEmpty() || !venitString.matches("[0-9.]+")) {
                    Toast.makeText(LoginActivity.this, "Va rugam sa introduceti un venit valid (doar numere si zecimale)", Toast.LENGTH_SHORT).show();
                    return; // Iesire din metoda daca nu se introduce un venit valid
                }

                // Converteste venitul valid la tipul double
                double venit = Double.parseDouble(venitString);

                // Salvare venit introdus
                SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putFloat("venit", (float) venit);
                editor.apply(); // Salvare modificari

                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = "Bine ati venit, " + nameEditText.getText().toString();
        // TODO : initiate successful logged in experience

        // Preiau parola din interfata utilizator
        String password = passwordEditText.getText().toString();

        // Stocare sigura a hash-ului parolei
        String hashedPassword = hashPassword(password); // Implement hashPassword method

        // Salvare parola codificată în SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("user_login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("hashed_password", hashedPassword);
        editor.apply(); // Utilizare apply() pentru salvarea asincrona

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    // Metoda de criptare a parolei
    private String hashPassword(String password) {
        // Criptare parola
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(hash, Base64.NO_WRAP); // Use Base64.NO_WRAP for a more compact string
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}