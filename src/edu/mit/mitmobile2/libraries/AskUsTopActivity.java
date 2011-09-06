package edu.mit.mitmobile2.libraries;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;

public class AskUsTopActivity extends ModuleActivity {

    private TwoLineActionRow askUsRow;
    private TwoLineActionRow makeAppontmentRow;
    private TwoLineActionRow generalHelpRow;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.library_askus_top);
        
        askUsRow = (TwoLineActionRow) findViewById(R.id.askUsItem);
        makeAppontmentRow = (TwoLineActionRow) findViewById(R.id.makeAppointmentItem);
        generalHelpRow = (TwoLineActionRow) findViewById(R.id.generalHelpItem);
        ImageView phoneImage = (ImageView) generalHelpRow.findViewById(R.id.simpleRowActionIcon);
        phoneImage.setImageResource(R.drawable.action_phone);
        
        askUsRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AskUsTopActivity.this, AskUsActivity.class));
            }
        });
        makeAppontmentRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AskUsTopActivity.this, AppointmentActivity.class));
            }
        });
        generalHelpRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonActions.callPhone(AskUsTopActivity.this, generalHelpRow.getTitle().toString());
            }
        });
        

    }
    
    @Override
    protected Module getModule() {
        return new LibraryModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

    @Override
    protected void prepareActivityOptionsMenu(Menu menu) {
    }

}
