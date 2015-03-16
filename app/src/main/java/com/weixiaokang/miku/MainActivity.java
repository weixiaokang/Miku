package com.weixiaokang.miku;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.weixiaokang.miku.adapter.ChatMessageAdapter;
import com.weixiaokang.miku.model.ChatMessage;
import com.weixiaokang.miku.model.ChatMessage.Type;
import com.weixiaokang.miku.utils.HttpUtils;

public class MainActivity extends ActionBarActivity {

    /**
     * 发送按钮
     */
    private Button mSendBtn;
    /**
     * 展示消息的listview
     */
    private ListView mChatView;
    /**
     * 文本域
     */
    private EditText mMsg;
    /**
     * 存储聊天消息
     */
    private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    /**
     * 适配器
     */
    private ChatMessageAdapter mAdapter;
    /**
     * 发送的信息
     */
    private String msg;
    /**
     * 更新UI
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ChatMessage from = (ChatMessage) msg.obj;
            mDatas.add(from);
            mAdapter.notifyDataSetChanged();
            mChatView.setSelection(mDatas.size() - 1);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chatting);

        initView();
        initListener();

        mAdapter = new ChatMessageAdapter(this, mDatas);
        mChatView.setAdapter(mAdapter);
    }

    /**
     * 初始化列表的第一行
     */
    private void initView() {
        mSendBtn = (Button) findViewById(R.id.id_chat_send);
        mChatView = (ListView) findViewById(R.id.id_chat_listView);
        mMsg = (EditText) findViewById(R.id.id_chat_msg);
        mDatas.add(new ChatMessage(Type.INPUT, "你好~，我是miku~"));
    }

    /**
     * 初始化按钮监听
     */
    private void initListener() {
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = mMsg.getText().toString();
                String reg = "[\\S]";
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(msg);
                if (!matcher.find()) {
                    Toast.makeText(MainActivity.this, "您还没有填写信息呢...", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
                to.setDate(new Date());
                mDatas.add(to);

                mAdapter.notifyDataSetChanged();
                mChatView.setSelection(mDatas.size() - 1);

                mMsg.setText("");

                /**
                 * 发送信息后如果键盘是开着的则关闭
                 */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 得到InputMethodManager的实例
                if (imm.isActive()) {
                    // 如果开启
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
                }

                /**
                 * 开启另外的线程处理网络请求
                 */
                new Thread()
                {
                    public void run()
                    {
                        ChatMessage from = null;
                        try
                        {
                            from = HttpUtils.sendMsg(msg);
                        } catch (Exception e)
                        {
                            from = new ChatMessage(Type.INPUT, "服务器挂了呢...");
                        }
                        Message message = Message.obtain();
                        message.obj = from;
                        mHandler.sendMessage(message);
                    };
                }.start();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
