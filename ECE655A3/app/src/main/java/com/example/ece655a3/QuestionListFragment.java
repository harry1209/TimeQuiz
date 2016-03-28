package com.example.ece655a3;


import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class QuestionListFragment extends ListFragment
{
   // callback methods implemented by MainActivity  
   public interface QuestionListFragmentListener
   {
      // called when user selects a question
      public void onQuestionSelected(long rowID);

      // called when user decides to add a question
      public void onAddQuestion();
   }
   
   private QuestionListFragmentListener listener; 
   
   private ListView questionListView; // the ListActivity's ListView
   private CursorAdapter questionAdapter; // adapter for ListView
   
   // set QuestionListFragmentListener when fragment attached   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (QuestionListFragmentListener) activity;
   }

   // remove QuestionListFragmentListener when Fragment detached
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null;
   }

   // called after View is created
   @Override
   public void onViewCreated(View view, Bundle savedInstanceState)
   {
      super.onViewCreated(view, savedInstanceState);
      setRetainInstance(true); // save fragment across config changes
      setHasOptionsMenu(true); // this fragment has menu items to display

      // set text to display when there are no questions
      setEmptyText(getResources().getString(R.string.no_questions));

      // get ListView reference and configure ListView
      questionListView = getListView(); 
      questionListView.setOnItemClickListener(viewQuestionListener);      
      questionListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      
      // map each question's name to a TextView in the ListView layout
      String[] from = new String[] { "question" };
      int[] to = new int[] { android.R.id.text1 };
      questionAdapter = new SimpleCursorAdapter(getActivity(), 
         android.R.layout.simple_list_item_1, null, from, to, 0);
      setListAdapter(questionAdapter); // set adapter that supplies data
   }

   // responds to the user touching a question's name in the ListView
   OnItemClickListener viewQuestionListener = new OnItemClickListener() 
   {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, 
         int position, long id) 
      {
         listener.onQuestionSelected(id); // pass selection to MainActivity
      } 
   }; // end viewQuestionListener

   // when fragment resumes, use a GetQuestionsTask to load questions 
   @Override
   public void onResume() 
   {
      super.onResume(); 
      new GetQuestionsTask().execute((Object[]) null);
   }

   // performs database query outside GUI thread
   private class GetQuestionsTask extends AsyncTask<Object, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      // open database and return Cursor for all questions
      @Override
      protected Cursor doInBackground(Object... params)
      {
         databaseConnector.open();
         return databaseConnector.getAllQuestions(); 
      } 

      // use the Cursor returned from the doInBackground method
      @Override
      protected void onPostExecute(Cursor result)
      {
         questionAdapter.changeCursor(result); // set the adapter's Cursor
         databaseConnector.close();
      } 
   } // end class GetQuestionsTask

   // when fragment stops, close Cursor and remove from questionAdapter 
   @Override
   public void onStop() 
   {
      Cursor cursor = questionAdapter.getCursor(); // get current Cursor
      questionAdapter.changeCursor(null); // adapter now has no Cursor
      
      if (cursor != null) 
         cursor.close(); // release the Cursor's resources
      
      super.onStop();
   } 

   // display this fragment's menu items
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_question_list_menu, menu);
   }

   // handle choice from options menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId())
      {
         case R.id.action_add:
            listener.onAddQuestion();
            return true;
      }
      
      return super.onOptionsItemSelected(item); // call super's method
   }
   
   // update data set
   public void updateQuestionList()
   {
      new GetQuestionsTask().execute((Object[]) null);
   }
} // end class QuestionListFragment