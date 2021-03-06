package com.nit.weixi.study_c_system.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nit.weixi.study_c_system.fragment.QuestionFragment;
import com.nit.weixi.study_c_system.tools.TeacherUtils;

/**
 * 开始答疑模块
 * Created by weixi on 2016/5/8.
 */
public class StartAnswerActivity extends MyBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackActionBar("开始答疑",null);
        TeacherUtils.setTeacherFragment(this,new QuestionFragment(),"wenti");
    }

}
