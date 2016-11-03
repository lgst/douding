package com.ddgj.dd.util.net;

import java.util.List;

/**
 * Created by Administrator on 2016/11/3.
 */

public interface DataCallback<T> {
    void Failed(Exception e);
    void Success(List<T> datas);
}
