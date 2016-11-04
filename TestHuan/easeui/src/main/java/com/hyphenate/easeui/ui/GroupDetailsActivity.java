package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;


    private String groupId;
    private ProgressBar loadingPB;
    private Button exitBtn;
    private Button deleteBtn;
    private EMGroup group;
    private GridAdapter adapter;
    private ProgressDialog progressDialog;

    String st = "";

    private EaseSwitchButton switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupId = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        group = EMClient.getInstance().groupManager().getGroup(groupId);

        // we are not supposed to show the group if we don't find the group
        if(group == null){
            finish();
            return;
        }
        setContentView(R.layout.em_activity_group_details);

        st = getResources().getString(R.string.people);
        EaseExpandGridView userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);

        RelativeLayout changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        RelativeLayout rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);

        if (group.getOwner() == null || "".equals(group.getOwner())
                || !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            changeGroupNameLayout.setVisibility(View.GONE);
        }
        // show dismiss button if you are owner of group
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        GroupChangeListener groupChangeListener = new GroupChangeListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);

        ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount() + st);

        List<String> members = new ArrayList<>();
        members.addAll(group.getMembers());

        adapter = new GridAdapter(this, R.layout.em_grid, members);
        userGridview.setAdapter(adapter);

        // 保证每次进详情看到的都是最新的group
        updateGroup();

        // 设置OnTouchListener
        userGridview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.isInDeleteMode) {
                            adapter.isInDeleteMode = false;
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        changeGroupNameLayout.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);

        progressDialog = new ProgressDialog(GroupDetailsActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void updateGroup() {
        new Thread(new Runnable() {
        public void run() {
            try {
                EMClient.getInstance().groupManager().getGroupFromServer(groupId);

                runOnUiThread(new Runnable() {
                    public void run() {
                        ((TextView) findViewById(R.id.group_name))
                                .setText(group.getGroupName() + "(" + group.getAffiliationsCount() + ")");
                        loadingPB.setVisibility(View.INVISIBLE);
                        refreshMembers();
                        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
                            // 显示解散按钮
                            exitBtn.setVisibility(View.GONE);
                            deleteBtn.setVisibility(View.VISIBLE);
                        } else {
                            // 显示退出按钮
                            exitBtn.setVisibility(View.VISIBLE);
                            deleteBtn.setVisibility(View.GONE);
                        }

                        // update block
                        EMLog.d(TAG, "group msg is blocked:" + group.isMsgBlocked());
                        if (group.isMsgBlocked()) {
                            switchButton.openSwitch();
                        } else {
                            switchButton.closeSwitch();
                        }
                    }
                });

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        loadingPB.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }).start();
}

    private void refreshMembers() {
        adapter.clear();
        List<String> members = new ArrayList<>();
        members.addAll(group.getMembers());
        adapter.addAll(members);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.rl_change_group_name){

        }
        else if(id == R.id.rl_switch_block_groupmsg){
            toggleBlockGroup();
        }
    }

    private void toggleBlockGroup() {
        if(switchButton.isSwitchOpen()){
            EMLog.d(TAG, "change to unblock group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(getString(R.string.Is_unblock));
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.closeSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.remove_group_of, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }).start();

        } else {
            String st8 = getResources().getString(R.string.group_is_blocked);
            final String st9 = getResources().getString(R.string.group_of_shielding);
            EMLog.d(TAG, "change to block group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(st8);
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().blockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.openSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).start();
        }
    }

    public void back(View view){
        finish();
    }

    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {
        //startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);
        progressDialog.setMessage("exit");
        progressDialog.show();
        exitGroup();
    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
        //        startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
        //                REQUEST_CODE_EXIT_DELETE);
        progressDialog.setMessage("delete");
        progressDialog.show();
        deleteGroup();
    }

    /**
     * 退出群组
     */
    private void exitGroup() {
        String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().leaveGroup(groupId);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if(ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Exit_the_group_chat_failure) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 解散群组
     */
    private void deleteGroup() {
        final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().destroyGroup(groupId);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if(ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st5 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private static class ViewHolder{
        ImageView imageView;
        TextView textView;
        ImageView badgeDeleteView;
    }

    public class GridAdapter extends ArrayAdapter<String> {

        private int res;
        public boolean isInDeleteMode;
        private List<String> objects;

        public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            res = textViewResourceId;
            isInDeleteMode = false;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
                convertView.setTag(holder);
            }else{
                 holder = (ViewHolder) convertView.getTag();
            }
            final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
            // 最后一个item，减人按钮
            if (position == getCount() - 1) {
                holder.textView.setText("");
                // 设置成删除按钮
                holder.imageView.setImageResource(R.drawable.em_smiley_minus_btn);
                // 如果不是创建者或者没有相应权限，不提供加减人按钮
                if (!group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    convertView.setVisibility(android.view.View.INVISIBLE);
                }
                else { // 显示删除按钮
                    if (isInDeleteMode) {
                        // 正处于删除模式下，隐藏删除按钮
                        convertView.setVisibility(android.view.View.INVISIBLE);
                    }
                    else {
                        // 正常模式
                        convertView.setVisibility(android.view.View.VISIBLE);
                        convertView.findViewById(R.id.badge_delete).setVisibility(android.view.View.INVISIBLE);
                    }
                    final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
                    button.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, st10);
                            isInDeleteMode = true;
                            notifyDataSetChanged();
                        }
                    });
                }
            }
            else if (position == getCount() - 2) { // 添加群组成员按钮
                holder.textView.setText("");
                holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
                // 如果不是创建者或者没有相应权限
                if (!group.isAllowInvites() && !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    convertView.setVisibility(android.view.View.INVISIBLE);
                } else {
                    // 正处于删除模式下,隐藏添加按钮
                    if (isInDeleteMode) {
                        convertView.setVisibility(android.view.View.INVISIBLE);
                    } else {
                        convertView.setVisibility(android.view.View.VISIBLE);
                        convertView.findViewById(R.id.badge_delete).setVisibility(android.view.View.INVISIBLE);
                    }
                    final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                    //不要加人
                    button.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, st11);
                            // 进入选人页面
                            Toast.makeText(GroupDetailsActivity.this, "还是不现实的号", Toast.LENGTH_SHORT).show();
//                            startActivityForResult(
//                                    (new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId)),
//                                    REQUEST_CODE_ADD_USER);
                        }
                    });
                }
            }
            else { // 普通item，显示群组成员
                final String username = getItem(position);
                convertView.setVisibility(android.view.View.VISIBLE);
                button.setVisibility(android.view.View.VISIBLE);

                EaseUserUtils.setUserNick(username, holder.textView);
                EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
                if (isInDeleteMode) {
                    // 如果是删除模式下，显示减人图标
                    convertView.findViewById(R.id.badge_delete).setVisibility(android.view.View.VISIBLE);
                } else {
                    convertView.findViewById(R.id.badge_delete).setVisibility(android.view.View.INVISIBLE);
                }
                final String st12 = getResources().getString(R.string.not_delete_myself);
                final String st13 = getResources().getString(R.string.Are_removed);
                final String st14 = getResources().getString(R.string.Delete_failed);
                final String st15 = getResources().getString(R.string.confirm_the_members);
                button.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInDeleteMode) {
                            // 如果是删除自己，return
                            if (EMClient.getInstance().getCurrentUser().equals(username)) {
                                new EaseAlertDialog(GroupDetailsActivity.this, st12).show();
                                return;
                            }
                            if (!NetUtils.hasNetwork(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            EMLog.d("group", "remove user from group:" + username);
                            deleteMembersFromGroup(username);
                        }
                        else {
                            //TODO 添加用户详情
                            // 正常情况下点击user，可以进入用户详情或者聊天页面等等
//                            Intent intent = new Intent(GroupDetailsActivity.this, ChatActivity.class);
//                            intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
//                            startActivity(intent);
                        }
                    }

                    /**
                     * 删除群成员
                     *
                     * @param username
                     */
                    protected void deleteMembersFromGroup(final String username) {
                        final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
                        deleteDialog.setMessage(st13);
                        deleteDialog.setCanceledOnTouchOutside(false);
                        deleteDialog.show();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    // 删除被选中的成员
                                    EMClient.getInstance().groupManager().removeUserFromGroup(groupId, username);
                                    isInDeleteMode = false;
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            deleteDialog.dismiss();
                                            refreshMembers();
                                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "("
                                                    + group.getAffiliationsCount() + st);
                                        }
                                    });
                                } catch (final Exception e) {
                                    deleteDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), st14 + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }
                        }).start();
                    }
                });

                button.setOnLongClickListener(new android.view.View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        if(EMClient.getInstance().getCurrentUser().equals(username))
                            return true;
                        if (group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                            new EaseAlertDialog(GroupDetailsActivity.this, null, st15, null, new EaseAlertDialog.AlertDialogUser() {

                                @Override
                                public void onResult(boolean confirmed, Bundle bundle) {
                                    if(confirmed){
                                        //addUserToBlackList(username);
                                    }
                                }
                            }, true).show();

                        }
                        return false;
                    }
                });
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount() + 2;
        }
    }

    private class GroupChangeListener implements EMGroupChangeListener{
        @Override
        public void onInvitationReceived(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onApplicationReceived(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onApplicationAccept(String s, String s1, String s2) {

        }

        @Override
        public void onApplicationDeclined(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onInvitationAccpted(String s, String s1, String s2) {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    refreshMembers();
                }

            });
        }

        @Override
        public void onInvitationDeclined(String s, String s1, String s2) {

        }

        @Override
        public void onUserRemoved(String s, String s1) {
            finish();
        }

        @Override
        public void onGroupDestroy(String s, String s1) {
            finish();
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }
    }

}

