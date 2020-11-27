package e.gusta.gerenciador_local.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import e.gusta.gerenciador_local.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void novoLocal(View view) {
        startActivity(new Intent(this, CadastroLocalActivity.class));
    }
}