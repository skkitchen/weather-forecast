package your.pack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SimpleRSSReaderActivity extends Activity {
    /** Called when the activity is first created. */
	//フィールド宣言。ビューの宣言が主
	static final String TAG = "SimpleRSSReaderActivity"; //デバッグ用のタグ
	static final int DAY_MAX = 3; //何日分表示するか
	static final int CONTENTS_NUM = 3; //１日当たりのコンテンツの数（日付、天気、気温）
	ArrayList<String> areaList; //スピナーで表示する地方名を格納
	Spinner selectSpinner;// //地方選択スピナー
	ArrayAdapter<String> spinnerAdapter; //スピナー用アダプター
	HashMap<String, String> urlMap; //スピナーで選択された地方とURLとの関連ハッシュマップ
	TextView[][] tableContents; //結果テーブル
	ListView listViewData; //結果リストビュー
	ArrayAdapter<String> listAdapter; //リストビュー用アダプター
	String selectedArea; //スピナーで選択された地方名
	
	//アクティビティ中で一番最初に呼び出されるメソッド
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); //ここまでおまじない

        //スピナー関連のビューのレイアウトとの関連付け
    	selectSpinner = (Spinner)findViewById(R.id.AreaSpinner); //地方選択スピナー
    	spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item); //スピナー用アダプター
    	spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //スピナードロップダウンのUI定義
    	this.setAreaList(); //スピナーで表示する地方名を格納する、同アクティビティ内のメソッド呼び出し
    	this.initializeSpinnerAdapter(); //スピナー用アダプターにareaListの地方名をセット
    	selectSpinner.setAdapter(spinnerAdapter); //スピナー用アダプターをスピナーにセット
    	selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //スピナーで選択された項目に対する処理
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Spinner spinner = (Spinner)parent;
				selectedArea = (String)spinner.getItemAtPosition(position); //selectedAreaにスピナーで選択した地方を格納
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { //何もしない
				// TODO Auto-generated method stub
			}
		});
	}

	//ボタンが押されたときに呼び出されるメソッド
    public void parseRSS(View view) throws IOException, InterruptedException{
    	//結果関連のビューとレイアウトとの関連付け
    	listViewData = (ListView)findViewById(R.id.ListViewData); //結果リストビュー
    	listAdapter = new ArrayAdapter<String>(this, R.layout.list); //リストビュー用アダプター
    	tableContents = new TextView[DAY_MAX][CONTENTS_NUM]; //結果テーブル
    	urlMap = new HashMap<String, String>(); //選択された地方名とURLとの関連ハッシュマップ
    	TextView titleTextView = (TextView)findViewById(R.id.TitleTextView); //選択された地方を表示するタイトルテキストビュー

    	this.initializeURLMap(); //地方名とURLとの関連ハッシュマップの定義メソッド呼び出し
    	this.initializeTableContents(); //結果テーブルとレイアウトとの関連付けメソッド呼び出し

    	titleTextView.setText(selectedArea + "地方の天気"); //タイトルテキストビューにテキストをセット
    	String rssURL = urlMap.get(selectedArea); //選択された地方のURL
    	
    	//ネットを通じた情報取得には、AsyncTaskを利用。
    	SimpleRSSReaderAsync asyncTask = new SimpleRSSReaderAsync(listAdapter, listViewData, tableContents); //AsyncTaskをインスタンス化
    	asyncTask.execute(rssURL); //AsyncTask実行：結果の表示はasyncTaskクラス内で行う
    }

	private void setAreaList() { //スピナーで表示する地方名を格納する、同アクティビティ内のメソッド呼び出し
		// TODO Auto-generated method stub
		areaList = new ArrayList<String>();

		areaList.add("札幌");
    	areaList.add("仙台");
    	areaList.add("東京");
    	areaList.add("名古屋");
    	areaList.add("大阪");
    	areaList.add("広島");
    	areaList.add("福岡");
    	areaList.add("那覇");
	}
	private void initializeURLMap() { //地方名とURLとの関連ハッシュマップの定義メソッド呼び出し
		// TODO Auto-generated method stub
		urlMap.put(areaList.get(0), "http://rss.rssad.jp/rss/tenki/forecast/city_4.xml");
		urlMap.put(areaList.get(1), "http://rss.rssad.jp/rss/tenki/forecast/city_25.xml");
		urlMap.put(areaList.get(2), "http://rss.rssad.jp/rss/tenki/forecast/city_63.xml");
		urlMap.put(areaList.get(3), "http://rss.rssad.jp/rss/tenki/forecast/city_38.xml");
		urlMap.put(areaList.get(4), "http://rss.rssad.jp/rss/tenki/forecast/city_81.xml");
		urlMap.put(areaList.get(5), "http://rss.rssad.jp/rss/tenki/forecast/city_90.xml");
		urlMap.put(areaList.get(6), "http://rss.rssad.jp/rss/tenki/forecast/city_110.xml");
		urlMap.put(areaList.get(7), "http://rss.rssad.jp/rss/tenki/forecast/city_136.xml");
	}
	private void initializeTableContents() { //結果テーブルとレイアウトとの関連付けメソッド呼び出し
		// TODO Auto-generated method stub
    	tableContents[0][0] = (TextView)findViewById(R.id.TextViewDate1);
    	tableContents[1][0] = (TextView)findViewById(R.id.TextViewDate2);
    	tableContents[2][0] = (TextView)findViewById(R.id.TextViewDate3);
    	tableContents[0][1] = (TextView)findViewById(R.id.TextViewWeather1);
    	tableContents[1][1] = (TextView)findViewById(R.id.TextViewWeather2);
    	tableContents[2][1] = (TextView)findViewById(R.id.TextViewWeather3);
    	tableContents[0][2] = (TextView)findViewById(R.id.TextViewTemperature1);
    	tableContents[1][2] = (TextView)findViewById(R.id.TextViewTemperature2);
    	tableContents[2][2] = (TextView)findViewById(R.id.TextViewTemperature3);
    }
	private void initializeSpinnerAdapter() { //スピナー用アダプターにareaListの地方名をセット

		// TODO Auto-generated method stub
		for(String s : areaList){
			spinnerAdapter.add(s);
		}
	}
}