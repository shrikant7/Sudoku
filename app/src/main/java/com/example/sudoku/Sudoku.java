package com.example.sudoku;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;

public class Sudoku extends AppCompatActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        //setup click listeners for all buttons
        Button aboutButton = (Button) findViewById(R.id.about_b);
        aboutButton.setOnClickListener(this);

        View continueButton = findViewById(R.id.continue_b);
        continueButton.setOnClickListener(this);

        View newButton = findViewById(R.id.new_game_b);
        newButton.setOnClickListener(this);

        View exitButton = findViewById(R.id.exit_b);
        exitButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Music.play(this,R.raw.game);
    }
    @Override
    protected void onPause(){
        super.onPause();
        //Music.stop(this);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_b:
                Intent i= new Intent(this,About.class);
                startActivity(i);
                break;
            case R.id.new_game_b:
                openNewGameDialog();
                break;
            case R.id.exit_b:
                finish();
                break;
            case R.id.continue_b:
                startGame(Game.DIFFICULTY_CONTINUE);
                break;
        }
    }

    private static final String TAG="Sudoku";
    private void openNewGameDialog()
    {
        new AlertDialog.Builder(this)
        .setTitle(R.string.new_game_title)
        .setItems(R.array.difficulty, new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialogInterface, int i)
        {
            startGame(i);
        }
    })
            .show();
    }

    private  void startGame(int i)
    {
        Log.d(TAG,"clicked on "+ i);
        Intent intent=new Intent(this,Game.class);
        intent.putExtra(Game.KEY_DIFFICULTY,i);
        startActivity(intent);
    }
    //for menu
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                startActivity(new Intent(this,Prefs.class));
                return true;
        }
        return false;
    }
}
