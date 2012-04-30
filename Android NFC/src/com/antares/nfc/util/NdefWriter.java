package com.antares.nfc.util;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

import com.antares.nfc.client.AndroidNfcActivity;

/**
 * 
 * NFC tag writer.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */


public class NdefWriter {

    private static final String TAG = AndroidNfcActivity.class.getSimpleName();
    
    public static interface NdefWriterListener {

    	void writeNdefFormattedFailed(Exception e);
    	
    	void writeNdefUnformattedFailed(Exception e);
    	
    	void writeNdefNotWritable();
    	
    	void writeNdefTooSmall(int required, int capacity);
    	
    	void writeNdefCannotWriteTech();
    	
    	void wroteNdefFormatted();
    	
    	void wroteNdefUnformatted();

    }
    
	protected NfcAdapter mNfcAdapter;
	
	protected NdefWriterListener listener;
	
	public NdefWriter(Context context) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
	}

	public boolean write(NdefMessage message, Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Write unformatted tag");
		    try {
		        format.connect();
		        format.format(message);
		        
        		listener.wroteNdefUnformatted();

		        return true;
		    } catch (Exception e) {
           		listener.writeNdefUnformattedFailed(e);
		    }
			Log.d(TAG, "Cannot write unformatted tag");
		} else {
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {
            	try {
            		Log.d(TAG, "Write formatted tag");

            		ndef.connect();
            		if (!ndef.isWritable()) {
            			Log.d(TAG, "Tag is not writeable");

            			listener.writeNdefNotWritable();
                        
            		    return false;
            		}
            		if (ndef.getMaxSize() < message.toByteArray().length) {
            			Log.d(TAG, "Tag size is too small, have " + ndef.getMaxSize() + ", need " + message.toByteArray().length);

            			listener.writeNdefTooSmall(message.toByteArray().length, ndef.getMaxSize());

            		    return false;
            		}
            		ndef.writeNdefMessage(message);
            		
            		listener.wroteNdefFormatted();
            		
            		return true;
            	} catch (Exception e) {
            		listener.writeNdefFormattedFailed(e);
	            }
            } else {
            	listener.writeNdefCannotWriteTech();
            }
			Log.d(TAG, "Cannot write formatted tag");
		}

	    return false;
	}

	public void setListener(NdefWriterListener listener) {
		this.listener = listener;
	}

}
