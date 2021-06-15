package com.tuber_mobile_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ModuleFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module,container,false);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation_modules);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.bt_menu_my_modules);

        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.bt_menu_my_modules:
                    selectedFragment = new MyModulesFragment();
                    break;

                case R.id.bt_menu_offered_modules:
                    selectedFragment = new OfferedModulesFragment();
                    break;
            }

            getFragmentManager().beginTransaction().replace(R.id.module_fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
