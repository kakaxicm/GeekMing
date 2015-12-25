package com.kakaxicm.geekming;

import android.os.Bundle;
import android.widget.ListView;

import com.kakaxicm.geekming.domain.CommonDomain1;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.universaladapter.MultiTypeSurpport;
import com.kakaxicm.geekming.frameworks.universaladapter.UniversalAdapter;
import com.kakaxicm.geekming.frameworks.universaladapter.UniversalViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kakaxicm on 2015/12/25.
 */
@ContentViewAnnotation(value = R.layout.activity_common_listview)
public class CommonAdapterListViewActivity extends BaseActivity {
    @ViewIdAnnotation(value = R.id.listview)
    private ListView mListView;

    private List<CommonDomain1> mData = new ArrayList<>();
    private MultiTypeSurpport<CommonDomain1> mMultiType = new MultiTypeSurpport<CommonDomain1>() {
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getLayoutId(int position, CommonDomain1 item) {
            if(getItemViewType(position, item) == 0){
                return R.layout.item1_conmon_list;
            }
            return R.layout.item2_conmon_list;
        }

        @Override
        public int getItemViewType(int postion, CommonDomain1 item) {
            if(postion%2 == 0){
                return 0;
            }
            return 1;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 50; i++) {
            CommonDomain1 item = new CommonDomain1();
            item.setS1("" + i);
            item.setS2("item" + i);
            mData.add(item);
        }

        UniversalAdapter<CommonDomain1> adapter = new UniversalAdapter<CommonDomain1>(this, mData, mMultiType) {
            @Override
            public void bindView(UniversalViewHolder vh, int postion, CommonDomain1 item) {
                switch (vh.getmLayoutId()) {
                    case R.layout.item1_conmon_list:
                        vh.setTextView(R.id.tv1, item.getS1());
                        vh.setTextView(R.id.tv2, item.getS2());
                        break;
                    case R.layout.item2_conmon_list:
                        vh.setImageView(R.id.item_img, R.drawable.ic_launcher);
                        break;
                }
            }
        };
        mListView.setAdapter(adapter);
    }
}
