package com.example.ece655a3;





import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.FragmentTransaction;

/*
import android.view.Menu;
import android.view.MenuItem;
*/

public class MainActivity extends Activity 
	implements LoginFragment.LoginFragmentListener,
	QuestionListFragment.QuestionListFragmentListener,
    DetailsFragment.DetailsFragmentListener, 
    AddEditFragment.AddEditFragmentListener
{
    // keys for storing row ID in Bundle passed to a fragment
    public static final String ROW_ID = "row_id"; 
	   
	QuestionListFragment questionListFragment; // displays question list
	LoginFragment loginFragment;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        loginFragment = new LoginFragment();
        
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainer,loginFragment);
        transaction.commit();
    
    }
    
    @Override
    public void onLoginButtonClick(String user){
    	getFragmentManager().popBackStack();
    	if (user == "Taker")
    	{
    		// now that the default preferences have been set,  
            // initialize QuizFragment and start the quiz

    		QTFragment qtFragment = new QTFragment();
    		FragmentTransaction transaction = getFragmentManager().beginTransaction();
    		transaction.replace(R.id.fragmentContainer,qtFragment);
    		transaction.addToBackStack(null);
    		transaction.commit();

    	}
    	else
    	{
    		QuestionListFragment qmQuestionListFragment = new QuestionListFragment();
    		FragmentTransaction transaction = getFragmentManager().beginTransaction();
    		transaction.replace(R.id.fragmentContainer,qmQuestionListFragment);
    		transaction.addToBackStack(null);
    		transaction.commit();
    	}
    }
    
 // called when MainActivity resumes
    @Override
    protected void onResume()
    {
       super.onResume();
       
       // if questionListFragment is null, activity running on tablet, 
       // so get reference from FragmentManager
       if (questionListFragment == null)
       {
          questionListFragment = 
             (QuestionListFragment) getFragmentManager().findFragmentById(
                R.id.questionListFragment);      
       }
    }
    
    // display DetailsFragment for selected question
    @Override
    public void onQuestionSelected(long rowID)
    {
       if (findViewById(R.id.fragmentContainer) != null) // phone
          displayQuestion(rowID, R.id.fragmentContainer);
       else // tablet
       {
          getFragmentManager().popBackStack(); // removes top of back stack
          displayQuestion(rowID, R.id.rightPaneContainer);
       }
    }

    // display a question
    private void displayQuestion(long rowID, int viewID)
    {
       DetailsFragment detailsFragment = new DetailsFragment();
       
       // specify rowID as an argument to the DetailsFragment
       Bundle arguments = new Bundle();
       arguments.putLong(ROW_ID, rowID);
       detailsFragment.setArguments(arguments);
       
       // use a FragmentTransaction to display the DetailsFragment
       FragmentTransaction transaction = 
          getFragmentManager().beginTransaction();
       transaction.replace(viewID, detailsFragment);
       transaction.addToBackStack(null);
       transaction.commit(); // causes DetailsFragment to display
    }
    
    // display the AddEditFragment to add a new question
    @Override
    public void onAddQuestion()
    {
       if (findViewById(R.id.fragmentContainer) != null)
          displayAddEditFragment(R.id.fragmentContainer, null); 
       else
          displayAddEditFragment(R.id.rightPaneContainer, null);
    }
    
    // display fragment for adding a new or editing an existing question
    private void displayAddEditFragment(int viewID, Bundle arguments)
    {
       AddEditFragment addEditFragment = new AddEditFragment();
       
       if (arguments != null) // editing existing question
          addEditFragment.setArguments(arguments);
       
       // use a FragmentTransaction to display the AddEditFragment
       FragmentTransaction transaction = 
          getFragmentManager().beginTransaction();
       transaction.replace(viewID, addEditFragment);
       transaction.addToBackStack(null);
       transaction.commit(); // causes AddEditFragment to display
    }
    
    // return to question list when displayed question deleted
    @Override
    public void onQuestionDeleted()
    {
       getFragmentManager().popBackStack(); // removes top of back stack
       
       if (findViewById(R.id.fragmentContainer) == null) // tablet
          questionListFragment.updateQuestionList();
    }

    // display the AddEditFragment to edit an existing question
    @Override
    public void onEditQuestion(Bundle arguments)
    {
       if (findViewById(R.id.fragmentContainer) != null) // phone
          displayAddEditFragment(R.id.fragmentContainer, arguments); 
       else // tablet
          displayAddEditFragment(R.id.rightPaneContainer, arguments);
    }

    // update GUI after new question or updated question saved
    @Override
    public void onAddEditCompleted(long rowID)
    {
       getFragmentManager().popBackStack(); // removes top of back stack

       if (findViewById(R.id.fragmentContainer) == null) // tablet
       {
          getFragmentManager().popBackStack(); // removes top of back stack
          questionListFragment.updateQuestionList(); // refresh question

          // on tablet, display question that was just added or edited
          displayQuestion(rowID, R.id.rightPaneContainer); 
       }
    }
}




