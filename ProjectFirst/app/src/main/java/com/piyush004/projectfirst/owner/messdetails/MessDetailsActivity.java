package com.piyush004.projectfirst.owner.messdetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.piyush004.projectfirst.Dashboard.OwnerDashboard;
import com.piyush004.projectfirst.LoginKey;
import com.piyush004.projectfirst.R;

public class MessDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton backButton;
    private EditText editTextName, editTextAddress, editTextMobile, editTextEmail, editTextCity, editTextClosedDays;
    private TextView textViewHeader, textViewName, textViewAddress, textViewMobile, textViewEmail, textViewCity, textViewClosedDays;
    private Button buttonSave;
    private static int SELECT_PHOTO = 1;
    private ImageView imageView;
    private Uri uri;
    private DatabaseReference databaseReference;
    private String mess_name, mess_address, mess_mobile, mess_city, mess_email, mess_closed_days;
    private String login_name;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_details);

        toolbar = findViewById(R.id.toolbarMessDetails);
        backButton = findViewById(R.id.messdetailsBackBtn);
        textViewHeader = findViewById(R.id.messDetailsHeader);
        imageView = findViewById(R.id.messPhoto);

        textViewName = findViewById(R.id.text_view_mess_name);
        textViewAddress = findViewById(R.id.text_view_address);
        textViewMobile = findViewById(R.id.text_view_mobile);
        textViewEmail = findViewById(R.id.text_view_email);
        textViewCity = findViewById(R.id.text_view_city);
        textViewClosedDays = findViewById(R.id.text_view_closed_days);

        editTextName = findViewById(R.id.edit_text_name);
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextMobile = findViewById(R.id.edit_text_mobile);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextCity = findViewById(R.id.edit_text_city);
        editTextClosedDays = findViewById(R.id.edit_text_closed_days);

        buttonSave = findViewById(R.id.button_save);

        login_name = LoginKey.loginKey;

    }

    public void onClickMessDetailsBackBtn(View view) {
        Intent intent = new Intent(MessDetailsActivity.this, OwnerDashboard.class);
        startActivity(intent);
    }

    public void onClickImageViewMessDetails(View view) {
        Toast.makeText(this, "Select Image", Toast.LENGTH_LONG).show();
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PHOTO) {
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }

    public void onClickSaveEvent(View view) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Mess").child(login_name);
        mess_name = editTextName.getText().toString();
        mess_address = editTextAddress.getText().toString();
        mess_mobile = editTextMobile.getText().toString();
        mess_email = editTextEmail.getText().toString();
        mess_city = editTextCity.getText().toString();
        mess_closed_days = editTextClosedDays.getText().toString();

        if (mess_name.isEmpty()) {
            editTextName.setError("Please Enter Name");
            editTextName.requestFocus();
        } else if (mess_address.isEmpty()) {
            editTextAddress.setError("Please Enter Address");
            editTextAddress.requestFocus();
        } else if (mess_mobile.isEmpty()) {
            editTextMobile.setError("Please Enter Mobile");
            editTextMobile.requestFocus();
        } else if (mess_email.isEmpty()) {
            editTextEmail.setError("Please Re-Enter Email-ID");
            editTextEmail.requestFocus();
        } else if (mess_city.isEmpty()) {
            editTextCity.setError("Please Enter City");
            editTextCity.requestFocus();
        } else if (mess_closed_days.isEmpty()) {
            editTextClosedDays.setError("Please Enter Days");
            editTextClosedDays.requestFocus();
        } else if (!(mess_name.isEmpty() && mess_address.isEmpty() && mess_mobile.isEmpty() && mess_email.isEmpty() && mess_city.isEmpty() && mess_closed_days.isEmpty())) {

            MessDetailsModel messDetailsModel = new MessDetailsModel(mess_name, mess_address, mess_mobile, mess_city, mess_email, mess_closed_days);

            databaseReference.child("MessName").setValue(messDetailsModel.getMess_name());
            databaseReference.child("MessAddress").setValue(messDetailsModel.getMess_address());
            databaseReference.child("MessMobile").setValue(messDetailsModel.getMess_mobile());
            databaseReference.child("MessEmail").setValue(messDetailsModel.getMess_email());
            databaseReference.child("MessCity").setValue(messDetailsModel.getMess_city());
            databaseReference.child("MessClosedDays").setValue(messDetailsModel.getMess_closed_days());
            uploadImage();
            Toast.makeText(this, "Data Added", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MessDetailsActivity.this, OwnerDashboard.class);
            startActivity(intent);

        }

    }

    private void uploadImage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/").child(login_name);
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imguri = uri.toString();
                                    DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Mess").child(login_name);
                                    df.child("ImageURl").setValue(imguri);

                                }
                            });

                            Toast.makeText(MessDetailsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MessDetailsActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
}