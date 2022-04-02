package com.hapini.pvt.safety;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private Button login;
    private TextView resend;
    private EditText phone;
    private ImageView edit;
    private EditText otp;
    private TextView otp_verify;
    private FirebaseAuth mAuth;
    private Button verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationCode;
    private LinearLayout againSendOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        setIds();
        clickOnLogin();
        clickOnSignUp();
        clickOnVerify();
    }

    private void clickOnVerify() {

        verify.setOnClickListener(view -> {

            if(otp.getText().toString().trim().contentEquals(verificationCode)){

                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode,otp.getText().toString().trim());
                mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            startActivity(new Intent(Login.this,MainActivity.class));
                        }
                    }
                });


            }
            else
                Toast.makeText(getApplicationContext(),"Code didn't match",Toast.LENGTH_LONG).show();

        });
    }

    private void clickOnSignUp() {

        resend.setOnClickListener(view -> {
            startActivity(new Intent(Login.this,SignUp.class));
        });
    }


    private void clickOnLogin() {

        login.setOnClickListener(view -> {

            if(!phone.getText().toString().trim().isEmpty()){

                otpsend();


            }

        });

    }

    private void otpsend() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Toast.makeText(getApplicationContext(),"Verification Successfully",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                System.out.println(e.getMessage()  +"  "+e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                verificationCode = verificationId;
                edit.setVisibility(View.VISIBLE);
                phone.setFocusable(false);
                login.setVisibility(View.GONE);
                verify.setVisibility(View.VISIBLE);
                otp.setVisibility(View.VISIBLE);
                otp_verify.setVisibility(View.VISIBLE);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phone.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void setIds() {

        login = findViewById(R.id.login);
        resend = findViewById(R.id.resend);
        otp = findViewById(R.id.otp);
        phone = findViewById(R.id.phone);
        edit = findViewById(R.id.edit);
        otp_verify = findViewById(R.id.otp_verify);
        mAuth = FirebaseAuth.getInstance();
        verify = findViewById(R.id.verify);
        againSendOTP = findViewById(R.id.againSendOtp);

    }
}