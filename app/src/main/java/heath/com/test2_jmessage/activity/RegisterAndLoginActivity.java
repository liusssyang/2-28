package heath.com.test2_jmessage.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.setting.RegisterActivity;
import heath.com.test2_jmessage.application.IMDebugApplication;
import heath.com.test2_jmessage.recycleView_item.personMsg;
import heath.com.test2_jmessage.utils.AndroidUtils;

import static heath.com.test2_jmessage.activity.TypeActivity.TAG;
import static heath.com.test2_jmessage.activity.TypeActivity.adapter;
import static heath.com.test2_jmessage.activity.TypeActivity.friendList;
import static heath.com.test2_jmessage.activity.TypeActivity.friendsIcon;
import static heath.com.test2_jmessage.activity.TypeActivity.personList;

//import cn.jmessage.common.logger.Logger;

/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 注册和登陆界面
 */
public class RegisterAndLoginActivity extends Activity {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PWD = "pwd";
    public static final int REQ_CODE_FOR_REGISTER = 1;
    public EditText mEd_userName;
    public EditText mEd_password;
    private Button mBt_login;
    private Button mBt_login_with_infos;
    private Button mBt_gotoRegister;
    private ProgressDialog mProgressDialog = null;
    private RadioGroup mRgType;
    private boolean isTestVisibility = false;
    private TextView pwdIsVisible;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AndroidUtils.checkPermission(this, REQUIRED_PERMISSIONS)) {
            try {
                AndroidUtils.requestPermission(this, REQUIRED_PERMISSIONS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        initView();
        initData();
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT >= 23 && grantResults[0] == PackageManager.PERMISSION_DENIED && !shouldShowRequestPermissionRationale(permissions[0])) {
            Toast.makeText(getApplicationContext(), "请在设置中打开存储权限", Toast.LENGTH_SHORT).show();
        }
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FOR_REGISTER && RESULT_OK == resultCode && data != null) {
            mEd_userName.setText(data.getStringExtra(KEY_USERNAME));
            mEd_password.setText(data.getStringExtra(KEY_PWD));
        }
    }

    /**
     * #################    应用入口,登陆或者是注册    #################
     */
    private void initData() {
        /**=================     获取个人信息不是null，说明已经登陆，无需再次登陆，则直接进入type界面    =================*/
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null) {

            ContactManager.getFriendList(new GetUserInfoListCallback() {
                @Override
                public void gotResult(int i, String s, List<UserInfo> list) {
                    friendsIcon=new Bitmap[list.size()];
                    friendList=new personMsg[list.size()];
                    if (i == 0) {
                        for (int j=0;j<list.size();j++) {
                            friendsIcon[j]= BitmapFactory.decodeFile(list.get(j).getAvatarFile().getPath());;
                            friendList[j]=new personMsg(list.get(j).getUserID(),friendsIcon[j],list.get(j).getUserName(),null,null,null,null);
                            personList.add(friendList[j]);
                            //friendList[j].setBitmap(TIcon);
                            adapter.notifyItemChanged(personList.size()-1);
                            adapter.notifyDataSetChanged();
                        }
                        if (list.size() == 0) {
                            Toast.makeText(getApplicationContext(), "暂无好友", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        Log.i("FriendContactManager", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                    }
                }
            });
            Intent intent = new Intent(RegisterAndLoginActivity.this, TypeActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "R: "+personList.size());


        }
        /**=================     调用注册接口    =================*/
        mBt_gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
            }
        });
        mBt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterAndLoginActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String userName = mEd_userName.getText().toString();
                String password = mEd_password.getText().toString();
                /**=================     调用SDk登陆接口    =================*/
                JMessageClient.login(userName, password, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String LoginDesc) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), TypeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                        }
                    }
                });
            }
        });

        mBt_login_with_infos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterAndLoginActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String userName = mEd_userName.getText().toString();
                String password = mEd_password.getText().toString();
                /**=================     调用SDk登陆接口    =================*/
                JMessageClient.login(userName, password, new RequestCallback<List<DeviceInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<DeviceInfo> result) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + responseMessage);
                            Intent intent = new Intent(getApplicationContext(), TypeActivity.class);
                            Gson gson = new Gson();
                            intent.putExtra("deviceInfos", gson.toJson(result));
                            startActivity(intent);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + responseMessage);
                        }
                    }
                });
            }
        });

        //供jmessage sdk测试使用，开发者无需关心。
        mRgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_public:
                        swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), false, false);
                        break;
                    case R.id.rb_test:
                        swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), true, false);
                        break;
                    case R.id.rb_qa:
                        swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), false, true);
                        break;
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_login);
        //permission();
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#00C4FF"));
        mEd_userName = (EditText) findViewById(R.id.ed_login_username);
        mEd_password = (EditText) findViewById(R.id.ed_login_password);
        mBt_login = (Button) findViewById(R.id.bt_login);
        mBt_login_with_infos = (Button) findViewById(R.id.bt_login_with_infos);
        mBt_gotoRegister = (Button) findViewById(R.id.bt_goto_regester);
        RelativeLayout re=findViewById(R.id.login_all);
        pwdIsVisible=findViewById(R.id.eye);
        pwdIsVisible.setText(" ");
        pwdIsVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwdIsVisible.getText().toString().equals(" ")) {
                    mEd_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEd_password.setSelection(mEd_password.getText().toString().length());
                    pwdIsVisible.setText("");
                    pwdIsVisible.setBackgroundResource(R.drawable.eye_close);
                }else{
                    pwdIsVisible.setText(" ");
                    mEd_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEd_password.setSelection(mEd_password.getText().toString().length());
                    pwdIsVisible.setBackgroundResource(R.drawable.eye_open);
                }
            }
        });
        addLayoutListener(re,mBt_login);
        mRgType = (RadioGroup) findViewById(R.id.rg_environment);;
        if (!isTestVisibility) {
            mRgType.setVisibility(View.GONE);
        } else {
            //供jmessage sdk测试使用，开发者无需关心。
            Boolean isTestEvn = invokeIsTestEvn();
            Boolean isQAEvn = invokeIsQAEvn();
            mRgType.check(R.id.rb_public);
            if (isTestEvn) {
                mRgType.check(R.id.rb_test);
            } else if (isQAEvn) {
                mRgType.check(R.id.rb_qa);
            }
        }
    }

    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appkey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (null != metaData) {
            appkey = metaData.getString("JPUSH_APPKEY");
            if (TextUtils.isEmpty(appkey)) {
                return null;
            } else if (appkey.length() != 24) {
                return null;
            }
            appkey = appkey.toLowerCase(Locale.getDefault());
        }
        return appkey;
    }

    public static Boolean invokeIsTestEvn() {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("isTestEnvironment");
            Object result = method.invoke(null);
            return (Boolean) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Boolean invokeIsQAEvn() {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("isQAEnvironment");
            Object result = method.invoke(null);
            return (Boolean) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void swapEnvironment(Context context, boolean isTest, boolean isQA) {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("swapPublicEnvironment", Context.class);
            if (isTest) {
                method = cls.getDeclaredMethod("swapTestEnvironment", Context.class);
            } else if (isQA) {
                method = cls.getDeclaredMethod("swapQAEnvironment", Context.class);
            }
            method.invoke(null, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void permission(){
        AppOpsManager appOpt = (AppOpsManager) IMDebugApplication.getContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpt.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), IMDebugApplication.getContext().getPackageName());
        boolean isGranted=mode == AppOpsManager.MODE_ALLOWED;
        if(!isGranted)
            IMDebugApplication.getContext().startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

    }
    public static void addLayoutListener(final View main,final View scroll){
        main.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect=new Rect();
                        main.getWindowVisibleDisplayFrame(rect);
                        int mainInvisibleHeight=main.getRootView().getHeight()-rect.bottom;
                        if (mainInvisibleHeight>100){
                            int[] location=new int[2];
                            scroll.getLocationInWindow(location);
                            int srollHeight=
                                    (location[1]+scroll.getHeight())-rect.bottom;
                            main.scrollTo(0,srollHeight);
                        }else{
                            main.scrollTo(0,0);
                        }

                    }
                }
        );
    }
}
