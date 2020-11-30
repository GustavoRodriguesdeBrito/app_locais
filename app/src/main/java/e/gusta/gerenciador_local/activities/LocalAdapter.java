package e.gusta.gerenciador_local.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import e.gusta.gerenciador_local.R;
import e.gusta.gerenciador_local.models.Local;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {

    private StorageReference storeRef;

    private LayoutInflater inflater;
    private LinkedList<Local> listaLocais;
    //private LinkedList<Bitmap> listaImagens;


    public static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imagem;
        TextView descricao;
        TextView coordLat;
        TextView coordLong;
        TextView dataCadastro;

        LocalAdapter adapter;

        public LocalViewHolder(@NonNull View itemView, LocalAdapter adapter) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imagemFotoCard);
            descricao = itemView.findViewById(R.id.itemDesc);
            coordLat = itemView.findViewById(R.id.itemCoordLat);
            coordLong = itemView.findViewById(R.id.itemCoordLong);
            dataCadastro = itemView.findViewById(R.id.itemData);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //pegar a posição do item clicado
            int pos = getLayoutPosition();
            Toast.makeText(view.getContext(), "Item " + pos + " clicado", Toast.LENGTH_SHORT).show();
            //adapter.notifyDataSetChanged();
        }

        @Override
        public boolean onLongClick(View view) {
            int pos = getLayoutPosition();
            Toast.makeText(view.getContext(), "Item " + pos + " clicado-LONG", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public LocalAdapter(Context context, LinkedList<Local> listaLocais) {
        inflater = LayoutInflater.from(context);
        this.listaLocais = listaLocais;
        storeRef = FirebaseStorage.getInstance().getReference("imagens");
    }

    /**
     * chamado quando o recycler view precisa de um novo viewHolder pra representar um item
     */
    @NonNull
    @Override
    public LocalAdapter.LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflar o item view
        View itemView = inflater.inflate(R.layout.item_local, parent, false);
        return new LocalViewHolder(itemView, this);
    }

    /**
     * chamado pelo recyclerview para exibir os dados na posição especificada no parâmetro position
     */

    @Override
    public void onBindViewHolder(@NonNull LocalAdapter.LocalViewHolder holder, int position) {
        Local itemAtual = listaLocais.get(position);
        holder.descricao.setText(itemAtual.getDescricao());
        holder.dataCadastro.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(itemAtual.getdataCadastro()));
        holder.coordLat.setText((String.valueOf(itemAtual.getLat())));
        holder.coordLong.setText((String.valueOf(itemAtual.getLong())));
        storeRef.child(itemAtual.getIdImagem() + ".png").getDownloadUrl()
                .addOnCompleteListener(task -> {
                    //usando a biblioteca Glide para pegar as imagens
                    Glide.with(holder.itemView).load(task.getResult()).into(holder.imagem);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e("ERRO ONBIND", e.getMessage());
                });
    }

    @Override
    public int getItemCount() {
        return listaLocais.size();
    }
}
