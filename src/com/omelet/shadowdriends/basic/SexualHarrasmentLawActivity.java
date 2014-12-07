package com.omelet.shadowdriends.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.omelet.shadowdriends.R;

public class SexualHarrasmentLawActivity extends Activity {

	SexualHarrasmentLawListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sexual_harrasment_law_main);

		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.lvExp);

		// preparing list data
		prepareListData();

		listAdapter = new SexualHarrasmentLawListAdapter(this, listDataHeader,
				listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);

		// Listview Group click listener
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});

		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
			}
		});

		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
			}
		});

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("Definition of Sexual Harassment");
		listDataHeader
				.add("Stalking : A male individual stalks a female if the male engages in a course of conduct-");
		listDataHeader.add("Complaints:");
		listDataHeader.add("Punishment:");

		// Adding child data
		List<String> definition_sexual_harrasment = new ArrayList<String>();
		definition_sexual_harrasment
				.add("Unwelcome sexually determined behaviour(whether directly or by implication)"
						+ " as physical contact and advances.");
		definition_sexual_harrasment
				.add("Sexually coloured verbal representation");
		definition_sexual_harrasment
				.add("Demand or request for sexual favours");
		definition_sexual_harrasment.add("Showing pornography");
		definition_sexual_harrasment.add("Sexually coloured remark or gesture");
		definition_sexual_harrasment
				.add("Making love proposal and exerting pressure or posing threats in "
						+ "case of refusal to love proposal");
		definition_sexual_harrasment
				.add("Making love proposal and exerting pressure or posing threats in case of refusal to love proposal");

		List<String> stalking = new ArrayList<String>();
		stalking.add("Following the female");
		stalking.add("Keeping the female under surveillance");
		stalking.add("Contacting the female by post, telephone, fax, text message (SMS/MMS/blogging/twitting), email or other electronic communication or by any other means whatsoever");
		stalking.add("Acting in any other way that could reasonably be expected to arouse apprehension or fear in the female for her own safety or the safety of her family members");
		stalking.add("Causing an unauthorized computer function in a computer owned or used by the female or her family members");

		List<String> complaints = new ArrayList<String>();
		complaints
				.add("It must be ensured that the identity of the complainant and also that of the accused will not be disclosed until the allegation is proved");
		complaints
				.add("Security of complainant will be ensured by the Concerned Authority");
		complaints
				.add("Complaint can be lodged by the victim or through her relatives, friends or lawyers, and it can be sent by mail also");
		complaints
				.add("A complainant can file the complaint with a female member of the Complaint Committee separately");

		List<String> punishments = new ArrayList<String>();
		punishments
				.add("The Concerned Authority may suspend temporarily the accused person (other than students) and in case of students, may prevent them from attending their classes on the "
						+ "receipt of the recommendation of the Complaint Committee. If the accused is found guilty of sexual harassment,"
						+ " the Concerned Authority shall treat it as misconduct and take proper action according to the disciplinary rules of all work places and the "
						+ "educational institutions in both public and private sectors within 30(thirty) days and/or shall refer the matter to the appropriate Court or tribunal if the act complained of constitutes an"
						+ " offence under any penal law.");

		listDataChild.put(listDataHeader.get(0), definition_sexual_harrasment); // Header,
																				// Child
																				// data
		listDataChild.put(listDataHeader.get(1), stalking);
		listDataChild.put(listDataHeader.get(2), complaints);
		listDataChild.put(listDataHeader.get(3), punishments);
	}
}
