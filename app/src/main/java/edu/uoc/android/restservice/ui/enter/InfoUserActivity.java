package edu.uoc.android.restservice.ui.enter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.android.restservice.R;
import edu.uoc.android.restservice.rest.adapter.GitHubAdapter;
import edu.uoc.android.restservice.rest.model.Follower;
import edu.uoc.android.restservice.rest.model.Owner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;


public class InfoUserActivity extends AppCompatActivity {

    ArrayList<Owner> listaFollowers;
    RecyclerView recyclerViewFollowers;

    //Adaptador para mostrar datos en el recyclerview
    RecyclerView.Adapter adapter_;

    /* variable que guarda el contenido del xml*/
    View conten;

    // declaracion de variables
    ProgressBar progressBar;
    TextView textViewRepositories, textViewFollowing;
    ImageView imageViewProfile;
    TextView error;
/*
    // Cuadro de dialogo que muestra la carga de datos
    private ProgressDialog progressDialog;

    //variable para el procesos del progress
    private int estado=0;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
/*
        // se crea un metodo cargando
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando datos ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
              //muestra el prosgress
                progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //se establece un ciclo para saber si el progreso se completo
            while (estado<100){
                try {
                    // el progreso no termina hasta llegar al tiempo indicado
                    sleep(100);
                    // contador para seguir subiendo el progreso
                    estado++;
                    // se envia el estado del progreso
                    progressDialog.setProgress(estado);
                }catch (Exception e){e.printStackTrace();}
            }
            // se desaparece la ventana de proceso
            progressDialog.dismiss();
            }
        }).start();
*/
        // se indica los id, de acuerdo a cada componente
        conten = findViewById(R.id.content);
        progressBar = findViewById(R.id.progress);
        textViewFollowing = findViewById(R.id.textViewFollowing);
        textViewRepositories = findViewById(R.id.textViewRepositories);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        recyclerViewFollowers = (RecyclerView)findViewById(R.id.recyclerViewFollowers);
        recyclerViewFollowers.setLayoutManager(new LinearLayoutManager(this));
        error = (TextView) findViewById(R.id.error);

        // se crea una lista que contendra a los seguidores
        listaFollowers = new ArrayList<>();

        // se crea una variable para guardar el nombre que se buscara
        String loginName;
        // permite recibir datos de una actividad a otra
        Bundle extras = getIntent().getExtras();
        // si el contenido es nulo
        if(extras == null) {
            // se manda este mensaje de pantalla
        } else {
            // caso contratio se busca la información necesaria del usuario
            loginName= getIntent().getStringExtra("loginName");

            // muestra la cantidad de repositorios, de seguidores y su foto
            mostrarDatosBasicos(loginName);

        }
    }

    // muestra la cantidad de repositorios, de seguidores y su foto
    private void mostrarDatosBasicos( final String loginName){
        showLoading();
        // constuctor hacia la clase GitHubAdapter
        GitHubAdapter adapter = new GitHubAdapter();

        // se establec una paeticion segun el nombre del usuario
        Call<Owner> call = adapter.getOwner(loginName);
        call.enqueue(new Callback<Owner>() {

            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                // se guarda en una variable tipo Owner el cupero de la respuesta
                Owner owner = response.body();

                if (owner!=null){
                    // Se envia la cantidad de repositorios y seguidores, y la imagen del usuario
                    textViewRepositories.setText(owner.getPublicRepos().toString());
                    textViewFollowing.setText(owner.getFollowers().toString());
                    Picasso.with(getApplicationContext()).load(owner.getAvatarUrl()).into(imageViewProfile);
                    // muestra una lista de los seguidores del usuario
                    mostrarFollowers(loginName);
                }else{
                    //Alerta
                    showError();
                    /*Toast.makeText(getApplicationContext(),"No existe este usuario", Toast.LENGTH_LONG).show();
                    onBackPressed();*/
                }
            }

            @Override
            public void onFailure(Call<Owner> call, Throwable t) {
                //Alerta
                showError();
            }
        });
    }

    // muestra una lista de los seguidores del usuario
    private void mostrarFollowers(String loginName){
        //Se crea un ArrayList que guardara a los seguidores del usuario
        final ArrayList<Follower> seguidores = new ArrayList<>();

        // constuctor hacia la clase GitHubAdapter
        GitHubAdapter adapter = new GitHubAdapter();

        // se establec una paeticion segun el nombre del usuario
        Call<List<Owner>> call = adapter.getOwnerFollowers(loginName);
        call.enqueue(new Callback<List<Owner>>() {
            @Override
            public void onResponse(Call<List<Owner>> call, Response<List<Owner>> response) {
                // Se guarda en una lista el cuerpo de la respuesta
                List<Owner> list = response.body();
                if (list!=null){
                    // para cada dato en la lista, se la guarda en una variable tipo owner
                    for (Owner a : list){
                        // se agrega en la lista los nombres de los seguidores y la imagenes
                        seguidores.add(new Follower(a.getLogin(),a.getAvatarUrl()));
                    }
                    // comprobacion de que se guarda en la lista
                    Log.i("Lista",seguidores.toString());
                    // se envia los datos al adaptador de seguidores
                    adapter_ = new AdaptadorFollowers(seguidores);
                    // se establecio para actualizar cualquier cambio
                    adapter_.notifyDataSetChanged();
                    // en muestran los datos en el recyclerView
                    recyclerViewFollowers.setAdapter(adapter_);
                    // muestra el contenido
                    showUserContent();
                }else{
                    //Alerta
                    showError();}
            }

            @Override
            public void onFailure(Call<List<Owner>> call, Throwable t) {
                //Alerta
                showError();
            }
        });
    }

    // metodo que se encarga de mostrar el progress
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        conten.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    // metodo que se encarga de mostrar el mensaje de error
    private void showError() {
        progressBar.setVisibility(View.GONE);
        conten.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
    }

    // metodo que se encarga de mostrar los seguidores del usuario
    private void showUserContent() {
        progressBar.setVisibility(View.GONE);
        conten.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
    }
}
