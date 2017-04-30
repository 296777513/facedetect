package com.facedetect.push.android;


import com.facedetect.push.AndroidNotification;

public class AndroidCustomizedcast extends AndroidNotification {
	public AndroidCustomizedcast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "customizedcast");	
	}
	
	public void setAlias(String alias,String aliasType) throws Exception {
    	setPredefinedKeyValue("alias", alias);
    	setPredefinedKeyValue("alias_type", aliasType);
    }
			
	public void setFileId(String fileId,String aliasType) throws Exception {
    	setPredefinedKeyValue("file_id", fileId);
    	setPredefinedKeyValue("alias_type", aliasType);
    }

}
