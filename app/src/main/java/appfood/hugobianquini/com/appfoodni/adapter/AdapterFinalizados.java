package appfood.hugobianquini.com.appfoodni.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.model.Finalizado;
import appfood.hugobianquini.com.appfoodni.model.Pedido;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AdapterFinalizados extends RecyclerView.Adapter<AdapterFinalizados.MyViewHolder>{

    private List<Finalizado> finalizados;

    public AdapterFinalizados(List<Finalizado> finalizados) {
        this.finalizados = finalizados;}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_finalizados, parent, false);
        return new MyViewHolder(itemLista);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Finalizado finalizado = finalizados.get(i);

        //Picasso
        String urlImagem = finalizado.getUrlImagem();
        Picasso.get().load(urlImagem).into(holder.imagePedido);

        //pegando metodo de pagamento

        String metodoPgto = "";
        int pagamento = finalizado.getMetodoPagamento();

       if (pagamento == 0){

            metodoPgto = "Paypal";

        }

        if (pagamento == 1){

           metodoPgto = "Cartão de Crédito";

        }

        holder.nome.setText(finalizado.getNome());
        holder.endereco.setText("Endereço de entrega: "+finalizado.getEndereco());
        holder.pgto.setText("pagamento: " + metodoPgto);
        holder.status.setText("STATUS: " + finalizado.getStatus().toUpperCase());
        holder.textPedidoCep.setText("CEP: " + finalizado.getCep());



    }

    @Override
    public int getItemCount() {
        return finalizados.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView endereco;
        TextView pgto;
        TextView status;
        TextView textPedidoCep;
        ImageView imagePedido;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoNome2);
            endereco    = itemView.findViewById(R.id.textPedidoEndereco2);
            pgto        = itemView.findViewById(R.id.textPedidoPgto2);
            status  = itemView.findViewById(R.id.textPedidoObs2);
            textPedidoCep = itemView.findViewById(R.id.textCEP2);
            imagePedido = itemView.findViewById(R.id.imageFinalizado);


        }}
    }