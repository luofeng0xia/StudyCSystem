package com.nit.weixi.study_c_system.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.ZhangJieActivity;
import com.nit.weixi.study_c_system.data.MyCursorAdapter;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;


/**
 * training的具体界面
 * 这里我用的ListFragment 其实并不好，还不如自己写
 * 查询数据库要异步吗？我这里用了handler：后面都没用
 * Created by weixi on 2016/3/30.
 */
public class TrainingFragment extends ListFragment implements AdapterView.OnItemClickListener {
    static TrainingFragment f;
    static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                Cursor cursor=(Cursor)msg.obj;
                adapter=new MyCursorAdapter(f.getActivity(),cursor);
                f.setListAdapter(adapter);
                ListView listView = f.getListView();
                listView.setOnItemClickListener(f);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareData();
        f=this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fg_training, container, false);
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = Tool.getDataBase(getActivity());
                Cursor cursor = db.query(MyConstants.TABLE_ZHANGJIE_NAME, new String[]{"_id","name"}, null, null, null, null, null);
                Message msg=Message.obtain();
                msg.obj=cursor;
                msg.what=1;
                handler.sendMessage(msg);
            }
        }).start();
    }

   static MyCursorAdapter adapter;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(getActivity(), ZhangJieActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
