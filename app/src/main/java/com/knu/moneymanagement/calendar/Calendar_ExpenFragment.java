package com.knu.moneymanagement.calendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knu.moneymanagement.ItemDiffUtil;
import com.knu.moneymanagement.ModifyActivity;
import com.knu.moneymanagement.RecyclerViewAdapter;
import com.knu.moneymanagement.RecyclerViewItem;
import com.knu.moneymanagement.database.Constant;
import com.knu.moneymanagement.database.StaticVariable;
import com.knu.moneymanagement.database.MoneyDBCtrct;
import com.knu.moneymanagement.database.MoneyDBHelper;
import com.knu.moneymanagement.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Calendar_ExpenFragment extends Fragment implements Constant {

    private LinearLayout linearLayout;
    private TextView dayText, expenCountText, expenText, contentDetailText, notExistText, monBiggestExpenText, monAvgExpenText, monAllExpenText, allExpenText;
    private RecyclerView mRecyclerView = null;
    private RecyclerViewAdapter mAdapter = null;
    private final ArrayList<RecyclerViewItem> mItemList = new ArrayList<>();
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");

    public Calendar_ExpenFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d("MyTag", "Calendar_ExpenFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("MyTag", "Calendar_ExpenFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MyTag", "Calendar_ExpenFragment onCreateView");
        if(getActivity() != null) {
            View root = inflater.inflate(R.layout.fragment_calendar_expen, container, false);

            linearLayout = root.findViewById(R.id.calendarExpenView);
            dayText = root.findViewById(R.id.dayExpenText);
            expenCountText = root.findViewById(R.id.expenCountText);
            expenText = root.findViewById(R.id.expText);
            contentDetailText = root.findViewById(R.id.expenContentDetailText);
            notExistText = root.findViewById(R.id.dayExpenNotExist);
            monBiggestExpenText = root.findViewById(R.id.monBiggestExpenText);
            monAvgExpenText = root.findViewById(R.id.monAvgExpenText);
            monAllExpenText = root.findViewById(R.id.monAllExpenText);
            allExpenText = root.findViewById(R.id.allExpenText);
            mRecyclerView = root.findViewById(R.id.dayExpenRecyclerView);

            mAdapter = new RecyclerViewAdapter(new ItemDiffUtil());
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

//            mAdapter.setOnItemTouchListener(new RecyclerViewAdapter.OnItemTouchListener() {
//                @Override
//                public void onItemTouch(View view, MotionEvent event, int position) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        view.setBackgroundColor(0xCFDCDCDC);
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        view.setBackgroundColor(0x00000000);
//                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                        view.setBackgroundColor(0xCFDCDCDC);
//                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//                        view.setBackgroundColor(0x00000000);
//                    } else {
//                        view.setBackgroundColor(0x00000000);
//                    }
//                }
//            });

            ActivityResultLauncher<Intent> modifyActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == UPDATE_SUCCESS) {
                            Toast.makeText(getActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                        } else if (result.getResultCode() == DELETE_SUCCESS) {
                            Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        } else if (result.getResultCode() == MODIFY_CANCEL) {
                            Toast.makeText(getActivity(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

            mAdapter.setOnItemClickListener((view, position) -> {
                Intent it = new Intent(getActivity(), ModifyActivity.class);
                Bundle bundle = new Bundle();

                int i = mItemList.get(position).getId();
                String category = mItemList.get(position).getCategory();
                int year = mItemList.get(position).getYear();
                int month = mItemList.get(position).getMonth();
                int day = mItemList.get(position).getDay();
                String detail = mItemList.get(position).getDetail();
                int money = mItemList.get(position).getMoney();

                bundle.putInt("id", i);
                bundle.putString("category", category);
                bundle.putInt("year", year);
                bundle.putInt("month", month);
                bundle.putInt("day", day);
                bundle.putString("detail", detail);
                bundle.putInt("money", money);

                it.putExtras(bundle);

                modifyActivityResult.launch(it);
            });

            linearLayout.setVisibility(View.INVISIBLE);

            return root;
        }
        else
            return null;
    }

    @Override
    public void onStart() {
        Log.d("MyTag", "Calendar_ExpenFragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("MyTag", "Calendar_ExpenFragment onResume");
        super.onResume();
        linearLayout.setVisibility(View.VISIBLE);
        createExpenInfo(StaticVariable.year, StaticVariable.month, StaticVariable.day);
    }

    @Override
    public void onPause() {
        Log.d("MyTag", "Calendar_ExpenFragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("MyTag", "Calendar_ExpenFragment onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("MyTag", "Calendar_ExpenFragment onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("MyTag", "Calendar_ExpenFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d("MyTag", "Calendar_ExpenFragment onDetach");
        super.onDetach();
    }

    public void createExpenInfo(int year, int month, int day) {
        Log.d("MyTag", "Calendar_ExpenFragment createExpenInfo");
        MoneyDBHelper dbHelper = new MoneyDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlSelect;
        Cursor cursor;

        Bitmap expenIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_expen);

        dayText.setText(getString(R.string.contents, year + "년 " + month + "월 " + day + "일"));

        sqlSelect =
                "SELECT " + "SUM(" + MoneyDBCtrct.COL_MONEY + ")" + " FROM " + MoneyDBCtrct.TBL_MONEY + " " +
                "WHERE " + MoneyDBCtrct.COL_CATEGORY + "=" + "'지출'" + " AND " + MoneyDBCtrct.COL_YEAR + "=" + year + " AND " + MoneyDBCtrct.COL_MONTH + "=" + month + " AND " + MoneyDBCtrct.COL_DAY + "=" + day + " " +
                "GROUP BY " + MoneyDBCtrct.COL_YEAR + ", " + MoneyDBCtrct.COL_MONTH + ", " + MoneyDBCtrct.COL_DAY;
        cursor = db.rawQuery(sqlSelect, null);
        long expen = 0L;
        while (cursor.moveToNext())
            expen = cursor.getLong(0);

        expenText.setText(getString(R.string.contents, "지출 : " + myFormatter.format(expen) + "원"));

        contentDetailText.setText(getString(R.string.contents, year + "년 " + month + "월 " + day + "일 지출 내역"));

        mItemList.clear();
        sqlSelect =
                MoneyDBCtrct.SQL_SELECT + " " +
                        "WHERE " + MoneyDBCtrct.COL_CATEGORY + "=" + "'지출'" + " AND " + MoneyDBCtrct.COL_YEAR + "=" + year + " AND " + MoneyDBCtrct.COL_MONTH + "=" + month + " AND " + MoneyDBCtrct.COL_DAY + "=" + day + " " +
                        "ORDER BY " + MoneyDBCtrct.COL_DAY + " ASC";
        cursor = db.rawQuery(sqlSelect, null);
        while (cursor.moveToNext()) {
            RecyclerViewItem item = new RecyclerViewItem();

            int id = cursor.getInt(0);
            String category = cursor.getString(1);
            int yearValue = cursor.getInt(2);
            int monthValue = cursor.getInt(3);
            int dayValue = cursor.getInt(4);
            String detail = cursor.getString(5);
            int money = cursor.getInt(6);

            item.setId(id);
            item.setCategory(category);
            item.setYear(yearValue);
            item.setMonth(monthValue);
            item.setDay(dayValue);
            item.setDate(yearValue, monthValue, dayValue);
            item.setDetail(detail);
            item.setMoney(money);
            item.setMoneyStr(money);
            item.setIcon(expenIcon);

            mItemList.add(item);
        }
        //mAdapter.notifyDataSetChanged();
        mAdapter.submitList(mItemList);

        expenCountText.setText(getString(R.string.contents, "총 " + mAdapter.getItemCount() + "건"));

        if (mAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            notExistText.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            notExistText.setVisibility(View.GONE);
        }

        sqlSelect =
                "SELECT " + "SUM(" + MoneyDBCtrct.COL_MONEY + ")" + " FROM " + MoneyDBCtrct.TBL_MONEY + " " +
                "WHERE " + MoneyDBCtrct.COL_CATEGORY + "=" + "'지출'" + " AND " + MoneyDBCtrct.COL_YEAR + "=" + year + " AND " + MoneyDBCtrct.COL_MONTH + "=" + month + " " +
                "GROUP BY " + MoneyDBCtrct.COL_YEAR + ", " + MoneyDBCtrct.COL_MONTH;
        cursor = db.rawQuery(sqlSelect, null);
        long monAllExpen = 0L;
        while (cursor.moveToNext())
            monAllExpen = cursor.getLong(0);

        monAllExpenText.setText(getString(R.string.contents, year + "년 " + month + "월 " + "전체 지출 : " + myFormatter.format(monAllExpen) + "원"));

        sqlSelect =
                "SELECT " + "MAX(" + MoneyDBCtrct.COL_MONEY + ")" + " FROM " + MoneyDBCtrct.TBL_MONEY + " " +
                "WHERE " + MoneyDBCtrct.COL_CATEGORY + "=" + "'지출'" + " AND " + MoneyDBCtrct.COL_YEAR + "=" + year + " AND " + MoneyDBCtrct.COL_MONTH + "=" + month + " " +
                "GROUP BY " + MoneyDBCtrct.COL_YEAR + ", " + MoneyDBCtrct.COL_MONTH;
        cursor = db.rawQuery(sqlSelect, null);
        long monBiggestExpen = 0L;
        while (cursor.moveToNext())
            monBiggestExpen = cursor.getLong(0);

        monBiggestExpenText.setText(getString(R.string.contents, year + "년 " + month + "월 " + "최고 지출 : " + myFormatter.format(monBiggestExpen) + "원"));

        sqlSelect =
                "SELECT " + "COUNT(" + MoneyDBCtrct.COL_CATEGORY + ")" + " " +
                "FROM " + MoneyDBCtrct.TBL_MONEY + " " +
                "WHERE " + MoneyDBCtrct.COL_CATEGORY + "=" + "'지출'" + " AND " + MoneyDBCtrct.COL_YEAR + "=" + year + " AND " + MoneyDBCtrct.COL_MONTH + "=" + month;
        cursor = db.rawQuery(sqlSelect, null);
        int count = 0;
        while (cursor.moveToNext())
            count = cursor.getInt(0);

        long monAvgExpen;
        if (count == 0)
            monAvgExpen = monAllExpen;
        else
            monAvgExpen = monAllExpen / count;

        monAvgExpenText.setText(getString(R.string.contents, year + "년 " + month + "월 " + "평균 지출 : " + myFormatter.format(monAvgExpen) + "원"));

        sqlSelect =
                "SELECT " + "SUM(" + MoneyDBCtrct.COL_MONEY + ")" + " FROM " + MoneyDBCtrct.TBL_MONEY + " " +
                "WHERE " + MoneyDBCtrct.COL_CATEGORY + "=" + "'지출'" + " " +
                "GROUP BY " + MoneyDBCtrct.COL_CATEGORY;
        cursor = db.rawQuery(sqlSelect, null);
        long allExpen = 0L;
        while (cursor.moveToNext())
            allExpen = cursor.getLong(0);

        allExpenText.setText(getString(R.string.contents, "전체 누적 지출 : " + myFormatter.format(allExpen) + "원"));

        cursor.close();
        db.close();
        dbHelper.close();

        if (monAllExpen == 0L)
            Toast.makeText(getActivity(), "지출 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
    }
}
