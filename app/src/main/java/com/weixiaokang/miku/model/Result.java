package com.weixiaokang.miku.model;

/**
 * Created by Administrator on 2015/3/15.
 */
public class Result {
    private int code;
    private String text;

    public Result()
    {
    }

    public Result(int resultCode, String msg)
    {
        this.code = resultCode;
        this.text = msg;
    }

    public Result(int resultCode)
    {
        this.code = resultCode;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
