package com.yourmother.android.worstmessengerever.screens.auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yourmother.android.worstmessengerever.screens.messenger.MessengerActivity;
import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.auth.AuthActivity;
import com.yourmother.android.worstmessengerever.screens.base.BaseFragment;

public class RegisterFragment extends BaseFragment {

    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private Button mAlreadyRegisteredButton;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mDatabase.getReference("users");
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        mUsernameEditText = v.findViewById(R.id.reg_username_field);
        mEmailEditText = v.findViewById(R.id.reg_email_field);
        mPasswordEditText = v.findViewById(R.id.reg_password_field);
        mConfirmPasswordEditText = v.findViewById(R.id.reg_confirm_password_field);

        mRegisterButton = v.findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(v1 -> {
            String username = mUsernameEditText.getText().toString().trim();
            String email = mEmailEditText.getText().toString().trim();
            String password1 = mPasswordEditText.getText().toString();
            String password2 = mConfirmPasswordEditText.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                Toast.makeText(getActivity(), "Empty fields detected",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password1.equals(password2)) {
                Toast.makeText(getActivity(), "Password not confirmed",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (password1.length() < 8) {
                Toast.makeText(getActivity(), "Unsafe password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (isOnline(getActivity()))
                mAuth.createUserWithEmailAndPassword(email, password1)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();
                                ColorGenerator generator = ColorGenerator.MATERIAL;
                                User user = new User(username, email, uid,
                                        generator.getRandomColor());
                                mUsersReference.child(uid).setValue(user);

                                startActivity(new Intent(getActivity(), MessengerActivity.class));
                                getActivity().finish();
                            } else
                                Toast.makeText(getActivity(), "Something is wrong",
                                        Toast.LENGTH_SHORT).show();
                        });
        });

        mAlreadyRegisteredButton = v.findViewById(R.id.already_registered_button);
        mAlreadyRegisteredButton.setOnClickListener(v2 -> {
            startActivity(new Intent(getActivity(), AuthActivity.class));
            getActivity().finish();
        });

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getActivity(), MessengerActivity.class));
            getActivity().finish();
        }

        return v;
    }
}
