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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private Bitmap imagemFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_local);

        txtCoorde = findViewById(R.id.local);
        txtDesc = findViewById(R.id.descricaoFoto);
        imagem = findViewById(R.id.foto);
        db = FirebaseFirestore.getInstance();
        storeRef = FirebaseStorage.getInstance().getReference("imagens");
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location local) {
                if (local != null) {
                    GPSlocal = local;
                    txtCoorde.setText(String.format("%s, %s", local.getLatitude(), local.getLongitude()));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(CadastroLocalActivity.this, "location provider ON", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(CadastroLocalActivity.this, "location provider OFF", Toast.LENGTH_SHORT).show();
            }
        };

        GPSlocal = getLocal();
        if (GPSlocal != null) {
            txtCoorde.setText(String.format("%s, %s", GPSlocal.getLatitude(), GPSlocal.getLongitude()));
        }
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

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
            Log.d("==== INITIAL BMP: ==== ", imageBitmap.getWidth()+", "+imageBitmap.getHeight());
            imagem.setImageBitmap(imageBitmap);
        }
    }

    public void testeCad(View view) {
        storeRef.child(UUID.randomUUID() + ".png").putBytes(BitMapper.getBitMapDeImageView(imagem))
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "TASKSNAP\n" + taskSnapshot.getMetadata().toString(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "um erro ocorreu no upload da imagem", Toast.LENGTH_SHORT).show();
                });

        // criando o objeto para enviar para o banco de dados
        Map<String, Object> local = new HashMap<>();
        local.put("descricao",txtDesc.getText().toString());
        local.put("lat", GPSlocal.getLatitude());
        local.put("long", GPSlocal.getLongitude());
        local.put("dataCadastro", Calendar.getInstance().getTime());



        db.collection("locais")
                .add(local)
                .addOnSuccessListener((OnSuccessListener<DocumentReference>) documentReference -> {
                    Toast.makeText(this, "cadastro feito. gen ID:\n"+documentReference.getId(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener((OnFailureListener) e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                });
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 5, locationListener);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                Toast.makeText(this, locationGPS.getLatitude() + ", " + locationGPS.getLongitude(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, permissions[0] + "\npermit granted", Toast.LENGTH_SHORT).show();
                    getLocal();
                }  else {
                    Toast.makeText(this, "Este app não irá funcionar sem a permissão", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}