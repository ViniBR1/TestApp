package appfood.hugobianquini.com.appfoodni.model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;

public class Pedido {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String endereco;
    private String numero;
    private String cep;
    private String urlImagem;
    private List<ItemPedido> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento;
    private String observacao;

    public Pedido() {
    }

    public Pedido(String idUsu, String idEmp){

        setIdUsuario(idUsu);
        setIdEmpresa(idEmp);

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(idEmp).child(idUsu);
        setIdPedido(pedidoRef.push().getKey());
    }

    public void salvar (){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(getIdEmpresa()).child(getIdUsuario());
        pedidoRef.setValue(this);

    }

    public void remover (){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(getIdEmpresa()).child(getIdUsuario());
        pedidoRef.removeValue();

    }

    public void removerPedidos (){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdEmpresa()).child(getIdPedido());
        pedidoRef.removeValue();

    }

    public void confirmar (){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdEmpresa()).child(getIdPedido());
        pedidoRef.setValue(this);

    }

    public void atualizarStatus (){

        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos").child(getIdEmpresa()).child(getIdPedido());
        pedidoRef.updateChildren(status);

    }



    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idRequisicao) {
        this.idPedido = idRequisicao;
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

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
