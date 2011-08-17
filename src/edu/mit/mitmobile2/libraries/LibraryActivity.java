package edu.mit.mitmobile2.libraries;

import android.os.Bundle;
import android.view.Menu;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;

public class LibraryActivity extends ModuleActivity {

    private TwoLineActionRow accountRow;
    private TwoLineActionRow locationRow;
    private TwoLineActionRow askUsRow;
    private TwoLineActionRow tellUsRow;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        createViews();
    }
    
    private void createViews() {
        setContentView(R.layout.library_main);
        
        accountRow = (TwoLineActionRow) findViewById(R.id.libraryAccount);
        locationRow = (TwoLineActionRow) findViewById(R.id.libraryLocationHours);
        askUsRow = (TwoLineActionRow) findViewById(R.id.libraryAskUs);
        tellUsRow = (TwoLineActionRow) findViewById(R.id.libraryTellUs);
    }
    
    @Override
    protected Module getModule() {
        return new LibraryModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return true;
    }

    @Override
    protected void prepareActivityOptionsMenu(Menu menu) {
    }

}
