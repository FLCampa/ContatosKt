package br.edu.ifsp.scl.ads.pdm.contatoskt.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.scl.ads.pdm.contatoskt.AutenticacaoFirebase
import br.edu.ifsp.scl.ads.pdm.contatoskt.R
import br.edu.ifsp.scl.ads.pdm.contatoskt.databinding.ActivityAutenticacaoBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class AutenticacaoActivity : AppCompatActivity() {

    private lateinit var activityAutenticacaoBinding: ActivityAutenticacaoBinding

    // ActivityResultLauncher para o processo de autenticacao com o google
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAutenticacaoBinding = ActivityAutenticacaoBinding.inflate(layoutInflater)
        setContentView(activityAutenticacaoBinding.root)

        // instanciando objetos de sign in com o google
        AutenticacaoFirebase.googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // cliente a partir dessas opcoes
        AutenticacaoFirebase.googleSignInClient = GoogleSignIn.getClient(this, AutenticacaoFirebase.googleSignInOptions!!)

        // busca a ultima conta google autenticada
        AutenticacaoFirebase.googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)

        // se ja autenticaram com conta google e a conta permanece
        if (AutenticacaoFirebase.googleSignInAccount != null) {
            Toast.makeText(this, "Usuário autenticado com sucesso", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        activityAutenticacaoBinding.entrarGoogleBt.setOnClickListener {
            // abrir a activity de sign in do google
            val googleSignInIntent = AutenticacaoFirebase.googleSignInClient?.signInIntent
            googleSignInLauncher.launch(googleSignInIntent)
        }

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // recuperar informacoes da conta google em um Task a partir da Intet de retorno
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                AutenticacaoFirebase.googleSignInAccount = task.getResult(ApiException::class.java)

                // extraindo credencial da conta google para autenticar no firebase
                val credential: AuthCredential =
                    GoogleAuthProvider.getCredential(AutenticacaoFirebase.googleSignInAccount?.idToken, null)

                // usa credencial para autenticar no firebase
                AutenticacaoFirebase.firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
                        Toast.makeText(this, "Usuário ${AutenticacaoFirebase.googleSignInAccount?.email} autenticado com sucesso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }.addOnFailureListener {
                    Toast.makeText(this, "Problema com a autenticação Google", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: ApiException) {
                Toast.makeText(this, "Problema com a autenticação Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onClick(view: View) {
        when (view) {
            activityAutenticacaoBinding.cadastrarBt -> {
                startActivity(Intent(this, CadastrarActivity::class.java))
            }
            activityAutenticacaoBinding.entrarBt -> {
                val email: String
                val senha: String
                with (activityAutenticacaoBinding) {
                    email = emailEt.text.toString()
                    senha = senhaEt.text.toString()
                }
                AutenticacaoFirebase.firebaseAuth.signInWithEmailAndPassword(email, senha).addOnSuccessListener {
                    Toast.makeText(this, "Usuário autenticado com sucesso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Usuário ou senha invalidos", Toast.LENGTH_SHORT).show()
                }

            }
            activityAutenticacaoBinding.recuperarSenhaBt -> {
                startActivity(Intent(this, RecuperarSenhaActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (AutenticacaoFirebase.firebaseAuth.currentUser != null) {
            Toast.makeText(this, "Usuário já autenticado", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}