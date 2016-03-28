package com.example.ece655a3;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment
{
   // callback methods implemented by MainActivity  
   public interface DetailsFragmentListener
   {
      // called when a question is deleted
      public void onQuestionDeleted();
      
      // called to pass Bundle of question's info for editing
      public void onEditQuestion(Bundle arguments);
   }
   
   private DetailsFragmentListener listener;
   
   private long rowID = -1; // selected question's rowID
   private TextView questionTextView; // displays question's name 
   private TextView answerTextView; // displays question's answer
   private TextView firstTextView; // displays question's firstnative answer
   private TextView secondTextView; // displays question's firstnative answer
   private TextView thirdTextView; // displays question's firstnative answer
   private TextView timerValueTextView; // displays question's timer settings
   
   
   // set DetailsFragmentListener when fragment attached   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (DetailsFragmentListener) activity;
   }
   
   // remove DetailsFragmentListener when fragment detached
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null;
   }

   // called when DetailsFragmentListener's view needs to be created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);  
      setRetainInstance(true); // save fragment across config changes

      // if DetailsFragment is being restored, get saved row ID
      if (savedInstanceState != null) 
         rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
      else 
      {
         // get Bundle of arguments then extract the question's row ID
         Bundle arguments = getArguments(); 
         
         if (arguments != null)
            rowID = arguments.getLong(MainActivity.ROW_ID);
      }
         
      // inflate DetailsFragment's layout
      View view = 
         inflater.inflate(R.layout.fragment_details, container, false);               
      setHasOptionsMenu(true); // this fragment has menu items to display

      // get the EditTexts
      questionTextView = (TextView) view.findViewById(R.id.questionTextView);
      answerTextView = (TextView) view.findViewById(R.id.answerTextView);
      firstTextView = (TextView) view.findViewById(R.id.firstTextView);
      secondTextView = (TextView) view.findViewById(R.id. secondTextView);
      thirdTextView = (TextView) view.findViewById(R.id.thirdTextView);
      timerValueTextView = (TextView) view.findViewById(R.id.timerValueTextView);
      return view;
   }
   
   // called when the DetailsFragment resumes
   @Override
   public void onResume()
   {
      super.onResume();
      new LoadQuestionTask().execute(rowID); // load question at rowID
   } 

   // save currently displayed question's row ID
   @Override
   public void onSaveInstanceState(Bundle outState) 
   {
       super.onSaveInstanceState(outState);
       outState.putLong(MainActivity.ROW_ID, rowID);
   }

   // display this fragment's menu items
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_details_menu, menu);
   }

   // handle menu item selections
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId())
      {
         case R.id.action_edit: 
            // create Bundle containing question data to edit
            Bundle arguments = new Bundle();
            arguments.putLong(MainActivity.ROW_ID, rowID);
            arguments.putCharSequence("question", questionTextView.getText());
            arguments.putCharSequence("answer", answerTextView.getText());
            arguments.putCharSequence("first", firstTextView.getText());
            arguments.putCharSequence("second", secondTextView.getText());
            arguments.putCharSequence("third", thirdTextView.getText());
            arguments.putCharSequence("timer", timerValueTextView.getText());

                        
            listener.onEditQuestion(arguments); // pass Bundle to listener
            return true;
         case R.id.action_delete:
            deleteQuestion();
            return true;
      }
      
      return super.onOptionsItemSelected(item);
   } 
   
   // performs database query outside GUI thread
   private class LoadQuestionTask extends AsyncTask<Long, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      // open database & get Cursor representing specified question's data
      @Override
      protected Cursor doInBackground(Long... params)
      {
         databaseConnector.open();
         return databaseConnector.getOneQuestion(params[0]);
      } 

      // use the Cursor returned from the doInBackground method
      @Override
      protected void onPostExecute(Cursor result)
      {
         super.onPostExecute(result);
         result.moveToFirst(); // move to the first item 
   
         // get the column index for each data item
         int questionIndex = result.getColumnIndex("question");
         int answerIndex = result.getColumnIndex("answer");
         int firstIndex = result.getColumnIndex("first");
         int secondIndex = result.getColumnIndex("second");
         int thirdIndex = result.getColumnIndex("third");
         int timerIndex = result.getColumnIndex("timer");

         
   
         // fill TextViews with the retrieved data
         questionTextView.setText(result.getString(questionIndex));
         answerTextView.setText(result.getString(answerIndex));
         firstTextView.setText(result.getString(firstIndex));
         secondTextView.setText(result.getString(secondIndex));
         thirdTextView.setText(result.getString(thirdIndex));
         timerValueTextView.setText(result.getString(timerIndex));

         
   
         result.close(); // close the result cursor
         databaseConnector.close(); // close database connection
      } // end method onPostExecute
   } // end class LoadQuestionTask

   // delete a question
   private void deleteQuestion()
   {         
      // use FragmentManager to display the confirmDelete DialogFragment
      confirmDelete.show(getFragmentManager(), "confirm delete");
   } 

   // DialogFragment to confirm deletion of question
   private DialogFragment confirmDelete = 
      new DialogFragment()
      {
         // create an AlertDialog and return it
         @Override
         public Dialog onCreateDialog(Bundle bundle)
         {
            // create a new AlertDialog Builder
            AlertDialog.Builder builder = 
               new AlertDialog.Builder(getActivity());
      
            builder.setTitle(R.string.confirm_title); 
            builder.setMessage(R.string.confirm_message);
      
            // provide an OK button that simply dismisses the dialog
            builder.setPositiveButton(R.string.button_delete,
               new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(
                     DialogInterface dialog, int button)
                  {
                     final DatabaseConnector databaseConnector = 
                        new DatabaseConnector(getActivity());
      
                     // AsyncTask deletes question and notifies listener
                     AsyncTask<Long, Object, Object> deleteTask =
                        new AsyncTask<Long, Object, Object>()
                        {
                           @Override
                           protected Object doInBackground(Long... params)
                           {
                              databaseConnector.deleteQuestion(params[0]); 
                              return null;
                           } 
      
                           @Override
                           protected void onPostExecute(Object result)
                           {                                 
                              listener.onQuestionDeleted();
                           }
                        }; // end new AsyncTask
      
                     // execute the AsyncTask to delete question at rowID
                     deleteTask.execute(new Long[] { rowID });               
                  } // end method onClick
               } // end anonymous inner class
            ); // end call to method setPositiveButton
            
            builder.setNegativeButton(R.string.button_cancel, null);
            return builder.create(); // return the AlertDialog
         }
      }; // end DialogFragment anonymous inner class
} // end class DetailsFragment