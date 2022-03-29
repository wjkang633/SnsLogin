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

import woojin.project.android.snslogin.view.FirstPinActivity;
import woojin.project.android.snslogin.R;

public class TwoButtonDialog extends DialogFragment {

    private String message;
    private boolean isGoBackMainActivity;

    public TwoButtonDialog(String message, boolean isGoBackMainActivity) {
        this.message = message;
        this.isGoBackMainActivity = isGoBackMainActivity;
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
        cont.setText(message);

        //'예'
        view.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGoBackMainActivity) {
                    dismiss();
                    startActivity(new Intent(getActivity(), FirstPinActivity.class));
                } else {
                    getActivity().finish();
                }
            }
        });

        //'아니오'
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}