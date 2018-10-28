package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/**
 * Created by sa on 2018/4/16.
 */

class RedPacketActivity extends BaseFragment {
    public RedPacketActivity(Bundle args) {
        super(args);
    }

    @Override
    public View createView(Context context) {
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionModeTopColor(Color.parseColor("#FF00ff"));

        actionBar.setTitle(LocaleController.getString("InviteFriends", R.string.find));

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);


        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        fragmentView = new FrameLayout(context);
        LookForward();
        return fragmentView;
    }

    public void LookForward(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setItems(new CharSequence[]{
               "程序员正在努力，稍后开启",
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishFragment();
            }
        });
        showDialog(builder.create());

    }
}
