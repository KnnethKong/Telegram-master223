package org.telegram.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.SuperSwipeRefreshLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/9.
 */

public class MoreActivity extends BaseFragment {

    private MoreAdapter moreAdapter;
    private ArrayList<String> zhongimg;
    private ArrayList<String> zhongurl;
    private ArrayList<String> zhongtitle;
    private FrameLayout counterView;
    private RecyclerView recyclerView;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    moreAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private SuperSwipeRefreshLayout superSwipeRefreshLayout;
    private LinearLayout scrollView;

    public MoreActivity(Bundle bundle) {
        try {
            zhongimg = bundle.getStringArrayList("zhongimg");
            zhongurl = bundle.getStringArrayList("zhongurl");
            zhongtitle = bundle.getStringArrayList("zhongtitle");
            if (moreAdapter != null) {
                handler.sendEmptyMessage(0);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public View createView(Context context) {
        allSpans.clear();
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionModeTopColor(Color.parseColor("#FF00ff"));

        actionBar.setTitle("交易所");

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
                //  counterView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));

                //  counterView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));

                scrollView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(200, MeasureSpec.AT_MOST));
                superSwipeRefreshLayout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height - scrollView.getMeasuredHeight(), MeasureSpec.EXACTLY));

            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                scrollView.layout(0, 0, scrollView.getMeasuredWidth(), scrollView.getMeasuredHeight());
                superSwipeRefreshLayout.layout(0, scrollView.getMeasuredHeight(), recyclerView.getMeasuredWidth(), scrollView.getMeasuredHeight() + recyclerView.getMeasuredHeight());

            }

        };
        ViewGroup frameLayout = (ViewGroup) fragmentView;
        scrollView = new LinearLayout(context) {
            @Override
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += AndroidUtilities.dp(20);
                rectangle.bottom += AndroidUtilities.dp(50);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        scrollView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(scrollView);
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        moreAdapter = new MoreAdapter(context, zhongimg, zhongtitle, zhongurl);
        recyclerView.setAdapter(moreAdapter);
        superSwipeRefreshLayout = new SuperSwipeRefreshLayout(context);
        superSwipeRefreshLayout.addView(recyclerView);
        superSwipeRefreshLayout.setEnabled(false);
        superSwipeRefreshLayout.setFocusable(false);
        superSwipeRefreshLayout.setRefreshing(false);

        frameLayout.addView(superSwipeRefreshLayout);
        return fragmentView;
    }
    public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.ViewHolder> {
        private Context context;
        private ArrayList<String> zhongimg;
        private ArrayList<String> zhongtitle;
        private ArrayList<String> zhongurl;


        public MoreAdapter(Context context, ArrayList<String> zhongimg, ArrayList<String> zhongtitle, ArrayList<String> zhongurl) {
            this.context = context;
            this.zhongimg = zhongimg;
            this.zhongtitle = zhongtitle;
            this.zhongurl = zhongurl;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item_more, parent, false);
            return new ViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            try {
                Uri parse = Uri.parse((zhongimg.get(position)));
                holder.image.setImageURI(parse);
                holder.title.setText(zhongtitle.get(position));
            } catch (Exception e) {

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presentFragment(new WebviewActivity(zhongurl.get(position), "发现", "发现", "发现", null));
                }
            });
        }

        @Override
        public int getItemCount() {
            return zhongimg.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView title;
            private SimpleDraweeView image;

            public ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.titletit);
                image = itemView.findViewById(R.id.simpleimg);
            }
        }

    }
}
