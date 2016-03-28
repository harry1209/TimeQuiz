package com.example.ece655a3;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.widget.Button;


public class LoginFragment extends Fragment {
	
	// callback method implemented by MainActivity  
    public interface LoginFragmentListener
    {
       public void onLoginButtonClick(String user);
    }
   
    private LoginFragmentListener listener;
    
    private Button qtLoginButton;
    private Button qmLoginButton;
	
    
    //set LoginFragmentListener when fragment attached
    @Override
    public void onAttach(Activity activity)
    {
    	super.onAttach(activity);
    	listener = (LoginFragmentListener) activity;
    }
   
    //remove LoginFragmentListener when fragment detached
    @Override
    public void onDetach()
    {
    	super.onDetach();
    	listener = null;
    }
    

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		
		qtLoginButton = (Button) view.findViewById(R.id.qtLoginButton);
		qtLoginButton.setOnClickListener(LoginButtonListener);
		qtLoginButton.setText("Taker");
		qmLoginButton = (Button) view.findViewById(R.id.qmLoginButton);
		qmLoginButton.setOnClickListener(LoginButtonListener);
		
		return view;
	}
	
	private OnClickListener LoginButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Button button = ((Button) v);
			if ((String)button.getText() == "Taker")
			{
				listener.onLoginButtonClick("Taker");
			}
			else
			{
				listener.onLoginButtonClick("Master");
			}
		}
	};
	
	
}
