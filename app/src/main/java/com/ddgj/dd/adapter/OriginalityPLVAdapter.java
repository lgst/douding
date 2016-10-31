package com.ddgj.dd.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/13.
 */
public class OriginalityPLVAdapter extends BaseAdapter {
    private Activity act;
    private List<Originality> originalitys;
    private String[] types;

    public OriginalityPLVAdapter(Activity act, List<Originality> originalitys) {
        this.act = act;
        this.originalitys = originalitys;
        types = act.getResources().getStringArray(R.array.originalityTypes);
    }

    @Override
    public int getCount() {
        return originalitys.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = act.getLayoutInflater().inflate(R.layout.item_originality, null);
            vh = new ViewHolder();
            vh.userIcon = (ImageView) convertView.findViewById(R.id.user_icon);
            vh.title = (TextView) convertView.findViewById(R.id.title);
            vh.userName = (TextView) convertView.findViewById(R.id.user_name);
            vh.type = (TextView) convertView.findViewById(R.id.type);
            vh.content = (TextView) convertView.findViewById(R.id.content_text);
            vh.img1 = (ImageView) convertView.findViewById(R.id.img1);
            vh.approve = (TextView) convertView.findViewById(R.id.approve);
            vh.browse = (TextView) convertView.findViewById(R.id.browse);
            vh.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Originality originality = originalitys.get(position);
        //用户图像
        String userIcon = originality.getHead_picture();
        if (userIcon == null)
            userIcon = UserHelper.getInstance().getUser().getHead_picture();
        Glide.with(act)
                .load(NetWorkInterface.HOST + "/" + userIcon)
                .thumbnail(0.5f)
                .into(vh.userIcon);
//        标题
        vh.title.setText(originality.getOriginality_name());
//        用户名
        vh.userName.setText(originality.getAccount());
//        类型
        if (originality.getOriginality_type() != null)
            vh.type.setText(types[Integer.parseInt(originality.getOriginality_type())]);
//        介绍
        vh.content.setText(originality.getOriginality_details());
        //tu
        showImages(act, originality, vh);
        vh.approve.setText(originality.getO_praise_amount());
        vh.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = originality.getOriginality_id();
                supportClick(v, id);
            }
        });
        vh.browse.setText(originality.getO_browse_amount());
        vh.date.setText(StringUtils.getDate(originality.getO_creation_time()));
        return convertView;
    }

    public void supportClick(final View v, String id) {
        OkHttpUtils.get().url(NetWorkInterface.ORIGINALITY_SUPPORT + "?originality_id=" + id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", "onError: 创意点赞失败" + e.getMessage());
                Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.network_is_not_connection), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", "创意点赞结果：onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        Toast.makeText(v.getContext(), "点赞成功！", Toast.LENGTH_SHORT).show();
                        TextView tv = (TextView) v;
                        tv.setText(String.valueOf(Integer.parseInt(tv.getText().toString())+1));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showImages(Activity act, Originality originality, ViewHolder vh) {
        if (originality.getO_picture() != null) {
            String[] imgs = originality.getO_picture().split(",");
            Glide.with(act)
                    .load(NetWorkInterface.HOST + "/" + imgs[0])
                    .thumbnail(0.1f)
                    .into(vh.img1);
        }
    }

    class ViewHolder {
        public ImageView userIcon;
        public TextView title;
        public TextView userName;
        public TextView type;
        public TextView content;
        public ImageView img1;
        public TextView approve;
        public TextView browse;
        public TextView date;
    }
}
