package e.gusta.gerenciador_local.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import androidx.appcompat.app.AppCompatActivity;
import e.gusta.gerenciador_local.R;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText txtLogin;
    private EditText txtSenha;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        txtLogin = findViewById(R.id.loginNovoUsuarioEditText);
        txtSenha = findViewById(R.id.senhaNovoUsuarioEditText);
        auth = FirebaseAuth.getInstance();
    }

    public void cadastrarUsuario(View view) {
        // validar email e senha
        if (!Patterns.EMAIL_ADDRESS.matcher(this.txtLogin.getText().toString()).matches()) {
            Toast.makeText(this, "Digite um endereço de E-mail válido", Toast.LENGTH_SHORT).show();
            return;
        } else if (this.txtSenha.getText().toString().length() < 6) {
            Toast.makeText(this, "A senha precisa ter mais de 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        } else {
            auth.createUserWithEmailAndPassword(
                    this.txtLogin.getText().toString(),
                    this.txtSenha.getText().toString()
            ).addOnSuccessListener(authResult -> {
                Toast.makeText(this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                if (e instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "Já existe um usuário usando este E-mail", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Houve um erro ao cadastrar", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}