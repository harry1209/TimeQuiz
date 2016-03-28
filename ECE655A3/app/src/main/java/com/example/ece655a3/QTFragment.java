package com.example.ece655a3;


import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class QTFragment extends Fragment{

  
   
   //GUI element references
   private TextView qtquestionTestView;
   private TextView resultTestView;
   private Button answers1Button;
   private Button answers2Button;
   private Button answers3Button;
   private Button answers4Button;
   private Button startButton;
   private int random_num;
   private DatabaseConnector databaseConnector;
   private int counter;
   private int correctAnswers;
   private Handler handler; // used to delay loading question
   private Cursor questionCursor; // adapter for database
   private Timer myTimer;
   private TimerTask myTimerTask;
   private Drawable background;
   TextView timerTextView;
   long startTime = 0;
   private Handler timerHandler;
   private List<String> question_list;
   
   // called when Fragment's view needs to be created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
		super.onCreateView(inflater, container, savedInstanceState);
		question_list = new ArrayList<String>();
		// inflate GUI and get references to EditTexts
		View view = inflater.inflate(R.layout.fragment_quiz_taker, container, false);
		qtquestionTestView = (TextView) view.findViewById(R.id.qtQuestionTextView);
		resultTestView = (TextView) view.findViewById(R.id.resultTextView);
		timerTextView = (TextView) view.findViewById(R.id.timerSetTextView);
		answers1Button = (Button) view.findViewById(R.id.answers1Button);
		answers2Button = (Button) view.findViewById(R.id.answers2Button);
		answers3Button = (Button) view.findViewById(R.id.answers3Button);
		answers4Button = (Button) view.findViewById(R.id.answers4Button);
		startButton = (Button) view.findViewById(R.id.startButton);
		background = answers1Button.getBackground();
	    // configure listeners for the guess Buttons
		answers1Button.setOnClickListener(guessButtonListener);
		answers2Button.setOnClickListener(guessButtonListener);
		answers3Button.setOnClickListener(guessButtonListener);
		answers4Button.setOnClickListener(guessButtonListener);
		startButton.setOnClickListener(startButtonListener);
		
		//make everything invisible except for start button
	    answers1Button.setVisibility(4);
	    answers2Button.setVisibility(4);
	    answers3Button.setVisibility(4);
	    answers4Button.setVisibility(4);
	    resultTestView.setVisibility(4);
	    qtquestionTestView.setVisibility(4);
	    
		timerHandler=new Handler();
		correctAnswers = 0;
		counter = -1;
	    databaseConnector = new DatabaseConnector(getActivity());
	    databaseConnector.open();
	    questionCursor = databaseConnector.returnAll();
	    
		 
	    
	   return view;
   
   }
   
   //timer tick on every 1 s 
   public void onTimerTick() {
       myTimerTask = new TimerTask() {
           //this method is called every 1ms
           public void run() {                 
                timerHandler.post(new Runnable() {
                    public void run() {  
                       //update textView
                       //ERROR:textView2 cannot be resolved
                    	long millis = System.currentTimeMillis() - startTime;
                        int seconds = (int) (millis / 1000);
                        seconds =  Integer.parseInt(questionCursor.getString(6))- seconds;
                        timerTextView.setText(String.format("Time: %d seconds", seconds));
                        if (seconds == 0)
                        {
                        	time_up();
	                        
                        }
                    }
                });                    
           }};      
   }      
   private void time_up() {
	myTimer.cancel();
	resultTestView.setText("TIME'S UP!");
	resultTestView.setVisibility(0);
	// SLEEP 2 SECONDS HERE ...
	// load the next flag after a 1-second delay
	handler = new Handler();
	handler.postDelayed(
		   new Runnable()
		   { 
			   @Override
			   public void run()
			   {
				   loadNextQuestion();
			   }
		   }, 2000); // 2000 milliseconds for 2-second delay
	}
   public OnClickListener startButtonListener = new OnClickListener()
   {
	   @Override
	   public void onClick(View v)
	   {
		   Button button = ((Button) v);
           if (button.getText().equals("Start")) {
        	   myTimer = new Timer();
        	   startTime = System.currentTimeMillis();
               onTimerTick();
               myTimer.schedule(myTimerTask, 10, 1000);
               button.setVisibility(4);
               
             //reveal first question
       	    if (questionCursor.moveToFirst()) {
       		    counter = questionCursor.getInt(0);
       		    qtquestionTestView.setText(questionCursor.getString(1));
       		    question_list.clear();
       		    question_list.add(questionCursor.getString(2));
       		    question_list.add(questionCursor.getString(3));
       		 	question_list.add(questionCursor.getString(4));
       		 	question_list.add(questionCursor.getString(5));
       		 	Collections.shuffle(question_list);
       		    answers1Button.setText(question_list.get(0));
       		    answers2Button.setText(question_list.get(1));
       		    answers3Button.setText(question_list.get(2));
       		    answers4Button.setText(question_list.get(3));
       		 }
       	    
	    	    answers1Button.setVisibility(0);
	    	    answers2Button.setVisibility(0);
	    	    answers3Button.setVisibility(0);
	    	    answers4Button.setVisibility(0);
	    	    qtquestionTestView.setVisibility(0);
           }
	   }
   };
   private OnClickListener guessButtonListener = new OnClickListener()
   {
	   @Override
	   public void onClick(View v)
	   {
		   Button button = ((Button) v);
		   myTimer.cancel();
		   if (questionCursor.getString(2).equals((String)button.getText()))
		   {	
			   button.setBackgroundColor(Color.GREEN);
			   ++correctAnswers;
			   resultTestView.setText("CORRECT");
			   resultTestView.setVisibility(0);
			// SLEEP 2 SECONDS HERE ...
			// load the next flag after a 1-second delay
		   handler = new Handler();
		   handler.postDelayed(
				   new Runnable()
				   { 
					   @Override
					   public void run()
					   {
						   loadNextQuestion();
					   }
				   }, 2000); // 2000 milliseconds for 2-second delay
		   }
		   else
		   {
			   resultTestView.setText("WRONG");
			   resultTestView.setVisibility(0);
			   button.setBackgroundColor(Color.RED);
			   if(questionCursor.getString(2).equals((String)answers1Button.getText()))
			   {
				   answers1Button.setBackgroundColor(Color.GREEN);
			   }
			   if(questionCursor.getString(2).equals((String)answers2Button.getText()))
			   {
				   answers2Button.setBackgroundColor(Color.GREEN);
			   }
			   if(questionCursor.getString(2).equals((String)answers3Button.getText()))
			   {
				   answers3Button.setBackgroundColor(Color.GREEN);
			   }
			   if(questionCursor.getString(2).equals((String)answers4Button.getText()))
			   {
				   answers4Button.setBackgroundColor(Color.GREEN);
			   }
	
			// SLEEP 2 SECONDS HERE ...
				// load the next flag after a 1-second delay
			   handler = new Handler();
			   handler.postDelayed(
					   new Runnable()
					   { 
						   @Override
						   public void run()
						   {
							   loadNextQuestion();
						   }
					   }, 2000); // 2000 milliseconds for 2-second delay
			   
		   }
	   }
   };
	
   // after the user guesses a correct flag, load the next flag
   private void loadNextQuestion() 
   {

	   questionCursor.moveToNext();
	   if(questionCursor.isAfterLast() == false)
	   {
		   answers1Button.setBackground(background);
		   answers2Button.setBackground(background);
		   answers3Button.setBackground(background);
		   answers4Button.setBackground(background);
			counter = questionCursor.getInt(0);
			qtquestionTestView.setText(questionCursor.getString(1));
   		    question_list.clear();
   		    question_list.add(questionCursor.getString(2));
   		    question_list.add(questionCursor.getString(3));
   		 	question_list.add(questionCursor.getString(4));
   		 	question_list.add(questionCursor.getString(5));
   		 	Collections.shuffle(question_list);
   		    answers1Button.setText(question_list.get(0));
   		    answers2Button.setText(question_list.get(1));
   		    answers3Button.setText(question_list.get(2));
   		    answers4Button.setText(question_list.get(3));
			//make result invisible
			resultTestView.setVisibility(4);
     	    myTimer = new Timer();
     	    startTime = System.currentTimeMillis();
     	   onTimerTick();
            myTimer.schedule(myTimerTask, 10, 1000);
			
	   }
	   else
	   {
		   qtquestionTestView.setVisibility(4);
		   answers1Button.setVisibility(4);
		   answers2Button.setVisibility(4);
		   answers3Button.setVisibility(4);
		   answers4Button.setVisibility(4);
		   resultTestView.setVisibility(0);
		   timerTextView.setVisibility(4);
		   resultTestView.setText("Score:" + correctAnswers);
		   
	   }
   }
   
}
