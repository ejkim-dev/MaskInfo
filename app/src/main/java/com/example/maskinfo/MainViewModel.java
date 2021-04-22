package com.example.maskinfo;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.maskinfo.model.Store;
import com.example.maskinfo.model.StoreInfo;
import com.example.maskinfo.repository.MaskService;

import java.util.ArrayList;
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

    private List<Store> items = new ArrayList<>();

    // 통신 1. 준비하는 코드
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(MaskService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build();

    private MaskService service = retrofit.create(MaskService.class);

    // 데이터를 받아올 준비를 함
    private Call<StoreInfo> storeInfoCall = service.fetchStoreInfo();

    // 호출을 해서 콜백을 받도록 함 - 'List<Store> items' 정보를 외부로 돌려주기 위해서!
    // livedate를 쓰면 관잘할 수 있기 때문에 콜백을 쓰지 않아도 됨
    private void fetchStoreInfo(){
        // 통신 2. 실행하는 코드
        storeInfoCall.enqueue(new Callback<StoreInfo>() {
            /* 아래 코드가 데이터가 다 얻어진 시점 -> 이걸 다시 호출한쪽(fetchStoreInfo)으로
            돌려주기 위해서 인터페이스로 콜백 구현을 해야됐음
            콜백을 쓰지 않기 위해서 "private List<Store> items = new ArrayList<>();" 여기에 라이브데이터를 넣을 예정*/
            @Override
            public void onResponse(Call<StoreInfo> call, Response<StoreInfo> response) {
                Log.d(TAG, "onResponse: refresh");
                List<Store> items = response.body().getStores();

                //null을 뺌
                adapter.updateItems(items
                        .stream()
                        .filter(item -> item.getRemainStat() != null)
                        .collect(Collectors.toList()));
                getSupportActionBar().setTitle("마스크 재고 있는 곳: "+ items.size()+"곳");
            }

            @Override
            public void onFailure(Call<StoreInfo> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    };
}