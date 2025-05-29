package com.example.myapplication.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DTO.request.TokenRequest;
import com.example.myapplication.DTO.response.UserResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Fragment {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signUpText;
    private FirebaseAuth mAuth;

    public LoginActivity() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.activity_login, container, false);

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        signUpText = view.findViewById(R.id.signUpText);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> loginUser());

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getActivity(), "Mật khẩu phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.getIdToken(true).addOnSuccessListener(result -> {
                                String idToken = result.getToken();
                                sendTokenToServer(idToken);

                            }).addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Lỗi lấy token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getActivity(), "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = "Đăng nhập thất bại";
                        if (task.getException() != null && task.getException().getMessage() != null) {
                            errorMsg = task.getException().getMessage();
                        }
                        Toast.makeText(getActivity(), "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendTokenToServer(String idToken) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        TokenRequest tokenRequest = new TokenRequest(idToken);
        Call<UserResponse> call = apiService.loginWithFirebaseToken(tokenRequest);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    saveUserLocally(user);

                    Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
//                    if (response.code() == 401) {
//                        Toast.makeText(getActivity(), "Xác thực thất bại: Token không hợp lệ hoặc không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
//                    } else {
                        String errorMessage = "Lỗi từ server: " + response.code();
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += "\n" + response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
//            }

            @Override
            public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveUserLocally(UserResponse user) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("uid", user.getUid());
        editor.putString("email", user.getEmail());
        editor.putString("name", user.getName());
        editor.apply();
        logout();
    }
    public void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }



}
