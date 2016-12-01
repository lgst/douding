package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CircleImageView;
import com.ddgj.dd.view.CustomGridView;
import com.ddgj.dd.view.CustomListView;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class MineOrderDetailActivity extends BaseActivity implements NetWorkInterface, AdapterView.OnItemClickListener, View.OnClickListener {

    private Toolbar mToolbar;
    private CircleImageView mCivUserIcon;
    private AppCompatTextView mTvUserName;
    private AppCompatTextView mTvOrderName;
    private AppCompatTextView mTvOrderPrice;
    private AppCompatTextView mTvOrderStatus;
    private AppCompatTextView mTvDate;
    private AppCompatTextView mTvAddress;
    private AppCompatTextView mTvTitle;
    private TextView mTvAmount;
    private TextView mTvTime;
    private TextView mTvDetail;
    private CustomGridView mImages;
    private CustomListView mLvUser;
    private Order mOrder;
    private ArrayList<String> mImagesList;
    private List<User> mUsers = new ArrayList<User>();
    //    0为等待接单 1为已接单 2为成功 3为失败 4服务方申请合作 5服务方申请验收
    private static final String[] STATUS = {"等待接单", "工作中", "交易成功", "交易失败", "待确认合作", "待验收"};
    private int[] colors = new int[]{R.color.waiting,
            R.color.working,
            R.color.finished,
            R.color.grey,
            R.color.colorPrimary,
            R.color.blue};
    private Button mAgreeCheck;
    private Button mRegectCheck;
    private LinearLayout mCheckLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_order_detail);
        initView();
        initData();
    }

    private void initData() {
        OkHttpUtils.get().url(GET_ORDER_USERS + "?made_id=" + getIntent().getStringExtra("id")).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "我的订制详情onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        mOrder = new Gson().fromJson(jo.getString("data"), Order.class);
                        mImagesList = new ArrayList<String>();
                        if (mOrder == null)
                            return;
                        if (mOrder.getMade_picture() != null) {
                            String[] strings = mOrder.getMade_picture().split(",");
                            for (String s : strings) {
                                if (!s.equals("null")) {
                                    mImagesList.add(HOST + "/" + s);
                                }
                            }
                        }
                        if (jo.getJSONArray("sum") == null) {
                            setData();
                            return;
                        }
                        JSONArray ja = jo.getJSONArray("sum");
                        for (int i = 0; i < ja.length(); i++) {
                            if (new JSONObject(ja.getString(i)).getString("account_type").equals("0")) {
                                PersonalUser user = new Gson().fromJson(ja.getString(i), PersonalUser.class);
                                mUsers.add(user);
                            } else {
                                EnterpriseUser user = new Gson().fromJson(ja.getString(i), EnterpriseUser.class);
                                mUsers.add(user);
                            }
                        }
                        setData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    setData();
                }
            }
        });
    }

    private void setData() {
        mTvUserName.setText(mOrder.getAccount());
        mTvOrderName.setText(mOrder.getMade_title());
        mTvOrderPrice.setText("￥" + mOrder.getMade_price());
        int status = Integer.parseInt(mOrder.getMade_state());
        mTvOrderStatus.setText(STATUS[status]);
        mTvOrderStatus.setBackgroundColor(getResources().getColor(colors[status]));
        mTvDate.setText(mOrder.getMade_time());
        mTvAddress.setText(mOrder.getMade_u_address());
        mTvAmount.setText(mOrder.getMade_amount());
        mTvTime.setText(mOrder.getMade_cycle());
        mTvDetail.setText(mOrder.getMade_describe());
        Glide.with(getApplicationContext()).load(HOST + "/" + mOrder.getHead_picture()).error(R.mipmap.ic_account_circle_grey600_24dp).into(mCivUserIcon);
        if (mImagesList != null)
            mImages.setAdapter(new ImageGVAdapter());
        mLvUser.setAdapter(new LvAdapter());
        if (mOrder.getMade_state().equals("5"))
            mCheckLl.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("订制详情");
        mToolbar.setTitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCivUserIcon = (CircleImageView) findViewById(R.id.civ_user_icon);
        mTvUserName = (AppCompatTextView) findViewById(R.id.tv_user_name);
        mTvOrderName = (AppCompatTextView) findViewById(R.id.tv_order_name);
        mTvOrderPrice = (AppCompatTextView) findViewById(R.id.tv_order_price);
        mTvOrderStatus = (AppCompatTextView) findViewById(R.id.tv_order_status);
        mTvTitle = (AppCompatTextView) findViewById(R.id.tv_title);
        mTvDate = (AppCompatTextView) findViewById(R.id.tv_date);
        mTvAddress = (AppCompatTextView) findViewById(R.id.tv_address);
        mTvAmount = (TextView) findViewById(R.id.tv_amount);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);
        mImages = (CustomGridView) findViewById(R.id.images);
        mImages.setOnItemClickListener(this);
        mLvUser = (CustomListView) findViewById(R.id.lv_user);
        mAgreeCheck = (Button) findViewById(R.id.agree_check);
        mAgreeCheck.setOnClickListener(this);
        mRegectCheck = (Button) findViewById(R.id.regect_check);
        mRegectCheck.setOnClickListener(this);
        mCheckLl = (LinearLayout) findViewById(R.id.check_ll);
        mCheckLl.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, PreviewImageActivity.class)
                .putExtra(PreviewImageActivity.PARAMAS_POSITION, position)
                .putStringArrayListExtra(PreviewImageActivity.PARAMAS_IMAGES, mImagesList));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_check:
                showDialog("您真的要确认验收吗？",0);
                break;
            case R.id.regect_check:
                showDialog("您真的要拒绝验收吗？",1);
                break;
        }
    }

    private void showDialog(String text, final int flag) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("提示")
                .setContentText(text)
                .setCancelText("取消")
                .setConfirmText("确认")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (flag == 0) {
                            agreeCheck();
                        }else
                            regectCheck();
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    private void regectCheck() {
        Map<String, String> params = new HashMap<>();
        params.put("made_id", mOrder.getMade_id());
        params.put("o_c_u_id", mOrder.getMade_o_u_id());
        Log.i(TAG, "madeid: "+mOrder.getMade_id());
        Log.i(TAG, "ocuid: "+mOrder.getMade_o_u_id());
        OkHttpUtils.post().url(NetWorkInterface.REGECT_CHECK).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", "拒绝验收出错：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", "拒绝验收：" + response);
                ResponseInfo resinfo = new Gson().fromJson(response, ResponseInfo.class);
                if (resinfo.getStatus() == 0) {
                    showToastLong("已拒绝验收！");
                    mCheckLl.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 同意验收
     */
    private void agreeCheck() {
        Map<String, String> params = new HashMap<>();
        params.put("made_id", mOrder.getMade_id());
        params.put("o_c_u_id", mOrder.getMade_o_u_id());
        OkHttpUtils.post().url(NetWorkInterface.AGREE_CHECK).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", "同意验收出错：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", "验收：" + response);
                ResponseInfo resinfo = new Gson().fromJson(response, ResponseInfo.class);
                if (resinfo.getStatus() == 0) {
                    showToastLong("验收成功！");
                    mCheckLl.setVisibility(View.GONE);
                }
            }
        });
    }

    class ImageGVAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mImagesList.size();
        }

        @Override
        public Object getItem(int position) {
            return mImagesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(MineOrderDetailActivity.this).load(mImagesList.get(position))
                    .into(iv);
            iv.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    DensityUtil.dip2px(MineOrderDetailActivity.this, 100)));
            return iv;
        }
    }

    class LvAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public Object getItem(int position) {
            return mUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private void cancel(int position) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("made_id", mOrder.getMade_id());
            params.put("o_c_u_id", mUsers.get(position).getAccount_id());
            OkHttpUtils.post().url(REGECT_ORDER).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e("lgst", "拒绝订单出错："+e.getMessage() );
                    showToastNotNetWork();
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.i("lgst", "拒绝订单：" + response);
                    try {
                        if (new JSONObject(response).getString("status").equals("0")) {
                            showToastLong("拒绝该厂家成功！");
                        } else {
                            showToastLong("出错！请稍后重试");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void confirm(int position) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("made_id", mOrder.getMade_id());
            params.put("o_c_u_id", mUsers.get(position).getAccount_id());
            OkHttpUtils.post().url(AGREE_ORDER).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e("lgst", "接受订单出错："+e.getMessage());
                    showToastNotNetWork();
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.i("lgst", "接受订单：" + response);
                    try {
                        if (new JSONObject(response).getString("status").equals("0")) {
                            showToastLong("选择厂家成功！");
                        } else {
                            showToastLong("出错！请稍后重试");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_mine_order_detail_user_lv, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            final User user = mUsers.get(position);
            vh.mBtnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreClick(v, user);
                }
            });
            String url = null;
            if (user instanceof PersonalUser) {
                vh.mTvName.setText(((PersonalUser) user).getUser_name());
                url = ((PersonalUser) user).getHead_picture();
            } else if (user instanceof EnterpriseUser) {
                vh.mTvName.setText(((EnterpriseUser) user).getFacilitator_name());
                url = ((EnterpriseUser) user).getFacilitator_head();
                vh.mTvContent.setText(((EnterpriseUser) user).getFacilitator_license());
            }
            Glide.with(MineOrderDetailActivity.this)
                    .load(HOST + "/" + url)
                    .into(vh.mCivIcon);
            switch (Integer.parseInt(mOrder.getMade_state())) {
                case 4://待确认合作显示按钮，接受和拒绝
                    vh.mBtnCancel.setVisibility(View.VISIBLE);
                    vh.mBtnConfirm.setVisibility(View.VISIBLE);
                    break;
                case 1://工作中状态
                    mTvTitle.setText("服务商");
                    break;
            }
            final ViewHolder finalVh = vh;
            vh.mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(MineOrderDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    cancel(position);
                                    finalVh.mBtnCancel.setVisibility(View.GONE);
                                    finalVh.mBtnConfirm.setVisibility(View.GONE);
                                    mUsers.remove(position);
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setCancelText("取消")
                            .setTitleText("提示")
                            .setContentText("确认后该厂家将被取消资格！")
                            .show();

                }
            });
            vh.mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(MineOrderDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    confirm(position);
                                    finalVh.mBtnConfirm.setVisibility(View.GONE);
                                    finalVh.mBtnCancel.setVisibility(View.GONE);
                                    User user = mUsers.get(position);
                                    mUsers.clear();
                                    mUsers.add(user);
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setCancelText("取消")
                            .setTitleText("提示")
                            .setContentText("确认后其他厂家将自动被取消资格！")
                            .show();

                }
            });
            return convertView;
        }

        private void moreClick(View v, final User user) {

            switch (v.getId()) {
                case R.id.btn_more:
                    PopupMenu popup = new PopupMenu(MineOrderDetailActivity.this, v);
                    popup.getMenuInflater().inflate(R.menu.pop_menu_custom_user, popup.getMenu());
//        通过反射开启图标显示
                    try {
                        Field field = popup.getClass().getDeclaredField("mPopup");
                        field.setAccessible(true);
                        MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popup);
                        mHelper.setForceShowIcon(true);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.send_message://发送消息
                                    startActivity(new Intent(MineOrderDetailActivity.this, ChatActivity.class)
                                            .putExtra(EaseConstant.EXTRA_USER_ID, user.getAccount()));
                                    break;
                                case R.id.call://打电话
                                    String phone = null;
                                    if (user instanceof PersonalUser) {
                                        phone = ((PersonalUser) user).getPhone_number();
                                    } else {
                                        phone = ((EnterpriseUser) user).getFacilitator_contact();
                                    }
                                    Intent intent = new Intent();
                                    intent.setData(Uri.parse("tel:" + phone));
                                    intent.setAction(Intent.ACTION_DIAL);
                                    startActivity(intent);
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show(); //showing popup menu
                    break;
                case R.id.confirm_button:
                    break;
            }
        }

        class ViewHolder {
            View rootView;
            CircleImageView mCivIcon;
            TextView mTvName;
            TextView mTvTime;
            TextView mTvContent;
            Button mBtnConfirm;
            Button mBtnCancel;
            ImageButton mBtnMore;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.mCivIcon = (CircleImageView) rootView.findViewById(R.id.civ_icon);
                this.mTvName = (TextView) rootView.findViewById(R.id.tv_name);
                this.mTvTime = (TextView) rootView.findViewById(R.id.tv_time);
                this.mTvContent = (TextView) rootView.findViewById(R.id.tv_content);
                this.mBtnConfirm = (Button) rootView.findViewById(R.id.btn_confirm);
                this.mBtnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
                this.mBtnMore = (ImageButton) rootView.findViewById(R.id.btn_more);
            }
        }
    }
}
