package com.chepelayo.formulario;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "formulario_prefs";

    private TextInputLayout tilNombre, tilApellidos;
    private EditText etNombre, etApellidos;
    private RadioGroup rgSexo;
    private RadioButton rbHombre, rbMujer;
    private CheckBox cbLectura, cbDeporte, cbCerveza, cbCine;
    private ProgressBar pbProgreso;
    private TextView tvUsarAnteriores;
    private Button btnLimpiar, btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tilNombre = findViewById(R.id.tilNombre);
        tilApellidos = findViewById(R.id.tilApellidos);
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        rgSexo = findViewById(R.id.rgSexo);
        rbHombre = findViewById(R.id.rbHombre);
        rbMujer = findViewById(R.id.rbMujer);
        cbLectura = findViewById(R.id.cbLectura);
        cbDeporte = findViewById(R.id.cbDeporte);
        cbCerveza = findViewById(R.id.cbCerveza);
        cbCine = findViewById(R.id.cbCine);
        pbProgreso = findViewById(R.id.pbProgreso);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnEnviar = findViewById(R.id.btnEnviar);
        tvUsarAnteriores = findViewById(R.id.tvUsarAnteriores);

        pbProgreso.setMax(3);

        configurarValidacionYProgreso();
        comprobarDatosGuardados();

        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnEnviar.setOnClickListener(v -> enviarDatos());
        tvUsarAnteriores.setOnClickListener(v -> cargarUltimosDatos());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollMain), (v, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            return insets;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View foco = getCurrentFocus();
            if (foco instanceof EditText) {
                Rect zona = new Rect();
                foco.getGlobalVisibleRect(zona);
                if (!zona.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    foco.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(foco.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void configurarValidacionYProgreso() {
        agregarEscuchaProgreso(etNombre, tilNombre);
        agregarEscuchaProgreso(etApellidos, tilApellidos);
        rgSexo.setOnCheckedChangeListener((group, checkedId) -> actualizarProgreso ());

        etNombre.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                tilNombre.setError(etNombre.getText().toString().trim().isEmpty()
                ? getString(R.string.error_nombre) : null);
            }
        });

        etApellidos.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                tilApellidos.setError(etApellidos.getText().toString().trim().isEmpty()
                ? getString(R.string.error_apellidos) : null);
            }
        });
    }

    private void agregarEscuchaProgreso(EditText campo, TextInputLayout til) {
        campo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    til.setError(null);
                }
                actualizarProgreso();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void actualizarProgreso() {
        int progreso = 0;
        if (!etNombre.getText().toString().trim().isEmpty()) progreso++;
        if (!etApellidos.getText().toString().trim().isEmpty()) progreso++;
        if (rgSexo.getCheckedRadioButtonId() != -1) progreso++;
        pbProgreso.setProgress(progreso);
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etApellidos.setText("");
        tilNombre.setError(null);
        tilApellidos.setError(null);
        rgSexo.clearCheck();
        cbLectura.setChecked(false);
        cbDeporte.setChecked(false);
        cbCerveza.setChecked(false);
        cbCine.setChecked(false);
        actualizarProgreso();
    }

    private void enviarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        int idSeleccionado = rgSexo.getCheckedRadioButtonId();

        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.error_nombre));
            Toast.makeText(this, R.string.error_nombre, Toast.LENGTH_SHORT).show();
            return;
        }
        if (apellidos.isEmpty()) {
            tilApellidos.setError(getString(R.string.error_apellidos));
            Toast.makeText(this, R.string.error_apellidos, Toast.LENGTH_SHORT).show();
            return;
        }
        if (idSeleccionado == -1) {
            Toast.makeText(this, R.string.error_sexo, Toast.LENGTH_SHORT).show();
            return;
        }

        String sexo = (idSeleccionado == R.id.rbHombre)
                ? getString(R.string.hombre)
                : getString(R.string.mujer);

        mostrarConfirmacion(nombre, apellidos, sexo, idSeleccionado);
    }

    private void mostrarConfirmacion(String nombre, String apellidos, String sexo, int idSeleccionado) {
        String textoAficiones = obtenerTextoAficiones();
        String mensaje = getString(R.string.confirmar_mensaje, nombre, apellidos, sexo, textoAficiones);

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_titulo)
                .setMessage(mensaje)
                .setPositiveButton(R.string.aceptar, (dialog, which) ->
                        continuarEnvio(nombre, apellidos, sexo, idSeleccionado, textoAficiones))
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void continuarEnvio(String nombre, String apellidos, String sexo, int idSeleccionado, String textoAficiones) {
        guardarDatos(nombre, apellidos, idSeleccionado == R.id.rbHombre,
                cbLectura.isChecked(), cbDeporte.isChecked(), cbCerveza.isChecked(), cbCine.isChecked());

        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("apellidos", apellidos);
        intent.putExtra("sexo", sexo);
        intent.putExtra("aficiones", textoAficiones);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private String obtenerTextoAficiones() {
        List<String> lista = new ArrayList<>();
        if (cbLectura.isChecked()) lista.add(getString(R.string.lecturaSalida));
        if (cbDeporte.isChecked()) lista.add(getString(R.string.deporteSalida));
        if (cbCerveza.isChecked()) lista.add(getString(R.string.cervezaSalida));
        if (cbCine.isChecked()) lista.add(getString(R.string.cineSalida));

        if (lista.isEmpty()) {
            return getString(R.string.sin_aficiones);
        }

        String primera = lista.get(0);
        String resultado = Character.toUpperCase(primera.charAt(0)) + primera.substring(1);

        if (lista.size() == 1) {
            return resultado + ".";
        }

        for (int i = 1; i < lista.size() - 1; i++) {
            resultado += ", " + lista.get(i);
        }

        resultado += ", y " + lista.get(lista.size() - 1);
        resultado += ".";

        return resultado;
    }

    private void guardarDatos(String nombre, String apellidos, boolean esHombre,
                              boolean lectura, boolean deporte, boolean cerveza, boolean cine) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", nombre);
        editor.putString("apellidos", apellidos);
        editor.putBoolean("esHombre", esHombre);
        editor.putBoolean("lectura", lectura);
        editor.putBoolean("deporte", deporte);
        editor.putBoolean("cerveza", cerveza);
        editor.putBoolean("cine", cine);
        editor.apply();
    }

    private void comprobarDatosGuardados() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        tvUsarAnteriores.setVisibility(
                prefs.contains("nombre") ? View.VISIBLE : View.GONE);
    }

    private void cargarUltimosDatos () {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etNombre.setText(prefs.getString("nombre", ""));
        etApellidos.setText(prefs.getString("apellidos", ""));

        if (prefs.getBoolean("esHombre", true)){
            rbHombre.setChecked(true);
        } else {
            rbMujer.setChecked(true);
        }

        cbLectura.setChecked(prefs.getBoolean("lectura", false));
        cbDeporte.setChecked(prefs.getBoolean("deporte", false));
        cbCerveza.setChecked(prefs.getBoolean("cerveza", false));
        cbCine.setChecked(prefs.getBoolean("cine", false));
    }
}