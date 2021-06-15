package com.tuber_mobile_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SessionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sessions, container, false);
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        bottomNav.setSelectedItemId(R.id.bt_menu_booked_session);

        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            LiveSessionFragment sessions = new LiveSessionFragment();
            sessions.setArguments(bundle);
            bottomNav.setSelectedItemId(R.id.bt_menu_live_session);
            getFragmentManager().beginTransaction().replace(R.id.session_fragment_container, sessions).commit();
        }

        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.bt_menu_booked_session:
                    selectedFragment = new BookedSessionFragment();
                    break;

                case R.id.bt_menu_live_session:
                    selectedFragment = new LiveSessionFragment();
                    break;
            }

            getFragmentManager().beginTransaction().replace(R.id.session_fragment_container, selectedFragment).commit();
            return true;
        }
    };

}
