package com.example.studivo.presentation.utils

import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object QRDataCompressor {
	
	// ⚠️ IMPORTANTE: En producción, considera usar Android Keystore
	private const val SECRET_KEY = "StudivoMusicApp2025SecretKey!!99" // 32 caracteres
	private const val ALGORITHM = "AES/CBC/PKCS5Padding"
	private const val IV ="StudivoVector16!"
	
	/**
	 * Comprime, cifra y codifica en Base64
	 */
	fun compressAndEncrypt(jsonData: String): String {
		try {
			// 1. Comprimir con GZIP
			val compressed = compress(jsonData.toByteArray(Charsets.UTF_8))
			
			// 2. Cifrar con AES
			val encrypted = encrypt(compressed)
			
			// 3. Codificar en Base64 (URL-safe para QR)
			return Base64.encodeToString(encrypted, Base64.URL_SAFE or Base64.NO_WRAP)
		} catch (e: Exception) {
			throw Exception("Error comprimiendo datos: ${e.message}")
		}
	}
	
	/**
	 * Decodifica Base64, descifra y descomprime
	 */
	fun decryptAndDecompress(encodedData: String): String {
		try {
			// 1. Decodificar Base64
			val encrypted = Base64.decode(encodedData, Base64.URL_SAFE or Base64.NO_WRAP)
			
			// 2. Descifrar AES
			val compressed = decrypt(encrypted)
			
			// 3. Descomprimir GZIP
			val decompressed = decompress(compressed)
			
			return String(decompressed, Charsets.UTF_8)
		} catch (e: Exception) {
			throw Exception("Error descomprimiendo datos: ${e.message}")
		}
	}
	
	// ========== Compresión GZIP ==========
	
	private fun compress(data: ByteArray): ByteArray {
		val byteArrayOutputStream = ByteArrayOutputStream()
		GZIPOutputStream(byteArrayOutputStream).use { gzipStream ->
			gzipStream.write(data)
		}
		return byteArrayOutputStream.toByteArray()
	}
	
	private fun decompress(data: ByteArray): ByteArray {
		val byteArrayInputStream = ByteArrayInputStream(data)
		val byteArrayOutputStream = ByteArrayOutputStream()
		
		GZIPInputStream(byteArrayInputStream).use { gzipStream ->
			val buffer = ByteArray(1024)
			var len: Int
			while (gzipStream.read(buffer).also { len = it } != -1) {
				byteArrayOutputStream.write(buffer, 0, len)
			}
		}
		return byteArrayOutputStream.toByteArray()
	}
	
	// ========== Cifrado AES ==========
	
	private fun encrypt(data: ByteArray): ByteArray {
		val secretKey: SecretKey = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
		val iv = IvParameterSpec(IV.toByteArray())
		
		val cipher = Cipher.getInstance(ALGORITHM)
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
		
		return cipher.doFinal(data)
	}
	
	private fun decrypt(data: ByteArray): ByteArray {
		val secretKey: SecretKey = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
		val iv = IvParameterSpec(IV.toByteArray())
		
		val cipher = Cipher.getInstance(ALGORITHM)
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
		
		return cipher.doFinal(data)
	}
}