package woojin.project.android.snslogin.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import woojin.project.android.snslogin.R;
import woojin.project.android.snslogin.databinding.ActivityFirstPinBinding;
import woojin.project.android.snslogin.dialog.TwoButtonDialog;
import woojin.project.android.snslogin.viewmodel.MainViewModel;

public class FirstPinActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_pin);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ActivityFirstPinBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_first_pin);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getLiveList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list) {
                Log.e("우진", list.toString());

                //연속된 숫자 6자리인지 체크
                if (checkContinuousNum(list)) {
                    //토스트 안내
                    Toast.makeText(FirstPinActivity.this, "연속된 숫자 6자리를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();

                    //핀 초기화
                    viewModel.clearPinArr();
                }

                //동일한 숫자 3번 이상 반복인지 체크
                if (checkSameNum(list)) {
                    //토스트 안내
                    Toast.makeText(FirstPinActivity.this, "동일한 숫자를 3번 이상 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();

                    //핀 초기화
                    viewModel.clearPinArr();
                }

                if (!checkContinuousNum(list)
                        && !checkSameNum(list)
                        && list.size() == 6) {
                    Intent intent = new Intent(FirstPinActivity.this, SecondPinActivity.class);
                    intent.putExtra("pin", list.toString());
                    startActivity(intent);
                    finish();
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

        //핀 최초 설정 안내 문구
        if (checkFirstAuth()) {
            findViewById(R.id.first_auth_guide_txt).setVisibility(View.GONE);
        } else {
            findViewById(R.id.first_auth_guide_txt).setVisibility(View.VISIBLE);
        }
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


    public void popUpDialog(String message, boolean isGoBackToMainActivity) {
       TwoButtonDialog dialog = new TwoButtonDialog(message, isGoBackToMainActivity);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    public boolean checkFirstAuth() {
        SharedPreferences sharedPreferences = getSharedPreferences("check_first_auth", MODE_PRIVATE);
        boolean isPinAuthCompleted = sharedPreferences.getBoolean("completed", false);

        if (isPinAuthCompleted) {
            return true;
        } else {
            return false;
        }
    }

    public void saveFirstAuth() {
        SharedPreferences sharedPreferences = getSharedPreferences("check_first_auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("completed", true);
        editor.commit();
    }

    public boolean checkContinuousNum(List<String> list) {
        int checkFlag = 0;
        int temp;

        for (int i = 0; i < list.size(); i++) {
            int target = Integer.valueOf(list.get(i));

            if (i >= 1) {
                temp = Integer.valueOf(list.get(i - 1));

                if (temp + 1 == target) {
                    checkFlag++;
                }
            }

            if (checkFlag >= 5) {
                Log.e("우진", "연속된 숫자 6자리 이상!");
                return true;
            }
        }

        return false;
    }

    public boolean checkSameNum(List<String> list) {
        int checkFlag = 0;
        int temp;

        for (int i = 0; i < list.size(); i++) {
            int target = Integer.valueOf(list.get(i));

            if (i >= 1) {
                temp = Integer.valueOf(list.get(i - 1));

                if (temp == target) {
                    checkFlag++;
                }
            }

            if (checkFlag >= 2) {
                Log.e("우진", "동일한 숫자 3자리 이상!");
                return true;
            }
        }

        return false;
    }
}