package com.yourmother.android.worstmessengerever.screens.messenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yourmother.android.worstmessengerever.screens.messenger.contacts.ContactsListFragment;
import com.yourmother.android.worstmessengerever.screens.messenger.conversations.ConversationsListFragment;
import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.auth.AuthActivity;
import com.yourmother.android.worstmessengerever.screens.base.BaseFragment;

public class MessengerFragment extends BaseFragment {

    private static final String TAG = "MessengerFragment";

    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ViewPager mViewPager;
    private SearchView mSearchView;

    private String mCurrentUserUid;

    public static MessengerFragment newInstance() {
        return new MessengerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
                case R.id.my_profile:
                    startActivity(UserProfileActivity.newIntent(getActivity()));
                default:
                    return true;
            }
        });

        if (mCurrentUserUid != null)
            setupNavigationHeader(mCurrentUserUid);

        mViewPager = view.findViewById(R.id.messenger_viewpager);
        setupViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mSearchView != null && !mSearchView.isIconified()) {
                    mSearchView.setIconified(true);
                    mSearchView.setIconified(true);
                }
                FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
                if (adapter != null) {
                    Fragment fragment = (Fragment) adapter.instantiateItem(mViewPager, i);
                    if (fragment instanceof BaseFragment)
                        ((BaseFragment) fragment).updateUI();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        TabLayout tabs = view.findViewById(R.id.messenger_tabs);
        tabs.setupWithViewPager(mViewPager);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_messenger, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search_item);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setIconifiedByDefault(true);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
                if (adapter != null) {
                    Fragment fragment = (Fragment) adapter
                            .instantiateItem(mViewPager, mViewPager.getCurrentItem());
                    if (fragment instanceof BaseFragment.Searchable)
                        ((Searchable) fragment).search(s);
                }
                return false;
            }
        });
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

                        ImageView imageView = view.findViewById(R.id.nav_header_image);
                        if (currentUser.getImageUrl() != null && !currentUser.getImageUrl().isEmpty()) {
                            Glide.with(getContext().getApplicationContext())
                                    .load(currentUser.getImageUrl())
                                    .centerCrop()
                                    .fitCenter()
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imageView);
                        } else {
                            String firstLetter = currentUser.getUsername()
                                    .substring(0, 1).toUpperCase();
                            TextDrawable drawable = TextDrawable.builder()
                                    .beginConfig().bold().endConfig()
                                    .buildRound(firstLetter, currentUser.getProfileImageColor());
                            imageView.setImageDrawable(drawable);
                        }
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
        adapter.addFragment(ContactsListFragment.newInstance(ContactsListFragment.Mode.AS_TAB), "Contacts");
        viewPager.setAdapter(adapter);
    }


}
