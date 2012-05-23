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
	//�t�B�[���h�錾�B�r���[�̐錾����
	static final String TAG = "SimpleRSSReaderActivity"; //�f�o�b�O�p�̃^�O
	static final int DAY_MAX = 3; //�������\�����邩
	static final int CONTENTS_NUM = 3; //�P��������̃R���e���c�̐��i���t�A�V�C�A�C���j
	ArrayList<String> areaList; //�X�s�i�[�ŕ\������n�������i�[
	Spinner selectSpinner;// //�n���I���X�s�i�[
	ArrayAdapter<String> spinnerAdapter; //�X�s�i�[�p�A�_�v�^�[
	HashMap<String, String> urlMap; //�X�s�i�[�őI�����ꂽ�n����URL�Ƃ̊֘A�n�b�V���}�b�v
	TextView[][] tableContents; //���ʃe�[�u��
	ListView listViewData; //���ʃ��X�g�r���[
	ArrayAdapter<String> listAdapter; //���X�g�r���[�p�A�_�v�^�[
	String selectedArea; //�X�s�i�[�őI�����ꂽ�n����
	
	//�A�N�e�B�r�e�B���ň�ԍŏ��ɌĂяo����郁�\�b�h
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); //�����܂ł��܂��Ȃ�

        //�X�s�i�[�֘A�̃r���[�̃��C�A�E�g�Ƃ̊֘A�t��
    	selectSpinner = (Spinner)findViewById(R.id.AreaSpinner); //�n���I���X�s�i�[
    	spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item); //�X�s�i�[�p�A�_�v�^�[
    	spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //�X�s�i�[�h���b�v�_�E����UI��`
    	this.setAreaList(); //�X�s�i�[�ŕ\������n�������i�[����A���A�N�e�B�r�e�B���̃��\�b�h�Ăяo��
    	this.initializeSpinnerAdapter(); //�X�s�i�[�p�A�_�v�^�[��areaList�̒n�������Z�b�g
    	selectSpinner.setAdapter(spinnerAdapter); //�X�s�i�[�p�A�_�v�^�[���X�s�i�[�ɃZ�b�g
    	selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //�X�s�i�[�őI�����ꂽ���ڂɑ΂��鏈��
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Spinner spinner = (Spinner)parent;
				selectedArea = (String)spinner.getItemAtPosition(position); //selectedArea�ɃX�s�i�[�őI�������n�����i�[
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { //�������Ȃ�
				// TODO Auto-generated method stub
			}
		});
	}

	//�{�^���������ꂽ�Ƃ��ɌĂяo����郁�\�b�h
    public void parseRSS(View view) throws IOException, InterruptedException{
    	//���ʊ֘A�̃r���[�ƃ��C�A�E�g�Ƃ̊֘A�t��
    	listViewData = (ListView)findViewById(R.id.ListViewData); //���ʃ��X�g�r���[
    	listAdapter = new ArrayAdapter<String>(this, R.layout.list); //���X�g�r���[�p�A�_�v�^�[
    	tableContents = new TextView[DAY_MAX][CONTENTS_NUM]; //���ʃe�[�u��
    	urlMap = new HashMap<String, String>(); //�I�����ꂽ�n������URL�Ƃ̊֘A�n�b�V���}�b�v
    	TextView titleTextView = (TextView)findViewById(R.id.TitleTextView); //�I�����ꂽ�n����\������^�C�g���e�L�X�g�r���[

    	this.initializeURLMap(); //�n������URL�Ƃ̊֘A�n�b�V���}�b�v�̒�`���\�b�h�Ăяo��
    	this.initializeTableContents(); //���ʃe�[�u���ƃ��C�A�E�g�Ƃ̊֘A�t�����\�b�h�Ăяo��

    	titleTextView.setText(selectedArea + "�n���̓V�C"); //�^�C�g���e�L�X�g�r���[�Ƀe�L�X�g���Z�b�g
    	String rssURL = urlMap.get(selectedArea); //�I�����ꂽ�n����URL
    	
    	//�l�b�g��ʂ������擾�ɂ́AAsyncTask�𗘗p�B
    	SimpleRSSReaderAsync asyncTask = new SimpleRSSReaderAsync(listAdapter, listViewData, tableContents); //AsyncTask���C���X�^���X��
    	asyncTask.execute(rssURL); //AsyncTask���s�F���ʂ̕\����asyncTask�N���X���ōs��
    }

	private void setAreaList() { //�X�s�i�[�ŕ\������n�������i�[����A���A�N�e�B�r�e�B���̃��\�b�h�Ăяo��
		// TODO Auto-generated method stub
		areaList = new ArrayList<String>();

		areaList.add("�D�y");
    	areaList.add("���");
    	areaList.add("����");
    	areaList.add("���É�");
    	areaList.add("���");
    	areaList.add("�L��");
    	areaList.add("����");
    	areaList.add("�ߔe");
	}
	private void initializeURLMap() { //�n������URL�Ƃ̊֘A�n�b�V���}�b�v�̒�`���\�b�h�Ăяo��
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
	private void initializeTableContents() { //���ʃe�[�u���ƃ��C�A�E�g�Ƃ̊֘A�t�����\�b�h�Ăяo��
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
	private void initializeSpinnerAdapter() { //�X�s�i�[�p�A�_�v�^�[��areaList�̒n�������Z�b�g

		// TODO Auto-generated method stub
		for(String s : areaList){
			spinnerAdapter.add(s);
		}
	}
}