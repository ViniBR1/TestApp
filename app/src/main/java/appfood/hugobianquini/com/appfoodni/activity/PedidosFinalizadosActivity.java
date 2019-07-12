package appfood.hugobianquini.com.appfoodni.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.adapter.AdapterFinalizados;
import appfood.hugobianquini.com.appfoodni.adapter.AdapterPedido;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.listener.RecyclerItemClickListener;
import appfood.hugobianquini.com.appfoodni.model.Finalizado;
import appfood.hugobianquini.com.appfoodni.model.Pedido;
import dmax.dialog.SpotsDialog;

public class PedidosFinalizadosActivity extends AppCompatActivity {

    private RecyclerView recyclerFinalizados;
    private AdapterFinalizados adapterFinalizados;
    private List<Finalizado> finalizados = new ArrayList<>();
    private AlertDialog dialog;
    private AlertDialog.Builder dialog1;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_finalizados);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();


        //Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos - Finalizados");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference pedidoRef = firebaseRef.child("pedidos_finalizados")
                .child(idEmpresa);

        //Configura RecyclerView
        recyclerFinalizados.setLayoutManager(new LinearLayoutManager(this));
        recyclerFinalizados.setHasFixedSize(true);
        adapterFinalizados = new AdapterFinalizados(finalizados);
        recyclerFinalizados.setAdapter(adapterFinalizados);

        recuperarPedidos();

        //Configurar evento de clique no recyclerview

        recyclerFinalizados.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerFinalizados,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        dialog1 = new AlertDialog.Builder(PedidosFinalizadosActivity.this);
                        dialog1.setTitle("Remover Pedido");
                        dialog1.setMessage("Deletar Registro de Pedido?");
                        dialog1.setCancelable(false);
                        dialog1.setIcon(android.R.drawable.ic_delete);
                        dialog1.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        dialog1.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Finalizado finalizado = finalizados.get(position);
                                finalizado.excluir();
                                adapterFinalizados.notifyDataSetChanged();
                                Toast.makeText(PedidosFinalizadosActivity.this, "Pedido finalizado excluído",
                                        Toast.LENGTH_SHORT).show();

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

    private void recuperarPedidos(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_finalizados")
                .child(idEmpresa);

        Query finalizadoPesquisa = pedidoRef.orderByChild("status").equalTo("finalizado");

        finalizadoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                finalizados.clear();
                if (dataSnapshot.getValue()!= null){

                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        Finalizado finalizado = ds.getValue(Finalizado.class);
                        finalizados.add(finalizado);
                    }

                    adapterFinalizados.notifyDataSetChanged();
                    dialog.dismiss();

                }

                if (dataSnapshot.getValue() == null){
                    dialog.dismiss();
                    Toast.makeText(PedidosFinalizadosActivity.this, "NENHUM PEDIDO FINALIZADO \n Marque pedidos como finalizados segurando uma opção na aba pedidos",
                            Toast.LENGTH_LONG).show();
                    adapterFinalizados.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void inicializarComponentes(){
        recyclerFinalizados = findViewById(R.id.recyclerFinalizados);
    }
}
