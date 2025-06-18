package com.example.myapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.DTO.response.UserResponse;
import com.example.myapplication.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.myapplication.activitys.AuthActivity;
import com.example.myapplication.activitys.SettingsActivity;
import com.example.myapplication.activitys.SubscriptionActivity;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView userName;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userName = view.findViewById(R.id.userName);
        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        ImageView btnSettings = view.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            apiService.getUserProfile(uid).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        userName.setText(response.body().getName());
                    } else {
                        userName.setText("Không tải được tên");
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    userName.setText("Lỗi kết nối");
                }
            });
        }

        CardView logoutCard = view.findViewById(R.id.logoutCard);
        logoutCard.setOnClickListener(v -> logout());

        CardView subscriptionCard = view.findViewById(R.id.subscriptionCard);
        subscriptionCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SubscriptionActivity.class);
            startActivity(intent);
        });

    }

    private void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        mAuth.signOut();
        mGoogleSignInClient.signOut();

        ApiClient.resetClient();

        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
