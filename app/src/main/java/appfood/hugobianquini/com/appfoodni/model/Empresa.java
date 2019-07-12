package appfood.hugobianquini.com.appfoodni.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;


public class Empresa implements Serializable {
    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String descricao;
    private String CNPJ;
    private String endereco;
    private String numero;
    private String email;
    private int nota;
    private int vezes;

    public Empresa() {
    }
    public void salvar(){
        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresa").child(getIdUsuario());
        empresaRef.setValue(this);
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public int getVezes() {
        return vezes;
    }

    public void setVezes(int vezes) {
        this.vezes = vezes;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
