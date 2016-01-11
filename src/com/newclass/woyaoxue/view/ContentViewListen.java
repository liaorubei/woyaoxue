package com.newclass.woyaoxue.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.LazyViewPager.OnPageChangeListener;
import com.voc.woyaoxue.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ContentViewListen extends ContentView
{

	private LazyViewPager viewpager;
	private RadioGroup ll_levels;
	private List<ContentView> contentViews = new ArrayList<ContentView>();
	private Gson gson = new Gson();
	private MyAdapter adapter;
	private List<Level> levels = new ArrayList<Level>();

	public ContentViewListen(Context context)
	{
		super(context);
		initData();
	}

	private void initData()
	{
		contentViews.add(new ContentViewDownload(getContext()));
		adapter.notifyDataSetChanged();
		HttpUtil.post(NetworkUtil.levelSelect, null, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Response<List<Level>> resp = gson.fromJson(responseInfo.result, new TypeToken<Response<List<Level>>>()
				{}.getType());
				if (resp.code == 200)
				{
					RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.MATCH_PARENT, 1);
					levels = resp.info;
					Collections.sort(levels, new Comparator<Level>()
					{

						@Override
						public int compare(Level lhs, Level rhs)
						{
							return Integer.valueOf(lhs.Sort).compareTo(rhs.Sort);
						}
					});

					for (int i = 0; i < levels.size(); i++)
					{
						Level level = levels.get(i);

						RadioButton child = new RadioButton(getContext());
						child.setButtonDrawable(null);
						child.setGravity(Gravity.CENTER);
						child.setText(level.Name);
						child.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						ll_levels.addView(child, i, params);

						contentViews.add(0, new ContentViewFolder(getContext(), level.Id));
					}
					adapter.notifyDataSetChanged();

					int childCount = ll_levels.getChildCount();
					for (int i = 0; i < childCount; i++)
					{
						final int position = i;
						RadioButton childAt = (RadioButton) ll_levels.getChildAt(i);
						childAt.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								viewpager.setCurrentItem(position);
							}
						});
					}

				}

				showView(ViewState.SUCCESS);
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				showView(ViewState.FAILURE);
			}
		});

	}

	@Override
	public View onCreateSuccessView()
	{
		View inflate = View.inflate(getContext(), R.layout.contentview_listen, null);
		viewpager = (LazyViewPager) inflate.findViewById(R.id.viewpager);
		ll_levels = (RadioGroup) inflate.findViewById(R.id.ll_levels);

		adapter = new MyAdapter();
		viewpager.setAdapter(adapter);
		viewpager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				RadioButton childAt = (RadioButton) ll_levels.getChildAt(position);
				childAt.setChecked(true);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
				// TODO Auto-generated method stub

			}
		});

		return inflate;
	}

	private class MyAdapter extends PagerAdapter
	{

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return contentViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			View view = contentViews.get(position);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
		}

	}

}
