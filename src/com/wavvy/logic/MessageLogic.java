package com.wavvy.logic;

import java.util.List;

import android.content.Context;
import android.widget.ListView;

import com.wavvy.R;
import com.wavvy.logic.adapters.MessageAdapter;
import com.wavvy.model.Message;

public class MessageLogic extends BaseContext {

	private final MessageAdapter mAdapter;
	private final ListView mList;
	
	public MessageLogic(Context context, ListView list) {
	
		super(context);

		this.mAdapter = new MessageAdapter(context, R.layout.message_row);
		this.mList = list;
		
		this.mList.setAdapter(this.mAdapter);
	}
	
	public void add(String text) {
	
		final Message message = new Message();
		message.setIsMine();
		message.setMessage(text);
		message.setFromIdUser(-1);
		
		this.mAdapter.add(message);
		this.update();
	}
	
	public void addTheirs(List<Message> messages) {
	
		this.mAdapter.addTheirs(messages);
		this.update();
	}
	
	private void update() {
		
		this.mAdapter.notifyDataSetChanged();
		this.mList.setSelection(this.mAdapter.getCount() - 1);
	}
}