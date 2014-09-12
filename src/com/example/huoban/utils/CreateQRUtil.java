package com.example.huoban.utils;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 生成二维码工具类
 * @author talent.zhao
 * */
public class CreateQRUtil {
	private static final String LOG_URL_ERROR="com.huoban.createQRUtil.url.error";
	private static final int QR_WIDTH = 350;//二维码宽度
	private static final int QR_HEIGHT = 350;//二维码高度
	
	/**
	 * 将要转换的字符串或地址转换成二维码，并显示在ImageView上
	 * @param url
	 * @param imageView
	 * */
	public static void createQRImage(String url,ImageView imageView){
		if(url==null || url.equals("") || url.length()==0){
			Log.e(LOG_URL_ERROR, "url is null");
			return;
		}
		
		Hashtable<EncodeHintType, String> hints=new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

		//图像数据转换
		try {
			BitMatrix bitMatrix=new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels=new int[QR_WIDTH*QR_HEIGHT];
			//按照二维码算法生成二维码图片
			for(int y=0;y<QR_HEIGHT;y++){
				
				for(int x=0;x<QR_WIDTH;x++){
					
					if(bitMatrix.get(x, y)){
						pixels[y*QR_WIDTH+x]=0xff000000;
					}else{
						pixels[y*QR_WIDTH+x]=0xffffffff;
					}
					
				}
				
			}
			//生成二维码图片的格式
			Bitmap bitmap=Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			//将二维码显示到ImageView中
			imageView.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
			Log.i(LOG_URL_ERROR, e.getMessage());
		}
	}

}
