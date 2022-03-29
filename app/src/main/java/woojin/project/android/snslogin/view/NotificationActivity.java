package woojin.project.android.snslogin.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import woojin.project.android.snslogin.R;
import woojin.project.android.snslogin.dialog.NotificationDialog;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog("푸시 알림을 통해 LG Wallet에서의 서비스 알림을 받으시겠습니까?");
            }
        });
    }

    public void popUpDialog(String message) {
        NotificationDialog dialog = new NotificationDialog(message);
        dialog.show(getSupportFragmentManager(), "dialog");
    }
}