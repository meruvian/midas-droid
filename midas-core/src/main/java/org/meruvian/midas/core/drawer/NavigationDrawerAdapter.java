package org.meruvian.midas.core.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.meruvian.midas.core.R;
import org.meruvian.midas.core.drawer.Navigation.NavigationType;

import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Navigation> navigations = new ArrayList<Navigation>();
	
	public NavigationDrawerAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}
	
	public void addNavigation(Navigation navigation) {
		navigations.add(navigation);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return navigations.size();
	}

	public Object getItem(int position) {
		return navigations.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public int getItemViewType(int position) {
		return navigations.get(position).getType().ordinal();
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			
			if (getItemViewType(position) == NavigationType.TITLE.ordinal()) 
				convertView = inflater.inflate(R.layout.adapter_navigation_title, parent, false);
			else
				convertView = inflater.inflate(R.layout.adapter_navigation_menu, parent, false);
				
			holder.menu = (TextView) convertView.findViewById(R.id.name);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.menu.setText(navigations.get(position).getName());
		
		return convertView;
	}

	private class ViewHolder {
		public TextView menu;
	}
}
