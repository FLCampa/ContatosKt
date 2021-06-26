package br.edu.ifsp.scl.ads.pdm.contatoskt.model

import br.edu.ifsp.scl.ads.pdm.contatoskt.model.ContatoFirebase.Constantes.LISTA_CONTATOS_DATABASE
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ContatoFirebase: ContatoDao {

    object Constantes {
        val LISTA_CONTATOS_DATABASE = "listaContatos"
    }

    // referencia para o nó principal do RealtimeDatabase (listaContatos)
    private val contatosListRtDb = Firebase.database.getReference(LISTA_CONTATOS_DATABASE)

    // lista de contatos que simula consulta ao banco de dados
    private val contatosList: MutableList<Contato> = mutableListOf()
    init {
        contatosListRtDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // valor do snapshot é um obj do tipo Contato
                val novoContato: Contato = snapshot.getValue<Contato>()?:Contato()
                if (contatosList.indexOfFirst { it.nome.equals(novoContato.nome) } == -1) {
                    contatosList.add(novoContato)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val contatoEditado: Contato = snapshot.getValue<Contato>()?:Contato()
                val indice = contatosList.indexOfFirst { it.nome.equals(contatoEditado.nome) }
                contatosList[indice] = contatoEditado
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val contatoRemovido: Contato = snapshot.getValue<Contato>()?:Contato()
                contatosList.remove(contatoRemovido)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // não usa
            }

            override fun onCancelled(error: DatabaseError) {
                // não usa
            }
        })
        contatosListRtDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // fazer
            }

            override fun onCancelled(error: DatabaseError) {
                // fazer
            }
        })
    }

    override fun createContato(contato: Contato) = criaOuAtulizaContato(contato)

    override fun readContato(nome: String): Contato = contatosList[contatosList.indexOfFirst { it.nome.equals(nome) }]

    override fun readContatos(): MutableList<Contato> = contatosList

    override fun updateContato(contato: Contato) = criaOuAtulizaContato(contato)

    // qnd o valor fica vazio o firebase remove o nó
    override fun deleteContato(nome: String) {
        contatosListRtDb.child(nome).removeValue()
    }

    private fun criaOuAtulizaContato(contato: Contato) {
        contatosListRtDb.child(contato.nome).setValue(contato)
    }

}