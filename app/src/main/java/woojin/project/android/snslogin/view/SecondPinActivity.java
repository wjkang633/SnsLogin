package woojin.project.android.snslogin.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import woojin.project.android.snslogin.R;
import woojin.project.android.snslogin.databinding.ActivitySecondPinBinding;
import woojin.project.android.snslogin.dialog.BioAuthDialog;
import woojin.project.android.snslogin.dialog.TwoButtonDialog;
import woojin.project.android.snslogin.viewmodel.MainViewModel;


public class SecondPinActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private String pin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pin = getIntent().getStringExtra("pin");

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ActivitySecondPinBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_second_pin);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        viewModel.getLiveList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if (strings.size() == 6){
                    if (pin.equals(strings.toString())){
                        //핀 설정 여부 저장
                        savePinSettingState();

                        popUpBioAuthDialog();
                    }
                    else{
                        //토스트 안내
                        Toast.makeText(SecondPinActivity.this, "PIN이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                        //핀 초기화
                        viewModel.clearPinArr();
                    }
                }
            }
        });

        //취소
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog("회원 가입을 종료하시겠습니까?", false);
            }
        });

        //키패드
        findViewById(R.id.one).setOnClickListener(keypadClickListener);
        findViewById(R.id.two).setOnClickListener(keypadClickListener);
        findViewById(R.id.three).setOnClickListener(keypadClickListener);
        findViewById(R.id.four).setOnClickListener(keypadClickListener);
        findViewById(R.id.five).setOnClickListener(keypadClickListener);
        findViewById(R.id.six).setOnClickListener(keypadClickListener);
        findViewById(R.id.seven).setOnClickListener(keypadClickListener);
        findViewById(R.id.eight).setOnClickListener(keypadClickListener);
        findViewById(R.id.nine).setOnClickListener(keypadClickListener);
        findViewById(R.id.zero).setOnClickListener(keypadClickListener);
        findViewById(R.id.back).setOnClickListener(keypadClickListener);
    }

    View.OnClickListener keypadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.one) {
                viewModel.addNum("1");
            } else if (v.getId() == R.id.two) {
                viewModel.addNum("2");
            } else if (v.getId() == R.id.three) {
                viewModel.addNum("3");
            } else if (v.getId() == R.id.four) {
                viewModel.addNum("4");
            } else if (v.getId() == R.id.five) {
                viewModel.addNum("5");
            } else if (v.getId() == R.id.six) {
                viewModel.addNum("6");
            } else if (v.getId() == R.id.seven) {
                viewModel.addNum("7");
            } else if (v.getId() == R.id.eight) {
                viewModel.addNum("8");
            } else if (v.getId() == R.id.nine) {
                viewModel.addNum("9");
            } else if (v.getId() == R.id.zero) {
                viewModel.addNum("0");
            } else if (v.getId() == R.id.back) {
//                viewModel.arrangeSize();
                viewModel.removeNum();
            }
        }
    };

    public void savePinSettingState() {
        SharedPreferences sharedPreferences = getSharedPreferences("check_first_auth", MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putBoolean("completed", true);
        editor.commit();
    }

    public void popUpDialog(String message, boolean isGoBackToMainActivity) {
        TwoButtonDialog dialog = new TwoButtonDialog(message, isGoBackToMainActivity);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    public void popUpBioAuthDialog() {
        BioAuthDialog dialog = new BioAuthDialog();
        dialog.show(getSupportFragmentManager(), "bio_dialog");
    }
}
