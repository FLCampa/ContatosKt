package br.edu.ifsp.scl.ads.pdm.contatoskt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contato(
    val nome: String = "", // primary key
    var email: String = "",
    var telefone: String = "",
    var telefoneComercial: Boolean = false,
    var telefoneCelular: String = "",
    var site: String = ""
): Parcelable
