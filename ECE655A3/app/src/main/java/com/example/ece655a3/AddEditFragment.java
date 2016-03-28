package com.example.ece655a3;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import java.text.NumberFormat;



public class AddEditFragment extends Fragment
{
   // callback method implemented by MainActivity  
   public interface AddEditFragmentListener
   {
      // called after edit completed so question can be redisplayed
      public void onAddEditCompleted(long rowID);
   }
   
   private AddEditFragmentListener listener; 
   
   private long rowID; // database row ID of the question
   private Bundle questionInfoBundle; // arguments for editing a question
   
   private int timerValue = 20;
   private static final NumberFormat integFormat = NumberFormat.getIntegerInstance();

   // EditTexts for question information
   private EditText questionEditText;
   private EditText answerEditText;
   private EditText firstEditText;
   private EditText secondEditText;
   private EditText thirdEditText;
   private TextView timerSetTextView;
   

   // set AddEditFragmentListener when Fragment attached   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (AddEditFragmentListener) activity; 
   }

   // remove AddEditFragmentListener when Fragment detached
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null; 
   }
   
   // called when Fragment's view needs to be created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);    
      setRetainInstance(true); // save fragment across config changes
      setHasOptionsMenu(true); // fragment has menu items to display
      
      // inflate GUI and get references to EditTexts
      View view = 
         inflater.inflate(R.layout.fragment_add_edit, container, false);
      questionEditText = (EditText) view.findViewById(R.id.questionEditText);
      answerEditText = (EditText) view.findViewById(R.id.answerEditText);
      firstEditText = (EditText) view.findViewById(R.id.firstEditText);
      secondEditText = (EditText) view.findViewById(R.id.secondEditText);
      thirdEditText = (EditText) view.findViewById(R.id.thirdEditText);
      timerSetTextView = (TextView) view.findViewById(R.id.timerSetTextView);
      SeekBar seekBarTimer = (SeekBar) view.findViewById(R.id.seekBarTimer);
      seekBarTimer.setOnSeekBarChangeListener(timerSeekBarListener);
      

      questionInfoBundle = getArguments(); // null if creating new question

      if (questionInfoBundle != null)
      {
         rowID = questionInfoBundle.getLong(MainActivity.ROW_ID);
         questionEditText.setText(questionInfoBundle.getString("question"));  
         answerEditText.setText(questionInfoBundle.getString("answer"));  
         firstEditText.setText(questionInfoBundle.getString("first"));  
         secondEditText.setText(questionInfoBundle.getString("second"));  
         thirdEditText.setText(questionInfoBundle.getString("third"));  
           
      } 
      
      // set Save question Button's event listener 
      Button saveQuestionButton = 
         (Button) view.findViewById(R.id.saveQuestionButton);
      saveQuestionButton.setOnClickListener(saveQuestionButtonClicked);
      return view;
   }

   
   
   // inner class that implements interface OnSeekBarChangeListener
   // called when the user changes the position of SeekBar
   
   private OnSeekBarChangeListener timerSeekBarListener = new OnSeekBarChangeListener()
   {
 	@Override
 	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
 	{
 		timerValue = progress;
 		timerSetTextView.setText(integFormat.format(timerValue));
 	}
 	
 	@Override
 	public void onStartTrackingTouch(SeekBar seekBar)
 	{
 		
 	}
 	
 	@Override
 	public void onStopTrackingTouch(SeekBar seekBar)
 	{
 		
 	}
   }; 
   
   
   // responds to event generated when user saves a question
   OnClickListener saveQuestionButtonClicked = new OnClickListener() 
   {
      @Override
      public void onClick(View v) 
      {
         if (questionEditText.getText().toString().trim().length() != 0)
         {
            // AsyncTask to save question, then notify listener 
            AsyncTask<Object, Object, Object> saveQuestionTask = 
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                     saveQuestion(); // save question to the database
                     return null;
                  } 
      
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     // hide soft keyboard
                     InputMethodManager imm = (InputMethodManager) 
                        getActivity().getSystemService(
                           Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(
                        getView().getWindowToken(), 0);

                     listener.onAddEditCompleted(rowID);
                  } 
               }; // end AsyncTask
               
            // save the question to the database using a separate thread
            saveQuestionTask.execute((Object[]) null); 
         } 
         else // required question name is blank, so display error dialog
         {
            DialogFragment errorSaving = 
               new DialogFragment()
               {
                  @Override
                  public Dialog onCreateDialog(Bundle savedInstanceState)
                  {
                     AlertDialog.Builder builder = 
                        new AlertDialog.Builder(getActivity());
                     builder.setMessage(R.string.error_message);
                     builder.setPositiveButton(R.string.ok, null);                     
                     return builder.create();
                  }               
               };
            
            errorSaving.show(getFragmentManager(), "error saving question");
         } 
      } // end method onClick
   }; // end OnClickListener saveQuestionButtonClicked

   // saves question information to the database
   private void saveQuestion() 
   {
      // get DatabaseConnector to interact with the SQLite database
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      if (questionInfoBundle == null)
      {
         // insert the question information into the database
         rowID = databaseConnector.insertQuestion(
            questionEditText.getText().toString(),
            answerEditText.getText().toString(), 
            firstEditText.getText().toString(), 
            secondEditText.getText().toString(),
            thirdEditText.getText().toString(),
            String.valueOf(timerValue));
      } 
      else
      {
         databaseConnector.updateQuestion(rowID,
            questionEditText.getText().toString(),
            answerEditText.getText().toString(), 
            firstEditText.getText().toString(), 
            secondEditText.getText().toString(),
            thirdEditText.getText().toString(),
            String.valueOf(timerValue));
      }
   } // end method saveQuestion
} // end class AddEditFragment