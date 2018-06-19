package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * Author: Hugo Mochao
 * Atividade para consultar os dados do utilizador
 */
public class AreaPessoalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //Lista dos dados
    private ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       /*
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




/*

        ArrayList<String> arrayList =  new ArrayList<>();
        arrayList.add("As minhas ocorrências");
        arrayList.add("O meu perfil");


        mList = (ListView) findViewById(R.id.listOcorrencias);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        mList.setAdapter(arrayAdapter);

        //Ver que atividade comecar
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent a = new Intent(AreaPessoalActivity.this, MinhasOcorrenciasActivity.class);
                        startActivity(a);
                        return;
                    case 1:
                        Intent b = new Intent(AreaPessoalActivity.this, AlterarDadosActivity.class);
                        startActivity(b);
                        return;
                }
            }
        });

*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            setResult(RESULT_CANCELED);
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.area_pessoal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            return true;
        } else if(id == R.id.action_logout){
            //TODO: sair da app
            final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(AreaPessoalActivity.this);
            alert.setTitle("Terminar sessão");
            alert
                    .setMessage("Deseja terminar sessão?")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(AreaPessoalActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            android.support.v7.app.AlertDialog alertDialog = alert.create();
            alertDialog.show();
        } else if(id == R.id.action_acerca){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anovaleaf.ddns.net"));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            Intent i = new Intent(AreaPessoalActivity.this, FeedActivity.class);
            startActivity(i);
            //finish();
        } else if(id == R.id.nav_adicionar_report){

            android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(AreaPessoalActivity.this);
            alert.setTitle("Criar report");
            alert
                    .setMessage("O local do report é a sua localização atual?")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(AreaPessoalActivity.this, CriarOcorrenciaActivity.class);
                            intent.putExtra("estaLocal", true);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(AreaPessoalActivity.this, MapsActivity.class);
                            intent.putExtra("toast", true);
                            startActivity(intent);
                        }
                    });

            android.support.v7.app.AlertDialog alertDialog = alert.create();
            alertDialog.show();

        }else if (id == R.id.nav_mapa) {

            Intent i = new Intent(AreaPessoalActivity.this, MapsActivity.class);
            startActivity(i);
            //finish();

        } else if (id == R.id.nav_area_pessoal) {

        } else if (id == R.id.nav_grupos) {

            Intent i = new Intent(AreaPessoalActivity.this, GruposListActivity.class);
            startActivity(i);
            //finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
