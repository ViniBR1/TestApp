package appfood.hugobianquini.com.appfoodni.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.adapter.AdapterProduto;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.listener.RecyclerItemClickListener;
import appfood.hugobianquini.com.appfoodni.model.Empresa;
import appfood.hugobianquini.com.appfoodni.model.ItemPedido;
import appfood.hugobianquini.com.appfoodni.model.Pedido;
import appfood.hugobianquini.com.appfoodni.model.PedidoUsuario;
import appfood.hugobianquini.com.appfoodni.model.Produto;
import appfood.hugobianquini.com.appfoodni.model.Usuario;
import dmax.dialog.SpotsDialog;

public class ProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosEmpresa;
    private ImageView imageEmpresa;
    private TextView textEmpresaProdutos;
    private Empresa empresaSelecionada;
    private AlertDialog dialog;
    private TextView textCarrinhoQtd, textCarrinhoTotal;


    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsuarioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private int metodoPagamento;
    private String verificacao = "0";
    private String nomeEmpresa;
    private String emailEmpresa;
    private String numeroEmpresa;

    public static final int PAYPAL_REQUEST_CODE = 9999;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK) //tipo de serviço
            .clientId(ConfigPaypal.PAYPAL_CLIENTE_ID);

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        inicializarComponentes();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Recuperar empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            empresaSelecionada = (Empresa)bundle.getSerializable("empresa");
            nomeEmpresa = empresaSelecionada.getNome();
            emailEmpresa = empresaSelecionada.getEmail();
            numeroEmpresa = empresaSelecionada.getNumero();
            idEmpresa = empresaSelecionada.getIdUsuario();//id da empresa
            textEmpresaProdutos.setText(empresaSelecionada.getNome());
            String url = empresaSelecionada.getUrlImagem();

            if(url != ""){
                Picasso.get().load(url).into(imageEmpresa);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurar RecyclerView

        recyclerProdutosEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosEmpresa.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosEmpresa.setAdapter(adapterProduto);

        //Configurar evento de clique no RecyclerView
        recyclerProdutosEmpresa.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerProdutosEmpresa, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               /* Usuario usuario = new Usuario();
                String nome =  usuario.getNome();   if (nome!=null){*/

                confirmarQuantidade(position);}


            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));



        //Recupera Produtos para Empresa
        recuperarProdutos();
        recuperarDadosUsuario();

        DatabaseReference verificacaoRef = firebaseRef.child("usuarios").child(idUsuarioLogado);

        verificacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){

                    verificacao = "1";

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

}

    private void confirmarQuantidade(final int posicao){



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade desejada");

        final EditText editQuantidade = new EditText(this);

        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (verificacao == "1"){

                String quantidade = editQuantidade.getText().toString();

                Produto produtoSelecionado = produtos.get(posicao);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));
                itensCarrinho.add(itemPedido);

                if(pedidoRecuperado == null){

                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                }

                pedidoRecuperado.setNome(usuario.getNome());
                pedidoRecuperado.setEndereco(usuario.getEndereco());
                pedidoRecuperado.setNumero(usuario.getNumero());
                pedidoRecuperado.setCep(usuario.getCep());
                pedidoRecuperado.setUrlImagem(usuario.getUrlImagem());
                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.salvar();}

                else {
                    Toast.makeText(ProdutosActivity.this,
                            "Preencha os dados de Configurações de Usuário para poder efetuar um pedido",
                            Toast.LENGTH_LONG).show();
                }


            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarDadosUsuario() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(idUsuarioLogado);
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarPedido() {

        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario")
                .child(idEmpresa).child(idUsuarioLogado);

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                qtdItensCarrinho = 0;
                totalCarrinho = 0.00;
                itensCarrinho = new ArrayList<>();

                if (dataSnapshot.getValue() != null){
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();

                    for (ItemPedido itemPedido: itensCarrinho){

                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;

                    }

                }

                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtd.setText("Quantidade: " + String.valueOf(qtdItensCarrinho));
                textCarrinhoTotal.setText("R$ " + df.format(totalCarrinho));

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuPedido :
                if (qtdItensCarrinho == 0 ){
                    Toast.makeText(ProdutosActivity.this,
                            "Selecione pelo menos um item para confirmar o pedido",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                confirmarPedido();
                }
                break;
            case R.id.menuCancelar:
                if (qtdItensCarrinho >= 1){
                pedidoRecuperado.remover();
                Toast.makeText(this, "Pedido Cancelado", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.menuSobreEmpresa:
                Intent intent = new Intent(ProdutosActivity.this, SobreEmpresaActivity.class);
                intent.putExtra("empresa", empresaSelecionada);
                startActivity(intent);
                break;
            case R.id.menuAvaliacao:
                Intent i = new Intent(ProdutosActivity.this, NotaRestaurante.class);
                i.putExtra("empresa", empresaSelecionada);
                startActivity(i);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmarPedido() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o método de pagamento");

        CharSequence[] itens = new CharSequence[]{
                "Paypal", "Cartão de Crédito"
        };
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                metodoPagamento = i;


            }
        });

        final EditText editObservacao = new EditText(this);
        editObservacao.setHint("Observações...");
        builder.setView(editObservacao);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //O PAYPAL ENTRARÁ AQUI, DA SEGUINTE FORMA: if(metodoPagamento == 0) {executar Paypal}

                if (metodoPagamento == 0){

                    Intent intent = new Intent(ProdutosActivity.this, PayPalService.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    startService(intent);

                    String Preço = totalCarrinho.toString();

                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(Preço)), "BRL", "Pague agora", PayPalPayment.PAYMENT_INTENT_SALE);
                    Intent intentt = new Intent(ProdutosActivity.this, PaymentActivity.class);
                    intentt.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intentt.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intentt, PAYPAL_REQUEST_CODE);

                }

                if (metodoPagamento == 3){
                    pedidoRecuperado.remover();
                }
                String observacao = editObservacao.getText().toString();
                pedidoRecuperado.setMetodoPagamento(metodoPagamento);
                pedidoRecuperado.setObservacao(observacao);

            }
        });


        builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK) {
                assert data != null;
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {

                    pedidoRecuperado.setStatus("confirmado");
                    pedidoRecuperado.confirmar();
                    pedidoRecuperado.remover();

                    PedidoUsuario pedidoUsuario = new PedidoUsuario();
                    pedidoUsuario.setIdUsuario(pedidoRecuperado.getIdUsuario());
                    pedidoUsuario.setIdEmpresa(pedidoRecuperado.getIdEmpresa());
                    pedidoUsuario.setIdPedido(pedidoRecuperado.getIdPedido());
                    pedidoUsuario.setNome(pedidoRecuperado.getNome());
                    pedidoUsuario.setNomeEmpresa(nomeEmpresa);
                    pedidoUsuario.setEndereco(pedidoRecuperado.getEndereco());
                    pedidoUsuario.setNumeroEmpresa(numeroEmpresa);
                    pedidoUsuario.setEmailEmpresa(emailEmpresa);
                    pedidoUsuario.setTotal(totalCarrinho);
                    pedidoUsuario.setMetodoPagamento(pedidoRecuperado.getMetodoPagamento());
                    pedidoUsuario.setStatus("confirmado");
                    pedidoUsuario.setItens(pedidoRecuperado.getItens());
                    pedidoUsuario.setAvaliado("0");
                    pedidoUsuario.salvar();

                    pedidoRecuperado = null;

                    Toast.makeText(ProdutosActivity.this, "Pedido Realizado com Sucesso",
                            Toast.LENGTH_SHORT).show();

                    try {
                        String paymentDetais = confirmation.toJSONObject().toString(4);

                        String preco = totalCarrinho.toString();


                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetais)
                                .putExtra("PaymentAmount", preco )

                        );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }


    private void inicializarComponentes(){
        recyclerProdutosEmpresa = findViewById(R.id.recyclerProdutosEmpresa);
        imageEmpresa = findViewById(R.id.imageEmpresa);
        textEmpresaProdutos = findViewById(R.id.textNomeEmpresaProdutos);
        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

    }

    private void recuperarProdutos() {
        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idEmpresa);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
