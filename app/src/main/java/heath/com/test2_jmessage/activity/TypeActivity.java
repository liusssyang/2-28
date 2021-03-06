package heath.com.test2_jmessage.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.ChatRoomNotificationEvent;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.GroupAnnouncementChangedEvent;
import cn.jpush.im.android.api.event.GroupApprovalEvent;
import cn.jpush.im.android.api.event.GroupApprovalRefuseEvent;
import cn.jpush.im.android.api.event.GroupApprovedNotificationEvent;
import cn.jpush.im.android.api.event.GroupBlackListChangedEvent;
import cn.jpush.im.android.api.event.GroupMemNicknameChangedEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MyInfoUpdatedEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.chatroom.ChatRoomActivity;
import heath.com.test2_jmessage.activity.conversation.ConversationActivity;
import heath.com.test2_jmessage.activity.createmessage.CreateMessageActivity;
import heath.com.test2_jmessage.activity.createmessage.ShowTransCommandActivity;
import heath.com.test2_jmessage.activity.friend.FriendContactManager;
import heath.com.test2_jmessage.activity.friend.ShowFriendReasonActivity;
import heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity;
import heath.com.test2_jmessage.activity.groupinfo.ShowGroupApprovalActivity;
import heath.com.test2_jmessage.activity.groupinfo.ShowMemNicknameChangedActivity;
import heath.com.test2_jmessage.activity.jmrtc.JMRTCActivity;
import heath.com.test2_jmessage.activity.setting.SettingMainActivity;
import heath.com.test2_jmessage.activity.setting.ShowLogoutReasonActivity;
import heath.com.test2_jmessage.activity.setting.UpdateUserAvatar;
import heath.com.test2_jmessage.activity.setting.UpdateUserInfoActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowAnnouncementChangedActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowChatRoomNotificationActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowGroupBlcakListChangedActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowMyInfoUpdateActivity;
import heath.com.test2_jmessage.adapter.personAdapter;
import heath.com.test2_jmessage.application.IMDebugApplication;
import heath.com.test2_jmessage.recycleView_item.personMsg;

/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 各个接口的的引导界面
 */
