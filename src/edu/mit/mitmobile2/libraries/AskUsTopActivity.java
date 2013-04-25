package edu.mit.mitmobile2.libraries;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView.BufferType;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;

public class AskUsTopActivity extends NewModuleActivity {

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
        String generalHelpTitle = "General help (617.324.2275)";
        Spannable titleSpan = Spannable.Factory.getInstance().newSpannable(generalHelpTitle);
        TextAppearanceSpan secondaryStyle = new TextAppearanceSpan(this, R.style.ListItemSecondary);  
        titleSpan.setSpan(secondaryStyle, generalHelpTitle.indexOf("("), generalHelpTitle.indexOf(")")+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);     
        generalHelpRow.setTitle(titleSpan, BufferType.SPANNABLE);
        generalHelpRow.setActionIconResource(R.drawable.action_phone);
        
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
    protected NewModule getNewModule() {
        return new LibrariesModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }
}
