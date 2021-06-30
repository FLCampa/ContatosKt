package br.edu.ifsp.scl.ads.pdm.contatoskt

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

object AutenticacaoFirebase {
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // opcoes de sign in
    var googleSignInOptions: GoogleSignInOptions? = null

    // cliente interage com api do google
    var googleSignInClient: GoogleSignInClient? = null

    // guarda informacoes da conta autenticada
    var googleSignInAccount: GoogleSignInAccount? = null

}