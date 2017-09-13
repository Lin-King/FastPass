package com.linkings.fastpass.presenter;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.ui.activity.HomeActivity;
import com.linkings.fastpass.ui.fragment.ApkFragment;
import com.linkings.fastpass.ui.fragment.FileFragment;
import com.linkings.fastpass.ui.fragment.MediaFragment;
import com.linkings.fastpass.ui.fragment.PicFragment;
import com.linkings.fastpass.ui.fragment.VideoFragment;
import com.linkings.fastpass.utils.DialogUtil;
import com.linkings.fastpass.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/2.
 * Time: 17:46
 * Description: TOO
 */

public class HomePresenter {
    private HomeActivity mHomeActivity;

    public HomePresenter(HomeActivity homeActivity) {
        mHomeActivity = homeActivity;
    }

    public void initToolbar() {
        mHomeActivity.initToolbar();
    }

    public void init() {
        List<Fragment> mList = new ArrayList<>();
        mList.add(new ApkFragment());
        mList.add(new VideoFragment());
        mList.add(new MediaFragment());
        mList.add(new PicFragment());
        mList.add(new FileFragment());
        String[] title = {
                mHomeActivity.intoString(R.string.application),
                mHomeActivity.intoString(R.string.video),
                mHomeActivity.intoString(R.string.music),
                mHomeActivity.intoString(R.string.picture),
                mHomeActivity.intoString(R.string.file)
        };
        mHomeActivity.init(mList, title);
    }

    public void checkType() {
        DialogUtil.showDoubleDialog(mHomeActivity, "", "选择", "发送", "接受", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (FileInfoMG.getInstance().getListSize() > 0) {
                    mHomeActivity.toSendActivity();
                } else {
                    ToastUtil.show(mHomeActivity, "请选择要发送的文件！");
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHomeActivity.toAcceptActivity();
            }
        });
    }

    public void setSendNum(TextView tv) {
        if (FileInfoMG.getInstance().getListSize() > 0) {
            tv.setVisibility(View.VISIBLE);
            String num = FileInfoMG.getInstance().getListSize() + "";
            tv.setText(num);
        } else {
            tv.setVisibility(View.GONE);
        }
    }
}
