package sg.com.mapp.gradetracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.robertsimoes.quoteable.QuotePackage;
import com.robertsimoes.quoteable.Quoteable;

import java.util.Calendar;

public class QuoteActivity extends AppCompatActivity implements Quoteable.ResponseReadyListener {
    TextView text;
    TextView author;
    Button changeQuote;
    public static String textQuotes="";
    public static String textAuthor="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_quote);

        Quoteable quoteable=new Quoteable(this, "Jim Rohn", "Life is like the changing seasons.");
        quoteable.request();
        text=(TextView)findViewById(R.id.text);
        author=(TextView)findViewById(R.id.author);
        changeQuote=(Button)findViewById(R.id.button);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        Intent intent1 = new Intent(QuoteActivity.this, AutoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(QuoteActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) QuoteActivity.this.getSystemService(QuoteActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onQuoteResponseReady(QuotePackage response) {
        Log.d("QuoteActivity", response.getQuote());
        Log.d("QuoteActivity", response.getAuthor());
        textQuotes=response.getQuote();
        textAuthor=response.getAuthor();
        text.setText(textQuotes);
        author.setText(textAuthor);
    }

    @Override
    public void onQuoteResponseFailed(QuotePackage defaultResponse) {
        Log.d("QuoteActivity", defaultResponse.getQuote());
        Log.d("QuoteActivity", defaultResponse.getAuthor());
        textQuotes=defaultResponse.getQuote();
        textAuthor=defaultResponse.getAuthor();
    }
    public void onClick(View view) {
        Quoteable quoteable=new Quoteable(this, "Jim Rohn", "Life is like the changing seasons.");
        quoteable.request();
        Intent intent = new Intent(this, AutoReceiver.class);
        intent.setAction("VIDEO_TIMER");
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 10 * 1000, sender);

    }
}