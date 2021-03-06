package heath.com.test2_jmessage.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.RegisterAndLoginActivity;
import heath.com.test2_jmessage.activity.TypeActivity;

/**
 * Created by ${chenyn} on 16/4/17.
 *
 * @desc :同意或拒绝好友申请
 */
public class ShowFriendReasonActivity extends Activity {
    private static final String TAG = ShowFriendReasonActivity.class.getSimpleName();
    public static final String EXTRA_TYPE = "event_type";
    private TextView mTv_showAddFriendInfo;
    private Button mAccept_invitation;
    private Button mDeclined_invitation;
    private EditText mEt_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        TextView showFriend_toolbarName=findViewById(R.id.showFriend_toolbarName);
        showFriend_toolbarName.setText("新朋友");
        TextView showFriend_back=findViewById(R.id.showFriend_back);
        showFriend_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), TypeActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });
        TextView showFriend_toolbarAdd=findViewById(R.id.showFriend_toolbarAdd);
        showFriend_toolbarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(), FriendContactManager.class);
                startActivity(intent);
            }
        });
        TextView showFriend_person=findViewById(R.id.showFriend_person);
        TextView showFriend_message=findViewById(R.id.showFriend_message);
        TextView tiptext2=findViewById(R.id.tiptext2);
        TextView Appkeytext2=findViewById(R.id.Appkeytext2);
        final SharedPreferences pref=getSharedPreferences("friends",0);
        final Intent intent = getIntent();
       // ContactNotifyEvent.Type type = ContactNotifyEvent.Type.valueOf(intent.getStringExtra(EXTRA_TYPE));
        ContactNotifyEvent.Type type =ContactNotifyEvent.Type.valueOf(pref.getString(EXTRA_TYPE,null));
                switch (type) {
            case invite_received:
                showFriend_person.setText(pref.getString("username",null));
                tiptext2.setText(pref.getString("reason",null));
                Appkeytext2.setText(pref.getString("appkey",null));
                //mTv_showAddFriendInfo.append(pref.getString("invite_received",null));
                break;
            case invite_accepted:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("invite_accepted"));
                break;
            case invite_declined:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("invite_declined"));
                break;
            case contact_deleted:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("contact_deleted"));
                break;
            case contact_updated_by_dev_api:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("contact_updated_by_dev_api"));
                break;
            default:
                break;

        }
        //同意好友添加邀请
        mAccept_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactManager.acceptInvitation(pref.getString("username",null), pref.getString("appkey",null), new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "ContactManager.acceptInvitation" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });

        //拒绝好友添加邀请
        mDeclined_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = mEt_reason.getText().toString();
                ContactManager.declineInvitation(pref.getString("username",null), pref.getString("appkey",null), reason, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "拒绝成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "拒绝失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "ContactManager.declineInvitation" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_show_friend_reason);
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#00C4FF"));
        mTv_showAddFriendInfo = (TextView) findViewById(R.id.tv_show_add_friend_info);
        mAccept_invitation = (Button) findViewById(R.id.accept_invitation);
        mDeclined_invitation = (Button) findViewById(R.id.declined_invitation);
        mEt_reason = (EditText) findViewById(R.id.et_reason);
        LinearLayout linearLayout=findViewById(R.id.showFriend_mainLayout);
        RegisterAndLoginActivity.addLayoutListener(linearLayout,mDeclined_invitation);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
