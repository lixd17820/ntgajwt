package com.ntga.jwt;

import java.util.LinkedList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewManagerDemo extends Activity {
	/** Called when the activity is first created. */
	Button addViewButton;
	Button removeViewButton;
	LinearLayout myLayout;
	private LinkedList<TextView> textViews;

	boolean isEdited = false;

	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_manager_demo);

		textViews = new LinkedList<TextView>();
		findViews();
		setListeners();
		context = this;
	}

	private void findViews() {
		addViewButton = (Button) this.findViewById(R.id.addView);
		removeViewButton = (Button) this.findViewById(R.id.removeView);
		myLayout = (LinearLayout) this.findViewById(R.id.liLayout);

		textViews.addFirst((TextView) this.findViewById(R.id.textView_1));
		textViews.addFirst((TextView) this.findViewById(R.id.textView_2));
		textViews.addFirst((TextView) this.findViewById(R.id.textView_3));
	}

	private void setListeners() {
		if (addViewButton != null) {
			this.addViewButton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					TextView myTextView = new TextView(context);
					myTextView.setText("I am new TextView.");
					myTextView.setGravity(Gravity.CENTER);

					textViews.addFirst(myTextView);

					myLayout.addView(myTextView,
							new LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT)); // 调用addView
					if (!isEdited) {
						isEdited = true;
						myLayout.updateViewLayout(textViews.getLast(),
								new LinearLayout.LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.WRAP_CONTENT));
					}
				}
			});
		}

		if (removeViewButton != null) {
			this.removeViewButton
					.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							TextView tView = textViews.remove();
							myLayout.removeView(tView); // 移除View
							if (isEdited) {
								isEdited = false;
								myLayout.updateViewLayout(textViews.getLast(),
										new LinearLayout.LayoutParams(
												LayoutParams.WRAP_CONTENT,
												LayoutParams.WRAP_CONTENT));
							}
						}
					});
		}
	}
}
