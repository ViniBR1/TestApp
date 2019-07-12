package appfood.hugobianquini.com.appfoodni.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;

public class PedidoUsuario {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String nomeEmpresa;
    private String emailEmpresa;
    private String numeroEmpresa;
     private String endereco;
    private List<ItemPedido> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento;
    private String avaliado;


    public PedidoUsuario() {
    }


    public void salvar (){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("meus_pedidos").child(getIdUsuario())
                .child(getIdPedido());
        pedidoRef.setValue(this);

}

    public void remover(){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("meus_pedidos")
                .child(getIdUsuario()).child(getIdPedido());
        pedidoRef.removeValue();


    }

    public String getAvaliado() {
        return avaliado;
    }

    public void setAvaliado(String avaliado) {
        this.avaliado = avaliado;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getEmailEmpresa() {
        return emailEmpresa;
    }

    public void setEmailEmpresa(String emailEmpresa) {
        this.emailEmpresa = emailEmpresa;
    }

    public String getNumeroEmpresa() {
        return numeroEmpresa;
    }

    public void setNumeroEmpresa(String numeroEmpresa) {
        this.numeroEmpresa = numeroEmpresa;
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
