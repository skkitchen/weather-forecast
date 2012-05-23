package your.pack;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

//AsyncTaskのクラス。継承したAsyncTaskクラスの３つのGenericsは、それぞれpre, doInBack, onPostの引数の型
public class SimpleRSSReaderAsync extends AsyncTask<String, Integer, ArrayAdapter<String>> {
	//フィールドの宣言。ビューの定義が主
	static final String TAG = "SimpleRSSReaderAsync"; //デバッグ用タグ
	ListView listViewAsync = null; //結果リストビュー
	ArrayAdapter<String> adapterOutput = null; //リストビュー用アダプター
	TextView[][] tableContents = null; //結果テーブル
	String[][] rssData = null; //結果データ：１セルに相当
	
	// コンストラクタ
	public SimpleRSSReaderAsync(ArrayAdapter<String> adapterInput, ListView listView, TextView[][] tableContents){
		this.listViewAsync = listView;
		this.adapterOutput = adapterInput;
		this.tableContents = tableContents;
	}

	//メインのメソッドとは非同期で処理をするメソッド
	@Override
	protected ArrayAdapter<String> doInBackground(String... params){
		final int DAY_MAX = SimpleRSSReaderActivity.DAY_MAX;
		rssData = new String[DAY_MAX][]; //結果データを１次元分だけ定義
		boolean isItem = false; //タグ判断
		boolean isImage = false; //タグ判断
		int dayCount = 0; //ループ変数
		
    	try {
    		//ネット上から情報を取得するのに必要なオブジェクト定義
			URL url = new URL(params[0]);
			XmlPullParser xmlPullParser = Xml.newPullParser();
			try {
	    		xmlPullParser.setInput(url.openStream(), "UTF-8");
	    		int rssDataEvent = 0; //タグの種類を表わす整数
	    		while((rssDataEvent = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT){ //Endタグじゃない間ループ
	    			if(rssDataEvent == XmlPullParser.START_TAG){ //Startタグなら
	    				if(xmlPullParser.getName().equals("item")){ //itemタグなら
	    					isItem = true;
	    				}
	    				if(xmlPullParser.getName().equals("image")){ //imageタグなら
	    					isImage = true;
	    				}
	    				//スタートタグ内で、itemタグ内、かつimageタグ内でない、かつtitleタグであるときに、データを取得
		    			if(xmlPullParser.getName().equals("title") && isItem && !isImage){
		    				rssData[dayCount] = xmlPullParser.nextText().split(" "); //その次のデータから、" "でスプリットして変数rssDataに取得
		    				adapterOutput.add(rssData[dayCount][0] + "\t\t" + rssData[dayCount][1] + "\t\t" + rssData[dayCount][2]); //リストビュー用アダプターにデータ文字列をセット
		    				dayCount++;
		    			}
	    			} else if(rssDataEvent == XmlPullParser.END_TAG){ //Endタグなら
	    				if(xmlPullParser.getName().equals("image")){ //imageタグなら
	    					isImage = false;
	    				}
		    			if(xmlPullParser.getName().equals("item")){ //itemタグなら
		    				isItem = false;
		    			}	    			
	    			}
	    			if(dayCount == DAY_MAX){ //３日分データを取得したらbreak
	    				break;
	    			}
	    		}
	    		return adapterOutput; //onPostExecuteメソッドにアダプターを引数として渡す。
			} catch (XmlPullParserException e) {
	    		// TODO Auto-generated catch block
	    		rssData[0][0] = "open error: " + e.toString();
	    		e.printStackTrace();
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e){
			e.printStackTrace();	
		}
    	return null;		
	}

	//doInBackGroundメソッド内で取得したデータを元に、UIの表示を変更。※onPostExecuteメソッド内でしかUIは変更できない。
	protected void onPostExecute(ArrayAdapter<String> adapterInput){
		final int DAY_MAX = SimpleRSSReaderActivity.DAY_MAX;
		final int CONTENTS_NUM = SimpleRSSReaderActivity.CONTENTS_NUM;
    	Log.d(TAG, "in postExe!!!");
		listViewAsync.setAdapter(adapterInput); //結果リストビューにリストビュー用アダプターをセット
		for(int i = 0; i < CONTENTS_NUM; i++){
			for(int j = 0; j < DAY_MAX; j++){
				tableContents[i][j].setText(rssData[i][j]); //結果テーブルにテキストデータrssDataをセット
			}
		}
	}
}
