package com.svce.sparrowpro;


import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import se.simbio.encryption.Encryption;

public class EncryptionDecryption{
    String key="sos";
    String salt="SOS";

    Encryption encryptdecrypt = Encryption.getDefault(key, salt, new byte[16]);

    public String encrypt(String data)
    {
        try {
            return encryptdecrypt.encrypt(data);
        }
        catch (Exception e)
        {
            Log.d("Er",e+"");
        }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public String decrypt(String data)
    {
        try {
            return encryptdecrypt.decrypt(data);
        } catch (Exception e)
        {
            Log.d("Er",e+"");
        }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public String encryptNull(String data)
    {
        try {
            return encryptdecrypt.encryptOrNull(data);
        } catch (Exception e)
        {
            Log.d("Er",e+"");
        }
        return null;
    }
    public String decryptNull(String data)
    {
        try {
            return encryptdecrypt.decryptOrNull(data);
        } catch (Exception e)
        {
            Log.d("Er",e+"");
        }
        return null;
    }



}
