package br.edu.ifsp.scl.ads.pdm.contatoskt

import com.google.firebase.auth.FirebaseAuth

object AutenticacaoFirebase {
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
}