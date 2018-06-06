package com.ellalee.travelmaker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;



public class CalendarMain extends Activity {

    /**
     * 연/월 텍스트뷰
     */
    private TextView tvDate;


    /**
     * 그리드뷰 어댑터
     */
    private GridAdapter gridAdapter;


    /**
     * 일 저장 할 리스트
     */
    private ArrayList<Day> dayList;

    /**
     * 스케줄 저장되어있는 리스트
     */
    private List<String> doList;


    /**
     * 그리드뷰
     */
    private GridView gridView;


    /**
     * 캘린더 변수
     */
    private Calendar mCal;


    private int count = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar_main);


        tvDate = (TextView) findViewById(R.id.tv_date);

        gridView = (GridView) findViewById(R.id.gridview);


        // 오늘에 날짜를 세팅

        long now = System.currentTimeMillis();

        final Date date = new Date(now);

        //연,월,일을 따로 저장

        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);

        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);

        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);


        //현재 날짜 텍스트뷰에 뿌려줌

        tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));


        //gridview 요일 표시

        dayList = new ArrayList<Day>();
        doList = new ArrayList();

        Day sun = new Day();
        sun.setDay("일");
        dayList.add(sun);

        Day mon = new Day();
        mon.setDay("월");
        dayList.add(mon);

        Day tue = new Day();
        tue.setDay("화");
        dayList.add(tue);

        Day wed = new Day();
        wed.setDay("수");
        dayList.add(wed);

        Day thu = new Day();
        thu.setDay("목");
        dayList.add(thu);

        Day fri = new Day();
        fri.setDay("금");
        dayList.add(fri);

        Day sat = new Day();
        sat.setDay("토");
        dayList.add(sat);

        doList.add("TM presentation (2018-05-24)");
        doList.add("Okinawa (2018-05-29");
        doList.add("Okinawa (2018-05-30");
        doList.add("Okinawa (2018-05-31");
        doList.add("Hotel (2018-05-29)");
        doList.add("Hotel (2018-05-30");
        mCal = Calendar.getInstance();


        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)

        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);

        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        //1일 - 요일 매칭 시키기 위해 공백 add

        for (int i = 1; i < dayNum; i++) {
            Day empty = new Day();
            empty.setDay("");
            dayList.add(empty);

        }

        setCalendarDate(mCal.get(Calendar.MONTH) + 1);


        gridAdapter = new GridAdapter(getApplicationContext(), dayList);

        gridView.setAdapter(gridAdapter);


    }


    /**
     * 해당 월에 표시할 일 수 구함
     *
     * @param month
     */

    private void setCalendarDate(int month) {

        mCal.set(Calendar.MONTH, month - 1);


        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            Day d = new Day();
            count  = 0;
            d.setDay("" + String.valueOf(i + 1));

            for (int j = 0; j < doList.size(); j++) {
                if (doList.get(j).substring(doList.get(j).indexOf("(")+9, doList.get(j).indexOf("(")+11).equals(d.getDay().toString())) {
                    count += 1;
                    d.setSche(doList.get(j).substring(0, doList.get(j).indexOf("(")-1));
                }
            }
            dayList.add(d);
        }
    }


    /** 그리드뷰 어댑터 */

    private class GridAdapter extends BaseAdapter {

        private final ArrayList<Day> daylist;


        private final LayoutInflater inflater;



        /**

         * 생성자

         * @param context

         * @param list

         */

        public GridAdapter(Context context, ArrayList<Day> list) {

            this.daylist = list;

            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        @Override

        public int getCount() {

            return daylist.size();

        }


        @Override

        public String getItem(int position) {

            return daylist.get(position).getDay();

        }


        public String getScheItem(int day, int position) {
            if(daylist.size() > day)
                return daylist.get(day).getSche().get(position).toString();
            else
                return "";
        }


        public int getScheTotal(int position) {
            return daylist.get(position).getSche().size();
        }

        @Override

        public long getItemId(int position) {

            return position;

        }


        @Override

        public View getView(int position, View convertView, ViewGroup parent) {



            ViewHolder holder = null;



            if (convertView == null) {

                convertView = inflater.inflate(R.layout.activity_calendar_sub, parent, false);

                holder = new ViewHolder();
                holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_item_gridview);
                holder.firScheGridView = (TextView)convertView.findViewById(R.id.first_schedule);
                holder.secScheGridView = (TextView)convertView.findViewById(R.id.second_schedule);
                holder.totalScheGridView = (TextView)convertView.findViewById(R.id.total_shedule);


                convertView.setTag(holder);

            } else {

                holder = (ViewHolder)convertView.getTag();

            }

            holder.tvItemGridView.setText("" + getItem(position));
            if(daylist.get(position).getSche().size() != 0) {
                holder.firScheGridView.setText("" + getScheItem(position, 0));
                holder.firScheGridView.setBackgroundColor(Color.parseColor("#fbc2eb"));
                holder.firScheGridView.setSingleLine();
            }
            if(daylist.get(position).getSche().size() > 1) {
                holder.secScheGridView.setText("" + getScheItem(position, 1));
                holder.secScheGridView.setBackgroundColor(Color.parseColor("#c2e9fb"));
                holder.secScheGridView.setSingleLine();
            }
            if(getScheTotal(position)> 0)
                holder.totalScheGridView.setText("Total: " + getScheTotal(position));



            //해당 날짜 텍스트 컬러,배경 변경

            mCal = Calendar.getInstance();

            //오늘 day 가져옴

            Integer today = mCal.get(Calendar.DAY_OF_MONTH);

            String sToday = String.valueOf(today);

            if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경

                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.black));

            }

            return convertView;

        }

    }

    private class ViewHolder {

        TextView tvItemGridView;
        TextView firScheGridView;
        TextView secScheGridView;
        TextView totalScheGridView;
    }

    private class Day {
        private String day;
        private ArrayList<String> sche = new ArrayList<String>();
        private int count = 0;

        public void setDay(String d) {
            day = d;
        }

        public void setSche(String s) {
            sche.add(s);
        }

        public String getDay() {
            return day;
        }

        public ArrayList<String> getSche() {
            return sche;
        }
    }

}
