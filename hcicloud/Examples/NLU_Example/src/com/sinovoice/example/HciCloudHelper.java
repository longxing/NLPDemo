package com.sinovoice.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.vpr.VprConfig;
import com.sinovoice.hcicloudsdk.common.vpr.VprEnrollResult;
import com.sinovoice.hcicloudsdk.common.vpr.VprEnrollVoiceData;
import com.sinovoice.hcicloudsdk.common.vpr.VprEnrollVoiceDataItem;
import com.sinovoice.hcicloudsdk.common.vpr.VprInitParam;
import com.sinovoice.hcicloudsdk.common.vpr.VprVerifyResult;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.vpr.HciCloudVpr;


public class HciCloudHelper {
    private static final String TAG = HciCloudFuncHelper.class.getSimpleName();
    private static TextView mView = null;
    private static Context mContext = null;
    

    protected static void setContext(Context context){
    	mContext = context;
    }

	
	/**
	 * 获取指定Assert文件中的数据
	 * 
	 * @param fileName
	 * @return
	 */
	protected static byte[] getAssetFileData(String fileName) {
		InputStream in = null;
		int size = 0;
		try {
			in = mContext.getResources().getAssets().open(fileName);
			size = in.available();
			byte[] data = new byte[size];
			in.read(data, 0, size);

			return data;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
			return null;
		}
	}
	
	
	protected static void setTextView(TextView view){
    	mView = view;
    }
	
	/**
	 * 显示消息
	 * 
	 * @param message
	 * @return
	 */
	protected static void ShowMessage(String message){
		 mView.append(message + "\n");
	}
	
}
