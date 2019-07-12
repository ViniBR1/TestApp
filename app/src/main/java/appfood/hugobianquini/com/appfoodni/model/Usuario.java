package appfood.hugobianquini.com.appfoodni.model;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;

public class Usuario {

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String endereco;
    private String cep;
    private String numero;
    private String avaliacao;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(getIdUsuario());
        usuarioRef.setValue(this);

    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
