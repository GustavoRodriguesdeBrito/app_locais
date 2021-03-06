package e.gusta.gerenciador_local.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.LinkedList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import e.gusta.gerenciador_local.R;
import e.gusta.gerenciador_local.models.Local;

public class MainActivity extends AppCompatActivity {

    private LinkedList<Local> listaLocais;
    private RecyclerView recyclerView;
    private LocalAdapter adapter;

    private FirebaseFirestore db;
    private StorageReference storeRef;
    private FirebaseUser usuarioAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaLocais = new LinkedList<>();

        // iniciar firebase
        db = FirebaseFirestore.getInstance();
        storeRef = FirebaseStorage.getInstance().getReference("imagens");
        usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        // pegar os dados
        //getDados(); // como o oncreate chama o onresume indiretamente, chame o getDados apenas no onresume
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDados();
    }

    public void getDados() {
        db.collection("locais")
                .whereEqualTo("idUsuario", usuarioAtual.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    this.listaLocais.clear();

                    Object[] arrLocS = queryDocumentSnapshots.getDocuments().toArray();

                    for (Object arrLoc : arrLocS) {
                        DocumentSnapshot doc = (DocumentSnapshot) arrLoc;
                        Local local = new Local(doc.getId(),
                                doc.get("idUsuario").toString(),
                                ((Timestamp) doc.get("dataCadastro")).toDate(),
                                doc.get("descricao").toString(),
                                (Double) doc.get("lat"),
                                (Double) doc.get("long"),
                                doc.get("idImagem").toString()
                        );
                        Log.i("Local Criado", local.toString());
                        listaLocais.add(local);
                    }
                    //ordenar a lista
                    listaLocais.sort((local1, local2) -> local2.compareTo(local1));
                    //criar a recyclerview
                    recyclerView = findViewById(R.id.listarecycler);
                    //conectar o adapter e passar os dados
                    adapter = new LocalAdapter(this, listaLocais);
                    recyclerView.setAdapter(adapter);
                    //dar um layoutmanager padrão pra recyclerview
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                })
                .addOnCanceledListener(() -> {
                    Log.e("CANCELED_REQUEST", "CANCELED REQUEST FOR IMAGE");
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "erro ao buscar dados", Toast.LENGTH_SHORT).show();
                });
    }

    public void novoLocal(View view) {
        startActivity(new Intent(this, CadastroLocalActivity.class));
    }
}