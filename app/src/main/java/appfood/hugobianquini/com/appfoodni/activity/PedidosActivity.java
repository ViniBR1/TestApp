package appfood.hugobianquini.com.appfoodni.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.MapsActivity;
import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.adapter.AdapterPedido;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.listener.RecyclerItemClickListener;
import appfood.hugobianquini.com.appfoodni.model.Finalizado;
import appfood.hugobianquini.com.appfoodni.model.Pedido;
import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private AlertDialog.Builder dialog1;
    private AlertDialog.Builder dialog2;
    private DatabaseReference firebaseRef;
    private String idEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();


        //Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos - Reservas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configura RecyclerView
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        //Adiciona evento de clique no recyclerView
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPedidos,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                dialog2 = new AlertDialog.Builder(PedidosActivity.this);
                dialog2.setTitle("Deseja executar qual operação?" );
                dialog2.setMessage("\n Escolha uma das opções abaixo:");
                dialog2.setIcon(R.drawable.ic_dehaze_black_24dp);
                dialog2.setCancelable(true);

                dialog2.setNegativeButton("Finalizar Pedido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialog1 = new AlertDialog.Builder(PedidosActivity.this);
                        dialog1.setTitle("Finalizar Pedido");
                        dialog1.setMessage("Deseja marcar este pedido como Finalizado?");
                        dialog1.setCancelable(false);
                        dialog1.setIcon(android.R.drawable.checkbox_on_background);
                        dialog1.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog1.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Pedido pedido = pedidos.get(position);
                                pedido.setStatus("finalizado");
                                pedido.atualizarStatus();
                                Finalizado finalizado = new Finalizado();
                                finalizado.setIdEmpresa(pedido.getIdEmpresa());
                                finalizado.setIdPedido(pedido.getIdPedido());
                                finalizado.setNome(pedido.getNome());
                                finalizado.setEndereco(pedido.getEndereco());
                                finalizado.setCep(pedido.getCep());
                                finalizado.setStatus(pedido.getStatus());
                                finalizado.setNumero(pedido.getNumero());
                                finalizado.setUrlImagem(pedido.getUrlImagem());
                                finalizado.setTotal(pedido.getTotal());
                                finalizado.setMetodoPagamento(pedido.getMetodoPagamento());
                                finalizado.finalizarPedido();
                                pedido.removerPedidos();
                                adapterPedido.notifyDataSetChanged();

                            }
                        });



                        dialog1.create();
                        dialog1.show();


                    }
                });

                dialog2.setPositiveButton("Ver localização", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Pedido pedido = pedidos.get(position);
                        String endereco = pedido.getEndereco();
                        getLocationFromAddress(PedidosActivity.this, endereco);
                    }
                });

                dialog2.setNeutralButton("Voltar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                dialog2.create();
                dialog2.show();

            }

            @Override
            public void onLongItemClick(View view, final int position) {

                dialog1 = new AlertDialog.Builder(PedidosActivity.this);
                dialog1.setTitle("Finalizar Pedido");
                dialog1.setMessage("Deseja marcar este pedido como Finalizado?");
                dialog1.setCancelable(false);
                dialog1.setIcon(android.R.drawable.checkbox_on_background);
                dialog1.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog1.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                Pedido pedido = pedidos.get(position);
                pedido.setStatus("finalizado");
                pedido.atualizarStatus();
                Finalizado finalizado = new Finalizado();
                finalizado.setIdEmpresa(pedido.getIdEmpresa());
                finalizado.setIdPedido(pedido.getIdPedido());
                finalizado.setNome(pedido.getNome());
                finalizado.setEndereco(pedido.getEndereco());
                finalizado.setCep(pedido.getCep());
                finalizado.setStatus(pedido.getStatus());
                finalizado.setNumero(pedido.getNumero());
                finalizado.setUrlImagem(pedido.getUrlImagem());
                finalizado.setTotal(pedido.getTotal());
                finalizado.setMetodoPagamento(pedido.getMetodoPagamento());
                finalizado.finalizarPedido();
                pedido.removerPedidos();
                adapterPedido.notifyDataSetChanged();

                    }
                });



                dialog1.create();
                dialog1.show();

            }



            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(idEmpresa);

        Query pedidoPesquisa = pedidoRef.orderByChild("status").equalTo("confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pedidos.clear();
                if (dataSnapshot.getValue() != null){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(PedidosActivity.this, "Nenhum Pedido Pendente", Toast.LENGTH_LONG).show();
                    adapterPedido.notifyDataSetChanged();
                }

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);

            Double latitude = (location.getLatitude());
            Double longitude = (location.getLongitude());
            // p1 = new LatLng(location.getLatitude(), location.getLongitude() );


            Intent i = new Intent(PedidosActivity.this, MapsActivity.class);
            i.putExtra("lat", latitude);
            i.putExtra("long", longitude);
            i.putExtra("End", strAddress);
            startActivity(i);



        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private void inicializarComponentes(){
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }
}
