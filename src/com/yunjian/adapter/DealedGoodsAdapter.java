package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.AddBookActivity;
import com.yunjian.activity.R;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.fragment.MyBooksFragment;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.SerializableMap;
import com.yunjian.util.Utils;

public class DealedGoodsAdapter extends BaseAdapter{
	private List<Map<String, Object>>list;
	private LayoutInflater layoutInflater;
	private Context context;
	private ImageLoader mImageLoader;
	private OnQueryCompleteListener onQueryCompleteListener;
	private UserCenterService service;
	private Fragment dealedgoodsFragment;
	
	public DealedGoodsAdapter(Context context,List<Map<String, Object>> list,Fragment dealedgoods){
		this.context = context;
		this.layoutInflater=LayoutInflater.from(context);
		this.list = list;
		dealedgoodsFragment = dealedgoods;
		mImageLoader = ImageLoader.getInstance();
	}
	
	public class Item{
		public ImageView imageView;
		public TextView nameTextView;
		public TextView statusTextView;
		public Button editButton;
		public Button selloutButton;
		public Button sellbackButton;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressWarnings("unused")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Item item = null;
		if(item == null){
			item = new Item();
			arg1 = layoutInflater.inflate(R.layout.goodslist_item, null);
			item.imageView = (ImageView)arg1.findViewById(R.id.bookphoto);
			item.nameTextView = (TextView)arg1.findViewById(R.id.bookname);
			item.statusTextView = (TextView)arg1.findViewById(R.id.bookstaus);
			item.editButton = (Button)arg1.findViewById(R.id.edit_btn);
			item.selloutButton = (Button)arg1.findViewById(R.id.sellout_btn);
			item.sellbackButton = (Button)arg1.findViewById(R.id.sellback_btn);
			item.nameTextView.setText(list.get(arg0).get("bookname").toString());
			String status = list.get(arg0).get("status").toString();
			if(status.equals("0.0"))
				item.statusTextView.setText("正在出售");
			else if(status.equals("1.0")){			
				item.statusTextView.setText("已售出");
				item.selloutButton.setVisibility(View.GONE);
				item.editButton.setVisibility(View.GONE);
				item.sellbackButton.setVisibility(View.GONE);
			}else if(status.equals("2.0")){
				item.statusTextView.setText("已下架");
				item.selloutButton.setVisibility(View.GONE);
				item.editButton.setVisibility(View.GONE);
				item.sellbackButton.setVisibility(View.GONE);
			}
			arg1.setTag(item);
		}
		else {
			item = (Item)arg1.getTag();
		}
		//图片加载
		try {
			String imgs = list.get(arg0).get("imgs").toString().substring(1, 37);
			System.out.println(imgs);
			mImageLoader.displayImage(Utils.IMGURL + imgs, item.imageView,
					GetImgeLoadOption.getBookOption());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		onQueryCompleteListener = new OnQueryCompleteListener() {
			
			@SuppressLint("ShowToast")
			@Override
			public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
				// TODO Auto-generated method stub
				if(result.equals("success")){
					((MyBooksFragment) dealedgoodsFragment).resetService();
					Toast.makeText(context, "商品已下架", 2000).show();
				}
				else {
					Toast.makeText(context, "售出失败", 2000).show();
				}
			}
		};
		ItemClickListener itemClickListener = new ItemClickListener(arg0);
		//售出监听
		item.selloutButton.setOnClickListener(itemClickListener);
		//编辑监听
		item.editButton.setOnClickListener(itemClickListener);
		item.sellbackButton.setOnClickListener(itemClickListener);
		
		return arg1;
	}
    public class ItemClickListener implements OnClickListener{
    	private int position;
    	
    	public ItemClickListener(int position){
    		this.position = position;
    	}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0.getId()==R.id.edit_btn){
				Intent intent = new Intent(context,AddBookActivity.class);
				Bundle bundle = new Bundle();
				Map<String,Object> data=list.get(position);  
	            SerializableMap tmpmap=new SerializableMap();  
	            tmpmap.setMap(data);  
	            bundle.putSerializable("bookinfo", tmpmap);  
	            intent.putExtras(bundle);
	            Utils.IFEDITBOOK = 1;
				context.startActivity(intent);
			}
			else if(arg0.getId()==R.id.sellout_btn){
				new AlertDialog.Builder(context)   
				.setTitle("确认售出")  
				.setMessage("书籍出售后将不再显示，其他人也看不到此商品，是否确认售出")  
				.setPositiveButton("是", 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								service = new UserCenterService();
								service.setBookStatus(Utils.user_id, list.get(position).get("book_id").toString(), 1, onQueryCompleteListener);
							}
						})  
				.setNegativeButton("否", null)  
				.show();  
			}else if(arg0.getId()==R.id.sellback_btn){
				new AlertDialog.Builder(context)   
				.setTitle("确认撤销")  
				.setMessage("书籍撤销后将不再显示，其他人也看不到此商品，是否确认撤销")  
				.setPositiveButton("是", 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								service = new UserCenterService();
								service.setBookStatus(Utils.user_id, list.get(position).get("book_id").toString(), 2, onQueryCompleteListener);
							}
						})  
				.setNegativeButton("否", null)  
				.show();  
			}
		}
    } 
}
