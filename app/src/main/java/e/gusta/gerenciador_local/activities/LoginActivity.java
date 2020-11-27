package e.gusta.gerenciador_local.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import e.gusta.gerenciador_local.R;

public class LoginActivity extends AppCompatActivity {

    private EditText txtLogin;
    private EditText txtSenha;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtLogin = findViewById(R.id.loginEditText);
        txtSenha = findViewById(R.id.senhaEditText);
        auth = FirebaseAuth.getInstance();
    }

    public void abrirCadastro(View view) {
        startActivity(new Intent(this, CadastroUsuarioActivity.class));
    }

    public void fazerLogin(View view) {

        // validar email e senha
        if (!Patterns.EMAIL_ADDRESS.matcher(this.txtLogin.getText().toString()).matches()) {
            Toast.makeText(this, "Digite um endereço de E-mail válido", Toast.LENGTH_SHORT).show();
            return;
        } else if (this.txtSenha.getText().toString().length() < 6) {
            Toast.makeText(this, "A senha precisa ter mais de 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        } else {
            auth.signInWithEmailAndPassword(
                    txtLogin.getText().toString(),
                    txtSenha.getText().toString()
            ).addOnSuccessListener(result -> {
                //Toast.makeText(this, result.getUser().getEmail(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
            });
        }
    }
}