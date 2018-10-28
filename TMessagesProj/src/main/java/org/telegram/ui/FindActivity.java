/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.youth.banner.BannerConfig;
import com.youth.banner.SuperSwipeRefreshLayout;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.http.Find;
import org.telegram.http.GetProxyAddress;
import org.telegram.http.Proxy;
import org.telegram.http.Root;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.InviteUserCell;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FindActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, SwipeRefreshLayout.OnRefreshListener{

    private LinearLayout scrollView;
    private ListView listView;
    private com.youth.banner.SuperSwipeRefreshLayout superSwipeRefreshLayout;
    private com.youth.banner.Banner bannerView;
    private SimpleDraweeView image1,image2,image3,image4;
    private TextView MoreTextView;
    private TextView title1,title2,title3,title4;
    private FrameLayout counterView;
    private TextView counterTextView;
    private ArrayList<ContactsController.Contact> phoneBookContacts;
    private AsyncTask<Void, Void, JSONObject> currentTask;

    private HashMap<String, GroupCreateSpan> selectedContacts = new HashMap<>();
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();
    private GroupCreateSpan currentDeletingSpan;
    private AlertDialog progressDialog;

    private int fieldY;
    private InviteAdapter adapter;
    public FindActivity(Bundle args) {
        super(args);
    }
    @Override
    public View createView(Context context) {
        allSpans.clear();
        selectedContacts.clear();
        currentDeletingSpan = null;
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
        fragmentView = new ViewGroup(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                int maxSize;
                if (AndroidUtilities.isTablet() || height > width) {
                    maxSize = AndroidUtilities.dp(230);
                } else {
                    maxSize = AndroidUtilities.dp(56);
                }
                counterView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));


                scrollView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(maxSize, MeasureSpec.AT_MOST));
                superSwipeRefreshLayout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height - scrollView.getMeasuredHeight() , MeasureSpec.EXACTLY));

                counterView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                scrollView.layout(0, 0, scrollView.getMeasuredWidth(), scrollView.getMeasuredHeight());
                superSwipeRefreshLayout.layout(0, scrollView.getMeasuredHeight(), listView.getMeasuredWidth(), scrollView.getMeasuredHeight() + listView.getMeasuredHeight());
            }

        };
        ViewGroup frameLayout = (ViewGroup) fragmentView;

        scrollView = new LinearLayout(context) {
            @Override
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += fieldY + AndroidUtilities.dp(20);
                rectangle.bottom += fieldY + AndroidUtilities.dp(50);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        scrollView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(scrollView);


        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    getImages(GetProxyAddress.getDefProxyAddress());
                }catch (Exception e){
                    Log.i("TAG","3333333333333333333333");
                    getImages(GetProxyAddress.getBaseProxyAddress());
                }
            }
        });
        adapter=new InviteAdapter(context);
        listView = new ListView(context);
        listView.setDividerHeight(1);
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter);
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? View.SCROLLBAR_POSITION_LEFT : View.SCROLLBAR_POSITION_RIGHT);

        View view=View.inflate(context,R.layout.findactivity_header,null);
        image1=view.findViewById(R.id.simple1);
        image2=view.findViewById(R.id.simple2);
        image3=view.findViewById(R.id.simple3);
        image4=view.findViewById(R.id.simple4);
        title1=view.findViewById(R.id.title1);
        title2=view.findViewById(R.id.title2);
        title3=view.findViewById(R.id.title3);
        title4=view.findViewById(R.id.title4);
        MoreTextView=view.findViewById(R.id.findmore);
        bannerView = view.findViewById(R.id.banner);
        bannerView.setPadding(0, 0, 0, 0);
        bannerView.setIndicatorGravity(BannerConfig.RIGHT);
        bannerView.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);

        listView.addHeaderView(view);
        superSwipeRefreshLayout =new SuperSwipeRefreshLayout(context);
        superSwipeRefreshLayout.setOnRefreshListener(this);
        superSwipeRefreshLayout.addView(listView);
        frameLayout.addView(superSwipeRefreshLayout);

        counterView = new FrameLayout(context);
        counterView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        counterView.setVisibility(View.INVISIBLE);
        frameLayout.addView(counterView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.LEFT | Gravity.BOTTOM));
        counterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StringBuilder builder = new StringBuilder();
                    int num = 0;
                    for (int a = 0; a < allSpans.size(); a++) {
                        ContactsController.Contact contact = allSpans.get(a).getContact();
                        if (builder.length() != 0) {
                            builder.append(';');
                        }
                        builder.append(contact.phones.get(0));
                        if (a == 0 && allSpans.size() == 1) {
                            num = contact.imported;
                        }
                    }
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + builder.toString()));
                    intent.putExtra("sms_body", ContactsController.getInstance().getInviteText(num));
                    getParentActivity().startActivityForResult(intent, 500);
                    MediaController.getInstance().startSmsObserver();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                finishFragment();
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        counterView.addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        counterTextView = new TextView(context);
        counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        counterTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        counterTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        counterTextView.setGravity(Gravity.CENTER);
        counterTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10), 0xffffffff));
        counterTextView.setMinWidth(AndroidUtilities.dp(20));
        counterTextView.setPadding(AndroidUtilities.dp(6), 0, AndroidUtilities.dp(6), AndroidUtilities.dp(1));
        linearLayout.addView(counterTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 20, Gravity.CENTER_VERTICAL, 0, 0, 10, 0));

        updateHint();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.contactsImported) {
            fetchContacts();
        }
    }
    private void updateHint() {
        if (selectedContacts.isEmpty()) {
            counterView.setVisibility(View.INVISIBLE);
        } else {
            counterView.setVisibility(View.VISIBLE);
            counterTextView.setText(String.format("%d", selectedContacts.size()));
        }
    }
    private void fetchContacts() {
        phoneBookContacts = new ArrayList<>(ContactsController.getInstance().phoneBookContacts);
        Collections.sort(phoneBookContacts, new Comparator<ContactsController.Contact>() {
            @Override
            public int compare(ContactsController.Contact o1, ContactsController.Contact o2) {
                if (o1.imported > o2.imported) {
                    return -1;
                } else if (o1.imported < o2.imported) {
                    return 1;
                }
                return 0;
            }
        });
    }
    @Override
    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate сellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public void didSetColor(int color) {
                int count = listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    if (child instanceof InviteUserCell) {
                        ((InviteUserCell) child).update(0);
                    }
                }
            }
        };

        return new ThemeDescription[]{
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector),

                new ThemeDescription(scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_windowBackgroundWhite),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundViolet),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundGreen),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundCyan),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundPink),
                new ThemeDescription(counterView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteBackground),
                new ThemeDescription(counterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteBackground),
        };
    }
    static final int REFRESH_COMPLETE = 0X1112;
    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    getImages(GetProxyAddress.getDefProxyAddress());
                    superSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    //加群代码
    public void autojoingroup(final String queay, Context context,final int i,final TextView textView,final ImageView imageView){
        final SearchAdapterHelper searchAdapterHelper=new SearchAdapterHelper();
        if(i==1){
            long start  = System.currentTimeMillis();
            searchAdapterHelper.queryServerSearch1(queay, true, true, true, true, 0, false,FindActivity.this);
            long end = System.currentTimeMillis();
            Log.d("TAG","function last time is "+ (end - start));
            //needHideProgress();
        }
        //废弃
//        if(i==2){
//            AndroidUtilities.runOnUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    searchAdapterHelper.queryServerSearch3(queay, true, true, true, true, 0, false,FindActivity.this,textView,imageView);
//                }
//            });
//        }

    }
    public void getImages(final String url){
        superSwipeRefreshLayout.setRefreshing(true);
        OkGo.<String>get(url+"user/faxian")
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        superSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject data = new JSONObject();
                            JSONObject jsonObject = new JSONObject(response.body());
                            int status = (int) jsonObject.get("status");
                            if (status == 200) {
                                data = (JSONObject) jsonObject.get("data");
                                JSONArray banner = data.getJSONArray("banner");
                                JSONArray center = data.getJSONArray("qunzu");
                                JSONArray zhong = data.getJSONArray("zhon");
                                Log.i("TAG", "center" + center.toString());
                                List<JSONObject> BannerList = new ArrayList<>();
                                ArrayList<String> BannerImage = new ArrayList<>();
                                ArrayList<String> BannerTitle = new ArrayList<>();
                                final ArrayList<String> BannerUrl = new ArrayList<>();
                                for (int i = 0; i < banner.length(); i++) {
                                    JSONObject banneritem = banner.getJSONObject(i);
                                    BannerList.add(banneritem);
                                    BannerImage.add(banneritem.getString("image"));
                                    BannerTitle.add(banneritem.getString("title"));
                                    BannerUrl.add(banneritem.getString("link"));
                                }
                                ArrayList<String> CenterImage = new ArrayList<>();
                                ArrayList<String> CenterTitle = new ArrayList<>();
                                ArrayList<String> CenterUrl = new ArrayList<>();
                                ArrayList<String> Centerpeople = new ArrayList<>();
                                ArrayList<String> CenterGroup = new ArrayList<>();
                                for (int i = 0; i < center.length(); i++) {
                                    JSONObject centeritem = center.getJSONObject(i);
                                    CenterImage.add(centeritem.getString("img"));
                                    CenterTitle.add(centeritem.getString("title"));
                                    CenterUrl.add(centeritem.getString("introduction"));
                                    Centerpeople.add(centeritem.getString("ren_num"));
                                    CenterGroup.add(centeritem.getString("qun"));
                                }
                                ArrayList<String> ZhongImage = new ArrayList<>();
                                ArrayList<String> ZhongTitle = new ArrayList<>();
                                ArrayList<String> ZhongUrl = new ArrayList<>();
                                for (int i = 0; i < zhong.length(); i++) {
                                    JSONObject zhongitem = zhong.getJSONObject(i);
                                    ZhongImage.add(zhongitem.getString("img"));
                                    ZhongTitle.add(zhongitem.getString("title"));
                                    ZhongUrl.add(zhongitem.getString("url"));
                                }
                                bannerView.setBannerTitles(BannerTitle);
                                bannerView.setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    public void OnBannerClick(int position) {
                                        presentFragment(new WebviewActivity(BannerUrl.get(position), "发现", "发现", "发现", null));
                                    }
                                });
                                bannerView
                                        .setImages(BannerImage)
                                        .setImageLoader(new MyGlideImageLoader())
                                        .start();
                                adapter.initData(CenterTitle, CenterImage, CenterUrl, Centerpeople, CenterGroup, ZhongImage, ZhongUrl, ZhongTitle);

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        superSwipeRefreshLayout.setRefreshing(false);
                        //替换代理
                        getImages(GetProxyAddress.getBaseProxyAddress());
                    }
                });
    }



    public class MyGlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //用fresco加载图片简单用法，记得要写下面的createImageView方法
            Uri uri = Uri.parse((String) path);
            imageView.setImageURI(uri);
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
            return simpleDraweeView;
        }
    }
    public class InviteAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> titlelist = new ArrayList<>();
        private ArrayList<String> imglist = new ArrayList<>();
        private ArrayList<String> urllist = new ArrayList<>();
        private ArrayList<String> peoplelist = new ArrayList<>();
        private ArrayList<String> grouplist = new ArrayList<>();
        private ArrayList<String> zhongimg = new ArrayList<>();
        private ArrayList<String> zhongurl = new ArrayList<>();
        private ArrayList<String> zhongtitle = new ArrayList<>();

        public InviteAdapter(Context ctx) {
            context = ctx;
        }

        public void initData(final ArrayList<String> mtitle, final ArrayList<String> mimg, final ArrayList<String> murl, final ArrayList<String> mpeople, final ArrayList<String> mgrouplist, final ArrayList<String> mzhongimg, final ArrayList<String> mzhongurl, final ArrayList<String> mzhongtitle) {
            titlelist = mtitle;
            imglist = mimg;
            urllist = murl;
            peoplelist = mpeople;
            grouplist = mgrouplist;
            zhongimg = mzhongimg;
            zhongurl = mzhongurl;
            zhongtitle = mzhongtitle;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return imglist.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            SimpleDraweeView simpleDraweeView;
            TextView textView;
            final TextView contenxt;
            final TextView people;
            final ImageView joinBtn;
            final LinearLayout linearLayout;

            view = LayoutInflater.from(context).inflate(R.layout.findactivity_item, viewGroup, false);
            simpleDraweeView = view.findViewById(R.id.findimage);
            textView = view.findViewById(R.id.findtext);
            contenxt = view.findViewById(R.id.findcontenxt);
            people = view.findViewById(R.id.text2);
            joinBtn = view.findViewById(R.id.findjoin);

            Uri uri = Uri.parse(imglist.get(i));
            simpleDraweeView.setImageURI(uri);
            textView.setText(titlelist.get(i));
            contenxt.setText(urllist.get(i));
            //真实人数和状态
            //autojoingroup(grouplist.get(i),context,2,people,joinBtn);
            people.setText(peoplelist.get(i)+" 人 ");
            joinBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.foundimg2));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    needShowProgress();
                    autojoingroup(grouplist.get(i), context,1,null,null);
                }
            });
            //header

            MoreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("zhongimg",zhongimg);
                    bundle.putStringArrayList("zhongurl",zhongurl);
                    bundle.putStringArrayList("zhongtitle",zhongtitle);
                    presentFragment(new MoreActivity(bundle));

                }
            });
            title1.setText(zhongtitle.get(0));
            Uri uri1 = Uri.parse((zhongimg.get(0)));
            image1.setImageURI(uri1);
            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presentFragment(new WebviewActivity(zhongurl.get(0), "发现", "发现", "发现", null));
                }
            });
            title2.setText(zhongtitle.get(1));
            Uri uri2 = Uri.parse((zhongimg.get(1)));
            image2.setImageURI(uri2);
            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presentFragment(new WebviewActivity(zhongurl.get(1), "发现", "发现", "发现", null));
                }
            });
            title3.setText(zhongtitle.get(2));
            Uri uri3 = Uri.parse((zhongimg.get(2)));
            image3.setImageURI(uri3);
            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presentFragment(new WebviewActivity(zhongurl.get(2), "发现", "发现", "发现", null));
                }
            });
            title4.setText(zhongtitle.get(3));
            Uri uri4 = Uri.parse((zhongimg.get(3)));
            image4.setImageURI(uri4);
            image4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presentFragment(new WebviewActivity(zhongurl.get(3), "发现", "发现", "发现", null));
                }
            });
            return view;
        }
    }

    @Override
    public void onFragmentDestroy() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            progressDialog = null;
        }
        super.onFragmentDestroy();
    }

    @Override
    public void onPause() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            progressDialog = null;
        }
        super.onPause();
    }
    private void needShowProgress() {
        if (getParentActivity() == null || getParentActivity().isFinishing() || progressDialog != null) {
            return;
        }
        progressDialog = new AlertDialog(getParentActivity(), 1);
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void needHideProgress() {
        if (progressDialog == null) {
            return;
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        progressDialog = null;
    }
}