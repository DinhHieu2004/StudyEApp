package com.example.myapplication.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DAO.HistorySearchWordDao;
import com.example.myapplication.R;
import com.example.myapplication.activitys.DictionaryActivity;
import com.example.myapplication.utils.AppDatabase;

import java.util.List;

public class SearchDictionaryFragment extends Fragment {

    private AutoCompleteTextView etSearch;
    private HistorySearchWordDao historyDao;
    private ImageButton btnSearch;
    private AppCompatImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_dictionary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnBack = view.findViewById(R.id.btnBack);

        if (getActivity() instanceof DictionaryActivity) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> requireActivity().finish());
        } else {
            btnBack.setVisibility(View.GONE);
        }

        btnSearch.setOnClickListener(v -> search());
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
                return true;
            }
            return false;
        });

        etSearch.setOnClickListener(v -> {
            etSearch.showDropDown();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        Context context = getContext();
        Activity activity = getActivity();

        if (context == null || activity == null) return;


        new Thread(() -> {
            HistorySearchWordDao dao = AppDatabase.getDatabase(context).historySearchWordDao();
            List<String> historyWords = dao.getAllWords();

            activity.runOnUiThread(() -> {
                if (!isAdded()) return;

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        context,
                        R.layout.dropdown_item_search_dic_history,
                        R.id.text1,
                        historyWords
                );
                etSearch.setAdapter(adapter);
                etSearch.setThreshold(0);
                etSearch.setDropDownHeight(5 * dpToPx(48));
            });
        }).start();
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void search() {
        String keyword = etSearch.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập từ cần tra", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getActivity() instanceof DictionaryActivity) {
            ((DictionaryActivity) getActivity()).searchWord(keyword);
        } else {
            Intent intent = new Intent(getActivity(), DictionaryActivity.class);
            intent.putExtra("word", keyword);
            startActivity(intent);
        }
    }
    public void reloadHistoryDropdown() {
        new Thread(() -> {
            List<String> updatedWords = historyDao.getAllWords();
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.dropdown_item_search_dic_history,
                        R.id.text1,
                        updatedWords
                );
                etSearch.setAdapter(adapter);
                etSearch.setThreshold(0);
                int maxHeightInDp = 200;
                float scale = getResources().getDisplayMetrics().density;
                int maxHeightInPx = (int) (maxHeightInDp * scale + 0.5f);
                etSearch.setDropDownHeight(maxHeightInPx);
            });
        }).start();
    }


}

