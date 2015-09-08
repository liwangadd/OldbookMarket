package com.yunjian.activity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.adapter.SearchResultAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;
import com.yunjian.view.SelectSearchPop;
import com.yunjian.view.SelectSearchPop.OnItemSelListener;

public class SearchActivity extends Activity implements OnClickListener,
		OnQueryCompleteListener, OnItemClickListener {

	private EditText autoCompleteTextView;
	private Button searchButton;
	private ListView listView;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	// 保存历史记录
	private String history;
	private String[] historys;
	// 清空历史记录控件
	private View clearView;
	// 历史记录adapter
	private ArrayAdapter<String> historyAdapter;
	private String content;
	private SearchResultAdapter resultAdapter;
	private List<Map<String, Object>> list;
	private boolean isHistory = true;
	private View headerView, footerView;
	private LoadingDialog dialog;
	private SelectSearchPop searchPop;
	private View selectTab;
	private TextView selectContentView;
	private View selectSearchView;
	private int currentType = 0;
	private String[] items = new String[] { "教材/课件/笔记", "文学/小说/动漫", "历史/艺术/人文",
			"IT/工业技术", "经济/管理/法律", "数学/自然科学", "考试/外语/工具", "其他" };
	private View deleteTabView;
	private View backView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		sharedPreferences = this.getSharedPreferences("Historylist",
				Context.MODE_PRIVATE);
		initView();
	}

	public void initView() {
		autoCompleteTextView = (EditText) findViewById(R.id.search_et);
		listView = (ListView) findViewById(R.id.search_list);
		searchButton = (Button) findViewById(R.id.search_btn);
		selectTab = findViewById(R.id.select_tab);
		selectContentView = (TextView) findViewById(R.id.select_content);
		headerView = getLayoutInflater().inflate(
				R.layout.search_history_header, null);
		footerView = getLayoutInflater().inflate(
				R.layout.search_history_footer, null);
		listView.addHeaderView(headerView);
		listView.addFooterView(footerView);
		clearView = footerView.findViewById(R.id.clear);
		selectSearchView = findViewById(R.id.select_search);
		deleteTabView = findViewById(R.id.delete_tab);
		backView = findViewById(R.id.back);

		dialog = new LoadingDialog(this);
		searchPop = new SelectSearchPop(this);
		getData();

		listView.setAdapter(historyAdapter);
		listView.setOnItemClickListener(this);
		deleteTabView.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		clearView.setOnClickListener(this);
		selectSearchView.setOnClickListener(this);
		backView.setOnClickListener(this);

		searchPop.setOnItemSelListener(new OnItemSelListener() {

			@Override
			public void selPosition(int position) {
				selectTab.setVisibility(View.VISIBLE);
				selectContentView.setText(items[position]);
				currentType = position + 1;
				new BookService().searchBook(content, Utils.curUniversity,
						String.valueOf(currentType), String.valueOf(1),
						SearchActivity.this);
			}
		});

		autoCompleteTextView
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							searchButton.setVisibility(View.VISIBLE);
							backView.setVisibility(View.GONE);
							selectSearchView.setVisibility(View.GONE);
						}
					}
				});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.search_btn:
			content = autoCompleteTextView.getText().toString();
			if (content.equals("")) {
				Toast.makeText(this, "请输入要搜索的内容", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!Arrays.asList(historys).contains(content)) {
				if (!history.equals("")) {
					history += ",";
				}
				history += content;
				editor = sharedPreferences.edit();
				editor.putString("history", history);
				editor.commit();
			}
			isHistory = false;
			new BookService().searchBook(content, Utils.university,
					String.valueOf(currentType), String.valueOf(1), this);
			dialog.show();
			searchButton.setVisibility(View.GONE);
			backView.setVisibility(View.VISIBLE);
			selectSearchView.setVisibility(View.VISIBLE);
			autoCompleteTextView.clearFocus();
			break;
		case R.id.clear:
			editor = sharedPreferences.edit();
			editor.putString("history", "");
			editor.commit();
			getData();
			listView.setAdapter(historyAdapter);
			Toast.makeText(SearchActivity.this, "历史记录已清除", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.select_search:
			searchPop.showAsDropDown(selectSearchView, -getResources()
					.getDisplayMetrics().widthPixels / 4, 0);
			break;
		case R.id.delete_tab:
			selectTab.setVisibility(View.GONE);
			currentType = 0;
			new BookService().searchBook(content, Utils.university,
					String.valueOf(currentType), String.valueOf(1), this);
			break;
		case R.id.back:
			getData();
			isHistory = true;
			listView.setAdapter(null);
			listView.addHeaderView(headerView);
			listView.addFooterView(footerView);
			listView.setAdapter(historyAdapter);
			searchButton.setVisibility(View.VISIBLE);
			backView.setVisibility(View.GONE);
			selectSearchView.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		// TODO Auto-generated method stub
		if (result != null) {
			System.out.println(result.toString());
			dialog.dismiss();
			listView.removeHeaderView(headerView);
			listView.removeFooterView(footerView);
			list = (List<Map<String, Object>>) result;
			resultAdapter = new SearchResultAdapter(this, list);
			listView.setAdapter(resultAdapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (isHistory) {
			if (position != 0 && position != historys.length + 1) {
				dialog.show();
				isHistory = false;
				content = historys[position - 1];
				new BookService().searchBook(historys[position - 1],
						Utils.curUniversity, String.valueOf(currentType),
						String.valueOf(1), this);
				searchButton.setVisibility(View.GONE);
				backView.setVisibility(View.VISIBLE);
				selectSearchView.setVisibility(View.VISIBLE);
				autoCompleteTextView.clearFocus();
			}
		} else {
			Intent intent = new Intent(this, BookDetailActivity.class);
			intent.putExtra("book_id", list.get(position).get("book_id")
					.toString());
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	// 加载目录
	private void getData() {
		history = sharedPreferences.getString("history", "");
		if (!"".equals(history)) {
			historys = history.split(",");
		} else {
			historys = new String[0];
		}
		historyAdapter = new ArrayAdapter<String>(this,
				R.layout.search_history_item, R.id.history_item, historys);
	}

}
