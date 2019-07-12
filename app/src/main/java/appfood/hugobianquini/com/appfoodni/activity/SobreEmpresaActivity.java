package appfood.hugobianquini.com.appfoodni.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import appfood.hugobianquini.com.appfoodni.MapsActivity;
import appfood.hugobianquini.com.appfoodni.R;
import appfood.hugobianquini.com.appfoodni.model.Empresa;

public class SobreEmpresaActivity extends AppCompatActivity {

    private ImageView imageSobreEmpresa, imageLigar, logoMaps;
    private TextView nome;
    private TextView Endereço, Descricao, textTelefone, textEmail, textLigar, textMaps;
    private Empresa empresaSelecionada;
    private String nomeEmpresa;
    private String Endereco;
    private String DescricaoEmpresa;
    private String urlImagem;
    private String email;
    private String telefone;
    private String idEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_empresa);

        inicializaComponentes();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            empresaSelecionada = (Empresa)bundle.getSerializable("empresa");
            nomeEmpresa = empresaSelecionada.getNome();
            Endereco = empresaSelecionada.getEndereco();
            DescricaoEmpresa = empresaSelecionada.getDescricao();
            email = empresaSelecionada.getEmail();
            telefone = empresaSelecionada.getNumero();
            urlImagem = empresaSelecionada.getUrlImagem();
            idEmpresa = empresaSelecionada.getIdUsuario();



            Picasso.get().load(urlImagem).into(imageSobreEmpresa);

        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sobre o Restaurante");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nome.setText(nomeEmpresa);
        Endereço.setText(Endereco);
        Descricao.setText(idEmpresa);
        textTelefone.setText("Telefone: " + telefone + "    ");
        textEmail.setText("Email: " + email);
        if (DescricaoEmpresa != null){

            Descricao.setText(DescricaoEmpresa);

        }else {

            Descricao.setText("Este Restaurante não possui descrição");
        }

        textLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", telefone, null));
                startActivity(i);
            }
        });

        imageLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", telefone, null));
                startActivity(i);
            }
        });


        logoMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLocationFromAddress(SobreEmpresaActivity.this, Endereco);
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


            Intent i = new Intent(SobreEmpresaActivity.this, MapsActivity.class);
            i.putExtra("lat", latitude);
            i.putExtra("long", longitude);
            i.putExtra("End", strAddress);
            startActivity(i);



        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }






    private void inicializaComponentes(){

        nome = findViewById(R.id.textNomeSobreEmpresa);
        Endereço = findViewById(R.id.textEnderecoSobreEmpresa);
        Descricao = findViewById(R.id.textDescricaoEmpresa);
        textEmail = findViewById(R.id.textEmailSobreEmpresa);
        textTelefone = findViewById(R.id.textTelefoneSobreEmpresa);
        imageSobreEmpresa = findViewById(R.id.imageSobreEmpresa);
        textLigar = findViewById(R.id.textLigar);
        imageLigar = findViewById(R.id.imageLigar);
        textMaps = findViewById(R.id.textMaps);
        logoMaps = findViewById(R.id.logoMaps);


    }
}
