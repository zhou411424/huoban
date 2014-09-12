package com.example.huoban.activity.question;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.os.Environment;

import com.example.huoban.model.QuestionNoUpdateResult;

public class QuestionNoUpdateFile 
{	
	public static final String QUESTION_FILE_NAME = "/need_undate";
	
	
	
	public static String getFilepath(Context context)
	{
		String fileDir = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory() + "/"
					+ context.getPackageName() + "/questionFile";
		} else {
			fileDir = context.getFilesDir().getAbsolutePath() + "/"
					+ context.getPackageName() + "/questionFile";
		}
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		
		return fileDir;
	}
	
	public static void writeUpdateFailedQuestion(Context context ,QuestionNoUpdateResult noUpdateResult ,String uid)
	{	
		
		String fileDir = null;
		if(context==null||noUpdateResult==null)
		{
			return;
		}
		fileDir = getFilepath(context);
		File file = new File(fileDir+QUESTION_FILE_NAME+uid);
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		try 
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(noUpdateResult);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static QuestionNoUpdateResult  readUnUpdateFile(Context context,String uid)
	{
		QuestionNoUpdateResult noUpdateResult  = null;
		
		String fileDir = null;
		if(context==null)
		{
			return noUpdateResult;
		}
		fileDir = getFilepath(context);
		File file = new File(fileDir+QUESTION_FILE_NAME+uid);
		if(!file.exists())
		{
			return noUpdateResult;
		}
		
		try 
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			noUpdateResult = (QuestionNoUpdateResult) ois.readObject();
			ois.close();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return noUpdateResult;
	}
	
	public static void clearFileQuestions(Context context,String uid)
	{
		String fileDir = null;
		if(context==null)
		{
			return;
		}
		fileDir = getFilepath(context);
		File file = new File(fileDir+QUESTION_FILE_NAME+uid);
		if(file.exists())
		{
			file.delete();
		}
	}
	
}
