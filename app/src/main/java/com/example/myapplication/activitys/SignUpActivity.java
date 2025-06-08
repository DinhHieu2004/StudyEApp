package com.example.myapplication.activitys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DTO.request.UserRequest;
import com.example.myapplication.R;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends Fragment {

    private EditText emailInput, passwordInput, confirmPasswordInput, fullnameInput;
    private Button signUpButton;
    private FirebaseAuth mAuth;

    public SignUpActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        signUpButton = view.findViewById(R.id.signupButton);
        fullnameInput = view.findViewById(R.id.fullnameInput);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String name = fullnameInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo tài khoản Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileUpdateTask -> {
                                        if (profileUpdateTask.isSuccessful()) {
                                            String uid = user.getUid();
                                            String emailUser = user.getEmail();
                                            String displayName = name;

                                            sendUserToServer(uid, emailUser, displayName);
                                            Toast.makeText(getActivity(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                            emailInput.setText("");
                                            passwordInput.setText("");
                                            confirmPasswordInput.setText("");
                                            fullnameInput.setText("");

                                            requireActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.fragmentContainer, new LoginActivity())
                                                    .addToBackStack(null)
                                                    .commit();
                                        } else {
                                            Toast.makeText(getActivity(), "Lỗi cập nhật tên người dùng", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        String errorMsg = "Đăng ký thất bại";
                        if (task.getException() != null && task.getException().getMessage() != null) {
                            errorMsg = task.getException().getMessage();
                        }
                        Toast.makeText(getActivity(), "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserToServer(String uid, String email, String name) {
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        UserRequest userRequest = new UserRequest(uid, email, name);

        Call<Void> call = apiService.registerUser(userRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi gửi server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
