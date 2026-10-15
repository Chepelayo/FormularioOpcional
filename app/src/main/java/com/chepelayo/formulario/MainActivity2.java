package com.chepelayo.formulario;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity{

    private String nombre, apellidos, sexo, textoAficiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getColor(R.color.color_primary)));
        }

        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        TextView tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        TextView tvSexoMostrado = findViewById(R.id.tvSexoMostrado);
        TextView tvAficionesMostradas = findViewById(R.id.tvAficionesMostradas);
        Button btnCompartir = findViewById(R.id.btnCompartir);

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        apellidos = intent.getStringExtra("apellidos");
        sexo = intent.getStringExtra("sexo");
        textoAficiones = intent.getStringExtra("aficiones");

        tvNombreCompleto.setText(getString(R.string.formato_nombre_completo, nombre, apellidos));
        tvSexoMostrado.setText(sexo.toUpperCase());
        tvAficionesMostradas.setText(textoAficiones);

        ivAvatar.setImageResource(
                sexo.equals(getString(R.string.hombre)) ? R.drawable.ic_hombre : R.drawable.ic_mujer);

        btnCompartir.setOnClickListener(v -> compartirDatos());
    }

    private void compartirDatos() {
        String texto = nombre + " " + apellidos + "\n" + sexo + "\nLe gusta: " + textoAficiones;

        Intent intentCompartir = new Intent(Intent.ACTION_SEND);
        intentCompartir.setType("text/plain");
        intentCompartir.putExtra(Intent.EXTRA_TEXT, texto);

        startActivity(Intent.createChooser(intentCompartir, getString(R.string.compartir_titulo)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}