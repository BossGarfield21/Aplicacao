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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import static pt.novaleaf.www.maisverde.ItemGruposFragment.myItemGrupoFragmentRecyclerViewAdapter;


/**
 * Author: Hugo Mochao
 * Atividade relativa aos grupos
 * Mostra os grupos a que uma pessoa esta associada
 */
public class GruposListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    MyItemGrupoFragmentRecyclerViewAdapter adapter;
    static ArrayList<Grupo> grupos = new ArrayList<>();
    static ArrayList<Grupo> tempGrupos = new ArrayList<>();
    private ItemGruposFragment.OnListFragmentInteractionListener mListener;
    private PopupMenu popup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GruposListActivity.this, CriarGrupoActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mListener = new ItemGruposFragment.OnListFragmentInteractionListener() {
            @Override
            public void onGrupoInteraction(Grupo item) {
                Intent intent = new Intent(GruposListActivity.this, GrupoFeedActivity.class);
                intent.putExtra("toolbar", item.getName());
                startActivity(intent);

            }
        };

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);
/**
 FragmentManager fragmentManager = getSupportFragmentManager();
 Fragment fragment = fragmentManager.findFragmentById(R.id.gruposLinear);
 if (fragment==null) {
 fragment = ItemGruposFragment.newInstance(1);

 fragmentManager.beginTransaction()
 .add(R.id.gruposLinear, fragment)
 .commit();

 //myItemGrupoFragmentRecyclerViewAdapter.notifyDataSetChanged();
 }
 */
        grupos.add(new Grupo("Binas churra", null, null, 0, 0,
                null, null, "Público", "Setúbal"));
        grupos.add(new Grupo("Bombeir0's Crew", null, null, 0, 0,
                null, null, "Público", "Porto"));

        adapter = new MyItemGrupoFragmentRecyclerViewAdapter(grupos, mListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gruposLinear);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        tempGrupos = new ArrayList<>(grupos);

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
        getMenuInflater().inflate(R.menu.grupos_main, menu);


        MenuItem item = menu.findItem(R.id.search);


        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //myItemGrupoFragmentRecyclerViewAdapter.getFilter().filter(query);

                adapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //myItemGrupoFragmentRecyclerViewAdapter
                //grupos.add(new Grupo("Binas churra", null, null, 0, 0,
                //      null, null, "Público", "Setubal"));
                //adapter.notifyDataSetChanged();
                adapter.getFilter().filter(newText);
                //adapter.notifyDataSetChanged();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.search) {

            return true;

        } else if (id == R.id.filter) {

            showMenu(findViewById(R.id.filter));
        } else if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {
            //TODO: sair da app
            final AlertDialog.Builder alert = new AlertDialog.Builder(GruposListActivity.this);
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
                            Intent intent = new Intent(GruposListActivity.this, LoginActivity.class);
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
        } else if (id == R.id.action_acerca) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anovaleaf.ddns.net"));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUncheckedMenu(PopupMenu menu, MenuItem item) {


        for (int i = 0; i < menu.getMenu().size(); i++) {
            if (!menu.getMenu().getItem(i).equals(item)) {
                menu.getMenu().getItem(i).setCheckable(false);
                menu.getMenu().getItem(i).setChecked(false);
            }
        }

    }

    public void showMenu(View v) {
        if (popup == null) {
            popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (!item.isChecked()) {
                        item.setCheckable(true);
                        item.setChecked(!item.isChecked());
                        setUncheckedMenu(popup, item);
                        showDistrict(item.getTitle().toString());
                    }

                    return false;
                }
            });// to implement on click event on items of menu
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.distritos_menu, popup.getMenu());

            popup.getMenu().findItem(R.id.d0).setCheckable(true);
            popup.getMenu().findItem(R.id.d0).setChecked(true);
        }
        popup.show();
    }

    private void showDistrict(String distrito) {

        //tempGrupos.clear();
        Log.d("DITRITO", distrito);
        if (!distrito.equals("TUDO")) {
            tempGrupos.clear();
            adapter.setDistrito(distrito);

            for (Grupo grupo : grupos) {
                if (grupo.getDistrito().equals(distrito))
                    tempGrupos.add(grupo);
            }
            MyItemGrupoFragmentRecyclerViewAdapter.mValues = tempGrupos;
            adapter.notifyDataSetChanged();
        } else {
            tempGrupos = new ArrayList<>(grupos);
            adapter.setDistrito("");
            MyItemGrupoFragmentRecyclerViewAdapter.mValues = tempGrupos;
            adapter.notifyDataSetChanged();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_eventos) {
            Intent i = new Intent(GruposListActivity.this, FeedEventosActivity.class);
            startActivityForResult(i, 0);
            finish();
        } else if (id == R.id.nav_feed) {
            Intent i = new Intent(GruposListActivity.this, FeedActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_mapa) {
            Intent i = new Intent(GruposListActivity.this, MapsActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_area_pessoal) {
            Intent i = new Intent(GruposListActivity.this, AlterarDadosActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_grupos) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
