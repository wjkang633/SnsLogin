package woojin.project.android.snslogin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import woojin.project.android.snslogin.R;
import woojin.project.android.snslogin.view.LoginResultActivity;

public class NotificationDialog extends DialogFragment {

    private String message;

    public NotificationDialog(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_notification, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setCanceledOnTouchOutside(false);

        TextView cont = view.findViewById(R.id.content_txt1);
        cont.setText(message);

        //'허용 안함'
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //알림 비활성화
                //알림 state 저장하는 것 까지만
                savePushNotiState(false);

                //메인으로
                dismiss();
                startLoginResultActivity(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
            }
        });

        //'허용'
        view.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //알림 활성화
                //알림 state 저장하는 것 까지만
                savePushNotiState(true);

                //메인으로
                dismiss();
                startLoginResultActivity(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
            }
        });

    }

    private void startLoginResultActivity(int flags) {
        Intent intent = new Intent(getActivity(), LoginResultActivity.class);
        intent.setFlags(flags);
        startActivity(intent);
    }

    private void savePushNotiState(boolean state) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("notification_state", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", state);
        editor.commit();
    }
}