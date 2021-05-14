package com.example.merchantapp;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.TextView;
import com.example.merchantapp.emvn.ConsoleWriter;
import com.example.merchantapp.emvn.CountryCodeEnum;
import com.example.merchantapp.emvn.CurrencyEnum;
import com.example.merchantapp.emvn.EmvCard;
import com.example.merchantapp.emvn.EmvCardScheme;
import com.example.merchantapp.emvn.EmvParser;
import com.example.merchantapp.emvn.EmvTransactionRecord;
import com.example.merchantapp.emvn.IRefreshable;
import com.example.merchantapp.emvn.LoggerFactory;
import com.example.merchantapp.emvn.TransactionTypeEnum;
import com.example.merchantapp.nfcmodel.CardUtils;
import com.example.merchantapp.nfcmodel.Provider;
import com.example.merchantapp.nfcreader.NFCCardReader;
import com.example.merchantapp.nfcreader.NFCUtils;
import com.example.merchantapp.nfcutils.AndroidCommonsUtils;
import com.example.merchantapp.nfcutils.AtrUtils;
import com.example.merchantapp.nfcutils.BytesUtils;
import com.example.merchantapp.nfcutils.CroutonUtils;
import com.example.merchantapp.nfcutils.SimpleAsyncTask;
import com.google.android.gms.common.util.IOUtils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class NFCACtivity extends AppCompatActivity {

    /**
     * Nfc utils
     */
    private NFCUtils mNfcUtils;

    /**
     * Waiting Dialog
     */
    private ProgressDialog mDialog;

    /**
     * Alert dialog
     */
    private AlertDialog mAlertDialog;
    /**
     * IsoDep provider
     */
    private Provider mProvider = new Provider();

    /**
     * Emv card
     */
    private EmvCard mReadCard;

    /**
     * Reference for refreshable content
     */
    private WeakReference<IRefreshable> mRefreshableContent;

    /**
     * last selected Menu
     */
    private int mLastSelectedMenu = -1;


    /**
     * Last Ats
     */
    private byte[] lastAts;

    private NFCCardReader nfcCardReader;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcactivity);
        mNfcUtils = new NFCUtils(this);
        if (getIntent().getAction() == NfcAdapter.ACTION_TECH_DISCOVERED) {
            onNewIntent(getIntent());
        }
        LoggerFactory.setLogWriter(new ConsoleWriter());
        nfcCardReader = new NFCCardReader(this);
        textView = (TextView) findViewById(R.id.text);
    }



    @Override
    protected void onResume() {
        mNfcUtils.enableDispatch();
        // Close
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }

        // Check if NFC is available
        if (!NFCUtils.isNfcAvailable(getApplicationContext())) {

            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle(getString(R.string.msg_info));
            alertbox.setMessage(getString(R.string.msg_nfc_not_available));
            alertbox.setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dialog.dismiss();
                }
            });
            alertbox.setCancelable(false);
            mAlertDialog = alertbox.show();
        }

        // Check if NFC is enabled

        if (!NFCUtils.isNfcEnabled(getApplicationContext())) {

            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle(getString(R.string.msg_info));
            alertbox.setMessage(getString(R.string.msg_nfc_not_enabled));
            alertbox.setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    Intent intent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    } else {
                        intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    }
                    dialog.dismiss();
                    startActivity(intent);
                }
            });
            alertbox.setCancelable(false);
            mAlertDialog = alertbox.show();
        }
        else{

        }
        super.onResume();
        }

        @Override
    protected void onPause() {
        super.onPause();
        mNfcUtils.disableDispatch();
    }

    private void showCardInfo(EmvCard card) {
        String text;
        if (card != null) {
            text = TextUtils.join("\n", new Object[]{
                    CardUtils.formatCardNumber(card.getCardNumber(), card.getType()),
                    DateFormat.format("M/y", card.getExpireDate()),
                    "---",
                    "Bank info (probably): ",
                    card.getAtrDescription(),
                    "---",
                    card.toString().replace(", ", ",\n")
            });
        } else {
            text = getString(R.string.error);
        }
        textView.setText(text);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        final Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (mTag != null) {

            new SimpleAsyncTask() {

                /**
                 * Tag comm
                 */
                private IsoDep mTagcomm;

                /**
                 * Emv Card
                 */
                private EmvCard mCard;

                /**
                 * Boolean to indicate exception
                 */
                private boolean mException;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();


                    mProvider.getLog().setLength(0);
                    // Show dialog
                    if (mDialog == null) {
                        mDialog = ProgressDialog.show(NFCACtivity.this, getString(R.string.card_reading),
                                getString(R.string.card_reading_desc), true, false);
                    } else {
                        mDialog.show();
                    }
                }

                @Override
                protected void doInBackground() {

                    mTagcomm = IsoDep.get(mTag);
                    if (mTagcomm == null) {
                        CroutonUtils.display(NFCACtivity.this, getText(R.string.error_communication_nfc), CroutonUtils.CoutonColor.BLACK);
                        return;
                    }
                    mException = false;

                    try {
                        mReadCard = null;
                        // Open connection
                        mTagcomm.connect();
                        lastAts = getAts(mTagcomm);

                        mProvider.setmTagCom(mTagcomm);

                        EmvParser parser = new EmvParser(mProvider, true);
                        mCard = parser.readEmvCard();
                        if (mCard != null) {
                            mCard.setAtrDescription(extractAtsDescription(lastAts));
                        }

                    } catch (IOException e) {
                        mException = true;
                    } finally {
                        // close tagcomm
                        IOUtils.closeQuietly(mTagcomm);
                    }
                }

                @Override
                protected void onPostExecute(final Object result) {
                    // close dialog
                    if (mDialog != null) {
                        mDialog.cancel();
                    }

                    if (!mException) {
                        if (mCard != null) {
                            if (AndroidCommonsUtils.isNotBlank(mCard.getCardNumber())) {
                                CroutonUtils.display(NFCACtivity.this, getText(R.string.card_read), CroutonUtils.CoutonColor.GREEN);
                                mReadCard = mCard;
                            } else if (mCard.isNfcLocked()) {
                                CroutonUtils.display(NFCACtivity.this, getText(R.string.nfc_locked), CroutonUtils.CoutonColor.ORANGE);
                            }
                        } else {
                            CroutonUtils.display(NFCACtivity.this, getText(R.string.error_card_unknown), CroutonUtils.CoutonColor.BLACK);
                        }
                    } else {
                        CroutonUtils
                                .display(NFCACtivity.this, getResources().getText(R.string.error_communication_nfc), CroutonUtils.CoutonColor.BLACK);
                    }

                    refreshContent();
                }

            }.execute();
        }

    }
    private void refreshContent() {
        if (mRefreshableContent != null && mRefreshableContent.get() != null) {
            mRefreshableContent.get().update();
        }
    }

    /**
     * Method used to clear data
     */
    public void clear() {
        mReadCard = null;
        mProvider.getLog().setLength(0);
        IRefreshable content = mRefreshableContent.get();
        if (content != null) {
            content.update();
        }
    }

    /**
     * Get ATS from isoDep
     *
     * @param pIso isodep
     * @return ATS byte array
     */
    private byte[] getAts(final IsoDep pIso) {
        byte[] ret = null;
        if (pIso.isConnected()) {
            // Extract ATS from NFC-A
            ret = pIso.getHistoricalBytes();
            if (ret == null) {
                // Extract ATS from NFC-B
                ret = pIso.getHiLayerResponse();
            }
        }
        return ret;
    }

    /**
     * Method used to get description from ATS
     *
     * @param pAts ATS byte
     */
    public Collection<String> extractAtsDescription(final byte[] pAts) {
        return AtrUtils.getDescriptionFromAts(BytesUtils.bytesToString(pAts));

    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.DEBUG) {
            if (mReadCard == null) {
                StringBuffer buff = mProvider.getLog();
                for (int i = 0; i < 60; i++) {
                    buff.append("=============<br/>");
                }
                mReadCard = new EmvCard();
                mReadCard.setCardNumber("4123456789012345");
                mReadCard.setAid("A0 00 00 000310 10");
                mReadCard.setLeftPinTry(3);
                mReadCard.setAtrDescription(Arrays.asList("CB Visa Banque Populaire (France)"));
                mReadCard.setApplicationLabel("CB");
                mReadCard.setHolderFirstname("John");
                mReadCard.setHolderFirstname("Doe");
                mReadCard.setExpireDate(new Date());
                mReadCard.setType(EmvCardScheme.UNIONPAY);
                List<EmvTransactionRecord> records = new ArrayList<EmvTransactionRecord>();
                // payment
                EmvTransactionRecord payment = new EmvTransactionRecord();
                payment.setAmount((float) 100.0);
                payment.setCurrency(CurrencyEnum.EUR);
                payment.setCyptogramData("12");
                payment.setTerminalCountry(CountryCodeEnum.FR);
                payment.setDate(new Date());
                payment.setTransactionType(TransactionTypeEnum.REFUND);
                records.add(payment);

                payment = new EmvTransactionRecord();
                payment.setAmount((float) 12.0);
                payment.setCurrency(CurrencyEnum.USD);
                payment.setCyptogramData("40");
                payment.setTerminalCountry(CountryCodeEnum.US);
                payment.setDate(new Date());
                payment.setTransactionType(TransactionTypeEnum.PURCHASE);
                records.add(payment);

                payment = new EmvTransactionRecord();
                payment.setAmount((float) 120.0);
                payment.setCurrency(CurrencyEnum.USD);
                payment.setCyptogramData("40");
                payment.setTerminalCountry(null);
                payment.setDate(new Date());
                payment.setTransactionType(null);
                records.add(payment);

                mReadCard.setListTransactions(records);
                refreshContent();
                CroutonUtils.display(NFCACtivity.this, getText(R.string.card_read), CroutonUtils.CoutonColor.GREEN);
            } else if (mReadCard != null) {
                mReadCard = null;
                refreshContent();
                CroutonUtils.display(NFCACtivity.this, getText(R.string.card_read), CroutonUtils.CoutonColor.ORANGE);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
}

