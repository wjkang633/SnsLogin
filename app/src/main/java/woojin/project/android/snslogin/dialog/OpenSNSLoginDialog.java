package woojin.project.android.snslogin.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.facebook.login.LoginManager;

import java.util.Arrays;

import woojin.project.android.snslogin.R;

import static woojin.project.android.snslogin.view.LoginActivity.authLoginHandler;
import static woojin.project.android.snslogin.view.LoginActivity.authLoginModule;

public class OpenSNSLoginDialog extends DialogFragment {

    private String message;

    private Intent signInIntent;
    private int REQUEST_CODE;

    public OpenSNSLoginDialog(String message) {
        this.message = message;
    }

    public OpenSNSLoginDialog(String message, Intent signInIntent, int REQUEST_CODE) {
        this.message = message;
        this.signInIntent = signInIntent;
        this.REQUEST_CODE = REQUEST_CODE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_two_btn, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setCanceledOnTouchOutside(false);

        TextView cont = view.findViewById(R.id.content_txt1);
        cont.setText("'Wallet'에서\n" + "'" + this.message + "'" + "을(를)\n 열려고 합니다.");

        //'열기'
        view.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //페이스북 로그인
                if (message.equals("페이스북")) {
                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                } else if (message.equals("구글")) {
                    getActivity().startActivityForResult(signInIntent, REQUEST_CODE);
                } else if (message.equals("네이버")) {
                    authLoginModule.startOauthLoginActivity(getActivity(), authLoginHandler);
                }

                dismiss();
            }
        });

        //'취소'
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
