package com.example.aegis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText RAName,RAEmail,RAPassword,RACountryCode,RAPhone,RAotp;
    TextView phew,clickHere;
    Button RASendOTP,RABackToLogin;
    //Button RAVerifyOTP;
    Button RAVerifyEmail;
    String phonenumber;
    Spinner RAProfession;
    String verificationID;
    PhoneAuthProvider.ForceResendingToken Token;
    private FirebaseAuth firebaseAuth;
    boolean verificationInProgress =false;
    private FirebaseDatabase firebaseDatabase;
    String userOTP;
    String Role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        RAName=(EditText)findViewById(R.id.etRAName);
        RAEmail=(EditText) findViewById(R.id.etRAEmail);
        RAPassword=(EditText)findViewById(R.id.etRAPassword);
        RAProfession=(Spinner)findViewById(R.id.spnRAProfession);
        RAVerifyEmail=(Button)findViewById(R.id.btnVerifyEmail);
        RACountryCode=(EditText)findViewById(R.id.etRACountryCode);
        RAPhone=(EditText)findViewById(R.id.etRAPhone);
        RASendOTP=(Button)findViewById(R.id.btnRASendOTP);
        RABackToLogin=(Button)findViewById(R.id.btnGoBackToLogin);
        RAotp=(EditText)findViewById(R.id.etRAotp);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        RAProfession.setOnItemSelectedListener(this);
        phew=findViewById(R.id.tvPhew);
        clickHere=findViewById(R.id.tvClickHere);
        String Name=RAName.getText().toString();
        String Email=RAEmail.getText().toString();
        String Password=RAPassword.getText().toString();


        if(Name.isEmpty()){
            RAName.setError("Name is Required");
            RAName.requestFocus();
        }
        if(Email.isEmpty()){
            RAEmail.setError("Email is Required");
            RAEmail.requestFocus();
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            RAEmail.setError("Please enter a valid email");
            RAEmail.requestFocus();
        }
        if(Password.isEmpty()){
            RAPassword.setError("Password is Required");
            RAPassword.requestFocus();
        }
        if(Password.length()<6){
            RAPassword.setError("Minimum 6 characters required");
            RAPassword.requestFocus();
        }


        RASendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verificationInProgress){
                    //validate phone number
                    ValidatePhone();
                    requestOTP(phonenumber);
                }
                else{
                    userOTP=RAotp.getText().toString();
                    if(!userOTP.isEmpty()){
                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,userOTP);
                        verifyAuth(credential);
                    }
                    else{
                        RAotp.setError("Valid OTP is required.");
                    }

                }


            }
        });

        RAVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=RAName.getText().toString();
                String mail=RAEmail.getText().toString();
                String pw=RAPassword.getText().toString();
                if(username.isEmpty()){
                    RAName.setError("Name Requried");
                    RAName.requestFocus();
                    return;
                }
                if(mail.isEmpty()){
                    RAEmail.setError("Email Requried");
                    RAEmail.requestFocus();
                    return;
                }
                if(username.isEmpty()){
                    RAPassword.setError("Password Requried");
                    RAPassword.requestFocus();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(mail,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user=new User(username,mail,Role,phonenumber);
                            firebaseDatabase.getReference("users")
                                    .child(firebaseAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this,"User Registered Successfully",Toast.LENGTH_SHORT).show();
                                        RAVerifyEmail.setVisibility(View.GONE);
                                        phew.setVisibility(View.VISIBLE);
                                        clickHere.setVisibility(View.VISIBLE);
                                        RABackToLogin.setVisibility(View.VISIBLE);
                                        //sign in the user and go to dashboard directly
                                    }
                                    else{
                                        Toast.makeText(RegistrationActivity.this,"Failed to register! Try Again",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegistrationActivity.this,"Failed to register",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        FirebaseUser user=firebaseAuth.getCurrentUser();
        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification();
                Toast.makeText(RegistrationActivity.this,"A verification link has been sent to your registered email",Toast.LENGTH_LONG).show();
            }
        });

        RABackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }

    private void verifyAuth(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this,"OTP Verified",Toast.LENGTH_LONG).show();
                    RAotp.setVisibility(View.GONE);
                    RASendOTP.setVisibility(View.GONE);
                    RAVerifyEmail.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(RegistrationActivity.this,"Incorrect OTP",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void requestOTP(String phonenumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 120L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //whenever a code is sent this method will be called
                RAotp.setVisibility(View.VISIBLE);
                //RAVerifyOTP.setVisibility(View.VISIBLE);
                verificationID=s;
                Token=forceResendingToken;
                RASendOTP.setText("Verify");
                verificationInProgress=true;

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                Toast.makeText(RegistrationActivity.this,"Didn't detect code",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String Code=phoneAuthCredential.getSmsCode();
                if(Code!=null){
                    RAotp.setText(Code);
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,Code);
                    verifyAuth(credential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(RegistrationActivity.this,"Server is not able to send otp: "+ e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ValidatePhone(){
        String CountryCode=RACountryCode.getText().toString();
        String Phone=RAPhone.getText().toString();
        phonenumber= "+"+CountryCode+Phone;
        if(CountryCode.isEmpty()){
            RACountryCode.setError("Country Code Required");
            RACountryCode.requestFocus();
            return;
        }
        if(Phone.isEmpty()){
            RAPhone.setError("Phone Number Required");
            RAPhone.requestFocus();
            return;
        }
        if(Phone.length()>10){
            RAPhone.setError("Enter 10 digit Phone number");
            RAPhone.requestFocus();
            return;
        }
        else{
            Toast.makeText(RegistrationActivity.this,"Sending OTP...",Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Role=parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}