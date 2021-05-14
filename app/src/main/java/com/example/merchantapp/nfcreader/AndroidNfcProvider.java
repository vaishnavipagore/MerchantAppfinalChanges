package com.example.merchantapp.nfcreader;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.example.merchantapp.emvn.CommunicationException;
import com.example.merchantapp.emvn.SwEnum;
import com.example.merchantapp.nfcutils.BytesUtils;
import com.example.merchantapp.nfcutils.TlvUtil;

import java.io.IOException;



/**
 * Provider used to communicate with EMV card
 */
class AndroidNfcProvider implements io.github.tapcard.emvnfccard.parser.IProvider {

    /**
     * TAG for logger
     */
    private static final String TAG = AndroidNfcProvider.class.getName();

    /**
     * Tag comm
     */
    private IsoDep mTagCom;

    private boolean debugMode = false;

    @Override
    public byte[] transceive(final byte[] pCommand) throws CommunicationException {
        if (debugMode) {
            log("send: " + BytesUtils.bytesToString(pCommand));
        }

        byte[] response;
        try {
            // send command to emv card
            response = mTagCom.transceive(pCommand);
        } catch (IOException e) {
            throw new CommunicationException(e.getMessage());
        }

        log("resp: " + BytesUtils.bytesToString(response));
        try {
            log("resp: " + TlvUtil.prettyPrintAPDUResponse(response));
            SwEnum val = SwEnum.getSW(response);
            if (val != null) {
                log("resp: " + val.getDetail());
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        return response;
    }

    /**
     * Enable ro disable debug info logging
     */
    public AndroidNfcProvider setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    /**
     * Setter for the field mTagCom
     *
     * @param mTagCom the mTagCom to set
     */
    void setmTagCom(final IsoDep mTagCom) {
        this.mTagCom = mTagCom;
    }

    private void log(String logLine) {
        if (debugMode) {
            Log.d(TAG, logLine);
        }
    }
}
