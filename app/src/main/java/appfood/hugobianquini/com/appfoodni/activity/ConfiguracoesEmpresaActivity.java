package appfood.hugobianquini.com.appfoodni.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.helper.MaskType;
import appfood.hugobianquini.com.appfoodni.helper.MaskUtil;
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.model.Empresa;
import dmax.dialog.SpotsDialog;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaEndereco, editEmpresaNumero,
            editEmpresaEmail, editEmpresaDescricao, editEmpresaCnpj;
    private ImageView imagePerfilEmpresa;
    private AlertDialog dialog;
    private Button buttonAcesso2;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    String urlImagemSelecionada = "https://firebasestorage.googleapis.com/v0/b/autopecasni.appspot.com/o/imagens%2Fempresas%2FhVxS2bnhcrOhN3ljB5hvrksRZiX2jpeg?alt=media&token=5571dd59-6fe2-4e88-b63e-38f7e19c63be";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        inicializarComponentes();
        storageReference = ConfiguraçãoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            if (i.resolveActivity(getPackageManager()) != null){
                                startActivityForResult(i, SELECAO_GALERIA);


                }
            }
        });

        recuperarDadosEmpresa();

        editEmpresaCnpj.addTextChangedListener(MaskUtil.insert(editEmpresaCnpj, MaskType.CNPJ));

    }

    private void recuperarDadosEmpresa (){

        DatabaseReference empresaRef = firebaseRef.child("empresa").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaEndereco.setText(empresa.getEndereco());
                    editEmpresaNumero.setText(empresa.getNumero());
                    editEmpresaEmail.setText(empresa.getEmail());
                    editEmpresaDescricao.setText(empresa.getDescricao());
                    editEmpresaCnpj.setText(empresa.getCNPJ());
                    urlImagemSelecionada = empresa.getUrlImagem();
                    if ( urlImagemSelecionada != "" ){

                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilEmpresa);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void validarDadosEmpresa(View view){

        String nome = editEmpresaNome.getText().toString();
        String endereco = editEmpresaEndereco.getText().toString();
        String numero = editEmpresaNumero.getText().toString();
        String email = editEmpresaEmail.getText().toString();
        String descricao = editEmpresaDescricao.getText().toString();
        String cnpj = editEmpresaCnpj.getText().toString();

        if ((!nome.isEmpty())){
            if (!endereco.isEmpty()){
                if (numero.length() == 14 || numero.length() == 15){
                    if (!email.isEmpty()){
                        if (cnpj.length() == 18) {

                            Empresa empresa = new Empresa();
                            empresa.setIdUsuario(idUsuarioLogado);
                            empresa.setNome(nome);
                            empresa.setEndereco(endereco);
                            empresa.setNumero(numero);
                            empresa.setEmail(email);
                            empresa.setUrlImagem(urlImagemSelecionada);
                            empresa.setDescricao(descricao);
                            empresa.setCNPJ(cnpj);
                            empresa.salvar();
                            finish();
                        }else exibirMensagem("CNPJ inválido");
                    }else exibirMensagem("Digite o email para contato");

                }else exibirMensagem("Telefone inválido");

            }else exibirMensagem("Digite o Endereço da Empresa");
        }else {
            exibirMensagem("Digite um nome para empresa");
        }


    }

    private void exibirMensagem(String texto){
        Toast.makeText(ConfiguracoesEmpresaActivity.this, texto, Toast.LENGTH_SHORT).show();
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

                    dialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage("Carregando Imagem")
                            .setCancelable(false)
                            .build();
                    dialog.show();

                    imagePerfilEmpresa.setImageBitmap(imagem);
                    //fazendo upload da imagem
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference.child("imagens").child("empresas").child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this, "Erro ao fazer Upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesEmpresaActivity.this, "Sucesso ao fazer Upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                            storageReference.child("imagens").child("empresas").child(idUsuarioLogado + "jpeg").getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            urlImagemSelecionada = task.getResult().toString();
                                        }
                                    });

                            dialog.dismiss();
                        }
                    });
                }


            }catch (Exception e) {
                e.printStackTrace();
            }


        }}


    private void inicializarComponentes(){

        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaEndereco = findViewById(R.id.editEmpresaEndereco);
        editEmpresaNumero = findViewById(R.id.editEmpresaNumero);
        editEmpresaEmail = findViewById(R.id.editEmpresaEmail);
        editEmpresaDescricao = findViewById(R.id.editEmpresaDescricao);
        editEmpresaCnpj = findViewById(R.id.editEmpresaCnpj);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
        buttonAcesso2 = findViewById(R.id.buttonAcesso2);

    }

}
