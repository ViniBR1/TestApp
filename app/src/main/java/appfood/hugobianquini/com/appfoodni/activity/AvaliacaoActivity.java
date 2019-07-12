package appfood.hugobianquini.com.appfoodni.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Collections;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.helper.UsuarioFirebase;
import appfood.hugobianquini.com.appfoodni.model.Avaliação;
import appfood.hugobianquini.com.appfoodni.model.Empresa;
import appfood.hugobianquini.com.appfoodni.model.PedidoUsuario;
import appfood.hugobianquini.com.appfoodni.model.Usuario;

public class AvaliacaoActivity extends AppCompatActivity {

    private TextView textAvaliacao, textNota, textDescr;
    private SeekBar seekBar;
    private ImageView imageAvaliacao;
    private Button btnAvaliacao;
    private Usuario usuario;
   // private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private DatabaseReference firebaseRef;

    Double media;

    private String avaliação;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao);

        inicializarComponentes();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();
        final String idUsuario = UsuarioFirebase.getIdUsuario();


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            idEmpresa = bundle.getString("idEmpresa");
            idPedido = bundle.getString("idPedido");


        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Avaliação - Restaurante");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference avaliacaoRef = firebaseRef.child("empresa").child(idEmpresa);
        avaliacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Empresa empresa = dataSnapshot.getValue(Empresa.class);
                int nota = empresa.getNota();
                int vezes = empresa.getVezes();

                if (vezes != 0  ) {
                    media = Double.valueOf(nota / vezes);
                    String media1 = String.valueOf(media);


                    DecimalFormat df = new DecimalFormat("0.0");

                    textNota.setText(df.format(media));

                    if (media >= 0 && media<= 4){
                        textDescr.setText("Ruim");
                        textNota.setTextColor(getResources().getColor(R.color.vermelho));
                    }

                    if (media>4 && media <= 6) {
                        textDescr.setText("Regular");
                        textNota.setTextColor(getResources().getColor(R.color.amarelo));
                    }
                    if (media > 6 && media <= 8){
                        textDescr.setText("Bom!");
                        textNota.setTextColor(getResources().getColor(R.color.verde));
                    }
                    if (media >8 && media <=10){
                        textDescr.setText("Ótimo!");
                        textNota.setTextColor(getResources().getColor(R.color.verde));
                    }


                }else {
                    textNota.setText("Ainda não foi avaliado");
                    textDescr.setText("Sem avaliação");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        textAvaliacao.setText("Arraste para avaliar");







/*DatabaseReference verificacaoRef = firebaseRef.child("usuarios").child(idUsuario);

        verificacaoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        seekBar.setProgress(10);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {

                final int avaliacao = i;



                if (avaliacao == 0) {
                    imageAvaliacao.setImageResource(R.drawable.icone_triste);
                    textAvaliacao.setText("0/10");
                }
                if (avaliacao == 1) {
                    imageAvaliacao.setImageResource(R.drawable.icone_triste);
                    textAvaliacao.setText("1/10");
                }
                if (avaliacao == 2) {
                    imageAvaliacao.setImageResource(R.drawable.icone_triste);
                    textAvaliacao.setText("2/10");
                }
                if (avaliacao == 3) {
                    imageAvaliacao.setImageResource(R.drawable.icone_triste);
                    textAvaliacao.setText("3/10");
                }
                if (avaliacao == 4) {
                    imageAvaliacao.setImageResource(R.drawable.icone_triste);
                    textAvaliacao.setText("4/10");
                }

                if (avaliacao == 5) {
                    imageAvaliacao.setImageResource(R.drawable.icone_regular);
                    textAvaliacao.setText("5/10");
                }
                if (avaliacao == 6) {
                    imageAvaliacao.setImageResource(R.drawable.icone_regular);
                    textAvaliacao.setText("6/10");
                }
                if (avaliacao == 7) {
                    imageAvaliacao.setImageResource(R.drawable.icone_regular);
                    textAvaliacao.setText("7/10");
                }
                if (avaliacao == 8) {
                    imageAvaliacao.setImageResource(R.drawable.icone_feliz);
                    textAvaliacao.setText("8/10");
                }
                if (avaliacao == 9) {
                    imageAvaliacao.setImageResource(R.drawable.icone_feliz);
                    textAvaliacao.setText("9/10");
                }
                if (avaliacao == 10) {
                    imageAvaliacao.setImageResource(R.drawable.icone_feliz);
                    textAvaliacao.setText("10/10");
                }


                btnAvaliacao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final DatabaseReference avaliacaoRef = firebaseRef.child("empresa").child(idEmpresa);
                        avaliacaoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Empresa empresa = dataSnapshot.getValue(Empresa.class);
                                int vezes = empresa.getVezes();
                                int nota = empresa.getNota();


                                avaliacaoRef.child("nota").setValue(nota + avaliacao);
                                avaliacaoRef.child("vezes").setValue(vezes + 1);



                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        final DatabaseReference avaliadoRef = firebaseRef.child("meus_pedidos").child(idUsuario).child(idPedido);
                        avaliadoRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                PedidoUsuario pedidoUsuario = dataSnapshot.getValue(PedidoUsuario.class);
                                String confirmacao = pedidoUsuario.getAvaliado();
                                if (confirmacao.equals("0")){
                                avaliadoRef.child("avaliado").setValue("1");

                                finish();
                                startActivity(new Intent(AvaliacaoActivity.this, HomeActivity.class));}

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                   // usuario.setAvaliacao("1");
                        // suario.salvar();



                    }
                });


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





                   /* avaliação = editText.getText().toString();

                    avaliacao = Integer.parseInt(avaliação);


                    nota += avaliacao;
                    vezes += 1;

                    empresaSelecionada.setNota(nota);
                    empresaSelecionada.setVezes(vezes);
                    empresaSelecionada.salvar();

                    finish();*/


    }

        private void inicializarComponentes(){

        btnAvaliacao = findViewById(R.id.btnAvaliacao);
        seekBar = findViewById(R.id.seekBar2);
        textAvaliacao = findViewById(R.id.textAvaliacao);
        imageAvaliacao = findViewById(R.id.imageAvaliacao);
        textNota = findViewById(R.id.textNota);
        textDescr = findViewById(R.id.textDescr);



        }

    @Override
    public void finish() {
        super.finish();
    }
}
