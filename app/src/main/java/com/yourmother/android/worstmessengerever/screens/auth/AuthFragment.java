package com.yourmother.android.worstmessengerever.screens.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yourmother.android.worstmessengerever.screens.messenger.MessengerActivity;
import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.screens.auth.register.RegisterActivity;
import com.yourmother.android.worstmessengerever.screens.auth.reset_password.ResetPasswordActivity;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.base.BaseFragment;

public class AuthFragment extends BaseFragment
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AuthFragment";
    private static final int RC_SIGN_IN = 100;
    private static final int NEW_INTENT_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mRegisterButton;
    private Button mForgotPasswordButton;
    private SignInButton mSignInButton;

    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUsersReference = FirebaseDatabase.getInstance().getReference("users");

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (mAuth.getCurrentUser() != null) {
            startActivity(MessengerActivity.newIntent(getActivity(), NEW_INTENT_FLAGS));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auth, container, false);

        mEmailField = v.findViewById(R.id.auth_email_field);
        mPasswordField = v.findViewById(R.id.auth_password_field);

        mLoginButton = v.findViewById(R.id.auth_login_button);
        mLoginButton.setOnClickListener(v1 -> {
            String email = mEmailField.getText().toString().trim();
            String password = mPasswordField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Empty fields detected",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (isOnline(getActivity()))
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(MessengerActivity.
                                        newIntent(getActivity(), NEW_INTENT_FLAGS));
                            } else
                                Toast.makeText(getActivity(), "Something is wrong",
                                        Toast.LENGTH_SHORT).show();
                        });
        });

        mRegisterButton = v.findViewById(R.id.auth_register_button);
        mRegisterButton.setOnClickListener(v2 ->
                startActivity(RegisterActivity.newIntent(getActivity(), NEW_INTENT_FLAGS)));

        mForgotPasswordButton = v.findViewById(R.id.forgot_password_button);
        mForgotPasswordButton.setOnClickListener(v3 ->
                startActivity(new Intent(getActivity(), ResetPasswordActivity.class)));

        mSignInButton = v.findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(v4 -> {
            if (isOnline(getActivity())) {
                Intent i = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(i, RC_SIGN_IN);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
                authWithGoogle(result.getSignInAccount());
        }
    }

    private void authWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        mUsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.hasChild(firebaseUser.getUid())) {
                                    ColorGenerator generator = ColorGenerator.MATERIAL;
                                    User user = new User(firebaseUser.getDisplayName(),
                                            firebaseUser.getEmail(), firebaseUser.getUid(),
                                            null, generator.getRandomColor());
                                    mUsersReference.child(firebaseUser.getUid()).setValue(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        startActivity(MessengerActivity.newIntent(getActivity(), NEW_INTENT_FLAGS));
                        Log.i(TAG, "AuthFragment finished");
                    } else
                        Toast.makeText(getActivity(), "Auth error!",
                                Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
    }
}
