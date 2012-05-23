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

//AsyncTask�̃N���X�B�p������AsyncTask�N���X�̂R��Generics�́A���ꂼ��pre, doInBack, onPost�̈����̌^
public class SimpleRSSReaderAsync extends AsyncTask<String, Integer, ArrayAdapter<String>> {
	//�t�B�[���h�̐錾�B�r���[�̒�`����
	static final String TAG = "SimpleRSSReaderAsync"; //�f�o�b�O�p�^�O
	ListView listViewAsync = null; //���ʃ��X�g�r���[
	ArrayAdapter<String> adapterOutput = null; //���X�g�r���[�p�A�_�v�^�[
	TextView[][] tableContents = null; //���ʃe�[�u��
	String[][] rssData = null; //���ʃf�[�^�F�P�Z���ɑ���
	
	// �R���X�g���N�^
	public SimpleRSSReaderAsync(ArrayAdapter<String> adapterInput, ListView listView, TextView[][] tableContents){
		this.listViewAsync = listView;
		this.adapterOutput = adapterInput;
		this.tableContents = tableContents;
	}

	//���C���̃��\�b�h�Ƃ͔񓯊��ŏ��������郁�\�b�h
	@Override
	protected ArrayAdapter<String> doInBackground(String... params){
		final int DAY_MAX = SimpleRSSReaderActivity.DAY_MAX;
		rssData = new String[DAY_MAX][]; //���ʃf�[�^���P������������`
		boolean isItem = false; //�^�O���f
		boolean isImage = false; //�^�O���f
		int dayCount = 0; //���[�v�ϐ�
		
    	try {
    		//�l�b�g�ォ������擾����̂ɕK�v�ȃI�u�W�F�N�g��`
			URL url = new URL(params[0]);
			XmlPullParser xmlPullParser = Xml.newPullParser();
			try {
	    		xmlPullParser.setInput(url.openStream(), "UTF-8");
	    		int rssDataEvent = 0; //�^�O�̎�ނ�\�킷����
	    		while((rssDataEvent = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT){ //End�^�O����Ȃ��ԃ��[�v
	    			if(rssDataEvent == XmlPullParser.START_TAG){ //Start�^�O�Ȃ�
	    				if(xmlPullParser.getName().equals("item")){ //item�^�O�Ȃ�
	    					isItem = true;
	    				}
	    				if(xmlPullParser.getName().equals("image")){ //image�^�O�Ȃ�
	    					isImage = true;
	    				}
	    				//�X�^�[�g�^�O���ŁAitem�^�O���A����image�^�O���łȂ��A����title�^�O�ł���Ƃ��ɁA�f�[�^���擾
		    			if(xmlPullParser.getName().equals("title") && isItem && !isImage){
		    				rssData[dayCount] = xmlPullParser.nextText().split(" "); //���̎��̃f�[�^����A" "�ŃX�v���b�g���ĕϐ�rssData�Ɏ擾
		    				adapterOutput.add(rssData[dayCount][0] + "\t\t" + rssData[dayCount][1] + "\t\t" + rssData[dayCount][2]); //���X�g�r���[�p�A�_�v�^�[�Ƀf�[�^��������Z�b�g
		    				dayCount++;
		    			}
	    			} else if(rssDataEvent == XmlPullParser.END_TAG){ //End�^�O�Ȃ�
	    				if(xmlPullParser.getName().equals("image")){ //image�^�O�Ȃ�
	    					isImage = false;
	    				}
		    			if(xmlPullParser.getName().equals("item")){ //item�^�O�Ȃ�
		    				isItem = false;
		    			}	    			
	    			}
	    			if(dayCount == DAY_MAX){ //�R�����f�[�^���擾������break
	    				break;
	    			}
	    		}
	    		return adapterOutput; //onPostExecute���\�b�h�ɃA�_�v�^�[�������Ƃ��ēn���B
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

	//doInBackGround���\�b�h���Ŏ擾�����f�[�^�����ɁAUI�̕\����ύX�B��onPostExecute���\�b�h���ł���UI�͕ύX�ł��Ȃ��B
	protected void onPostExecute(ArrayAdapter<String> adapterInput){
		final int DAY_MAX = SimpleRSSReaderActivity.DAY_MAX;
		final int CONTENTS_NUM = SimpleRSSReaderActivity.CONTENTS_NUM;
    	Log.d(TAG, "in postExe!!!");
		listViewAsync.setAdapter(adapterInput); //���ʃ��X�g�r���[�Ƀ��X�g�r���[�p�A�_�v�^�[���Z�b�g
		for(int i = 0; i < CONTENTS_NUM; i++){
			for(int j = 0; j < DAY_MAX; j++){
				tableContents[i][j].setText(rssData[i][j]); //���ʃe�[�u���Ƀe�L�X�g�f�[�^rssData���Z�b�g
			}
		}
	}
}
