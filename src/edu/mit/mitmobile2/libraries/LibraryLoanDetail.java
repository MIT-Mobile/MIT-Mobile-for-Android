package edu.mit.mitmobile2.libraries;

import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryLoanDetail extends Activity{
    private TextView loanTitleTV;
    private TextView loanAuthorTV;
    private TextView loanCallNoTV;
	private TextView loanLibraryTV;
	private TextView loanISBNTV;
	private TextView loanOverdueTV;
	private Button loanRenewButton;
    
    private int index;
    
//        Intent intent = new Intent(context, LibraryDetailActivity.class);
//        intent.putExtra(LibraryLoanDetail.KEY_POSITION, position);
//
//        context.startActivity(intent);
//    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_loan_detail);
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        LoanListItem item = LibraryLoans.getLoanData().getLoans().get(index);

        loanTitleTV = (TextView)findViewById(R.id.loanTitleTV);
        loanTitleTV.setText(item.getTitle());

        loanAuthorTV = (TextView)findViewById(R.id.loanAuthorTV);
        loanAuthorTV.setText(item.getYear() + " " + item.getAuthor());

        loanCallNoTV = (TextView)findViewById(R.id.loanCallNoTV);
        loanCallNoTV.setText(item.getCallNo());
      
        loanLibraryTV = (TextView)findViewById(R.id.loanLibraryTV);
        loanLibraryTV.setText("");

        loanISBNTV = (TextView)findViewById(R.id.loanISBNTV);
        loanISBNTV.setText(item.getIsbnIssnDisplay());

        loanOverdueTV = (TextView)findViewById(R.id.loanOverdueTV);
        loanOverdueTV.setText(item.getDueText());

    }
    
}
