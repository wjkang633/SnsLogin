package woojin.project.android.snslogin.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<String>> pinLive;
    private ArrayList<String> pinArr = new ArrayList<>();

    public MainViewModel() {

    }

    public MutableLiveData<List<String>> getLiveList() {
        if (pinLive == null) {
            pinLive = new MutableLiveData<>();
        }

        return pinLive;
    }

    public void addNum(String a) {
        pinArr.add(a);
        pinLive.setValue(pinArr);
    }

    public void removeNum() {
        int len = pinArr.size();

        if (len >= 1) {
            pinArr.remove(len - 1);
            pinLive.setValue(pinArr);
        }
    }

    public void clearPinArr(){
        pinArr.clear();
        pinLive.setValue(pinArr);
    }

    public void arrangeSize() {
        int len = pinArr.size();

        if (len >= 6) {
            for (int i = 6; i < len; i++) {
                pinArr.remove(i);
            }

            pinLive.setValue(pinArr);
        }
    }
}
