package e.gusta.gerenciador_local.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import e.gusta.gerenciador_local.R;
import e.gusta.gerenciador_local.models.Local;
import e.gusta.gerenciador_local.utils.BitMapper;

public class CadastroLocalActivity extends AppCompatActivity {

    Local localACadastrar;
    private ImageView imagem;
    private TextView txtCoorde;
    private EditText txtDesc;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location GPSlocal;
    private static final int GPS_REQUEST_PERMISSION_CODE = 1001;
    private FirebaseFirestore db;
    private StorageReference storeRef;
    private FirebaseUser usuarioAtual;
    private Bitmap imagemFull; //TODO: get img full size
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private boolean fotoTirada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_local);

        txtCoorde = findViewById(R.id.local);
        txtDesc = findViewById(R.id.descricaoFoto);
        fotoTirada = false;
        imagem = findViewById(R.id.foto);
        db = FirebaseFirestore.getInstance();
        storeRef = FirebaseStorage.getInstance().getReference("imagens");
        usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location local) {
                if (local != null) {
                    GPSlocal = local;
                    txtCoorde.setText(String.format("Localização encontrada:\n%s, %s", local.getLatitude(), local.getLongitude()));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(CadastroLocalActivity.this, "Serviço de localização ligado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(CadastroLocalActivity.this, "Serviço de localização desligado", Toast.LENGTH_SHORT).show();
            }
        };

        GPSlocal = getLocal();
        if (GPSlocal != null) {
            txtCoorde.setText(String.format("%s, %s", GPSlocal.getLatitude(), GPSlocal.getLongitude()));
        }
    }

    /**
     * Método(s) para tirar a foto
     */
    public void tirarFoto(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //data. //TODO: imagem full size
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //Log.d("==== INITIAL BMP: ==== ", imageBitmap.getWidth()+", "+imageBitmap.getHeight());
            imagem.setImageBitmap(imageBitmap);
            fotoTirada = true;
        }
    }

    public void cadastrarLocal(View view) {
        if (!fotoTirada || GPSlocal == null || TextUtils.isEmpty(txtDesc.getText().toString())) {
            // montar a mensagem de erro
            StringBuilder msgErro = new StringBuilder();
            if (!fotoTirada) {
                msgErro.append("\nFoto não foi tirada");
            }
            if (GPSlocal == null) {
                msgErro.append("\nLocalização não encontrada");
            }
            if (TextUtils.isEmpty(txtDesc.getText().toString())) {
                msgErro.append("\nDescrição vazia");
            }
            Toast.makeText(this, "Dados não preenchidos:" + msgErro.toString(), Toast.LENGTH_SHORT).show();
        } else {
            //gerando um nome único para a imagem
            UUID nomeUnicoImagem = UUID.randomUUID();
            //cadastrando a imagem primeiro para obter a ID dela e garantir que os dados escritos não sejam inseridos sem a imagem
            storeRef.child(nomeUnicoImagem + ".png").putBytes(BitMapper.getBitMapDeImageView(imagem))
                    .addOnSuccessListener(taskSnapshot -> {

                        localACadastrar = new Local(
                                "",
                                usuarioAtual.getUid(),
                                Calendar.getInstance().getTime(),
                                txtDesc.getText().toString(),
                                GPSlocal.getLatitude(),
                                GPSlocal.getLongitude(),
                                nomeUnicoImagem.toString()
                        );

                        // criando o objeto para enviar para o banco de dados
                        Map<String, Object> local = new HashMap<>();
                        local.put("idUsuario", localACadastrar.getIdUsuario());
                        local.put("idImagem", localACadastrar.getIdImagem());
                        local.put("descricao", localACadastrar.getDescricao());
                        local.put("lat", localACadastrar.getLat());
                        local.put("long", localACadastrar.getLong());
                        local.put("dataCadastro", localACadastrar.getdataCadastro());

                        db.collection("locais")
                                .add(local)
                                .addOnSuccessListener((OnSuccessListener<DocumentReference>) documentReference -> {
                                    Toast.makeText(this, "Cadastro efetuado com sucesso.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener((OnFailureListener) e -> {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "um erro ocorreu no upload da imagem", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void voltar(View view) {
        finish();
    }


    /**
     * retorna as coordenadas do local do dispositivo
     */
    public Location getLocal() {
        //caso não tenha a permissão de usar o GPS, peça; se tiver, retorne o objeto que contém a latitude e longitude
        if (ActivityCompat.checkSelfPermission(
                CadastroLocalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                CadastroLocalActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            //pegar a localização
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, locationListener);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                //Toast.makeText(this, locationGPS.getLatitude() + ", " + locationGPS.getLongitude(), Toast.LENGTH_SHORT).show();
                return locationGPS;
            } else {
                Toast.makeText(this, "Localização não encontrada.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                // array de resultados de permissão estarão vazios se permissão for negada
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, permissions[0] + "\npermit granted", Toast.LENGTH_SHORT).show();
                    getLocal();
                } else {
                    Toast.makeText(this, "Este app não irá funcionar sem a permissão", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}