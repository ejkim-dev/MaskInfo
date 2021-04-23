package com.example.maskinfo;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.maskinfo.model.Store;
import com.example.maskinfo.model.StoreInfo;
import com.example.maskinfo.repository.MaskService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

// 껍데기 ( MainActivity ) 는 최종 결과만 알면 되기 때문에 ViewModle에서 데이터 주고 받는 작업을 한다.
public class MainViewModel extends ViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    // MutableLiveData : 변경이 가능한 라이브데이터가 됨
    // 외부에서 'fetchStoreInfo()'를 호출하면 'items'의 값만 변경되게 한다.
    // 원래 getter setter로 하는게 맞는데 일단 'public'으로 열어놓음
    public MutableLiveData<List<Store>> itemLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> loadingLiveData = new MutableLiveData();

    public Location location;

    // 통신 1. 준비하는 코드
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(MaskService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build();

    private MaskService service = retrofit.create(MaskService.class);

    // 데이터를 받아올 준비를 함
//    private Call<StoreInfo> storeInfoCall = service.fetchStoreInfo();

//    public MainViewModel() {
//        fetchStoreInfo();
//    }

    // 호출을 해서 콜백을 받도록 함 - 'List<Store> items' 정보를 외부로 돌려주기 위해서!
    // livedate를 쓰면 관잘할 수 있기 때문에 콜백을 쓰지 않아도 됨
    public void fetchStoreInfo(){
        // 로딩 시작
        loadingLiveData.setValue(true);

        // 통신 2. 실행하는 코드
        /*ERR : Already executed (업데이트 시 발생)-> call객체에 .clon() 한번 하명 됨*/
        service.fetchStoreInfo(location.getLatitude(), location.getLongitude()).clone().enqueue(new Callback<StoreInfo>() {
            /* 아래 코드가 데이터가 다 얻어진 시점 -> 이걸 다시 호출한쪽(fetchStoreInfo)으로
            돌려주기 위해서 인터페이스로 콜백 구현을 해야됐음, 이러면 코드가 복잡해짐.
            콜백을 쓰지 않기 위해서 "private List<Store> items = new ArrayList<>();" 여기에 라이브데이터를 넣을 예정
            라이브 데이터를 넣으면 걔를 변경하는 것에서 끝낼 수 있음 -> 엑티비티 쪽에서는 이걸 관찰하고 있다가 변경점만 캐치하면 됨*/
            @Override
            public void onResponse(Call<StoreInfo> call, Response<StoreInfo> response) {
                Log.d(TAG, "onResponse: refresh");
                List<Store> items = response.body().getStores()
                        .stream()
                        .filter(item -> item.getRemainStat() != null)
                        .filter(item -> !item.getRemainStat().equals("empty"))
//                        .sorted() // 로 정렬해도 되지만 순수 자바에서 정렬하겠
                        .collect(Collectors.toList());

                for (Store store : items){
                    double distance = LocationDistance.distance(location.getLatitude(), location.getLongitude(), store.getLat(), store.getLng(), "k");
                    store.setDistance(distance);
                }
                Log.d(TAG, "onResponse: "+items);

                // 이렇게 하면 distance로 정렬이 가능한 객체가 됨
                Collections.sort(items);

                // 아래 코드는 비동기로 돌아가는 코드. 백그라운드에서 스래드로 동작함. 따라서 비동기에 안전한 코드(postValue())를 써야함
                itemLiveData.postValue(items);

                // 로딩 끝
                loadingLiveData.postValue(false);
            }

            @Override
            public void onFailure(Call<StoreInfo> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                // 에러가 났을 때 빈 걸 세팅해주는 코드
                itemLiveData.postValue(Collections.emptyList());

                // 로딩 끝
                loadingLiveData.postValue(false);

            }
        });
    };
}