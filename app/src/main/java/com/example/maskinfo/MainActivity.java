package com.example.maskinfo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // 변경이 되지 않도록 final로 명시함
        final StoreAdapter adapter = new StoreAdapter();
        recyclerView.setAdapter(adapter);

        // 뷰모델을 사용하도록 코드 작성청
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //UI 변경 감지 해서 업데이트
        viewModel.itemLiveData.observe(this, stores -> {
            adapter.updateItems(stores);
            getSupportActionBar().setTitle("마스크 재고 있는 곳: " + stores.size()+"곳");
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refrash:
                //refresh
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder>{

    private List<Store> mItems = new ArrayList<>();

    // 아이템 뷰 정보를 가지고 있는 클래스
    static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, distanceTextView, remainTextView, countTextView;
        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_text_view);
            addressTextView = itemView.findViewById(R.id.addr_text_view);
            distanceTextView = itemView.findViewById(R.id.distance_text_view);
            remainTextView = itemView.findViewById(R.id.remain_text_view);
            countTextView = itemView.findViewById(R.id.count_text_view);
        }
    }

    public void updateItems(List<Store> items) {
        mItems = items;
        notifyDataSetChanged(); //UI 갱신
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 안드로이드의 모든 뷰, 뷰그룹에는 Context를 얻을 수 있음
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);

        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = mItems.get(position);
        holder.nameTextView.setText(store.getName());
        holder.addressTextView.setText(store.getAddr());
        holder.distanceTextView.setText("1.0km");

        String remainStat = "충분";
        String count = "100개 이상";
        int color = Color.GREEN;
        switch (store.getRemainStat()){
            case "plenty" :
                remainStat = "충분";
                count = "100개 이상";
                color = Color.GREEN;
                break;
            case "some" :
                remainStat = "여유";
                count = "30개 이상";
                color = Color.YELLOW;
                break;
            case "few":
                remainStat = "매진 임박";
                count = "2개 이상";
                color = Color.RED;
                break;
            case "empty":
                remainStat = "재고 없음";
                count = "1개 이하";
                color = Color.GRAY;
                break;
            default:
        }

        holder.remainTextView.setText(remainStat);
        holder.countTextView.setText(count);

        holder.remainTextView.setTextColor(color);
        holder.countTextView.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
