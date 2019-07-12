package appfood.hugobianquini.com.appfoodni.model;

import android.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.adapter.AdapterPedido;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;

public class Finalizado {

    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String endereco;
    private String numero;
    private String cep;
    private String urlImagem;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento;

    public Finalizado() {
    }

    public void excluir (){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_finalizados")
                .child(getIdEmpresa()).child(getIdPedido());
        pedidoRef.removeValue();

    }


    public void finalizarPedido(){
        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_finalizados")
                .child(getIdEmpresa()).child(getIdPedido());
        pedidoRef.setValue(this);}

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
}
