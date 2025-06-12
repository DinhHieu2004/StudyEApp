package com.example.myapplication.activitys;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DTO.request.UserRequest;
import com.example.myapplication.DTO.response.UserResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editName, editDob, editPassword, editPhone;
    private Button btnUpdate;
    private ImageView backIcon;
    private ApiService apiService;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        editEmail = findViewById(R.id.editEmail);
        editName = findViewById(R.id.editName);
        editDob = findViewById(R.id.editDob);
        editPhone = findViewById(R.id.editPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        backIcon = findViewById(R.id.backIcon);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        apiService = ApiClient.getClient().create(ApiService.class);

        editDob.setOnClickListener(v -> showDatePicker());
        backIcon.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateProfile());

        if (firebaseUser != null) {
            editEmail.setText(firebaseUser.getEmail());
            loadUserProfile(firebaseUser.getUid());
        } else {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (isGoogleAccount()) {
            editEmail.setEnabled(false);
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        String dobText = editDob.getText().toString().trim();
        if (!dobText.isEmpty()) {
            String[] parts = dobText.split("/");
            if (parts.length == 3) {
                try {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int year = Integer.parseInt(parts[2]);
                    calendar.set(year, month, day);
                } catch (Exception e) {
                    Log.w("DatePicker", "Ngày sinh không hợp lệ, dùng ngày hiện tại");
                }
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dob = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    editDob.setText(dob);
                },
                year, month, day
        );
        dialog.show();
    }

    private void loadUserProfile(String uid) {
        apiService.getUserProfile(uid).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse profile = response.body();
                    editName.setText(profile.getName());
                    editDob.setText(profile.getDob());
                    editPhone.setText(profile.getPhone());
                    editEmail.setText(profile.getEmail());
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Không tải được thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(UpdateProfileActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                Log.e("UpdateProfile", "Error: ", t);
            }
        });
    }

    private void updateProfile() {
        String uid = firebaseUser.getUid();
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String name  = editName.getText() != null ? editName.getText().toString().trim() : "";
        String dob   = editDob.getText() != null ? editDob.getText().toString().trim() : "";
        String phone = editPhone.getText() != null ? editPhone.getText().toString().trim() : "";

        if (email.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Email và tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isGoogleAccount() && !email.equals(firebaseUser.getEmail())) {
            firebaseUser.updateEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateProfileToServer(new UserResponse(uid, email, name, dob, phone));
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật email Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            String emailU = firebaseUser.getEmail();
            updateProfileToServer(new UserResponse(uid, emailU, name, dob, phone));
        }
    }

    private void updateProfileToServer(UserResponse updated) {
        apiService.updateUserProfile(updated).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Lỗi: " + response.code();
                    Toast.makeText(UpdateProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UpdateProfileActivity.this, "Không thể kết nối server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private boolean isGoogleAccount() {
        for (com.google.firebase.auth.UserInfo userInfo : firebaseUser.getProviderData()) {
            if ("google.com".equals(userInfo.getProviderId())) {
                return true;
            }
        }
        return false;
    }
}
