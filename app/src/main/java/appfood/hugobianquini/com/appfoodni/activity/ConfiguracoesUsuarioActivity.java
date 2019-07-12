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
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.model.Usuario;
import dmax.dialog.SpotsDialog;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {


    private EditText editUsuarioNome, editUsuarioEndereco, editUsuarioCep, editUsuarioNumero;
    private ImageView imagePerfilUsuario;
    private AlertDialog dialog;
    private String idUsuario;
    private DatabaseReference firebaseRef;
    private StorageReference storageReference;
    private static final int SELECAO_GALERIA = 200;
    String urlImagemSelecionada = "https://firebasestorage.googleapis.com/v0/b/appfood-484a0.appspot.com/o/imagens%2Fclientes%2Fperfil.png?alt=media&token=293e3625-b4a4-42df-a1cc-5bb8ac2da109";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();
        storageReference = ConfiguraçãoFirebase.getFirebaseStorage();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações Usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);


                }
            }
        });

        //Recupera os dados do Usuário
        recuperarDadosUsuario();

    }

    public void validarDadosUsuario(View view){
        //Validar se os campos foram preenchidos
        String nome = editUsuarioNome.getText().toString();
        String Endereco = editUsuarioEndereco.getText().toString();
        String cep = editUsuarioCep.getText().toString();
        String numero = editUsuarioNumero.getText().toString();

        if (!nome.isEmpty()){
            if (!Endereco.isEmpty()) {
                if (cep.length() == 9){
                    if (numero.length() == 15 || numero.length()==14){
                        dialog = new SpotsDialog.Builder()
                                .setContext(this)
                                .setMessage("Salvando dados")
                                .setCancelable(false)
                                .build();
                        dialog.show();


                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(idUsuario);
                        usuario.setNome(nome);
                        usuario.setEndereco(Endereco);
                        usuario.setCep(cep);
                        usuario.setNumero(numero);
                        usuario.setUrlImagem(urlImagemSelecionada);
                        usuario.salvar();
                        dialog.dismiss();
                        finish();

                    }else {
                        exibirMensagem("Telefone Inválido");
                    }
                }else {
                    exibirMensagem("CEP Inválido");
                }
            }else {
                exibirMensagem("Digite seu Endereço para as entregas");
            }
        }else {
            exibirMensagem("Digite seu Nome");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(ConfiguracoesUsuarioActivity.this, texto, Toast.LENGTH_SHORT).show();
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
                    imagePerfilUsuario.setImageBitmap(imagem);

                    dialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage("Carregando imagem")
                            .setCancelable(false)
                            .build();
                    dialog.show();

                    //fazendo upload da imagem
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference.child("imagens").child("clientes").child(idUsuario + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesUsuarioActivity.this, "Erro ao fazer Upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesUsuarioActivity.this, "Sucesso ao fazer Upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                            storageReference.child("imagens").child("clientes").child(idUsuario + "jpeg").getDownloadUrl()
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
                dialog.dismiss();
            }


        }}






    private void inicializarComponentes(){
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
        editUsuarioCep = findViewById(R.id.editUsuarioCep);
        editUsuarioNumero = findViewById(R.id.editUsuarioNumero);
        imagePerfilUsuario = findViewById(R.id.imagePerfilUsuario);
    }

    private void recuperarDadosUsuario(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioEndereco.setText(usuario.getEndereco());
                    editUsuarioCep.setText(usuario.getCep());
                    editUsuarioNumero.setText(usuario.getNumero());
                    urlImagemSelecionada = usuario.getUrlImagem();

                    if (urlImagemSelecionada != ""){
                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilUsuario);

                        dialog.dismiss();
                    }
                }

                dialog.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
