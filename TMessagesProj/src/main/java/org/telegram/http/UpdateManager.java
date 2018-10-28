package org.telegram.http;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.WebviewActivity;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;

public class UpdateManager {
    /**
     * 版本更新
     */
    private Activity mContext;
    private String apkUrl;
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/移动审批/";
    private static final String saveFileName = savePath + "Biying.apk";
    private TextView tv;
    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    try {
                        tv.setText(progress + "%");
                        mProgress.setProgress(progress);
//                popup_tv.setText(progress + "%");
//                popup_progress.setProgress(progress);
                        if (progress == 99) {
                            downloadDialog.dismiss();
//                    popWindow.dismiss();
                        }
                    } catch (Exception e) {

                    }

                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Activity context) {
        this.mContext = context;
    }

    // 外部接口让主Activity调用
    public void checkUpdateInfo(String is_qian, String title, String content, String apkUrl,Context context) {
        this.apkUrl = apkUrl;
        showNoticeDialog(is_qian, title, content,context);

//        showPopWindow_jcgx(mContext, view, str);
    }

    private View view;
    private PopupWindow popWindow;
    private LinearLayout popup_no, popup_yes;
    private TextView popup_gx;
    private TextView popup_tv;
    private ProgressBar popup_progress;
    private LinearLayout popup_qx;

//    private void showPopWindow_jcgx(Context context, View parent, String str) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View vPopWindow = inflater.inflate(R.layout.popup_jcgx, null, false);
//        // 宽300 高300
//        popWindow = new PopupWindow(vPopWindow, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
//
//        popup_gx = (TextView) vPopWindow.findViewById(R.id.popup_gx);
//        popup_no = (LinearLayout) vPopWindow.findViewById(R.id.popup_no);
//        popup_yes = (LinearLayout) vPopWindow.findViewById(R.id.popup_yes);
//
//        popup_gx.setText(str);
//
//        popup_no.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                popWindow.dismiss();
//            }
//        });
//        popup_yes.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                popWindow.dismiss();
//                showPopWindow_gx(mContext, view);
//            }
//        });
//
//        popWindow.dismiss(); // Close the Pop Window
//        popWindow.setOutsideTouchable(true);
//        popWindow.setFocusable(true);
//
//        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
//    }

    private void showPopWindow_gx(Context context, View parent) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View vPopWindow = inflater.inflate(R.layout.popup_jcgx, null, false);
//        // 宽300 高300
//        popWindow = new PopupWindow(vPopWindow, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
//
//        popup_tv = (TextView) vPopWindow.findViewById(R.id.popup_tv);
//        popup_progress = (ProgressBar) vPopWindow.findViewById(R.id.popup_progress);
//        popup_qx = (LinearLayout) vPopWindow.findViewById(R.id.popup_qx);
//
//        popup_qx.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                popWindow.dismiss();
//                interceptFlag = true;
//            }
//        });
//
//        downloadApk();
//
//        popWindow.dismiss(); // Close the Pop Window
//        popWindow.setOutsideTouchable(true);
//        popWindow.setFocusable(true);
//
//        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void showNoticeDialog(final String is_qian, String title, String str, final Context context) {
        Builder builder = new Builder(mContext);
        builder.setTitle(title);
        if (is_qian.equals("1")) {
            builder.setMessage("本次为强制更新" + str);
        } else {
            builder.setMessage(str);
        }
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(apkUrl);
                intent.setData(content_url);
                context.startActivity(intent);

                //showDownloadDialog();
            }
        });
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (is_qian.equals("1")) {
                    dialog.dismiss();
                    showNoticeDialog2(context);
                } else {
                    dialog.dismiss();
                }
            }
        });
        noticeDialog = builder.create();
        noticeDialog.setCancelable(false);
        noticeDialog.show();
    }

    private void showNoticeDialog2(final Context context) {
        final Builder builder = new Builder(mContext);
        builder.setTitle("本次为强制更新");
        builder.setMessage("您不更新将无法使用我们的应用");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //showDownloadDialog();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(apkUrl);
                intent.setData(content_url);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mContext.finish();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.setCancelable(false);
        noticeDialog.show();
    }

    private void showDownloadDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle("正在更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.popup_jcgx, null);
        tv = (TextView) v.findViewById(R.id.tv);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.setCancelable(false);
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);// 显示用户数据
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

}
