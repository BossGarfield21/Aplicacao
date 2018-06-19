package pt.novaleaf.www.maisverde;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DetalhesGrupoActivity extends AppCompatActivity {

    private ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_grupo);
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

        mList = (ListView) findViewById(R.id.myListDetalhesGrupo);
        List<String> heeee = new ArrayList<>();

        heeee.add("admins");
        heeee.add("users");
        heeee.add("pontos");

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, heeee);


        mList.setAdapter(adapter);

    }
}
