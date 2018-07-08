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
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.ByteRequest;

public class AddPostActivity extends AppCompatActivity {

    private Button mCriarPost;
    private TextInputEditText mEditText;
    private ImageButton imageButton;
    private ImageView imageView;
    private boolean isImage = false;
    byte[] imageBytes;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int PICK_IMAGE = 2;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
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

        mCriarPost = (Button) findViewById(R.id.buttonCriarPost);
        mEditText = (TextInputEditText) findViewById(R.id.editPost);
        imageView = (ImageView) findViewById(R.id.imageView3);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        linearLayout = (LinearLayout) findViewById(R.id.linearAddPost);


        mCriarPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mEditText.getText().toString();
                attemptPost(message);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                choosePicture();
            }
        });

    }

    private void takePic() {

        if (!isImage) {
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(intent, 0);

            dispatchTakePictureIntent();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
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
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
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
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
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


        AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
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


    private void attemptPost(String message) {

        mEditText.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(message)) {
            mEditText.setError("Publicação vazia");
            focusView = mEditText;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            createPostVolley(message);
        }

    }

    private void createPostVolley(String message) {


        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/publish?group_id=" + GrupoFeedActivity.novoGrupo.getGroupId();

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        try {
            grupo.put("message", message);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, grupo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.d("Novo post", response.toString());

                            String id = null;
                            String author = null;
                            String message = null;
                            String image = null;
                            String user_image = null;
                            List<String> likers = new ArrayList<>();
                            Map<String, Comentario> comments = new HashMap<>();
                            long likes = 0;
                            boolean liked = false;

                            JSONObject post = response;
                            if (post.has("id"))
                                id = post.getString("id");
                            if (post.has("author"))
                                author = post.getString("author");
                            if (post.has("message"))
                                message = post.getString("message");
                            if (post.has("likes"))
                                likes = post.getLong("likes");
                            if (post.has("liked"))
                                liked = post.getBoolean("liked");

                            if (post.has("likers")) {
                                JSONArray lik = post.getJSONArray("likers");
                                for (int a = 0; a < lik.length(); a++)
                                    likers.add(lik.getString(a));
                            }

                            JSONObject imag = null;
                            if (post.has("image")) {
                                imag = post.getJSONObject("image");
                                if (imag.has("value"))
                                    image = imag.getString("value");
                            }

                            JSONObject imageuser = null;
                            if (post.has("user_picture")) {
                                imageuser = post.getJSONObject("user_picture");
                                if (imageuser.has("value"))
                                    user_image = imageuser.getString("value");
                            }

                            if (post.has("comments")) {
                                JSONArray coms = post.getJSONArray("comments");


                                for (int a = 0; a < coms.length(); a++) {
                                    int origem;
                                    JSONObject com = coms.getJSONObject(a);
                                    if (com.getString("author").equals(
                                            getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", "")))
                                        origem = 1;
                                    else origem = 2;

                                    String comentImage = null;
                                    JSONObject im = null;
                                    if (post.has("image")) {
                                        im = post.getJSONObject("image");
                                        if (im.has("value"))
                                            comentImage = im.getString("value");
                                    }


                                    String comentID = com.getString("id");


                                    comments.put(comentID, new Comentario(comentID, com.getString("author"),
                                            com.getString("message"), comentImage,
                                            com.getLong("creation_date"), origem, null, id, GrupoFeedActivity.novoGrupo.getGroupId()));

                                }
                            }

//                                    Log.d("POST???", id);


                            Post post1 = new Post(id, author, message, image, likers, comments, likes, liked, GrupoFeedActivity.novoGrupo.getGroupId(), user_image);

                            if (image != null)
                                post1.setBitmap(imageBytes);
                            if (user_image != null)
                                receberImagemUserVolley(post1);
                            else
                                Toast.makeText(AddPostActivity.this, "olé", Toast.LENGTH_SHORT).show();

                            GrupoFeedActivity.posts.add(0, post1);
                            GrupoFeedActivity.adapter.notifyDataSetChanged();


                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
                Toast.makeText(AddPostActivity.this, "Verifique a ligação", Toast.LENGTH_SHORT).show();
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

    private void receberImagemUserVolley(final Post item) {
        String tag_json_obj = "octect_request";
        final String url = item.getUser_image();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                item.setBitmapUser(response);
                GrupoFeedActivity.adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMPost", "Error: " + error.getMessage());


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }


}
