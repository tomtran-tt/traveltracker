package tk.aegisstudios.traveltracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends Activity {
	
    EditText signUser;
    EditText signPass;
    
    static boolean isCompleted;
    static String result = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        
        signUser = (EditText) findViewById(R.id.signUser);
        signPass = (EditText) findViewById(R.id.signPass);
    }
    
    public void onSignIn(View v) {
        new SignIn().execute("SIGNIN;" + signUser.getText() + "," + signPass.getText());
    }
    
    public class SignIn extends InOutSocket {
    	
        @Override
        protected void onPostExecute(String result) {
            String signToastMessage = "";
            if (result.split(";").length > 1) {
            	signToastMessage = "You successfully logged in.";
                FileOutputStream savedAuthStream = openAuthenticationData();
                
                String[] separated = result.split(";");
                boolean authSaved = writeAuthenticationData(savedAuthStream, separated[1]);
                
                new Redirection(getApplicationContext()).redirectToHome();
            } else {
                if (result.split(";").length == 1) {
                    String returnVal = result.split(";")[0];
                    if (returnVal.equals("Wrong password")) {
                        signToastMessage = "Incorrect password entered";
                    } else if (returnVal.equals("User does not exist")) {
                        signToastMessage = "User does not exist";
                    } else if (returnVal.equals("Access error")) {
                        signToastMessage = "Error while accessing database";
                    } else {
                        signToastMessage = "Invalid server response";
                    }
                }
            }
            Toast.makeText(getApplicationContext(), signToastMessage, Toast.LENGTH_LONG).show();
        }
    }

    private FileOutputStream openAuthenticationData() {
    	File savedAuth;
    	FileOutputStream savedAuthStream;
        
        savedAuth = new File(this.getFilesDir(), "savedAuth.txt");
        savedAuthStream = convertFileToStream(savedAuth);
        
        return savedAuthStream;
    }
    
    private boolean writeAuthenticationData(FileOutputStream savedAuthStream, String data) {
    	OutputStreamWriter savedAuthWriter = null;
    	
    	try {
	    	savedAuthWriter = new OutputStreamWriter(savedAuthStream);
	    	savedAuthWriter.write(data);
	    	return true;
    	} catch (IOException exception) {
    		exception.printStackTrace();
    		return false;
    	} finally {
    		try {
		    	savedAuthWriter.flush();
		    	savedAuthWriter.close();
		    	savedAuthStream.close();
    		} catch (IOException exception) {
    			exception.printStackTrace();
    			return false;
    		}
    	}
    }
    
    private FileOutputStream convertFileToStream(File fileObj) {
    	FileOutputStream savedAuthStream = null;
    	
    	try {
    		savedAuthStream = new FileOutputStream(fileObj, true);
    	} catch (FileNotFoundException exception) {
    		showToast("Could not open file to save authentication data!");
    	}
    	
    	return savedAuthStream;
    }
    
    private void showToast(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, 
                Toast.LENGTH_LONG).show();
    }
}
