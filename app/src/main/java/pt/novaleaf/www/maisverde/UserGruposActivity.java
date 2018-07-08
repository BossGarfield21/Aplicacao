package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGruposActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_grupos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ConvitesEnviados extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ListView listView;
        List<String> items;
        ArrayAdapter<String> arrayAdapter;
        Map<String, String > grupos;

        public ConvitesEnviados() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ConvitesEnviados newInstance(int sectionNumber) {
            ConvitesEnviados fragment = new ConvitesEnviados();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user_grupos, container, false);
            listView = (ListView) rootView.findViewById(R.id.section_label);
            items = new ArrayList<>();
            grupos = new HashMap<>();
            arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!items.get(i).equals("Não há pedidos..."))
                        popupCancelar(view, items.get(i));
                    return false;
                }
            });
            volleyGetPedidosEnviados();
            return rootView;
        }

        private void popupCancelar(View view, final String pedido) {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.cancelar_pedido) {
                        cancelJoinGroupVolley(pedido);
                    }
                    return false;
                }
            });

            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.cancelar_pedido_menu, popupMenu.getMenu());
            popupMenu.show();

        }

        private void cancelJoinGroupVolley(final String grupo) {

            String tag_json_obj = "json_obj_req";
            String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/cancel_request?group_id=" + grupos.get(grupo);

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Prefs", MODE_PRIVATE);
            final String token = sharedPreferences.getString("tokenID", "erro");


            StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            items.remove(grupo);
                            grupos.remove(grupo);
                            arrayAdapter.notifyDataSetChanged();

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    return headers;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        }

        private void volleyGetPedidosEnviados() {
            String tag_json_obj = "json_request";
            String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/listrequests";

            Log.d("ché bate só", url);

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Prefs", MODE_PRIVATE);
            JSONArray eventos = new JSONArray();
            final String token = sharedPreferences.getString("tokenID", "erro");


            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, eventos,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("tao mano", response.toString());
                            try {
                                if (response!=null && response.length()>0) {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject pedido = response.getJSONObject(i);

                                        items.add(pedido.getString("key"));
                                        grupos.put(pedido.getString("value"), pedido.getString("key"));
                                        arrayAdapter.notifyDataSetChanged();

                                    }
                                } else {
                                    Log.d("olá", "heheehehe");
                                    items.add("Não há pedidos...");
                                    arrayAdapter.notifyDataSetChanged();

                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener()

            {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erro lista", "Error: " + error.getMessage());
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    return headers;
                }
            };


            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        }


    }


    public static class ConvitesRecebidos extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ListView listView;
        List<String> items;
        ArrayAdapter<String> arrayAdapter;
        Map<String, String> ids;

        public ConvitesRecebidos() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ConvitesRecebidos newInstance(int sectionNumber) {
            ConvitesRecebidos fragment = new ConvitesRecebidos();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user_grupos, container, false);
            listView = (ListView) rootView.findViewById(R.id.section_label);
            items = new ArrayList<>();
            ids = new HashMap<>();
            arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!items.get(i).equals("Não há convites..."))
                        popupPedidos(view, items.get(i));
                    return false;
                }
            });
            volleyGetPedidos();

            return rootView;
        }

        private void volleyGetPedidos() {
            String tag_json_obj = "json_request";
            String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/invites?cursor=startquery";

            Log.d("ché bate só", url);

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Prefs", MODE_PRIVATE);
            JSONObject eventos = new JSONObject();
            final String token = sharedPreferences.getString("tokenID", "erro");


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, eventos,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("tao mano", response.toString());
                            try {
                                if (!response.isNull("list") && response.getJSONArray("list").length()>0) {
                                    JSONArray list = response.getJSONArray("list");
                                    for (int i = 0; i < list.length(); i++) {
                                        JSONObject pedido = list.getJSONObject(i);

                                        items.add(pedido.getString("name"));
                                        ids.put(pedido.getString("name"), pedido.getString("groupId"));
                                        arrayAdapter.notifyDataSetChanged();

                                    }
                                } else {
                                    Log.d("olá", "heheehehe");
                                    items.add("Não há convites...");
                                    arrayAdapter.notifyDataSetChanged();

                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener()

            {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erroLOGIN", "Error: " + error.getMessage());
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    return headers;
                }
            };


            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        }


        private void aceitarRequestGrupoVolley(final String pedido) {

            String tag_json_obj = "json_obj_req";
            String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/accept_volunteer?group_id=" + ids.get(pedido);

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Prefs", MODE_PRIVATE);
            final String token = sharedPreferences.getString("tokenID", "erro");


            StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            items.remove(pedido);
                            ids.remove(pedido);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    return headers;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        }


        private void popupPedidos(View view, final String pedido) {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.aceitar_convite) {
                        aceitarRequestGrupoVolley(pedido);
                    }
                    return false;
                }
            });

            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.aceitar_convite_menu, popupMenu.getMenu());
            popupMenu.show();

        }



    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)
                return ConvitesEnviados.newInstance(position);
            else
                return ConvitesRecebidos.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
