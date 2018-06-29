package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriarGrupoActivity extends AppCompatActivity implements Serializable {

    private Button mButtonCriarGrupo;
    private Button mButtonDistrito;
    private AutoCompleteTextView mNomeGrupo;
    private Switch mPrivacidade;
    private PopupMenu popup = null;
    private String distrito = "";
    private boolean isImage = false;
    LinearLayout linearLayout;
    byte[] imageBytes;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int PICK_IMAGE = 2;
    private ImageButton imageButton;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_grupo);
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

        mButtonCriarGrupo = (Button) findViewById(R.id.btn_criar_grupo);
        mButtonDistrito = (Button) findViewById(R.id.bDistritos);
        mNomeGrupo = (AutoCompleteTextView) findViewById(R.id.input_group);
        mPrivacidade = (Switch) findViewById(R.id.switchGoups);
        linearLayout = (LinearLayout) findViewById(R.id.linearCriarGrupo);
        imageView = (ImageView) findViewById(R.id.imageView3);
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        mButtonCriarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateGroup();
            }
        });

        mButtonDistrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMenu(findViewById(R.id.bDistritos));
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                choosePicture();
            }
        });

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

                    item.setCheckable(true);
                    item.setChecked(!item.isChecked());
                    setUncheckedMenu(popup, item);
                    mButtonDistrito.setText(item.getTitle());
                    mButtonDistrito.setError(null);
                    distrito = item.getTitle().toString().toLowerCase();

                    return false;
                }
            });// to implement on click event on items of menu
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.distritos_menu, popup.getMenu());
            popup.getMenu().getItem(0).setVisible(false);
        }
        popup.show();
    }


    private void takePic() {

        if (!isImage) {
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(intent, 0);

            dispatchTakePictureIntent();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CriarGrupoActivity.this);
            builder.setTitle("Outra imagem")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            if (isImage) {
                                Snackbar snackbar = Snackbar
                                        .make(linearLayout, "Fotografia escolhida", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Escolher outra fotografia", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                choosePicture();
                                                //dispatchTakePictureIntent();
                                            }
                                        });

                                snackbar.show();
                            }
                        }
                    })
                    .setCancelable(true)
                    .setMessage("Tem a certeza de que quer tirar outra foto?\n" +
                            "A foto tirada anteriormente será perdida")
                    .setPositiveButton("Tirar outra", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            imageButton.setVisibility(View.GONE);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 0);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    private void chooseGalery() {

        if (!isImage) {
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(intent, 0);

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CriarGrupoActivity.this);
            builder.setTitle("Outra imagem")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            if (isImage) {
                                Snackbar snackbar = Snackbar
                                        .make(linearLayout, "Fotografia escolhida", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Escolher outra fotografia", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                choosePicture();
                                                //dispatchTakePictureIntent();
                                            }
                                        });

                                snackbar.show();
                            }
                        }
                    })
                    .setCancelable(true)
                    .setMessage("Tem a certeza de que quer escolher outra foto?\n" +
                            "A foto tirada anteriormente será perdida")
                    .setPositiveButton("Escolher outra", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("che", ex.getMessage());
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                imageButton.setVisibility(View.GONE);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(mCurrentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

                Bitmap rotatedBitmap = null;
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }


                imageView.setImageBitmap(rotatedBitmap);
                isImage = true;
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                imageBytes = bao.toByteArray();

                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Fotografia captada", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Tirar outra fotografia", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                choosePicture();
                                //dispatchTakePictureIntent();
                            }
                        });

                snackbar.show();

            } else {
                if (!isImage)
                    imageButton.setVisibility(View.VISIBLE);
                else {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "Fotografia captada", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Tirar outra fotografia", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    choosePicture();
                                    //dispatchTakePictureIntent();
                                }
                            });

                    snackbar.show();
                }

            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);

                    isImage = true;
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                    imageBytes = bao.toByteArray();

                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "Fotografia escolhida", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Escolher outra fotografia", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    choosePicture();
                                    //dispatchTakePictureIntent();
                                }
                            });

                    snackbar.show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (!isImage)
                    imageButton.setVisibility(View.VISIBLE);
                else {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "Fotografia escolhida", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Escolher outra fotografia", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    choosePicture();
                                    //dispatchTakePictureIntent();
                                }
                            });

                    snackbar.show();
                }

            }
        } else {
            Snackbar snackbar = Snackbar
                    .make(linearLayout, "Fotografia escolhida", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Escolher outra fotografia", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            choosePicture();
                            //dispatchTakePictureIntent();
                        }
                    });

            snackbar.show();
        }
    }

    private void choosePicture() {


        AlertDialog.Builder builder = new AlertDialog.Builder(CriarGrupoActivity.this);
        builder.setTitle("Escolher foto")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (isImage) {
                            Snackbar snackbar = Snackbar
                                    .make(linearLayout, "Fotografia escolhida", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Escolher outra fotografia", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            choosePicture();
                                            //dispatchTakePictureIntent();
                                        }
                                    });

                            snackbar.show();
                        }
                    }
                })
                .setCancelable(true)
                .setMessage("Tirar foto, ou escolher da galeria?")
                .setPositiveButton("Tirar foto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        takePic();
                    }
                })
                .setNegativeButton("Escolher da galeria", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chooseGalery();
                    }
                });
        builder.create().show();
    }


    private void attemptCreateGroup() {

        String nomeGrupo = mNomeGrupo.getText().toString();

        boolean isPrivado = mPrivacidade.isChecked();

        mNomeGrupo.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nomeGrupo)) {
            mNomeGrupo.setError("O nome tem de ter pelo menos 4 caracteres");
            focusView = mNomeGrupo;
            cancel = true;
        } else if (TextUtils.isEmpty(distrito)) {
            mButtonDistrito.setError("Escolha um distrito");
            mButtonDistrito.setText("Escolha um distrito");
            focusView = mButtonDistrito;
            cancel = true;
        }

        String privacy;
        if (isPrivado)
            privacy = "private";
        else
            privacy = "public";


        if (cancel) {
            // There was an error; focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String id = UUID.randomUUID().toString();

            //enviarImagemVolley(imageBytes, id);
            criarGrupoVolley(nomeGrupo, privacy);
        }
    }

    private void criarGrupoVolley(String nomeGrupo, String privacy) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/create?group=" + nomeGrupo + "&privacy=" +
                privacy + "&district=" + distrito;

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            grupo.put("name", nomeGrupo);
            grupo.put("privacy", privacy);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, grupo,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent i = new Intent(CriarGrupoActivity.this, GruposListActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erroNOVAOCORRENCIA", "Error: " + error.getMessage());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
