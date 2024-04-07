package com.example.consultawebservicesgovernamentais;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_INTERNET = 1;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE_INTERNET);
            }
        }

        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editTextResultado);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.INDSOCIOECONOMICO:
                this.showCustomDialog("pais");
                break;
            case R.id.NOMEBR:
                this.showCustomDialog("nome");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void showCustomDialog(String dialogName) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if(dialogName == "nome") {
            dialog.setContentView(R.layout.dialog_nome);
        }else{
            dialog.setContentView(R.layout.dialog_pais);
        }

        final EditText txtEdit;
        Button confirmarButton;
        Button cancelarButton;

        if(dialogName == "nome") {
            txtEdit = dialog.findViewById(R.id.txtNome);
            confirmarButton = dialog.findViewById(R.id.btConfirmar1);
            cancelarButton = dialog.findViewById(R.id.btCancelar1);
        }else{
            txtEdit = dialog.findViewById(R.id.txtPais);
            confirmarButton = dialog.findViewById(R.id.btConfirmar2);
            cancelarButton = dialog.findViewById(R.id.btCancelar2);
        }

        confirmarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEditString = txtEdit.getText().toString();
                if(textEditString.length() > 0 && dialogName == "nome") {
                    WebServiceRequest webServiceRequestInd = new WebServiceRequest("https://servicodados.ibge.gov.br/api/v2/censos/nomes/" + textEditString);
                    webServiceRequestInd.execute();
                }else if(textEditString.length() > 0 && dialogName == "pais"){
                    WebServiceRequest webServiceRequestInd = new WebServiceRequest("https://servicodados.ibge.gov.br/api/v1/paises/"+textEditString+"/indicadores");
                    webServiceRequestInd.execute();
                }
                dialog.dismiss();
            }
        });

        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public class WebServiceRequest extends AsyncTask<Void, Void, String> {

        private static final String TAG = "WebServiceRequest";
        private final String url;

        public WebServiceRequest(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String mensagem = "";
            try {
                URL urlRequest = new URL(this.url);
                HttpURLConnection con = (HttpURLConnection) urlRequest.openConnection();
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line = br.readLine()) != null)
                {
                    mensagem += line;
                }

                br.close();
            }catch (Exception e)
            {
                mensagem = "Erro ao realizar a requisição: " + e.getMessage();
            }

            return mensagem;
        }

        @Override
        protected void onPostExecute(String resultJson) {
            super.onPostExecute(resultJson);

            if (resultJson != null) {
                editText.setText(resultJson);
            } else {
                editText.setText("Erro ao realizar a requisição.");
            }
        }
    }
}