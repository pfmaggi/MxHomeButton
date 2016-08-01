package com.pietromaggi.sample.mxhomebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

public class MainActivity extends AppCompatActivity implements EMDKManager.EMDKListener {

    private EMDKManager emdkManager;
    private ProfileManager mProfileManager;
    private String profileName = "HomeButton";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        final ToggleButton tgHomeButton = (ToggleButton)findViewById(R.id.tgHomeButton);

        //Check the return status of getEMDKManager
        if(results.statusCode != EMDKResults.STATUS_CODE.SUCCESS)
        {
            //Failed to create EMDKManager object
            Toast.makeText(this, "Error retrieving EMDK Manager instance.", Toast.LENGTH_SHORT);
            tgHomeButton.setEnabled(false);
        } else {
            tgHomeButton.setEnabled(true);

            tgHomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mProfileManager != null)
                    {
                        try{

                            String[] modifyData = new String[1];

                            boolean on = ((ToggleButton) v).isChecked();
                                modifyData[0] =
                                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--This is an auto generated document. Changes to this document may cause incorrect behavior.--><wap-provisioningdoc>\n" +
                                        "  <characteristic type=\"ProfileInfo\">\n" +
                                        "    <parm name=\"created_wizard_version\" value=\"4.1.1\"/>\n" +
                                        "  </characteristic>\n" +
                                        "  <characteristic type=\"Profile\">\n" +
                                        "    <parm name=\"ProfileName\" value=\"HomeButton\"/>\n" +
                                        "    <parm name=\"ModifiedDate\" value=\"2016-08-01 16:30:10\"/>\n" +
                                        "    <parm name=\"TargetSystemVersion\" value=\"4.4\"/>\n" +
                                        "    <characteristic type=\"UiMgr\" version=\"4.3\">\n" +
                                        "      <parm name=\"emdk_name\" value=\"\"/>\n" +
                                        "      <parm name=\"HomeKeyUsage\" value=\"" + (on?"1":"2") + "\"/>\n" +
                                        "    </characteristic>\n" +
                                        "  </characteristic>\n" +
                                        "</wap-provisioningdoc>\n";

                            //Call processProfile with profile name and SET flag to create the profile. The modifyData can be null.
                            EMDKResults results = mProfileManager.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, modifyData);
                            if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
                            {
                                //Failed to set profile
                                Toast.makeText(MainActivity.this, "Failed to set the new profile", Toast.LENGTH_SHORT).show();
                            } else if (results.statusCode == EMDKResults.STATUS_CODE.CHECK_XML) {
                                String responseXML = results.getStatusString();
                                if (responseXML.contains("error")) {
                                    Toast.makeText(MainActivity.this, "Failed to set the new profile", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception ex){
                            // Handle any exception
                            Toast.makeText(MainActivity.this, "Failed to set the new profile", Toast.LENGTH_SHORT).show();
                        }


                    }
                }

            });
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        //Get the ProfileManager object to process the profiles
        mProfileManager = (ProfileManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

        if(mProfileManager != null)
        {
            try{

                String[] modifyData = new String[1];
                //Call processProfile with profile name and SET flag to create the profile. The modifyData can be null.

                EMDKResults results = mProfileManager.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, modifyData);
                if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
                {
                    //Failed to set profile
                }
            }catch (Exception ex){
                // Handle any exception
                Toast.makeText(MainActivity.this, "Failed to set the new profile", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onClosed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Clean up the objects created by EMDK manager
        emdkManager.release();
    }
}

