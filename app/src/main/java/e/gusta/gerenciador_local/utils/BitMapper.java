package e.gusta.gerenciador_local.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class BitMapper {

    public static byte[] getBitMapDeImageView(ImageView imagem) {
        Bitmap bitmap = ((BitmapDrawable)imagem.getDrawable()).getBitmap();
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        // convertendo o bitmap pra um byteArray sem perder qualidade
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, saida);
        //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(saida.toByteArray()));
        return saida.toByteArray();
    }
}
