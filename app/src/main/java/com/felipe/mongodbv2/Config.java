package com.felipe.mongodbv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Config extends AppCompatActivity {

    private EditText etDBName;
    private EditText etCollections;
    private EditText etApiKey;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        iniciar();
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.DB_NAME = etDBName.getText().toString();
                Main.COLLECTION_NAME = etCollections.getText().toString();
                Main.API_KEY = etApiKey.getText().toString();
                Toast.makeText(Config.this, "Gravado.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getBaseContext(), Main.class));
                finish();
            }
        });
    }

    private void iniciar() {
        etDBName = findViewById(R.id.et_db_name);
        etCollections = findViewById(R.id.et_collection);
        etApiKey = findViewById(R.id.et_api_key);
        btnSalvar = findViewById(R.id.btn_salvar);
    }
}
