package appfood.hugobianquini.com.appfoodni.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.model.Empresa;
import appfood.hugobianquini.com.appfoodni.model.Produto;
import dmax.dialog.SpotsDialog;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private ImageView imagePerfilProduto;
    private FirebaseAuth autenticacao;
    private String idUsuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    String urlImagemSelecionada = "https://firebasestorage.googleapis.com/v0/b/appfood-484a0.appspot.com/o/imagens%2Fprodutos%2Fgastro_logo.png?alt=media&token=6f180495-8399-4f8c-bbe0-032449648fc1";
    private String idProduto;
    private android.app.AlertDialog dialog;
    private String verificacao = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        inicializarComponentes();
        storageReference = ConfiguraçãoFirebase.getFirebaseStorage();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        imagePerfilProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);


                }
            }
        });

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("empresa")
                .child(idUsuarioLogado);

        pedidoRef.addValueEventListener(new ValueEventListener() {
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



    public void validarDadosProduto(View view){





        if (verificacao == "1"){

        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();



        if ((!nome.isEmpty())){
            if (!descricao.isEmpty()){
                if (!preco.isEmpty()){


                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setUrlImagem(urlImagemSelecionada);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();
                    finish();
                    exibirMensagem("Produto Registrado Com Sucesso");

                }else{ exibirMensagem("Digite o preço para o Produto");}

            }else{ exibirMensagem("Digite uma descrição para o Produto");}
        }else {
            exibirMensagem("Digite um nome para o Produto");
        }}

        else {
            Toast.makeText(NovoProdutoEmpresaActivity.this, "Preencha as Configurações da Empresa para realizar cadastro de produtos",
                    Toast.LENGTH_LONG).show();
        }


    }

    private void exibirMensagem(String texto){
        Toast.makeText(NovoProdutoEmpresaActivity.this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                localImagem);
                        break;
                }

                if (imagem != null){

                    imagePerfilProduto.setImageBitmap(imagem);

                    dialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage("Carregando")
                            .setCancelable(false)
                            .build();
                    dialog.show();

                    //fazendo upload da imagem
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    Produto produto = new Produto();
                    idProduto = produto.getIdProduto();

                    StorageReference imagemRef = storageReference.child("imagens").child("produtos").child(idProduto + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this, "Erro ao fazer Upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //urlImagemSelecionada = String.valueOf(taskSnapshot.getDownloadUrl());
                            Toast.makeText(NovoProdutoEmpresaActivity.this, "Sucesso ao fazer Upload da imagem", Toast.LENGTH_SHORT)
                                    .show();

                            storageReference.child("imagens").child("produtos").child(idProduto + "jpeg").getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            urlImagemSelecionada = task.getResult().toString();
                                        }
                                    });
                        }
                    });

                    dialog.dismiss();
                }

            }catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
            }


        }}

    private void inicializarComponentes(){

        imagePerfilProduto = findViewById(R.id.imagePerfilProduto);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescrição);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);

    }

}
