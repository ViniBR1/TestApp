package appfood.hugobianquini.com.appfoodni.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.model.Finalizado;
import appfood.hugobianquini.com.appfoodni.model.ItemPedido;
import appfood.hugobianquini.com.appfoodni.model.Pedido;
import appfood.hugobianquini.com.appfoodni.model.PedidoUsuario;

public class AdapterMeusPedidos extends RecyclerView.Adapter<AdapterMeusPedidos.MyViewHolder> {

    DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();

    private List<PedidoUsuario> pedidoUsuarios;

    public AdapterMeusPedidos (List<PedidoUsuario> pedidoUsuarios){
        this.pedidoUsuarios = pedidoUsuarios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_meus_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {


        String idEmpresa;
        PedidoUsuario pedidoUsuario = pedidoUsuarios.get(i);
        idEmpresa = pedidoUsuario.getIdEmpresa();



        List<ItemPedido> itens = new ArrayList<>();
        itens = pedidoUsuario.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( ItemPedido itemPedido : itens ){

            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += (qtde * preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x R$ " + preco + ") \n";
            numeroItem++;
        }
        descricaoItens += "Total: R$ " + total;


        //pegando metodo de pagamento

        String metodoPgto = "";
        int pagamento = pedidoUsuario.getMetodoPagamento();
        if (pagamento == 0){

            metodoPgto = "Paypal";

        }

        if (pagamento == 1){

            metodoPgto = "Cartão de Crédito";

        }

        holder.nome.setText(pedidoUsuario.getNomeEmpresa());
        holder.endereco.setText("Endereço de entrega: "+ pedidoUsuario.getEndereco());
        holder.pgto.setText("pagamento: " + metodoPgto);
        holder.textPedidoCep.setText("Email do Restaurante: " + pedidoUsuario.getEmailEmpresa());
        holder.NumeroEmpresa.setText("Telefone do Restaurante: " + pedidoUsuario.getNumeroEmpresa());
        holder.total.setText(descricaoItens);



    }

    @Override
    public int getItemCount() {
        return pedidoUsuarios.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView endereco;
        TextView pgto;
        TextView textPedidoCep;
        TextView NumeroEmpresa;
        TextView total;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoEmpresa);
            endereco    = itemView.findViewById(R.id.textPedidoEndereco3);
            pgto        = itemView.findViewById(R.id.textPedidoPgto3);
            textPedidoCep = itemView.findViewById(R.id.textCEP3);
            total = itemView.findViewById(R.id.textPedidoTotal);
            NumeroEmpresa = itemView.findViewById(R.id.textNumeroEmpresaId);


        }}

}
