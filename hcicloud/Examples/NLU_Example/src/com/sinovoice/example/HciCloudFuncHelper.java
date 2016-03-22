package com.sinovoice.example;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.R.array;
import android.content.Context;
import android.widget.TextView;

import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.nlu.NluConfig;
import com.sinovoice.hcicloudsdk.common.nlu.NluInitParam;
import com.sinovoice.hcicloudsdk.common.nlu.NluRecogResult;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.nlu.HciCloudNlu;


public class HciCloudFuncHelper extends HciCloudHelper{
    private static final String TAG = HciCloudFuncHelper.class.getSimpleName();

    
    public static int Recog(String capkey, NluConfig recogConfig,String sTransText) {
		ShowMessage("Recog start...");
		
		NluConfig sessionConfig = new NluConfig();
		sessionConfig.addParam(NluConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
		// 创建会话
		Session session = new Session();
		// 开始会话
		int errCode = HciCloudNlu.hciNluSessionStart(sessionConfig.getStringConfig(), session);
		if (errCode != HciErrorCode.HCI_ERR_NONE) {
			ShowMessage("hciNluSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
			return errCode;
		}

		ShowMessage("hciNluSessionStart Success");

		// 开始翻译
		// 调用HciCloudMt.hciMtTrans() 方法进行合成
		NluRecogResult nluResult = new NluRecogResult();
		errCode = HciCloudNlu.hciNluRecog(session, sTransText, recogConfig.getStringConfig(), nluResult);
		if (errCode == HciErrorCode.HCI_ERR_NONE) {
			ShowMessage("hciNluRecog success");
			printNluResult(nluResult);
		}
		else{
			ShowMessage("hciNluRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));
		}
		
		// 停止会话
		// 合成结束后应该调用该方法通知引擎该会话已经结束
		errCode = HciCloudNlu.hciNluSessionStop(session);
		if (errCode != HciErrorCode.HCI_ERR_NONE) {
			ShowMessage("hciNluSessionStop error:" + HciCloudSys.hciGetErrorInfo(errCode));
			return errCode;
		}
		
		ShowMessage("hciNluSessionStop");
		ShowMessage("Recog stop...");
		return HciErrorCode.HCI_ERR_NONE;
	}


	/**
	 * 输出NLU识别结果
	 * 
	 * @param recogResult
	 *            识别结果
	 */
	private static void printNluResult(NluRecogResult recogResult) {
		for (int i = 0; i < recogResult.getRecogResultItemList().size(); i++) {
			ShowMessage(""+recogResult.getRecogResultItemList().get(i).getResult());
		}
	}

	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
		throw new IllegalArgumentException(from + " > " + to);
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0,
		Math.min(original.length - from, newLength));
		return copy;
		}
	
	public static void Func(Context context,String capkey,TextView view) {			

		setTextView(view);
		setContext(context);
		
		//初始化ASR
		//构造Asr初始化的帮助类的实例
        NluInitParam initParam = new NluInitParam();
        // 获取App应用中的lib的路径,放置能力所需资源文件。如果使用/data/data/packagename/lib目录,需要添加android_so的标记
        String dataPath = context.getFilesDir().getAbsolutePath()
                .replace("files", "lib");
        initParam.addParam(NluInitParam.PARAM_KEY_DATA_PATH, dataPath);
        initParam.addParam(NluInitParam.PARAM_KEY_FILE_FLAG, NluInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
        initParam.addParam(NluInitParam.PARAM_KEY_INIT_CAP_KEYS, capkey);
        ShowMessage("hciNluInit config :" + initParam.getStringConfig());
        int errCode = HciCloudNlu.hciNluInit(initParam.getStringConfig());
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            ShowMessage("hciNluInit error:" + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        } else {
        	ShowMessage("hciNluInit Success");
        }
        
        byte[] nluData = getAssetFileData("nlu.txt");
        
        //remove utf-8 bom head
        if (nluData[0]==0xffffffef && nluData[1]==0xffffffbb && nluData[2]==0xffffffbf) {
        	nluData= copyOfRange(nluData, 3, nluData.length);
		}
        
		String nluText = null;
		try {
			nluText = new String(nluData, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        NluConfig recogConfig = new NluConfig();
        recogConfig.addParam("intention", "weather");//云端必须传intention
        ShowMessage(recogConfig.toString());
        Recog(capkey,recogConfig,nluText);
        //反初始化NLU
        HciCloudNlu.hciNluRelease();
        ShowMessage("hciNluRelease");

        return;
	}
	
}
