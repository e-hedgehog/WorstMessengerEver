package com.yourmother.android.worstmessengerever;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends BaseFragment {

    private EditText mEmailField;
    private Button mResetPasswordButton;

    private FirebaseAuth mAuth;

    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reset_password,
                container, false);

        mEmailField = v.findViewById(R.id.reset_email_field);

        mResetPasswordButton = v.findViewById(R.id.reset_password_button);
        mResetPasswordButton.setOnClickListener(v1 -> {
            String email = mEmailField.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(getActivity(), "Empty email field",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (isOnline(getActivity()))
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) Toast.makeText(getActivity(),
                                    "Password reset link sent to your email",
                                    Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(), "Something is wrong",
                                        Toast.LENGTH_SHORT).show();
                        });
        });

        return v;
    }
}
