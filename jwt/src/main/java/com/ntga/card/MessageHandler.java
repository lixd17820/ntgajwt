package com.ntga.card;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class MessageHandler extends Handler{
	//MyView mView=null;
	//TextView mtvError=null;
	//TextView mtvDebug=null;
	//TextView mtvDebug2=null;
	boolean mConnected=false;
	
	public MessageHandler(Activity activity){
		//mView=(MyView)activity.findViewById(R.id.view1);
		//mView.setWillNotDraw(false);
		//mtvError=(TextView)activity.findViewById(R.id.tvError);
		//mtvDebug=(TextView)activity.findViewById(R.id.tvDebug);
		//mtvDebug2=(TextView)activity.findViewById(R.id.tvDebug2);
	}
	
	public void handleMessage (Message msg){
		switch(msg.what){
		case ReadCardThread.WM_CLEARSCREEN:
			//mView.mPerson.Empty();
			//mtvError.setText("正在读卡...");
			//mView.invalidate();
			break;
		case ReadCardThread.WM_READCARD:
			//mView.mPerson=(PersonInfo)msg.obj;
			//mtvError.setText("读卡成功！");
			mConnected=true;
			//mView.invalidate();
			break;
		case ReadCardThread.WM_ERROR:
			if(msg.arg2==Error.ERR_FIND){
				if(!mConnected){
					//mtvError.setText("请放卡...");
					mConnected=true;
				}
			}else{
				if(msg.arg2==Error.ERR_PORT)
					mConnected=false;
				//mtvError.setText(Error.GetErrorText(msg.arg2));
			}
			break;
		/*case 10:
			mtvDebug.setText((String)msg.obj);
			break;
		case 11:
			mtvDebug2.setText((String)msg.obj);
			break;*/
		}
	} 
}
