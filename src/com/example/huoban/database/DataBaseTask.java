package com.example.huoban.database;


import android.os.AsyncTask;

public class DataBaseTask extends AsyncTask<Object[], Void, Object[]> {

	synchronized public final static Object doInBackgroundSyn(DBOperaterInterFace dbOperaterInterFace, DbParamData dbParamData) {
		Object object = dbOperaterInterFace.getDataFromDB(dbParamData);
		return object;
	}

	@Override
	protected Object[] doInBackground(Object[]... params) {
		Object[] object = params[0];
		if (object != null && object.length == 2) {
			DBOperaterInterFace dbOperaterInterFace = (DBOperaterInterFace) object[0];
			DbParamData dbParamData = (DbParamData) object[1];
			if (dbOperaterInterFace != null&&dbParamData!=null)
				dbParamData.object = doInBackgroundSyn(dbOperaterInterFace, dbParamData);
		}

		return object;
	}

	@Override
	protected void onPostExecute(Object[] result) {
		super.onPostExecute(result);
		Object[] object = result;
		if (object != null && object.length == 2) {
			DBOperaterInterFace dbOperaterInterFace = (DBOperaterInterFace) object[0];
			if(dbOperaterInterFace!=null){
				dbOperaterInterFace.returnDataFromDb((DbParamData)object[1]);
			}
		}
		
	}
}
