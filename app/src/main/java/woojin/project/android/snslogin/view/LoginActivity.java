package woojin.project.android.snslogin.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;

import woojin.project.android.snslogin.R;
import woojin.project.android.snslogin.dialog.OpenSNSLoginDialog;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    private GoogleSignInClient googleSignInClient;

    public static OAuthLogin authLoginModule;
    public static OAuthLoginHandler authLoginHandler;

    private FirebaseAuth mAuth;

    private int GOOGLE_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //'계정을 잊어버리셨나요?' 텍스트 밑줄
        ((TextView) findViewById(R.id.find_account_btn)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        //'페이스북으로 시작하기'

        findViewById(R.id.facebook_login_btn).setOnClickListener(loginBtnListener);
        findViewById(R.id.google_login_btn).setOnClickListener(loginBtnListener);
        findViewById(R.id.naver_login_btn).setOnClickListener(loginBtnListener);

        mAuth = FirebaseAuth.getInstance();

        //페이스북 로그인 설정
        callbackManager = CallbackManager.Factory.create();

        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startResultActivity(getString(R.string.facebook));

                        saveCompanyInfo(getString(R.string.facebook));

                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "로그인이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

        //구글 로그인 설정
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //네이버 로그인 설정
        authLoginModule = OAuthLogin.getInstance();
        authLoginModule.init(
                this
                , getString(R.string.naver_client_id)
                , getString(R.string.naver_client_pw)
                , getString(R.string.app_name)
        );

        authLoginHandler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    startResultActivity(getString(R.string.naver));

                    saveCompanyInfo(getString(R.string.naver));
                } else {
                    Log.e("우진", authLoginModule.getLastErrorCode(getApplicationContext()).getCode() + "\n" + authLoginModule.getLastErrorDesc(getApplicationContext()));

                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("account_company", MODE_PRIVATE);
        String company = sharedPreferences.getString("company", null);

        if (company != null) {
            //파이어베이스 로그인 인증 상태 확인(페이스북, 구글)
            if (mAuth.getCurrentUser() != null ||
                    //네이버 로그인 인증 상태 확인
                    authLoginModule.getState(this) == OAuthLoginState.OK) {
                Log.e("우진", "로그인 상태 유지, 제공 업체: " + company);

                startResultActivity(company);
            } else {
                Log.e("우진", "로그인 상태 아님");

                //로그아웃 후 재진입 시, 기존 로그인 업체 정보만 띄우기
                if (company.equals(getString(R.string.facebook))) {
                    findViewById(R.id.google_login_btn).setVisibility(View.GONE);
                    findViewById(R.id.naver_login_btn).setVisibility(View.GONE);
                } else if (company.equals(getString(R.string.google))) {
                    findViewById(R.id.facebook_login_btn).setVisibility(View.GONE);
                    findViewById(R.id.naver_login_btn).setVisibility(View.GONE);
                } else if (company.equals(getString(R.string.naver))) {
                    findViewById(R.id.facebook_login_btn).setVisibility(View.GONE);
                    findViewById(R.id.google_login_btn).setVisibility(View.GONE);
                }
            }
        }
    }

    View.OnClickListener loginBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.facebook_login_btn:
                    popUpDialog(getString(R.string.facebook));
                    break;
                case R.id.google_login_btn:
                    popUpDialog(getString(R.string.google), googleSignInClient.getSignInIntent(), GOOGLE_SIGN_IN);
                    break;
                case R.id.naver_login_btn:
                    popUpDialog(getString(R.string.naver));
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.e("우진", "onActivityResult " + requestCode + " , " + resultCode);

        //페이스북 로그인
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //구글 로그인
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                startResultActivity(getString(R.string.google));

                saveCompanyInfo(getString(R.string.google));

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("우진", "[facebook]signInWithCredential:success");
                        } else {
                            Log.e("우진", "[facebook]signInWithCredential:failure", task.getException());

                            Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("우진", "[google]signInWithCredential:success");
                        } else {
                            Log.e("우진", "[google]signInWithCredential:failure", task.getException());

                            Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startResultActivity(String company) {
        Intent intent = new Intent(LoginActivity.this, LoginResultActivity.class);
        intent.putExtra("company", company);
        startActivity(intent);
        finish();
    }

    private void saveCompanyInfo(String company) {
        //로그인 제공업체 정보 저장
        SharedPreferences sharedPreferences = getSharedPreferences("account_company", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("company", company);
        editor.commit();
    }

    void popUpDialog(String message) {
        OpenSNSLoginDialog dialog = new OpenSNSLoginDialog(message);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    void popUpDialog(String message, Intent signInIntent, int GOOGLE_SIGN_IN) {
        OpenSNSLoginDialog dialog = new OpenSNSLoginDialog(message, signInIntent, GOOGLE_SIGN_IN);
        dialog.show(getSupportFragmentManager(), "dialog");
    }
}