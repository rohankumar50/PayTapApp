package com.vastgk.paytap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

public class NfcRead extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    private ImageView centerLogo;
    private static final String TAG = "NFCDEBUG";
    private static final String TAGDATA = "tagDataRead";
    private NfcAdapter nfcAdapter;
    private IntentFilter[] writeTagFilters;
    TextView paymentStatusTV;
    private boolean writeMode;
    PendingIntent pendingIntent;
    Tag myTag;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_read);
        paymentStatusTV = findViewById(R.id.nfcRead_paymentStatusTV);
        centerLogo=findViewById(R.id.nfcread_centerImage);
        username = getSharedPreferences(OTPVerify.PREFERENCE_FILE_KEY, MODE_PRIVATE).getString(OTPVerify.USERID, "null@nfcRead");

        startNfcAnimation(true);
        nfcReadnWrite();
    }

    private void nfcReadnWrite() {


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            paymentStatusTV.setText("");
            // Stop here, we definitely need NFC
            Snackbar snackbar = Snackbar.make(findViewById(R.id.NFC_rootLayout), "This Device do not Support  NFC ", Snackbar.LENGTH_INDEFINITE)
                    .setAction("close", (v) -> {
                        finish();
                    });
            snackbar.show();
            return;
        } else if (!nfcAdapter.isEnabled()) {
            paymentStatusTV.setText("");
            Snackbar snackbar = Snackbar.make(findViewById(R.id.NFC_rootLayout), "Please Enable NFC", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Open Settings", (v) -> {
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    });
            snackbar.show();
            return;
        }
        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
    }


    /******************************************************************************
     **********************************Read From NFC Tag***************************
     ******************************************************************************/
    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, new Intent().putExtra(TAGDATA, text.toString()));
            startNfcAnimation(false);
            sendTransactiontoServer(text);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        Log.d(TAG, "buildTagViews: " + text);
    }

    private void sendTransactiontoServer(String text) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            paymentStatusTV.setText("Processing Payment...");
            JSONObject jo = new JSONObject(text);
            String url = String.format("http://api.nixbymedia.com/paytap/transactions_add.php?username=%s&amount=%s&type=%s&vendorid=%s&itemid=%s",
                    username, jo.getString("amount"), "debit", jo.getString("vendorid"), "Payment @ Mercent"

            );
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String res = jsonObject.getString("response");
                    if (res.contains("Insufficient Balance.")) {
                        //fails to perform transaction
                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                paymentStatusTV.setText("Payment Failed \nRedirecting in " + millisUntilFinished / 1000 + " seconds");
                            }

                            @Override
                            public void onFinish() {
                                closeActivity(this);

                            }
                        }.start();


                        Toast.makeText(this, "Payment Fails \nLow Money in wallet", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                       centerLogo.setImageResource(R.drawable.payment_done);
                        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.completed);
                        mediaPlayer.start();
                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                try {
                                    paymentStatusTV.setText("Payment Successful of \u20b9"+jo.getString("amount")+" \nRedirecting in " + millisUntilFinished / 1000 + " seconds");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFinish() {
                                closeActivity(this);

                            }
                        }.start();
                    }


                } catch (JSONException e) {
                    Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }, error -> {
                Toast.makeText(this, "Error Connecting to Server", Toast.LENGTH_SHORT).show();

            });


            requestQueue.add(stringRequest);


        } catch (JSONException e) {
            Log.d(TAG, "sendTransactiontoServer: " + e.getLocalizedMessage());
            Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void closeActivity(CountDownTimer countDownTimer) {
        countDownTimer.cancel();
        finish();
    }


    /******************************************************************************
     **********************************Write to NFC Tag****************************
     ******************************************************************************/
    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //  WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        // WriteModeOn();
    }


    /******************************************************************************
     **********************************Enable Write********************************
     ******************************************************************************/
    private void WriteModeOn() {
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff() {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }


    private void startNfcAnimation(boolean state) {
        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.nfc_ripple_content);
        if (state)
            rippleBackground.startRippleAnimation();
        else rippleBackground.stopRippleAnimation();
    }
}
