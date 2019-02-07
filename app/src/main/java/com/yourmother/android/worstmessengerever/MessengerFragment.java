package com.yourmother.android.worstmessengerever;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessengerFragment extends BaseFragment {

    private static final String TAG = "MessengerFragment";

    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private String mCurrentUserUid;

    public static MessengerFragment newInstance() {
        return new MessengerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Creating...");

        isOnline(getActivity());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            mCurrentUserUid = currentUser.getUid();

        mUsersReference = FirebaseDatabase.getInstance().getReference("users");

        mAuthStateListener = firebaseAuth -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(AuthActivity.newIntent(getActivity()));
                Log.i(TAG, "AuthState changed, activity finishing");
                getActivity().finish();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger, container, false);

        Log.i(TAG, view == null ? "View is null" : "View not null");

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.messenger_toolbar);
        activity.setSupportActionBar(toolbar);

        mDrawerLayout = view.findViewById(R.id.messenger_drawer_container);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                activity, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mNavigationView = view.findViewById(R.id.messenger_navigation_view);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.sign_out:
                    mAuth.signOut();
                    Log.i(TAG, "Signed out");
                    return true;
                default:
                    return true;
            }
        });

        if (mCurrentUserUid != null)
            setupNavigationHeader(mCurrentUserUid);

        ViewPager viewPager = view.findViewById(R.id.messenger_viewpager);
        setupViewPager(viewPager);

        TabLayout tabs = view.findViewById(R.id.messenger_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    private void setupNavigationHeader(String currentUserUid) {
        mUsersReference.child(currentUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        View view = mNavigationView.getHeaderView(0);
                        TextView emailTextView = view.findViewById(R.id.nav_header_email);
                        emailTextView.setText(currentUser.getEmail());
                        TextView usernameTextView = view.findViewById(R.id.nav_header_username);
                        usernameTextView.setText(currentUser.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, "Single Value Event cancelled");
                    }
                });
    }

    private void setupViewPager(ViewPager viewPager) {
        MessengerPagerAdapter adapter = new MessengerPagerAdapter(getChildFragmentManager());
        adapter.addFragment(ConversationsListFragment.newInstance(), "Conversations");
        adapter.addFragment(ContactsListFragment.newInstance(), "Contacts");
        viewPager.setAdapter(adapter);
    }


}
