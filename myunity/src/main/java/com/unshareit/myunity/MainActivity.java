package com.unshareit.myunity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
    }

    public void androidCallUnity(){
        //第1个参数为Unity场景中用于接收android消息的对象名称
        //第2个参数为对象上的脚本的一个成员方法名称（脚本名称不限制）
        //第3个参数为unity方法的参数
        UnityPlayer.UnitySendMessage("","","");
    }
}