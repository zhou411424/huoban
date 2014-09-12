package com.example.huoban.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Base64;


public class RSAUtil {
	/** */
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * 非对称加密密钥算法
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 公钥加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            公钥
	 * @return byte[] 加密数据
	 * @throws EncryptException
	 */
	public static byte[] encryptByPublicKey(byte[] data, byte[] key) {

		ByteArrayOutputStream out = null;
		try {
			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

			// 对数据加密
			Cipher cipher = Cipher.getInstance("RSA");

			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			int inputLen = data.length;
			out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = out.toByteArray();
			return encryptedData;

		} catch (NoSuchAlgorithmException e) {
			// throw new Exception(
			// "RSA algorithm is not available in the environment",
			// WebConstants.SYS_STATUS_CODE_ENCRYPT_ERROR,
			// WebConstants.SYS_STATUS_DESC_ENCRYPT_ERROR,
			// WebConstants.ENCRYPT_METHOD_VALUE, e);
		} catch (NoSuchPaddingException e) {
			// throw new Exception(
			// "Padding is not available in the environment",
			// WebConstants.SYS_STATUS_CODE_ENCRYPT_ERROR,
			// WebConstants.SYS_STATUS_DESC_ENCRYPT_ERROR,
			// WebConstants.ENCRYPT_METHOD_VALUE, e);
		} catch (InvalidKeyException e) {
			// throw new Exception(
			// "invalid Keys (invalid encoding, wrong length, uninitialized, etc)",
			// WebConstants.SYS_STATUS_CODE_ENCRYPT_ERROR,
			// WebConstants.SYS_STATUS_DESC_ENCRYPT_ERROR,
			// WebConstants.ENCRYPT_METHOD_VALUE, e);
		} catch (IllegalBlockSizeException e) {
			// throw new Exception("the block size does not match with key",
			// WebConstants.SYS_STATUS_CODE_ENCRYPT_ERROR,
			// WebConstants.SYS_STATUS_DESC_ENCRYPT_ERROR,
			// WebConstants.ENCRYPT_METHOD_VALUE, e);
		} catch (BadPaddingException e) {
			// throw new Exception(
			// "the input data expected specific filling mechanism and the data is not properly filled",
			// WebConstants.SYS_STATUS_CODE_ENCRYPT_ERROR,
			// WebConstants.SYS_STATUS_DESC_ENCRYPT_ERROR,
			// WebConstants.ENCRYPT_METHOD_VALUE, e);
		} catch (InvalidKeySpecException e) {
			// throw new Exception("invalid key specifications",
			// WebConstants.SYS_STATUS_CODE_ENCRYPT_ERROR,
			// WebConstants.SYS_STATUS_DESC_ENCRYPT_ERROR,
			// WebConstants.ENCRYPT_METHOD_VALUE, e);
		} finally {

			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					// throw new Exception(
					// "errors occur while close ByteArrayOutputStream .",
					// WebConstants.SYS_STATUS_CODE_REQUEST_FAILED,
					// WebConstants.SYS_STATUS_DESC_REQUEST_FAILED, e);

				}

			}
		}
		
		return null;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws EncryptException
	 */
	public static byte[] encryptByPublicKey(byte[] data, String key) {

		return encryptByPublicKey(data, getKey(key));

	}

	/**
	 * 获取密钥
	 * 
	 * @param key
	 * @return
	 * @throws EncryptException
	 */
	public static byte[] getKey(String key) {
		return Base64.decode(key, Base64.DEFAULT);
		// return Hex.decodeHex(key.toCharArray());
	}

}