public class TypeActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "ly13172";
    public static final String LOGOUT_REASON = "logout_reason";
    public static String tv_username,tv_appkey;
    private TextView mTv_showOfflineMsg;
    private TextView tv_refreshEvent;
    private TextView tv_deviceInfo,newFriends;
    private TextView tv_header,addNew;
    private TextView menu,headusername,headappkey,headvision,signature;
    public static final String DOWNLOAD_INFO = "download_info";
    public static final String INFO_UPDATE = "info_update";
    public static final String TRANS_COMMAND_SENDER = "trans_command_sender";
    public static final String TRANS_COMMAND_TARGET = "trans_command_target";
    public static final String TRANS_COMMAND_TYPE = "trans_command_type";
    public static final String TRANS_COMMAND_CMD = "trans_command_cmd";
    private RecyclerView recyclerView;
    public static personAdapter adapter;
    public static personMsg friendList[];
    public static List<personMsg> personList=new ArrayList<>();
    private DrawerLayout drawerLayout;
    private IntentFilter intentFilter;
    private Localreceiver localRceiver;
    private LocalBroadcastManager localBroadcastManager;
    private CircleImageView circleImageView;
    public static long myUserId;
    public static Bitmap myIcon;
    public static Bitmap friendsIcon[]=null;
    //private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
        initView();

    }
    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_type);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setActionBar(toolbar);
        ActionBar actionBar=getActionBar();
        tv_header = (TextView) findViewById(R.id.tv_header);
        drawerLayout=findViewById(R.id.sigText_drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.headlayout);
        headusername=navHeaderView.findViewById(R.id.head_username);
        circleImageView=navHeaderView.findViewById(R.id.picture);
        headappkey=navHeaderView.findViewById(R.id.head_appkey);
        headvision=navHeaderView.findViewById(R.id.head_vision);
        signature=navHeaderView.findViewById(R.id.signature);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent();
                if (item.toString().equals("设置")) {
                    intent.setClass(getApplicationContext(), SettingMainActivity.class);
                    startActivityForResult(intent, 0);
                }
                if (item.toString().equals("退出")) {
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    if (myInfo != null) {
                        JMessageClient.logout();
                        Toast.makeText(getApplicationContext(), "登出成功", Toast.LENGTH_SHORT).show();
                        intent.setClass(TypeActivity.this, RegisterAndLoginActivity.class);
                        setResult(8);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(TypeActivity.this, "登出失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (item.toString().equals("历史纪录")) {
                    SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("history"+myUserId,0).edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"cleared",Toast.LENGTH_LONG).show();
                }
                    return true;
            }
        });
        Resources resource=getBaseContext().getResources();
        ColorStateList csl=resource.getColorStateList(R.color.selector);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);
        navigationView.getMenu().getItem(0).setChecked(true);
        RelativeLayout re=navHeaderView.findViewById(R.id.head);
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(IMDebugApplication.getContext(), UpdateUserInfoActivity.class);
                IMDebugApplication.getContext().startActivity(intent);
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IMDebugApplication.getContext(), UpdateUserAvatar.class);
                IMDebugApplication.getContext().startActivity(intent);
            }
        });
        findViewById(R.id.bt_about_setting).setOnClickListener(this);
        findViewById(R.id.bt_create_message).setOnClickListener(this);
        findViewById(R.id.bt_group_info).setOnClickListener(this);
        findViewById(R.id.bt_conversation).setOnClickListener(this);
        findViewById(R.id.bt_friend).setOnClickListener(this);
        findViewById(R.id.bt_chatroom).setOnClickListener(this);
        findViewById(R.id.bt_jmrtc).setOnClickListener(this);
        menu=findViewById(R.id.type_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        mTv_showOfflineMsg = (TextView) findViewById(R.id.tv_showOfflineMsg);
        tv_refreshEvent = (TextView) findViewById(R.id.tv_refreshEvent);
        tv_deviceInfo = (TextView) findViewById(R.id.tv_deviceInfo);
        recyclerView=findViewById(R.id.type_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new personAdapter(personList);
        recyclerView.setAdapter(adapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("message");
        localRceiver =new Localreceiver(personList,recyclerView,adapter);
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localRceiver,intentFilter);
        addNew=findViewById(R.id.addNew);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(), FriendContactManager.class);
                startActivity(intent);
            }
        });
        newFriends=findViewById(R.id.newFriends);
        newFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(), ShowFriendReasonActivity.class);
                startActivity(intent);
            }
        });
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref2=this.getSharedPreferences("backdata",0);
        String simpleMessage=pref2.getString("simplemessage","");
        String time=pref2.getString("time","");
        Log.d(TAG, "T"+personList.size());
        //for (int i=0;i<personList.size())
        //h1.setTime(time);
        //h1.setSimpleMessage(simpleMessage);
        Intent intent = getIntent();
        Gson gson = new Gson();
        List<DeviceInfo> deviceInfos = gson.fromJson(intent.getStringExtra("deviceInfos"), new TypeToken<List<DeviceInfo>>() {}.getType());
        if (deviceInfos != null) {
            for (DeviceInfo deviceInfo : deviceInfos) {
                tv_deviceInfo.append("设备登陆记录:\n");
                tv_deviceInfo.append("设备ID: " + deviceInfo.getDeviceID() + " 平台：" + deviceInfo.getPlatformType()
                        + " 上次登陆时间:" + deviceInfo.getLastLoginTime() + "登陆状态:" + deviceInfo.isLogin() + "在线状态:" + deviceInfo.getOnlineStatus()
                        + " flag:" + deviceInfo.getFlag());
            }
        }
    }
    protected void onStart() {
        super.onStart();

        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            myUserId=info.getUserID();
            headappkey.setText("Appkey："+info.getAppKey());
            headusername.setText(info.getUserName());
            signature.setText(info.getSignature());
            headvision.setText("Vision："+JMessageClient.getSdkVersionString());
            info.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    Log.d("Hresult", i+"\n"+s);
                    if (s.equals("Success")){
                        myIcon=bitmap;
                        circleImageView.setImageBitmap(myIcon);
                    }else myIcon= BitmapFactory.decodeResource(getResources(),R.drawable.icon_left_default);
                }
            });
        }

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_about_setting:
                intent.setClass(getApplicationContext(), SettingMainActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.bt_create_message:
                intent.setClass(getApplicationContext(), CreateMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_group_info:
                intent.setClass(getApplicationContext(), GroupInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_conversation:
                intent.setClass(getApplicationContext(), ConversationActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_friend:
                intent.setClass(getApplicationContext(), FriendContactManager.class);
                startActivity(intent);
                break;
            case R.id.bt_chatroom:
                intent.setClass(getApplicationContext(), ChatRoomActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_jmrtc:
                intent.setClass(getApplicationContext(), JMRTCActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 8) {
            mTv_showOfflineMsg.setText("");
            tv_refreshEvent.setText("");
        }
    }
    public void onEvent(ContactNotifyEvent event) {
        String reason = event.getReason();
        String fromUsername = event.getFromUsername();
        String appkey = event.getfromUserAppKey();
        String s=event.getCreateTime()+"";
        SharedPreferences.Editor editor=getApplicationContext().
                getSharedPreferences("friends",0).edit();
        editor.putString(ShowFriendReasonActivity.EXTRA_TYPE, event.getType().toString());
        Intent intent = new Intent(getApplicationContext(), ShowFriendReasonActivity.class);
        intent.putExtra(ShowFriendReasonActivity.EXTRA_TYPE, event.getType().toString());
        switch (event.getType()) {
            case invite_received://收到好友邀请
                editor.putString("invite_received", "fromUsername = " + fromUsername + "\nfromUserAppKey" + appkey + "\nreason = " + reason);
                editor.putString("username", fromUsername);
                editor.putString("appkey", appkey);
                editor.putString("reason",reason);
                editor.apply();
                intent.putExtra("invite_received", "fromUsername = " + fromUsername + "\nfromUserAppKey" + appkey + "\nreason = " + reason);
                intent.putExtra("username", fromUsername);
                intent.putExtra("appkey", appkey);
                //startActivity(intent);
                break;
            case invite_accepted://对方接收了你的好友邀请
                intent.putExtra("invite_accepted", "对方接受了你的好友邀请");
                startActivity(intent);
                break;
            case invite_declined://对方拒绝了你的好友邀请
                intent.putExtra("invite_declined", "对方拒绝了你的好友邀请\n拒绝原因:" + event.getReason());
                startActivity(intent);
                break;
            case contact_deleted://对方将你从好友中删除
                intent.putExtra("contact_deleted", "对方将你从好友中删除");
                startActivity(intent);
                break;
            case contact_updated_by_dev_api://好友关系更新，由api管理员操作引起
                intent.putExtra("contact_updated_by_dev_api", "好友关系被管理员更新");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(getApplicationContext(), ShowLogoutReasonActivity.class);
        intent.putExtra(LOGOUT_REASON, "reason = " + reason + "\n" + "logout user name = " + myInfo.getUserName());
        startActivity(intent);
    }
    public void onEventMainThread(OfflineMessageEvent event) {
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        List<Integer> offlineMsgIdList = new ArrayList<Integer>();
        if (conversation != null && newMessageList != null) {
            for (Message msg : newMessageList) {
                offlineMsgIdList.add(msg.getId());
            }
            mTv_showOfflineMsg.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
            mTv_showOfflineMsg.append("会话类型 = " + conversation.getType() + "\n消息ID = " + offlineMsgIdList + "\n\n");
        } else {
            mTv_showOfflineMsg.setText("conversation is null or new message list is null");
        }
    }
    public void onEventMainThread(ConversationRefreshEvent event) {
        Conversation conversation = event.getConversation();
        ConversationRefreshEvent.Reason reason = event.getReason();
        if (conversation != null) {
            tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
            tv_refreshEvent.append("事件发生的原因 : " + reason + "\n");
        } else {
            tv_refreshEvent.setText("conversation is null");
        }
    }
    public void onEvent(GroupMemNicknameChangedEvent event) {
        new ShowMemChangeTask(getApplicationContext()).execute(event);
    }
    public void onEvent(GroupAnnouncementChangedEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("群组ID:").append(event.getGroupID()).append("\n\n");
        for (GroupAnnouncementChangedEvent.ChangeEntity entity : event.getChangeEntities()) {
            builder.append("类型:").append(entity.getType().toString()).append("\n");
            builder.append("发起者(username):").append(entity.getFromUserInfo().getUserName()).append("\n");
            builder.append("内容:").append(entity.getAnnouncement().toJson()).append("\n");
            builder.append("时间:").append(entity.getCtime()).append("\n\n");
        }
        Intent intent = new Intent(getApplicationContext(), ShowAnnouncementChangedActivity.class);
        intent.putExtra(ShowAnnouncementChangedActivity.SHOW_ANNOUNCEMENT_CHANGED, builder.toString());
        startActivity(intent);
    }
    public void onEvent(GroupBlackListChangedEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("群组ID:").append(event.getGroupID()).append("\n\n");
        for (GroupBlackListChangedEvent.ChangeEntity entity : event.getChangeEntities()) {
            builder.append("类型:").append(entity.getType().toString()).append("\n");
            builder.append("操作者(username):").append(entity.getOperator().getUserName()).append("\n");
            builder.append("被操作的用户(username):\n");
            for (UserInfo userInfo : entity.getUserInfos()) {
                builder.append(userInfo.getUserName()).append(" ");
            }
            builder.append("\n");
            builder.append("时间:").append(entity.getCtime()).append("\n\n");
        }
        Intent intent = new Intent(getApplicationContext(), ShowGroupBlcakListChangedActivity.class);
        intent.putExtra(ShowGroupBlcakListChangedActivity.SHOW_GROUP_BLACK_LIST_CHANGED, builder.toString());
        startActivity(intent);
    }
    public void onEventBackgroundThread(ChatRoomNotificationEvent event) {
        new ShowChatRoomNotificationTask(getApplicationContext(), event).run();
    }
    private static class ShowChatRoomNotificationTask {
        private WeakReference<Context> contextWeakReference;
        ChatRoomNotificationEvent event;
        ShowChatRoomNotificationTask(Context context, ChatRoomNotificationEvent event) {
            this.contextWeakReference = new WeakReference<Context>(context);
            this.event = event;
        }
        void run() {
            final StringBuilder builder = new StringBuilder();
            builder.append("事件id:").append(event.getEventID()).append("\n");
            builder.append("聊天室id:").append(event.getRoomID()).append("\n");
            builder.append("事件类型:").append(event.getType()).append("\n");
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            event.getOperator(new GetUserInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                    builder.append("事件发起者:");
                    if (0 == responseCode) {
                        if (info == null) {
                            builder.append("api操作");
                        } else {
                            builder.append(info.getUserName());
                        }
                    } else {
                        builder.append("获取失败");
                    }
                    countDownLatch.countDown();
                }
            });
            event.getTargetUserInfoList(new GetUserInfoListCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                    builder.append("目标用户:\n");
                    if (0 == responseCode) {
                        for (UserInfo userInfo : userInfoList) {
                            builder.append(userInfo.getUserName()).append("\n");
                        }
                    } else {
                        builder.append("获取失败");
                    }
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            builder.append("事件发生时间:" + event.getCtime());
            Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent(context.getApplicationContext(), ShowChatRoomNotificationActivity.class);
                intent.putExtra(ShowChatRoomNotificationActivity.SHOW_CHAT_ROOM_NOTIFICATION, builder.toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
    public void onEvent(MyInfoUpdatedEvent event) {
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(TypeActivity.this, ShowMyInfoUpdateActivity.class);
        intent.putExtra(INFO_UPDATE, myInfo.getUserName());
        startActivity(intent);
    }
    public void onEvent(final CommandNotificationEvent event) {
        event.getSenderUserInfo(new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    final String sender = info.getUserName();
                    event.getTargetInfo(new CommandNotificationEvent.GetTargetInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, Object targetInfo, CommandNotificationEvent.Type type) {
                            if (0 == responseCode) {
                                String target = "";
                                if (type == CommandNotificationEvent.Type.single) {
                                    target = ((UserInfo) targetInfo).getUserName();
                                } else if (type == CommandNotificationEvent.Type.group) {
                                    target += ((GroupInfo) targetInfo).getGroupID();
                                }
                                Intent intent = new Intent(getApplicationContext(), ShowTransCommandActivity.class);
                                intent.putExtra(TRANS_COMMAND_SENDER, sender);
                                intent.putExtra(TRANS_COMMAND_TARGET, target);
                                intent.putExtra(TRANS_COMMAND_TYPE, event.getType().toString());
                                intent.putExtra(TRANS_COMMAND_CMD, event.getMsg());
                                startActivity(intent);
                            } else {
                                Log.w(TAG, "CommandNotificationEvent getSenderUserInfo failed. " + "code = " +
                                        responseCode + " desc = " + responseMessage);
                            }
                        }
                    });
                } else {
                    Log.w(TAG, "CommandNotificationEvent getTargetInfo failed. " + "code = " +
                            responseCode + " desc = " + responseMessage);
                }
            }
        });
    }
    public void onEvent(GroupApprovalEvent event) {
        Intent intent = new Intent(getApplicationContext(), ShowGroupApprovalActivity.class);
        Gson gson = new Gson();
        intent.putExtra("GroupApprovalEvent", gson.toJson(event));
        intent.putExtra(ShowGroupApprovalActivity.EXTRA_EVENT_TYPE, ShowGroupApprovalActivity.TYPE_APPROVAL);
        startActivity(intent);
    }
    public void onEvent(final GroupApprovalRefuseEvent event) {
        final Intent intent = new Intent(getApplicationContext(), ShowGroupApprovalActivity.class);
        event.getFromUserInfo(new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    final String fromUsername = info.getUserName();
                    final String fromAppKey = info.getAppKey();
                    event.getToUserInfoList(new GetUserInfoListCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                            if (0 == responseCode) {
                                String toUsername = userInfoList.get(0).getUserName();
                                String toAppKey = userInfoList.get(0).getAppKey();
                                intent.putExtra("notification", "入群审批拒绝通知" + "\n群组gid:" + event.getGid()
                                        + "\n群主username:" + fromUsername
                                        + "\n群主appKey:" + fromAppKey
                                        + "\n被拒绝者username:" + toUsername
                                        + "\n被拒绝者appKey:" + toAppKey
                                        + "\n拒绝理由:" + event.getReason());
                                intent.putExtra(ShowGroupApprovalActivity.EXTRA_EVENT_TYPE, ShowGroupApprovalActivity.TYPE_APPROVAL_REFUSE);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }
    public void onEventMainThread(GroupApprovedNotificationEvent event) {
        tv_refreshEvent.append("\n收到入群审批已审批事件通知.对应审批事件id: " + event.getApprovalEventID());
    }
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        Conversation conv = event.getConversation();
        tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "\n收到MessageReceiptStatusChangeEvent事件,会话对象是%s\n", conv.getTargetId()));
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : event.getMessageReceiptMetas()) {
            tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE,
                    "回执数有更新的消息serverMsgID：%d\n这条消息当前还未发送已读回执的人数：%d\n", meta.getServerMsgId(), meta.getUnReceiptCnt()));
        }

    }
    private static class ShowMemChangeTask extends AsyncTask<GroupMemNicknameChangedEvent, Integer, String> {
        private WeakReference<Context> contextWeakReference;

        ShowMemChangeTask(Context context) {
            this.contextWeakReference = new WeakReference<Context>(context);
        }
        @Override
        protected String doInBackground(GroupMemNicknameChangedEvent... groupMemNicknameChangedEvents) {
            final StringBuilder builder = new StringBuilder();
            if (groupMemNicknameChangedEvents != null && groupMemNicknameChangedEvents.length > 0) {
                GroupMemNicknameChangedEvent event = groupMemNicknameChangedEvents[0];
                builder.append("群组id:" + event.getGroupID() + "\n");
                final CountDownLatch countDownLatch = new CountDownLatch(event.getChangeEntities().size());
                for (final GroupMemNicknameChangedEvent.ChangeEntity changeEntity : event.getChangeEntities()) {
                    changeEntity.getFromUserInfo(new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                            String fromUsername = info != null ? info.getUserName() : "";
                            builder.append("修改昵称者：" + fromUsername + "\n");
                            changeEntity.getToUserInfoList(new GetUserInfoListCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                                    List<String> useNames = new ArrayList<>();
                                    if (userInfoList != null) {
                                        for (UserInfo userInfo : userInfoList) {
                                            useNames.add(userInfo.getUserName());
                                        }
                                    }
                                    builder.append("被修改昵称者: " + useNames + "\n");
                                    builder.append("修改后的昵称：" + changeEntity.getNickname() + "\n");
                                    builder.append("修改时间：" + changeEntity.getCtime() + "\n\n");
                                    countDownLatch.countDown();
                                }
                            });
                        }
                    });
                }
                try {
                    countDownLatch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent();
                intent.setClass(context, ShowMemNicknameChangedActivity.class);
                intent.putExtra(ShowMemNicknameChangedActivity.SHOW_MEM_NICKNAME_CHANGED, s);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
        localBroadcastManager.unregisterReceiver(localRceiver);
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }
    class  Localreceiver extends BroadcastReceiver {
        private List<personMsg> msgList;
        private RecyclerView msgRecyclerView;
        private personAdapter adapter;

        public Localreceiver(List<personMsg> msgList,RecyclerView msgRecyclerView,personAdapter adapter){
            this.msgList=msgList;
            this.msgRecyclerView=msgRecyclerView;
            this.adapter=adapter;
        }
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("key")!=null) {
                msgList.get(0).setSimpleMessage(intent.getStringExtra("key"));
                msgList.get(0).setTime(intent.getStringExtra("time"));
            }
            if (intent.getStringExtra("SysMessage")!=null){
                msgList.get(0).setSimpleMessage("[System]");
            }
            if (intent.getByteArrayExtra("image")!=null){
                msgList.get(0).setSimpleMessage("[图片]");
            }
            adapter.notifyDataSetChanged();
        }

    }
    private class MyTask extends AsyncTask<List, Integer, String> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
            //text.setText("加载中");
            // 执行前显示提示
        }


        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected String doInBackground(List... params) {


            return null;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Integer... progresses) {

            //progressBar.setProgress(progresses[0]);
            //text.setText("loading..." + progresses[0] + "%");

        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
            //text.setText("加载完毕");
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态
        @Override
        protected void onCancelled() {

            //text.setText("已取消");
            //progressBar.setProgress(0);

        }
    }

}
