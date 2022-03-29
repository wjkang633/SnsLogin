package woojin.project.android.snslogin.dialog;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.concurrent.Executor;

import woojin.project.android.snslogin.view.NotificationActivity;
import woojin.project.android.snslogin.R;

public class BioAuthDialog extends DialogFragment {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_bio_auth, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setCanceledOnTouchOutside(false);

        //'사용하지 않음'
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                startActivity(new Intent(getActivity(), NotificationActivity.class));
                getActivity().finish();
            }
        });

        //'사용하기'
        view.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                executor = ContextCompat.getMainExecutor(getContext());
                biometricPrompt = new BiometricPrompt(getActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getContext(), "생체인증 기능 사용에 실패했습니다.\n 디바이스 생체 인증 설정 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        dismiss();
                        startActivity(new Intent(getActivity(), NotificationActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getContext(), "생체인증 기능 사용에 실패했습니다.\n 디바이스 생체 인증 설정 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("생체 인증")
                        .setNegativeButtonText("취소")
                        .build();

                try {
                    biometricPrompt.authenticate(promptInfo);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "생체인증 기능 사용에 실패했습니다.\n 디바이스 생체 인증 설정 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void popUpDialog(String message) {
        OneButtonDialog dialog = new OneButtonDialog(message);
        dialog.show(getParentFragmentManager(), "dialog");
    }
}