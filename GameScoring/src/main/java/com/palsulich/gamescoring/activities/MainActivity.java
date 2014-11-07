package com.palsulich.gamescoring.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
//import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.palsulich.gamescore.R;
import com.palsulich.gamescoring.core.Game;
import com.palsulich.gamescoring.sql.GameSQLiteHelper;
import com.palsulich.gamescoring.core.Player;
import com.palsulich.gamescoring.fragments.PlayerAddFragment;
import com.palsulich.gamescoring.sql.ScoresSQLiteHelper;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class MainActivity extends Activity {
    private ArrayList<Player> playerList;
    private Game game;
    private boolean unsaved = false;
    private boolean loaded = false;
    private GameSQLiteHelper gameSql;
    private ScoresSQLiteHelper scoresSql;
    //private LinearLayout table;
    private ScrollView scrollView;
    private HorizontalScrollView hScrollView;
    private RelativeLayout mainLayout;
    private LinearLayout scrollableLayout;
    private LinearLayout headerRow;
    private LinearLayout scoreEntryRow;
    private ArrayList<EditText> scoreList;
    private Button endRoundButton;
    private LinearLayout roundTotalsRow;
    private LinearLayout totalScoresRow;
    private LinearLayout buttonRow;
    private float nameFontSize;
    private float numFontSize;
    private RelativeLayout.LayoutParams lp;
    RelativeLayout.LayoutParams relLp;
    private LinearLayout.LayoutParams lpRow;
    //TableRow.LayoutParams rp;
    private LinearLayout.LayoutParams lpEqualWeight;
    private LinearLayout.LayoutParams lpEqualWidth;
    private boolean rowColorFlag;
    private static final String TAG = "MainActivity";

    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerList = new ArrayList<Player>();
        scoreList = new ArrayList<EditText>();
        gameSql = new GameSQLiteHelper(this);
        scoresSql = new ScoresSQLiteHelper(this);
        if(savedInstanceState != null){
            playerList = (ArrayList<Player>)savedInstanceState.get("playerList");
            game = (Game)savedInstanceState.get("game");
//            Log.i(TAG, "PlayerList size = " + Integer.toString(playerList.size()));
//            Log.i(TAG, "savedInstanceState not null");
        }
        else {
//            Log.i(TAG, "savedInstanceState null");
            Intent intent = getIntent();
            Game game = (Game) intent.getSerializableExtra("game");
            if(game != null){
                loaded = true;
                game.setPlayerList(scoresSql.getGameScores(game.getId()));
                this.game = game;
                playerList = game.getPlayerList();
            }
            else {
                unsaved = true;
                ArrayList<String> playerNames = intent.getStringArrayListExtra("players");
                if (playerNames == null) playerNames = new ArrayList<String>();
                String gameName = playerNames.remove(0);
                for (String name : playerNames) {
                    playerList.add(new Player(name));
                }
                getNameLetters();
                this.game = new Game(gameName);
            }
        }
        setContentView(createLayout());
    }

    protected void onResume(){
        super.onResume();

    }

    private RelativeLayout createLayout () {
        invalidateOptionsMenu();
        //Determining min width for all cells in next two lines
        Resources r = getResources();
        float viewWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 71, r.getDisplayMetrics());
        nameFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, r.getDisplayMetrics());
        numFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, r.getDisplayMetrics());
        rowColorFlag = false;
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lpRow = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpEqualWeight = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lpEqualWidth = new LinearLayout.LayoutParams(
                (int) viewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainLayout = new RelativeLayout(this);
        mainLayout.setLayoutParams(lp);
        scrollableLayout = new LinearLayout(this);
        scrollableLayout.setOrientation(LinearLayout.VERTICAL);
        scrollableLayout.setLayoutParams(lpRow);
        headerRow = createRowLinLayout();
        scoreEntryRow = createRowLinLayout();
        totalScoresRow = createRowLinLayout();
        buttonRow = createRowLinLayout();
        buttonRow.setGravity(Gravity.BOTTOM);
        for (Player p : playerList){
            headerRow.addView(createTextView(p.getName(), R.layout.header_textview));
        }
        scrollableLayout.addView(headerRow, lpRow);
        addScoreEntryAndTotalRows();
        // This is done to repopulate rows on orientation change
        addExistingRounds();
        if(playerList.size() > 11){
            ActionBar actionBar = getActionBar();
            if(actionBar != null) actionBar.hide();
        }
        return genScrollViews();
    }

    private void saveGame() {
        Context context = getApplicationContext();
        CharSequence text = "Game saved!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        if(loaded){
            //TODO Update game date to reflect last time played
            loaded = false;
        }
        else {
            if (unsaved) {
                game.setId(gameSql.addGame(game).getId());
                unsaved = false;
            }
        }
        scoresSql.addLatestRound(game);
    }

    private String[] getNameLetters(String[] list){
        //String[] list = nameList;
        String[] newList = new String[list.length];
        for(int i=0;i<list.length;i++){
            if(i==0) list[0] = list[0].toLowerCase();
            String name = list[i];
            String match = name.substring(0,1);
            int matchIndex = 2;
            for(int j=0;j<list.length;j++){
                if(i==0) list[j] = list[j].toLowerCase();
                if(j!=i){
                    if(name.toLowerCase().equals(list[j].toLowerCase())){
                        if(name.length()>4)
                            match = name.substring(0,4);
                        else
                            match = name;
                        break;
                    }
                    else{
                        while(list[j].startsWith(match) && !match.equals(name)){
                            match = name.substring(0,matchIndex);
                            matchIndex++;
                            if(matchIndex==5)
                                break;
                        }
                    }
                }
            }
            newList[i]=match.substring(0,1).toUpperCase()+match.substring(1).toLowerCase();
        }
        return newList;
    }

    private void getNameLetters(){
        //TODO Make this function more efficient; There's no need to do 3 loops here.
        String[] nameList = new String[playerList.size()];
        int index = 0;
        for(Player player : playerList){
            nameList[index] = player.getName();
            index++;
        }
        nameList = getNameLetters(nameList);
        index = 0;
        for(String name : nameList){
            playerList.get(index).setName(name);
            index++;
        }

    }

    public void addScoreEntryAndTotalRows(){
        // SCORE ENTRY ROW
        scoreList = new ArrayList<EditText>();
        for (Player player : playerList){
            EditText score = new EditText(this);
            scoreList.add(score);
            // This super hack is a terrible workaround for a bug in Android.
            // https://code.google.com/p/android/issues/detail?id=68302
            // Without this, the HorizontalScrollView will scroll all the way right on any score input.
            int orientation = this.getResources().getConfiguration().orientation;
            if (!((orientation==Configuration.ORIENTATION_PORTRAIT && playerList.size()>6) || (orientation==Configuration.ORIENTATION_LANDSCAPE && playerList.size()>8))) {
                score.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            int maxLength = 3;
            score.setGravity(Gravity.CENTER | Gravity.BOTTOM);
            score.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            score.setTypeface(null, Typeface.BOLD);
            score.setTextColor(getResources().getColor(R.color.darkgray));
            score.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                    boolean flag = true;
//                    boolean covered = s.length() > 0 ? true : false;
                    for (EditText editText : scoreList) {
                        CharSequence charSequence = editText.getText();
                        if (charSequence != null) {
                            String text = charSequence.toString();
                            if (text.isEmpty() || !isNonNegInt(text)) {
//                                if (covered && isNonNegInt(s.toString())) {
//                                    covered = false;
//                                    continue;
//                                }
                                flag = false;
                                //endRoundButton.setClickable(false);
                                endRoundButton.setEnabled(false);
                                break;
                            }
                        }
                    }
                    if (flag) endRoundButton.setEnabled(true);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //scoreEntryRow.setLayoutParams(lpEqualWeight);
            applyLayoutParams(score);
            scoreEntryRow.addView(score, lpEqualWeight);
        }
        scrollableLayout.addView(scoreEntryRow, lpRow);
        //table.addView(scoreEntryRow, lpRow);

        // SCORE TOTALS ROW

        for (Player player : playerList){
            addScoreView(player.getScoreTotal());
        }
        scrollableLayout.addView(totalScoresRow, lpRow);
        //table.addView(totalScoresRow, lpRow);

        endRoundButton = createButton(getResources().getString(R.string.end_round), R.layout.button);
        endRoundButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                startNewRound();
            }
        });
        //endRoundButton.setClickable(false);
        endRoundButton.setEnabled(false);
        buttonRow.addView(endRoundButton, lp);
        //table.addView(buttonRow, lpRow);
    }

    public void addScoreView(Integer score){
        TextView total = createTextView(score.toString(), R.layout.total_textview);
        total.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        total.setText(score.toString());
        totalScoresRow.addView(total, lpEqualWeight);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        MenuItem accept = menu.findItem(R.id.action_accept);
        MenuItem addPlayer = menu.findItem(R.id.action_add_player);
        addPlayer.setVisible(namesDef);
        accept.setVisible(!namesDef);
       return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        //Log.i(TAG, "Function entered");
        switch (item.getItemId()) {
            case R.id.action_add_player:
                //Log.i(TAG, "In case");
                openAddPlayerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable("playerList", playerList);
        outState.putSerializable("game", game);
    }

    public void openAddPlayerDialog(){
        // NEED TO IMPLEMENT
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentManager fm = getFragmentManager();
        final PlayerAddFragment addPlayer = new PlayerAddFragment();
        addPlayer.show(fm, "fragment_add_player");
    }

    public void addPlayerToGame(String[] input){
//        Log.i(TAG, "Add player function called." + input[0] + input[1]);
        String name = input[0];
        int score = Integer.parseInt(input[1]);
        Player player = new Player(name);
        int numRounds = playerList.get(0).getNumRounds();
        //For all rounds but the most recent round, insert 0's
        if(numRounds>0){
            for (int i=0;i<numRounds-1;i++){
                player.addScore(0);
            }
            //For the most recent round insert the initial score
            player.addScore(score);
        }
        playerList.add(player);
        getNameLetters();
        scoresSql.addPlayerScores(game.getId(), player);
        int playerListLength = playerList.size();
        String[] names = new String[playerListLength];
        for (int i=0; i<playerListLength; i++){
            names[i]=playerList.get(i).getName();
        }
        names = getNameLetters(names);
        headerRow.addView(createTextView(names[playerListLength-1], R.layout.header_textview));
        setRowText(headerRow, names);
        EditText scoreView = new EditText(this);
        scoreList.add(scoreView);
        scoreView.setInputType(InputType.TYPE_CLASS_NUMBER);
        int maxLength = 3;
        scoreView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        scoreView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        scoreView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                Boolean flag = true;
                for (TextView tv : scoreList) {
                    CharSequence charSequence = tv.getText();
                    if (charSequence != null) {
                        String text = charSequence.toString();
                        if (text != null) {
                            if (text.isEmpty() || !(isNonNegInt(text))) {
                                flag = false;
                                break;
                            }
                        }
                    }
                }
                endRoundButton.setEnabled(flag);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setContentView(createLayout());
    }

    private TextView createTextView(String text, int layout){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) inflater.inflate(layout, null);
        if (textView == null) return null;
        textView.setText(text);
        textView.setTextSize(nameFontSize);
        textView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        textView.setHorizontallyScrolling(true);
        applyLayoutParams(textView);
        return textView;
    }

    private Button createButton(String text, int layout){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Button button = (Button) inflater.inflate(layout, null);
        if (button == null) return null;
        button.setText(text);
        return button;
    }

    private LinearLayout createRowLinLayout(){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.TOP);
        layout.setLayoutParams(lpRow);
        return layout;
    }

    public void startNewRound (){
        LayoutInflater inflater;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        roundTotalsRow = new LinearLayout(this);
        roundTotalsRow.setLayoutParams(lpEqualWeight);
        if(rowColorFlag)
            roundTotalsRow.setBackgroundColor(getResources().getColor(R.color.gray));
        rowColorFlag = !rowColorFlag;
        TextView roundTotal;
        int playerListLength = playerList.size();
        for (int i=0; i<playerListLength; i++){
            //roundTotal = new TextView(this);
            roundTotal = (TextView) inflater.inflate(R.layout.content_textview, roundTotalsRow, false);
            if (roundTotal != null) {
                roundTotal.setTextSize(numFontSize);
                roundTotal.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                String[] scores = getDataFromEditTextRow(scoreEntryRow);
                playerList.get(i).addScore(Integer.parseInt(scores[i]));
                roundTotal.setText(scores[i]);
                applyLayoutParams(roundTotal);
                roundTotalsRow.addView(roundTotal);
            }
        }
        scrollableLayout.addView(roundTotalsRow, scrollableLayout.getChildCount()-2, lpRow);
        setRowText(scoreEntryRow, "");
        updateTotals();
        game.setPlayerList(playerList);
        saveGame();
    }

    public String[] getDataFromEditTextRow(LinearLayout row){
        int playerListLength = playerList.size();
        String[] data = new String[playerListLength];
        for (int i=0;i<playerListLength;i++){
            EditText editText = (EditText) row.getChildAt(i);
            if (editText != null){
                CharSequence charSequence = editText.getText();
                if (charSequence != null) data[i] = charSequence.toString();
            }
        }
        return data;
    }

    public void deleteExistingRounds (){
        Player player = playerList.get(0);
        int numRounds = player.getScores().size();
        for(int i=0;i<numRounds;i++){
            View view = scrollableLayout.getChildAt(scrollableLayout.getChildCount() - 3);
            if (view != null) scrollableLayout.removeView(view);
        }
    }

    public void addExistingRounds (){
        LayoutInflater inflater;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Player player = playerList.get(0);
        int numRounds = player.getScores().size();
        for (int i=0;i<numRounds;i++){
            roundTotalsRow = new LinearLayout(this);
            if(rowColorFlag)
                roundTotalsRow.setBackgroundColor(getResources().getColor(R.color.gray));
            rowColorFlag = !rowColorFlag;
            TextView roundTotal;
            for (Player p : playerList){
                //roundTotal = new TextView(this);
                roundTotal = (TextView) inflater.inflate(R.layout.content_textview, roundTotalsRow, false);
                if (roundTotal != null){
                    roundTotal.setTextSize(numFontSize);
                    roundTotal.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                    roundTotal.setText(p.getScores().get(i).toString());
                    applyLayoutParams(roundTotal);
                    roundTotalsRow.addView(roundTotal);
                }
            }
            scrollableLayout.addView(roundTotalsRow, scrollableLayout.getChildCount()-2, lpRow);
        }
        updateTotals();
    }

    private void updateTotals (){
        String[] totalList = new String[playerList.size()];
        int playerListLength = playerList.size();
        for (int i=0; i<playerListLength;i++){
            totalList[i] = playerList.get(i).getScoreTotal().toString();
        }
        setRowText(totalScoresRow, totalList);
    }

    private boolean isInteger( String input ) {
        return input.matches("[0-9][0-9]*");
    }

    private boolean isNonNegInt(String input){
        return isInteger(input) && Integer.parseInt(input)>=0;
    }

    private void setRowText(LinearLayout tr, String s){
        for (int i=0;i<tr.getChildCount();i++){
            TextView tv = (TextView)tr.getChildAt(i);
            if (tv != null) tv.setText(s);
        }
    }

    private void setRowText(LinearLayout tr, String[] s){
        for (int i=0;i<tr.getChildCount();i++){
            TextView tv = (TextView)tr.getChildAt(i);
            if (tv != null) tv.setText(s[i]);
        }
    }

    private void applyLayoutParams(View view){
        int orientation = this.getResources().getConfiguration().orientation;
        if ((orientation==Configuration.ORIENTATION_PORTRAIT && playerList.size()>6) || (orientation==Configuration.ORIENTATION_LANDSCAPE && playerList.size()>8))
            view.setLayoutParams(lpEqualWidth);
        else
            view.setLayoutParams(lpEqualWeight);
    }

    private RelativeLayout genScrollViews(){
        deleteMainViewChildren();
        relLp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        scrollView = new ScrollView(this);
        int orientation = this.getResources().getConfiguration().orientation;
        if ((orientation==Configuration.ORIENTATION_PORTRAIT && playerList.size()>6) || (orientation==Configuration.ORIENTATION_LANDSCAPE && playerList.size()>8)){
            hScrollView = new HorizontalScrollView(this);
            hScrollView.addView(scrollableLayout, new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.MATCH_PARENT, HorizontalScrollView.LayoutParams.MATCH_PARENT));
            scrollView.addView(hScrollView, new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));
        }
        else
            scrollView.addView(scrollableLayout, new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));
        scrollView.setId(View.generateViewId());
        buttonRow.setId(View.generateViewId());
        relLp.addRule(RelativeLayout.ABOVE, buttonRow.getId());
        mainLayout.addView(scrollView, relLp);
        relLp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mainLayout.addView(buttonRow, relLp);
        return mainLayout;
    }

    private void deleteMainViewChildren(){
        if(hScrollView != null) hScrollView.removeAllViews();
        if(scrollView != null) scrollView.removeAllViews();
        if(mainLayout != null) mainLayout.removeAllViews();
    }
}