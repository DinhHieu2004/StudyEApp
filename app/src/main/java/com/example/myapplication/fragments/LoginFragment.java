package com.example.myapplication.fragments;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DTO.request.TokenRequest;
import com.example.myapplication.DTO.response.AuthenResponse;
import com.example.myapplication.R;
import com.example.myapplication.activitys.AuthActivity;
import com.example.myapplication.activitys.MainActivity;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginActivity";

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signUpText;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        signUpText = view.findViewById(R.id.signUpText);
        LinearLayout googleSignInBtn = view.findViewById(R.id.googleSignInBtn);

        mAuth = FirebaseAuth.getInstance();

        // Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            handleGoogleSignInResult(data);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Google sign-in cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        loginButton.setOnClickListener(v -> loginUser());

        signUpText.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.signupTab, new SignUpFragment())
                    .addToBackStack(null)
                    .commit();
        });

        googleSignInBtn.setOnClickListener(v -> signInWithGoogle());
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
                                Log.i(TAG, "Firebase idToken: " + idToken);
                                if (idToken != null) {
                                    sendTokenToServer(idToken);
                                } else {
                                    Toast.makeText(getActivity(), "Lỗi lấy token", Toast.LENGTH_SHORT).show();
                                }
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

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleGoogleSignInResult(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(account -> {
                    if (account != null) {
                        String idToken = account.getIdToken();
                        firebaseAuthWithGoogle(idToken);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null) {
            Toast.makeText(getActivity(), "Google ID token is null", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.getIdToken(true).addOnSuccessListener(result -> {
                                String firebaseToken = result.getToken();
                                Log.i(TAG, "Firebase token from Google login: " + firebaseToken);
                                if (firebaseToken != null) {
                                    sendTokenToServer(firebaseToken);
                                } else {
                                    Toast.makeText(getActivity(), "Lỗi lấy token", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Lỗi lấy token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendTokenToServer(String idToken) {
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        TokenRequest tokenRequest = new TokenRequest(idToken);

        Call<AuthenResponse> call = apiService.loginWithFirebaseToken(tokenRequest);

        call.enqueue(new Callback<AuthenResponse>() {
            @Override
            public void onResponse(Call<AuthenResponse> call, Response<AuthenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthenResponse authResponse = response.body();
                    String jwtToken = authResponse.getToken();
                    Log.i(TAG, "JWT token from server: " + jwtToken);

                    saveTokenLocally(jwtToken);

                    Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    String errorMessage = "Lỗi từ server: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += "\n" + response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e(TAG, "Server error: " + errorMessage);
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTokenLocally(String jwtToken) {
        if (!isAdded()) return;

        Context context = getContext();
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jwt_token", jwtToken);
        editor.apply();
    }
}
