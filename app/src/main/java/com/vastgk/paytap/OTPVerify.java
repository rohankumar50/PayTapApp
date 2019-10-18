package com.vastgk.paytap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerify extends AppCompatActivity {

    private static final String TAG = "OTPVERIFYDEBUG";
    Button btn_otpverify;
    EditText otp1,otp2,otp3,otp4,otp5,otp6;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    public static final String PREFERENCE_FILE_KEY = "PayTapPreference";
    public static final String IS_LOGGED_IN="isloggedin";
    public static final String USERID="userid";
    private String mMobileNumber;
    private TextView resendOtpbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        btn_otpverify = findViewById(R.id.btn_act_otpverfy);
        otp1=findViewById(R.id.OTP1);
        otp2=findViewById(R.id.OTP2);
        otp3=findViewById(R.id.OTP3);
        otp4=findViewById(R.id.OTP4);
        otp5=findViewById(R.id.OTP5);
        otp6=findViewById(R.id.OTP6);
        resendOtpbtn=findViewById(R.id.OTPVERIFY_resend_otp);
        mMobileNumber=getIntent().getStringExtra("mobilenumber");
        editTextSwitcher();
        editTextBackSpaceListner();
    btn_otpverify.setEnabled(false);
        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                String code = credential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {
                    char [] codes=code.toCharArray();
                    for (char s : codes)
                        Log.d(TAG, "onVerificationCompleted: "+s);
                    otp1.setText(String.valueOf(codes[0]));
                    otp2.setText(String.valueOf(codes[1]));
                    otp3.setText(String.valueOf(codes[2]));
                    otp4.setText(String.valueOf(codes[3]));
                    otp5.setText(String.valueOf(codes[4]));
                    otp6.setText(String.valueOf(codes[5]));


                    //verifying the code
                    verifyVerificationCode(code);
                }

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(OTPVerify.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(OTPVerify.this, "Sms Quota Over", Toast.LENGTH_SHORT).show();
                    // ...
                }
                else
                {
                    Log.e(TAG, "onVerificationFailed: ",e );
                }
                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(OTPVerify.this, "Verification Code Sent", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+mMobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);

        btn_otpverify.setOnClickListener((v)->{

            String otps="";
            otps+=otp1.getText().toString()+
                    otp2.getText().toString()+
                    otp3.getText().toString()+
                    otp4.getText().toString()+
            otp5.getText().toString()+
            otp6.getText().toString();
            verifyVerificationCode(otps);




        });
               // OnVerificationStateChangedCallbacks
        resendOtpbtn.setOnClickListener(v->{
            Toast.makeText(this, "Otp Resend To"+mMobileNumber, Toast.LENGTH_SHORT).show();
            resendVerificationCode("+91"+mMobileNumber,mResendToken,mCallbacks);

        });
    }

    private void verifyVerificationCode(String code) {
        Toast.makeText(this, "Verifying "+code, Toast.LENGTH_SHORT).show();
        //creating the credential
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            //signing the user
            signInWithPhoneAuthCredential(credential);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "verifyVerificationCode: "+e.getLocalizedMessage() );
        }

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
     FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(OTPVerify.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //verification successful we will start the profile activity
                            saveUserState();

                            Intent intent = new Intent(OTPVerify.this, Dashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            finish();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "e code entered...";
                                Toast.makeText(OTPVerify.this, message, Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                });
    }

    private void saveUserState() {

            SharedPreferences sharedPreferences=getSharedPreferences( PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(IS_LOGGED_IN,true);
            editor.putString(USERID,getIntent().getStringExtra("mobilenumber"));
            editor.commit();
        Toast.makeText(this, "Loggin in"
                +sharedPreferences.getString(USERID,"null@OTP"), Toast.LENGTH_SHORT).show();

    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    private void editTextBackSpaceListner() {
       otp2.setOnKeyListener((view,keycode,event)->{
           if(keycode== KeyEvent.KEYCODE_DEL) {
               //this is for backspace
               otp2.setText("");

               otp1.requestFocus();
               return true;
           }
           return false;
       });

        otp3.setOnKeyListener((view,keycode,event)->{
            if(keycode== KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                otp3.setText("");

                otp2.requestFocus();
                return true;
            }
            return false;
        });
        otp4.setOnKeyListener((view,keycode,event)->{
            if(keycode== KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                otp4.setText("");
                otp3.requestFocus();
                return true;
            }
            return false;
        });

        otp5.setOnKeyListener((view,keycode,event)->{
            if(keycode== KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                otp5.setText("");
                otp4.requestFocus();
                return true;
            }
            return false;
        });
        otp6.setOnKeyListener((view,keycode,event)->{
            if(keycode== KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                otp6.setText("");
                otp5.requestFocus();
                return true;
            }
            return false;
        });


    }

    private void editTextSwitcher() {

        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp1.getText().toString().length()==1)     //size as per your requirement
                {btn_otpverify.setEnabled(false);
                    otp2.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(otp2.getText().toString().length()==1)     //size as per your requirement
                {
                    otp3.requestFocus();
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp1.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp3.getText().toString().length()==1)     //size as per your requirement
                {
                    otp4.requestFocus();
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp4.getText().toString().length()==1)     //size as per your requirement
                {
                 otp5.requestFocus();
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp4.getText().toString().length()==1)     //size as per your requirement
                {
                    otp6.requestFocus();
                    btn_otpverify.setEnabled(false);
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp6.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(otp6.getText().toString().length()==1)     //size as per your requirement
                {btn_otpverify.setEnabled(true);
                    btn_otpverify.performClick();
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }
}
