package appfood.hugobianquini.com.appfoodni.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.adapter.AdapterFinalizados;
import appfood.hugobianquini.com.appfoodni.adapter.AdapterMeusPedidos;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.listener.RecyclerItemClickListener;
import appfood.hugobianquini.com.appfoodni.model.Empresa;
import appfood.hugobianquini.com.appfoodni.model.Finalizado;
import appfood.hugobianquini.com.appfoodni.model.PedidoUsuario;
import dmax.dialog.SpotsDialog;

public class MeusPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidoUsuario;
    private AdapterMeusPedidos adapterMeusPedidos;
    private List<PedidoUsuario> pedidoUsuarios = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuario;
    private AlertDialog dialog;
    private AlertDialog.Builder dialog1;
    private AlertDialog.Builder dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_pedidos);

        inicializaComponentes();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();

        idUsuario = UsuarioFirebase.getIdUsuario();


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerPedidoUsuario.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidoUsuario.setHasFixedSize(true);
        adapterMeusPedidos = new AdapterMeusPedidos(pedidoUsuarios);
        recyclerPedidoUsuario.setAdapter(adapterMeusPedidos);

        recuperarPedidos();

        recyclerPedidoUsuario.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPedidoUsuario,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {

                        dialog2 = new AlertDialog.Builder(MeusPedidosActivity.this);
                        dialog2.setTitle("Deseja executar qual operação?" );
                        dialog2.setMessage("\n Escolha uma das opções abaixo:");
                        dialog2.setIcon(R.drawable.ic_dehaze_black_24dp);
                        dialog2.setCancelable(true);

                        dialog2.setPositiveButton("Avaliar Restaurante", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                PedidoUsuario pedidoUsuario = pedidoUsuarios.get(position);
                                String avaliado = pedidoUsuario.getAvaliado();
                                final String idEmpresa = pedidoUsuario.getIdEmpresa();
                                final String idPedido = pedidoUsuario.getIdPedido();

                                if (avaliado.equals("0")){

                                Intent intent = new Intent(MeusPedidosActivity.this, AvaliacaoActivity.class);
                                intent.putExtra("idEmpresa", idEmpresa);
                                intent.putExtra("idPedido", idPedido);
                                startActivity(intent);

                                }

                                else {
                                    Toast.makeText(MeusPedidosActivity.this, "Você já avaliou essa entrega.",Toast.LENGTH_SHORT).show();
                                }


                                /* final DatabaseReference avaliacaoRef = firebaseRef.child("empresa").child(idEmpresa);
                                avaliacaoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                                        Empresa empresa = dataSnapshot.getValue(Empresa.class);

                                        int nota = empresa.getNota();

                                        avaliacaoRef.child("nota").setValue(nota + 5);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });*/


                            }
                        });

                        dialog2.setNeutralButton("Voltar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        dialog2.setNegativeButton("Remover Registro", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialog1 = new AlertDialog.Builder(MeusPedidosActivity.this);
                                dialog1.setTitle("Deletar registro de Pedido");
                                dialog1.setMessage("Deletar o registro desse pedido não o cancelará caso esteja em andamento. \n" +
                                        "Recomendamos que delete o registro apenas quando a entrega for concluída");
                                dialog1.setIcon(android.R.drawable.ic_delete);
                                dialog1.setCancelable(false);
                                dialog1.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                dialog1.setPositiveButton("Deletar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        PedidoUsuario pedidoUsuario = pedidoUsuarios.get(position);
                                        pedidoUsuario.remover();
                                        Toast.makeText(MeusPedidosActivity.this, "Registro de pedido deletado",
                                                Toast.LENGTH_SHORT ).show();
                                        adapterMeusPedidos.notifyDataSetChanged();

                                    }
                                });

                                dialog1.create();
                                dialog1.show();


                            }
                        });

                        dialog2.create();
                        dialog2.show();

                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {



                        dialog1 = new AlertDialog.Builder(MeusPedidosActivity.this);
                        dialog1.setTitle("Deletar registro de Pedido");
                        dialog1.setMessage("Deletar o registro desse pedido não o cancelará caso esteja em andamento. \n" +
                                "Recomendamos que delete o registro apenas quando a entrega for concluída");
                        dialog1.setIcon(android.R.drawable.ic_delete);
                        dialog1.setCancelable(false);
                        dialog1.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        dialog1.setPositiveButton("Deletar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                PedidoUsuario pedidoUsuario = pedidoUsuarios.get(position);
                                pedidoUsuario.remover();
                                Toast.makeText(MeusPedidosActivity.this, "Registro de pedido deletado",
                                        Toast.LENGTH_SHORT ).show();
                                adapterMeusPedidos.notifyDataSetChanged();

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
                .child("meus_pedidos")
                .child(idUsuario);


        Query finalizadoPesquisa = pedidoRef.orderByChild("status").equalTo("confirmado");

        finalizadoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pedidoUsuarios.clear();
                if (dataSnapshot.getValue()!= null){

                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        PedidoUsuario pedidoUsuario = ds.getValue(PedidoUsuario.class);
                        pedidoUsuarios.add(pedidoUsuario);
                    }

                    adapterMeusPedidos.notifyDataSetChanged();
                    dialog.dismiss();

                }

                if (dataSnapshot.getValue() == null){
                    dialog.dismiss();
                    Toast.makeText(MeusPedidosActivity.this, "Você ainda não fez pedidos. \n Preencha as configurações do Usuário e realize um pedido",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


   private void inicializaComponentes (){

        recyclerPedidoUsuario = findViewById(R.id.recyclerMeusPedidos);

    }

}
