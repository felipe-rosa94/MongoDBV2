package com.felipe.mongodbv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Main extends AppCompatActivity {

    // Variáveis //
    public static String DB_NAME = "mongo_db";
    public static String COLLECTION_NAME = "user";
    public static String API_KEY = "CexFKyp7zcNdGh6oV9SHv6lY6kxWILcr";
    public static String ID;
    public static String BASE_URL = "https://api.mlab.com/api/1/databases/%s/collections/%s/?apiKey=%s";
    public static String BASE_URL_PUT_DELETE = "https://api.mlab.com/api/1/databases/%s/collections/%s/%s?apiKey=%s";

    //Componentes //
    private EditText etUser;
    private ImageView btnAdd;
    private ImageView btnEdit;
    private ImageView btnDelete;
    private ListView lvUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pd;

    // Classes //
    private ArrayList<User> users;
    private UserAdapter adapter;
    private User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciar();
        if (DB_NAME == null) {
            startActivity(new Intent(getBaseContext(), Config.class));
            finish();
        }
        GetDB(String.format(BASE_URL, DB_NAME, COLLECTION_NAME, API_KEY));
        lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                u = users.get(position);
                etUser.setText(u.getUser());
                ID = u.get_id().getId();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"user\":\"" + etUser.getText().toString() + "\"}";
                PostDB(String.format(BASE_URL, DB_NAME, COLLECTION_NAME, API_KEY), json);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"user\":\"" + etUser.getText().toString() + "\"}";
                PutDB(String.format(BASE_URL_PUT_DELETE, DB_NAME, COLLECTION_NAME, ID, API_KEY), json);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"user\":\"" + etUser.getText().toString() + "\"}";
                DeleteDB(String.format(BASE_URL_PUT_DELETE, DB_NAME, COLLECTION_NAME, ID, API_KEY), json);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetDB(String.format(BASE_URL, DB_NAME, COLLECTION_NAME, API_KEY));
            }
        });
    }

    private void iniciar() {
        etUser = findViewById(R.id.et_user);
        btnAdd = findViewById(R.id.btn_add);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        lvUser = findViewById(R.id.lv_user);
        swipeRefreshLayout = findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        pd = new ProgressDialog(Main.this);
    }

    private void GetDB(String url) {
        pd.setTitle("Carregando...");
        pd.show();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(Main.this, responseString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<User>>() {
                }.getType();
                users = gson.fromJson(responseString, listType);
                adapter = new UserAdapter(getBaseContext(), users);
                lvUser.setAdapter(adapter);
                pd.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void PostDB(String url, String json) {
        try {
            pd.setTitle("Enviando...");
            pd.show();
            ByteArrayEntity entity = new ByteArrayEntity(json.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.post(getBaseContext(), url, entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(Main.this, responseString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    pd.dismiss();
                    GetDB(String.format(BASE_URL, DB_NAME, COLLECTION_NAME, API_KEY));
                    Limpa();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PutDB(String url, String json) {
        try {
            pd.setTitle("Atualizando...");
            pd.show();
            ByteArrayEntity entity = new ByteArrayEntity(json.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.put(getBaseContext(), url, entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(Main.this, responseString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    pd.dismiss();
                    GetDB(String.format(BASE_URL, DB_NAME, COLLECTION_NAME, API_KEY));
                    Limpa();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DeleteDB(String url, String json) {
        try {
            pd.setTitle("Deletando...");
            pd.show();
            ByteArrayEntity entity = new ByteArrayEntity(json.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.delete(url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(Main.this, responseString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    pd.dismiss();
                    GetDB(String.format(BASE_URL, DB_NAME, COLLECTION_NAME, API_KEY));
                    Limpa();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Limpa() {
        etUser.setText("");
    }
}
