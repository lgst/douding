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
import com.ddgj.dd.bean.Patent;
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
public class PatentPLVAdapter extends BaseAdapter {
    private Activity act;
    private List<Patent> patents;
    private String[] types;

    public PatentPLVAdapter(Activity act, List<Patent> patents) {
        this.act = act;
        this.patents = patents;
        types = act.getResources().getStringArray(R.array.patent_type);
    }

    @Override
    public int getCount() {
        return patents.size();
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = act.getLayoutInflater().inflate(R.layout.item_patent, null);
            vh = new ViewHolder();
            vh.userIcon = (ImageView) convertView.findViewById(R.id.user_icon);
            vh.title = (TextView) convertView.findViewById(R.id.title);
            vh.userName = (TextView) convertView.findViewById(R.id.user_name);
            vh.type = (TextView) convertView.findViewById(R.id.type);
            vh.content = (TextView) convertView.findViewById(R.id.content_text);
            vh.img1 = (ImageView) convertView.findViewById(R.id.img1);
            vh.img2 = (ImageView) convertView.findViewById(R.id.img2);
            vh.img3 = (ImageView) convertView.findViewById(R.id.img3);
            vh.approve = (TextView) convertView.findViewById(R.id.approve);
            vh.browse = (TextView) convertView.findViewById(R.id.browse);
            vh.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Patent patent = patents.get(position);

        String userIcon = patent.getHead_picture();
        if(userIcon==null)
            userIcon = UserHelper.getInstance().getUser().getHead_picture();
        Glide.with(act)
                .load(NetWorkInterface.HOST + "/" + userIcon)
                .thumbnail(0.5f)
                .into(vh.userIcon);
        vh.title.setText(patent.getPatent_name());
        vh.userName.setText(patent.getAccount());
        vh.type.setText(types[Integer.parseInt(patent.getPatent_type())]);
        vh.content.setText(patent.getPatent_details());
        setImages(patent, vh);
        vh.approve.setText(patent.getP_praise_amount());
        vh.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = patent.getPatent_id();
                supportClick(v, id);
            }
        });
        vh.browse.setText(patent.getP_browse_amount());
        vh.date.setText(StringUtils.getDate(patent.getP_creation_time()));
        return convertView;
    }
    public void supportClick(final View v, String id) {
        OkHttpUtils.get().url(NetWorkInterface.PATENT_SUPPORT + "?patent_id=" + id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", "onError: 专利点赞失败" + e.getMessage());
                Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.network_is_not_connection), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String response, int id) {
//                Log.i("lgst", "专利点赞结果：onResponse: " + response);
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

    /**折*/
    private void setImages(Patent patent, ViewHolder vh) {
        if (patent.getPatent_picture() == null) {
            return;
        }
        if (!patent.getPatent_picture().equals("")) {
            String[] imgs = patent.getPatent_picture().split("\\,");
            for (int i = 0; i < imgs.length; i++) {
                switch (i) {
                    case 0:
                        Glide.with(act)
                                .load(NetWorkInterface.HOST + "/" + imgs[i])
                                .thumbnail(0.1f)
                                .into(vh.img1);
                        break;
                    case 1:
                        Glide.with(act)
                                .load(NetWorkInterface.HOST + "/" + imgs[i])
                                .thumbnail(0.1f)
                                .into(vh.img2);
                        break;
                    case 2:
                        Glide.with(act)
                                .load(NetWorkInterface.HOST + "/" + imgs[i])
                                .thumbnail(0.1f)
                                .into(vh.img3);
                        break;
                }
            }
        }
    }

    class ViewHolder {
        public ImageView userIcon;
        public TextView title;
        public TextView userName;
        public TextView type;
        public TextView content;
        public ImageView img1;
        public ImageView img2;
        public ImageView img3;
        public TextView approve;
        public TextView browse;
        public TextView date;
    }
}
