package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Author: Hugo Mochao
 * Atividade do feed de ocorrencias
 * Implementa um cardview
 */

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OcorrenciaFragment.OnListFragmentInteractionListener {

    private CardView cardView;
    NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (fragment==null){
            fragment = OcorrenciaFragment.newInstance(1);

            fragmentManager.beginTransaction()
                                              .add(R.id.fragmentContainer, fragment)
                                              .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        cardView = (CardView) findViewById(R.id.cardView);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed_menu, menu);
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

            final AlertDialog.Builder alert = new AlertDialog.Builder(FeedActivity.this);
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
                            Intent intent = new Intent(FeedActivity.this, LoginActivity.class);
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

            AlertDialog alertDialog = alert.create();
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

        if (id == R.id.nav_mapa) {

            Intent i = new Intent(FeedActivity.this, MapsActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if(id == R.id.nav_adicionar_report){

            AlertDialog.Builder alert = new AlertDialog.Builder(FeedActivity.this);
            alert.setTitle("Criar report");
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            })
                    .setMessage("O local do report é a sua localização atual?")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(FeedActivity.this, CriarOcorrenciaActivity.class);
                            intent.putExtra("estaLocal", true);
                            startActivityForResult(intent, 0);
                            //startActivity(intent);
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(FeedActivity.this, MapsActivity.class);
                            intent.putExtra("toast", true);
                            startActivityForResult(intent, 0);
                            //startActivity(intent);
                        }
                    });

            AlertDialog alertDialog = alert.create();
            alertDialog.show();


        } else if (id == R.id.nav_area_pessoal) {
            Intent i = new Intent(FeedActivity.this, AlterarDadosActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_grupos) {

            Intent i = new Intent(FeedActivity.this, GruposMainActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_CANCELED) {
                // user pressed back from 2nd activity to go to 1st activity. code here
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    @Override
    public void onLikeInteraction(Ocorrencia item) {

    }

    @Override
    public void onCommentInteraction(Ocorrencia item) {
        //Toast.makeText(FeedActivity.this, "IR PARA A PAGINA DOS COMENTARIOS", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(FeedActivity.this, ComentariosActivity.class);
        startActivity(i);
    }

    @Override
    public void onFavoritoInteraction(Ocorrencia item) {
    }

    @Override
    public void onImagemInteraction(Ocorrencia item) {
        //Toast.makeText(FeedActivity.this, "IR PARA A PAGINA DA OCORRENCIA", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(FeedActivity.this, OcorrenciaActivity.class);
        startActivity(i);
    }
}
