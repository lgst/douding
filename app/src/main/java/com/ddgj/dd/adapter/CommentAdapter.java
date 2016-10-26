package com.ddgj.dd.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.ChatActivity;
import com.ddgj.dd.bean.Comment;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;
import com.hyphenate.easeui.EaseConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/26.
 */

public class CommentAdapter extends BaseAdapter {
    private List<Comment> comments = new ArrayList<Comment>();

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Comment comment = comments.get(position);
        Glide.with(parent.getContext())
                .load(NetWorkInterface.HOST + "/" + comment.getHead_picture())
                .into(vh.mIcon);
        vh.mContent.setText(comment.getC_content());
        vh.mNickName.setText(comment.getNickname());
        vh.mTiem.setText(StringUtils.getDate(comment.getComment_time()));
        if (UserHelper.getInstance().getUser().getAccount().equals(comment.getAccount())) {
            vh.mSendMessage.setVisibility(View.INVISIBLE);
        } else {
            vh.mSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.getContext().startActivity(
                            new Intent(parent.getContext(), ChatActivity.class)
                            .putExtra(EaseConstant.EXTRA_USER_ID, comment.getAccount()));
                }
            });
        }
        return convertView;
    }

    public static class ViewHolder {
        public View rootView;
        public CircleImageView mIcon;
        public TextView mNickName;
        public TextView mContent;
        public TextView mTiem;
        public ImageView mSendMessage;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mIcon = (CircleImageView) rootView.findViewById(R.id.icon);
            this.mNickName = (TextView) rootView.findViewById(R.id.nick_name);
            this.mContent = (TextView) rootView.findViewById(R.id.content_text);
            this.mTiem = (TextView) rootView.findViewById(R.id.tiem);
            this.mSendMessage = (ImageView) rootView.findViewById(R.id.send_message);
        }

    }
}
