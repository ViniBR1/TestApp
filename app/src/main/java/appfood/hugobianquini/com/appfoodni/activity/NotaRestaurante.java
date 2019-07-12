package appfood.hugobianquini.com.appfoodni.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;

import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;
import appfood.hugobianquini.com.appfoodni.model.Empresa;

public class NotaRestaurante extends AppCompatActivity {

    private TextView textNotaRestaurante, textDescrRestaurante;
    private Button btnMeusPedidos;
    private DatabaseReference firebaseRef;
    private int nota;
    private int vezes;
    private Double media;
    private Empresa empresaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota_restaurante);

        inicializarComponentes();
        firebaseRef = ConfiguraçãoFirebase.getFirebase();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            empresaSelecionada = (Empresa)bundle.getSerializable("empresa");
            nota = empresaSelecionada.getNota();
            vezes = empresaSelecionada.getVezes();

        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Avaliação - Restaurante");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (vezes != 0  ) {
            media = Double.valueOf(nota / vezes);
            String media1 = String.valueOf(media);


            DecimalFormat df = new DecimalFormat("0.0");

            textNotaRestaurante.setText(df.format(media));

            if (media >= 0 && media<= 4){
                textDescrRestaurante.setText("Ruim");
                textNotaRestaurante.setTextColor(getResources().getColor(R.color.vermelho));
            }

            if (media>4 && media <= 6) {
                textDescrRestaurante.setText("Regular");
                textNotaRestaurante.setTextColor(getResources().getColor(R.color.amarelo));
            }
            if (media > 6 && media <= 8){
                textDescrRestaurante.setText("Bom!");
                textNotaRestaurante.setTextColor(getResources().getColor(R.color.verde));
            }
            if (media >8 && media <=10){
                textDescrRestaurante.setText("Ótimo!");
                textNotaRestaurante.setTextColor(getResources().getColor(R.color.verde));
            }


        }else {
            textNotaRestaurante.setText("Ainda não foi avaliado");
            textDescrRestaurante.setText("Sem avaliação");
        }

        btnMeusPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotaRestaurante.this, MeusPedidosActivity.class));
                finish();
            }
        });
    }


    private void inicializarComponentes(){
        textDescrRestaurante = findViewById(R.id.textDescrRestaurante);
        textNotaRestaurante = findViewById(R.id.textNotaRestaurante);
        btnMeusPedidos = findViewById(R.id.btnMeusPedidos);
    }
}
