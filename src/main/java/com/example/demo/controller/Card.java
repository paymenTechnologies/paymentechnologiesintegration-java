package com.example.demo.controller;

import java.io.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.util.regex.*;  
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;
import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.util.logging.*;
import java.math.BigInteger;


class Card {

	public static String firstname;
	public static String lastname;
	public static String card_number;
	public static String expiration_month;
	public static String expiration_year;
	public static String cvc;
	public static String secret_key;
	public static String array_params;
	public static String card_info;

	public Card(String firstname, String lastname, String card_number, String expiration_month, String expiration_year, String cvc, String secret_key, String array_params ) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.card_number = card_number;
		this.expiration_month = expiration_month;
		this.expiration_year = expiration_year;
		this.cvc = cvc;
		this.secret_key = secret_key;
		this.array_params = array_params;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getCardnumber() {
		return card_number;
	}

	public String getExpiration_month() {
		return expiration_month;
	}

	public String getExpiration_year() {
		return expiration_year;
	}

	public String getCvc() {
		return cvc;
	}

	public String getSecret_key() {
		return secret_key;
	}

	public String getArray_params() {
		return array_params;
	}

	private String Encryptor(String TextToEncrypt, byte[] key, String strIV) {
		String data = "";
		try {
			byte[] byteV = strIV.getBytes("utf-8");
			byte[] byteKey = Arrays.copyOf(key, 32);
			byte[] byteIV = Arrays.copyOf(byteV, 16);
			
			SecretKeySpec skeySpec = new SecretKeySpec(byteKey, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(byteIV);
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

			byte[] byteText = TextToEncrypt.getBytes("utf-8");
			byte[] buf = cipher.doFinal(byteText);
			byte[] byteBase64 = Base64.getEncoder().encode(buf);
				
			data = new String(byteBase64);
		}
		catch(NoSuchPaddingException | BadPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException ex) {

		}
		
		return data;

	}

	public String EncryptCardInfo()
	{
		String encodedString = "";
		try {
			String expiry = this.expiration_month +"/"+ this.expiration_year;
			String payload = String.format("ccn||%1$s__expire||%2$s__cvc||%3$s__firstname||%4$s__lastname||%5$s", this.card_number, expiry, this.cvc, this.firstname, this.lastname);

			String newSecret = this.secret_key.replace(".", "").substring(0, 16);

			UUID Guid=java.util.UUID.randomUUID();

			String ivString = Guid.toString().replace("-", "").substring(0, 16);
			byte[] key = newSecret.getBytes("US-ASCII");

			String encrypted = this.Encryptor(payload, key, ivString);
			String originalInput = encrypted + "::" + ivString;
			encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		}
		catch(UnsupportedEncodingException ex) {

		}

		return encodedString;

	}

	public String Signature(TreeMap<String, String> dic)
	{
		StringBuilder sb = new StringBuilder();

		for(Map.Entry m:dic.entrySet()) {    
			// System.out.println(m.getKey()+" "+m.getValue());    
			sb.append(m.getValue().toString());
		}   

		sb.append(this.secret_key);
		System.out.println("-------------");    
		System.out.println(sb.toString());    
		System.out.println("-------------");    

		String res = "";
		try {
			res = SHA1(sb.toString());
		}
		catch(NoSuchAlgorithmException | UnsupportedEncodingException ex)
		{

		}

		return res;
	}

	public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String hashword = "";
		try {
          MessageDigest md = MessageDigest.getInstance("SHA-1");
          md.update(text.getBytes());
          BigInteger hash = new BigInteger(1, md.digest());
          hashword = hash.toString(16);
		} catch (NoSuchAlgorithmException ex) {

		}
		return hashword;
	}

}