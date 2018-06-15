package pt.novaleaf.www.maisverde;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TiposActivity extends AppCompatActivity {

    private LinearLayout imageFogoLeve;
    private LinearLayout imageIncendio;
    private LinearLayout imageLixo;
    private LinearLayout imageLimparMata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipos);
        this.setFinishOnTouchOutside(true);

        imageFogoLeve = (LinearLayout) findViewById(R.id.imageFogoLeve);
        imageIncendio = (LinearLayout) findViewById(R.id.imageIncendio);
        imageLixo = (LinearLayout) findViewById(R.id.imageLixo);
        imageLimparMata = (LinearLayout) findViewById(R.id.imageMataSuja);

        imageFogoLeve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TiposActivity.this, CriarOcorrenciaActivity.class);
                intent.putExtra("type", "bonfire");
                intent.putExtra("activity", "tipos");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        imageIncendio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TiposActivity.this, CriarOcorrenciaActivity.class);
                intent.putExtra("type", "fire");
                intent.putExtra("activity", "tipos");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        imageLixo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TiposActivity.this, CriarOcorrenciaActivity.class);
                intent.putExtra("type", "trash");
                intent.putExtra("activity", "tipos");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        imageLimparMata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TiposActivity.this, CriarOcorrenciaActivity.class);
                intent.putExtra("type", "dirty_woods");
                intent.putExtra("activity", "tipos");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
