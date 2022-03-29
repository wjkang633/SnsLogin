package woojin.project.android.snslogin.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import woojin.project.android.snslogin.R;

import static woojin.project.android.snslogin.view.LoginActivity.authLoginModule;

public class LoginResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_result);

        String company = getIntent().getStringExtra("company");

        //PIN 설정
        findViewById(R.id.setting_pin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginResultActivity.this, FirstPinActivity.class));
            }
        });

        //로그아웃
        findViewById(R.id.logout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (company.equals(getString(R.string.facebook)) || company.equals(getString(R.string.google))) {
                    FirebaseAuth.getInstance().signOut();
                } else if (company.equals(getString(R.string.naver))) {
                    authLoginModule.logout(getApplicationContext());
                }

                startActivity(new Intent(LoginResultActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}