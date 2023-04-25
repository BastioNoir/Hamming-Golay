package org.uvigo.hamminggolay.ui;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.hamminggolay.chess.pieces.PieceAdapter;
import org.uvigo.hamminggolay.core.Profile;
import org.uvigo.hamminggolay.R;
import org.uvigo.hamminggolay.core.Uploader;
import org.uvigo.hamminggolay.chess.board.BoardBox;
import org.uvigo.hamminggolay.chess.board.SkinBoard;
import org.uvigo.hamminggolay.chess.pieces.Piece;
import org.uvigo.hamminggolay.core.Achievement;
import org.uvigo.hamminggolay.core.History;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Actividad en la que se juega al juego del ajedrez.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class GameActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    Profile selectedProfile= MainActivity.getSelectedProfile();
    Profile selectedRival;
    ArrayList<Profile>profiles;

    Integer selectedTime;

    GridView board;
    PieceAdapter pieceAdapter;
    BoardBox[] boardboxes;
    SkinBoard skin;
    boolean selectedBoardBox;
    int posSelectedBoardBox;
    History history;
    ArrayList<Piece> deadPieces;

    //EMPTY PIECE
    Piece emptyPiece= new Piece("EMPTY",'E',"deadPieces.png",64);

    boolean turn;
    boolean normalMode;

    boolean newGame; //TRUE si newGame/ FALSE si continueGame

    private CountDownTimer wCountDownTimer;
    private CountDownTimer bCountDownTimer;
    private boolean wTimerRunning;
    private boolean bTimerRunning;
    private boolean whiteTurn = true;
    private long[] wTimeLeftInMillis = {0};
    private long[] bTimeLeftInMillis = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        profiles= Uploader.loadProfiles(getApplicationContext());
        //Recuperamos parametros de newActivity
        newGame=(boolean)getIntent().getSerializableExtra("type");

        normalMode= (boolean)getIntent().getSerializableExtra("mode");
        selectedRival= (Profile)getIntent().getSerializableExtra("rival");
        selectedTime= (Integer)getIntent().getSerializableExtra("time");
        wTimeLeftInMillis[0] = selectedTime*60000;
        bTimeLeftInMillis[0] = selectedTime*60000;

        profiles.remove(selectedProfile);
        profiles.remove(selectedRival);

        profiles.add(selectedProfile);
        profiles.add(selectedRival);

        turn= (boolean)getIntent().getSerializableExtra("turn");


        if(newGame){
            //Creamos nuevo historial
            history = new History(selectedProfile.getName()+" VS "+selectedRival.getName()+" "+Calendar.getInstance().getTime());
        }else{
            history=(History)getIntent().getSerializableExtra("history");
        }
        //Creamos array de piezas comidas
        deadPieces = new ArrayList<Piece>();

        //inicializar array casillas
        selectedBoardBox = false;
        boardboxes = new BoardBox[64];

        TextView wPlayer= findViewById(R.id.whitesPlayer);
        TextView bPlayer= findViewById(R.id.blacksPlayer);

        ImageView iv_wPlayer= findViewById(R.id.whitesPlayerImage);
        ImageView iv_bPlayer= findViewById(R.id.blacksPlayerImage);

        TextView wTime= findViewById(R.id.whiteTimeText);
        TextView bTime= findViewById(R.id.blackTimeText);
        ImageButton wTimeButton= findViewById(R.id.whiteTimeButton);
        ImageButton bTimeButton= findViewById(R.id.blackTimeButton);
        updateCountDownTextW(wTime);
        updateCountDownTextB(bTime);

        startTimerW(wTime);
        wTimerRunning = true;

        if(turn){
            wPlayer.setText(selectedProfile.getName());
            iv_wPlayer.setImageBitmap(Uploader.bitmapFromAssets(getApplicationContext(),selectedProfile.getImagePath()));

            bPlayer.setText(selectedRival.getName());
            iv_bPlayer.setImageBitmap(Uploader.bitmapFromAssets(getApplicationContext(),selectedRival.getImagePath()));
        }else{
            wPlayer.setText(selectedRival.getName());
            iv_wPlayer.setImageBitmap(Uploader.bitmapFromAssets(getApplicationContext(),selectedRival.getImagePath()));
            bPlayer.setText(selectedProfile.getName());
            iv_bPlayer.setImageBitmap(Uploader.bitmapFromAssets(getApplicationContext(),selectedProfile.getImagePath()));
        }

        String[] colors= selectedProfile.getSkinBoardName().replace("image","").split("#");
        skin = new SkinBoard(Color.parseColor("#"+colors[0]),
                             Color.parseColor("#"+colors[1]));

        //rellenamos los fondos
        drawBoard();

        //Piezas negras
        orderPieces(newGame);

        //Coger el tablero
        board = (GridView) findViewById(R.id.board);
        board.setHorizontalSpacing(0);
        board.setVerticalSpacing(0);
        pieceAdapter = new PieceAdapter(this,boardboxes);
        board.setAdapter(pieceAdapter);
        board.setOnItemClickListener(this);

        ImageButton backButton = this.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploader.updateHistory(getApplicationContext(),history);

                //Guardamos los perfiles actualizados en el uploader
                updateProfiles();

                GameActivity.this.setResult( MainActivity.RESULT_CANCELED );
                GameActivity.this.finish();
            }
        });

        wTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wTimerRunning){
                    //PAUSAMOS EL TIMER BLANCO
                    pauseTimer(wCountDownTimer, wTimeLeftInMillis);
                    wTimerRunning = false;
                    whiteTurn = false;
                    //INICIAMOS TIMER NEGRO
                    //startTimer(bCountDownTimer, bTimeLeftInMillis, bTime);
                    startTimerB(bTime);
                    bTimerRunning = true;
                }
            }
        });

        bTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bTimerRunning){
                    //PAUSAMOS EL TIMER NEGRO
                    bCountDownTimer.cancel();
                    pauseTimer(bCountDownTimer, bTimeLeftInMillis);
                    bTimerRunning = false;
                    whiteTurn = true;
                    //INICIAMOS TIMER BLANCO
                    startTimerW(wTime);
                    wTimerRunning = true;
                }
            }
        });
    }

    /**
     * Inicia y maneja el temporizador del jugador de piezas blancas. Devuelve el temporizador usado
     *
     * @param wTime TexView en el que se va a mostrar el temporizador
     * @return      el temporizador usado
     */
    private CountDownTimer startTimerW(TextView wTime) {
        wCountDownTimer = new CountDownTimer(wTimeLeftInMillis[0], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                wTimeLeftInMillis[0] = millisUntilFinished;
                updateCountDownTextW(wTime);
            }

            @Override
            public void onFinish() {
                wTimerRunning = false;
                Uploader.updateHistory(getApplicationContext(),history);

                //Guardamos los perfiles actualizados en el uploader
                updateProfiles();

                GameActivity.this.setResult( MainActivity.RESULT_CANCELED );
                GameActivity.this.finish();
            }
        }.start();

        wTimerRunning = true;

        return wCountDownTimer;
    }

    /**
     * Inicia y maneja el temporizador del jugador de piezas negras. Devuelve el temporizador usado
     *
     * @param bTime TexView en el que se va a mostrar el temporizador
     * @return     el temporizador usado
     */
    private CountDownTimer startTimerB(TextView bTime) {
        bCountDownTimer = new CountDownTimer(bTimeLeftInMillis[0], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bTimeLeftInMillis[0] = millisUntilFinished;
                updateCountDownTextB(bTime);
            }

            @Override
            public void onFinish() {
                bTimerRunning = false;
                Uploader.updateHistory(getApplicationContext(),history);

                //Guardamos los perfiles actualizados en el uploader
                updateProfiles();

                GameActivity.this.setResult( MainActivity.RESULT_CANCELED );
                GameActivity.this.finish();
            }
        }.start();

        bTimerRunning = true;

        return bCountDownTimer;
    }

    /**
     * Actualiza y da formato al tiempo del jugador de piezas blancas del temporizador y lo muestra
     * por el TextView especificado
     *
     * @param wTime TexView en el que se va a mostrar el temporizador
     */
    private void updateCountDownTextW(TextView wTime) {
        int minutes = (int) (wTimeLeftInMillis[0] / 1000) / 60;
        int seconds = (int) (wTimeLeftInMillis[0] / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        wTime.setText(timeLeftFormatted);
    }

    /**
     * Actualiza y da formato al tiempo del jugador de piezas negras del temporizador y lo muestra
     * por el TextView especificado
     *
     * @param bTime TexView en el que se va a mostrar el temporizador
     */
    private void updateCountDownTextB(TextView bTime) {
        int minutes = (int) (bTimeLeftInMillis[0] / 1000) / 60;
        int seconds = (int) (bTimeLeftInMillis[0] / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        bTime.setText(timeLeftFormatted);
    }

    /**
     * Pausa el temporizador especificado
     *
     * @param countDownTimer    temporizador a parar
     * @param millisRemaining   tiempo restante
     */
    private void pauseTimer(CountDownTimer countDownTimer, long[] millisRemaining) {
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        profiles=Uploader.loadProfiles(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        profiles=Uploader.loadProfiles(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Uploader.updateHistory(getApplicationContext(),history);

        //Guardamos los perfiles actualizados en el uploader
        updateProfiles();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Uploader.updateHistory(getApplicationContext(),history);

        //Guardamos los perfiles actualizados en el uploader
        updateProfiles();
    }

    /**
     * Rellena de color el tablero
     */
    public void drawBoard(){
        int numLinea;
        int ptr = 0;
        for(int fila = 0; fila <8 ; fila++){
            numLinea = fila;
            if(numLinea%2 == 0){
                for(int columna = 0; columna <8 ; columna++){
                    if(columna%2 == 0)
                        boardboxes[ptr] = new BoardBox(skin.getLightColor());
                    else
                        boardboxes[ptr] = new BoardBox(skin.getDarkcolor());

                    ptr++;
                }
            }else{
                for(int columna = 0; columna <8 ; columna++){
                    if(columna%2 != 0)
                        boardboxes[ptr] = new BoardBox(skin.getLightColor());
                    else
                        boardboxes[ptr] = new BoardBox(skin.getDarkcolor());

                    ptr++;
                }
            }
        }
    }

    /**
     * Crea las piezas en el tablero
     *
     * @param type  tipo de juego: "true" cuando es una nueva partida, "false" cuando es una partida
     *              que se va a continuar
     */
    public void orderPieces(boolean type){
        //NEW GAME
        if(type){
            //BLACKS
            Piece bp1= new Piece("PAWN1",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",8);
            Piece bp2= new Piece("PAWN2",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",9);
            Piece bp3= new Piece("PAWN3",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",10);
            Piece bp4= new Piece("PAWN4",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",11);
            Piece bp5= new Piece("PAWN5",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",12);
            Piece bp6= new Piece("PAWN6",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",13);
            Piece bp7= new Piece("PAWN7",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",14);
            Piece bp8= new Piece("PAWN8",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",15);

            Piece bt2= new Piece("TOWER2",'B',"blacktower"+selectedProfile.getSkinPieceName()+".png",7);

            Piece bk1= new Piece("KNIGHT1",'B',"blackknight"+selectedProfile.getSkinPieceName()+".png",1);
            Piece bk2= new Piece("KNIGHT2",'B',"blackknight"+selectedProfile.getSkinPieceName()+".png",6);

            Piece bb1= new Piece("BISHOP1",'B',"blackbishop"+selectedProfile.getSkinPieceName()+".png",2);
            Piece bb2= new Piece("BISHOP2",'B',"blackbishop"+selectedProfile.getSkinPieceName()+".png",5);

            Piece bq= new Piece("QUEEN",'B',"blackqueen"+selectedProfile.getSkinPieceName()+".png",3);

            Piece bK= new Piece("KING",'B',"blackking"+selectedProfile.getSkinPieceName()+".png",4);

            //Para que sobreescriba las que ya hay muertas ahi
            Piece bt1= new Piece("TOWER1",'B',"blacktower"+selectedProfile.getSkinPieceName()+".png",0);


            boardboxes[bp1.getPos()].setPiece(bp1);
            boardboxes[bp2.getPos()].setPiece(bp2);
            boardboxes[bp3.getPos()].setPiece(bp3);
            boardboxes[bp4.getPos()].setPiece(bp4);
            boardboxes[bp5.getPos()].setPiece(bp5);
            boardboxes[bp6.getPos()].setPiece(bp6);
            boardboxes[bp7.getPos()].setPiece(bp7);
            boardboxes[bp8.getPos()].setPiece(bp8);
            boardboxes[bt1.getPos()].setPiece(bt1);
            boardboxes[bt2.getPos()].setPiece(bt2);
            boardboxes[bk1.getPos()].setPiece(bk1);
            boardboxes[bk2.getPos()].setPiece(bk2);
            boardboxes[bb1.getPos()].setPiece(bb1);
            boardboxes[bb2.getPos()].setPiece(bb2);
            boardboxes[bq.getPos()].setPiece(bq);
            boardboxes[bK.getPos()].setPiece(bK);

            history.addPos(bp1.getColor()+bp1.getName(),Integer.toString(bp1.getPos()));
            history.addPos(bp2.getColor()+bp2.getName(),Integer.toString(bp2.getPos()));
            history.addPos(bp3.getColor()+bp3.getName(),Integer.toString(bp3.getPos()));
            history.addPos(bp4.getColor()+bp4.getName(),Integer.toString(bp4.getPos()));
            history.addPos(bp5.getColor()+bp5.getName(),Integer.toString(bp5.getPos()));
            history.addPos(bp6.getColor()+bp6.getName(),Integer.toString(bp6.getPos()));
            history.addPos(bp7.getColor()+bp7.getName(),Integer.toString(bp7.getPos()));
            history.addPos(bp8.getColor()+bp8.getName(),Integer.toString(bp8.getPos()));
            history.addPos(bt1.getColor()+bt1.getName(),Integer.toString(bt1.getPos()));
            history.addPos(bt2.getColor()+bt2.getName(),Integer.toString(bt2.getPos()));
            history.addPos(bk1.getColor()+bk1.getName(),Integer.toString(bk1.getPos()));
            history.addPos(bk2.getColor()+bk2.getName(),Integer.toString(bk2.getPos()));
            history.addPos(bb1.getColor()+bb1.getName(),Integer.toString(bb1.getPos()));
            history.addPos(bb2.getColor()+bb2.getName(),Integer.toString(bb2.getPos()));
            history.addPos(bq.getColor()+bq.getName(),Integer.toString(bq.getPos()));
            history.addPos(bK.getColor()+bK.getName(),Integer.toString(bK.getPos()));

            //WHITES

            Piece wp1= new Piece("PAWN1",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-9-7);
            Piece wp2= new Piece("PAWN2",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-8-7);
            Piece wp3= new Piece("PAWN3",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-7-7);
            Piece wp4= new Piece("PAWN4",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-6-7);
            Piece wp5= new Piece("PAWN5",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-5-7);
            Piece wp6= new Piece("PAWN6",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-4-7);
            Piece wp7= new Piece("PAWN7",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-3-7);
            Piece wp8= new Piece("PAWN8",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-2-7);

            Piece wt1= new Piece("TOWER1",'W',"whitetower"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-0);
            Piece wt2= new Piece("TOWER2",'W',"whitetower"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-7);

            Piece wk1= new Piece("KNIGHT1",'W',"whiteknight"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-1);
            Piece wk2= new Piece("KNIGHT2",'W',"whiteknight"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-6);

            Piece wb1= new Piece("BISHOP1",'W',"whitebishop"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-2);
            Piece wb2= new Piece("BISHOP2",'W',"whitebishop"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-5);

            Piece wq= new Piece("QUEEN",'W',"whitequeen"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-3);

            Piece wK= new Piece("KING",'W',"whiteking"+selectedProfile.getSkinPieceName()+".png",boardboxes.length-1-4);

            boardboxes[wp1.getPos()].setPiece(wp1);
            boardboxes[wp2.getPos()].setPiece(wp2);
            boardboxes[wp3.getPos()].setPiece(wp3);
            boardboxes[wp4.getPos()].setPiece(wp4);
            boardboxes[wp5.getPos()].setPiece(wp5);
            boardboxes[wp6.getPos()].setPiece(wp6);
            boardboxes[wp7.getPos()].setPiece(wp7);
            boardboxes[wp8.getPos()].setPiece(wp8);

            boardboxes[wt1.getPos()].setPiece(wt1);
            boardboxes[wt2.getPos()].setPiece(wt2);
            boardboxes[wk1.getPos()].setPiece(wk1);
            boardboxes[wk2.getPos()].setPiece(wk2);
            boardboxes[wb1.getPos()].setPiece(wb1);
            boardboxes[wb2.getPos()].setPiece(wb2);
            boardboxes[wq.getPos()].setPiece(wq);
            boardboxes[wK.getPos()].setPiece(wK);

            history.addPos(wp1.getColor()+wp1.getName(),Integer.toString(wp1.getPos()));
            history.addPos(wp2.getColor()+wp2.getName(),Integer.toString(wp2.getPos()));
            history.addPos(wp3.getColor()+wp3.getName(),Integer.toString(wp3.getPos()));
            history.addPos(wp4.getColor()+wp4.getName(),Integer.toString(wp4.getPos()));
            history.addPos(wp5.getColor()+wp5.getName(),Integer.toString(wp5.getPos()));
            history.addPos(wp6.getColor()+wp6.getName(),Integer.toString(wp6.getPos()));
            history.addPos(wp7.getColor()+wp7.getName(),Integer.toString(wp7.getPos()));
            history.addPos(wp8.getColor()+wp8.getName(),Integer.toString(wp8.getPos()));
            history.addPos(wt1.getColor()+wt1.getName(),Integer.toString(wt1.getPos()));
            history.addPos(wt2.getColor()+wt2.getName(),Integer.toString(wt2.getPos()));
            history.addPos(wk1.getColor()+wk1.getName(),Integer.toString(wk1.getPos()));
            history.addPos(wk2.getColor()+wk2.getName(),Integer.toString(wk2.getPos()));
            history.addPos(wb1.getColor()+wb1.getName(),Integer.toString(wb1.getPos()));
            history.addPos(wb2.getColor()+wb2.getName(),Integer.toString(wb2.getPos()));
            history.addPos(wq.getColor()+wq.getName(),Integer.toString(wq.getPos()));
            history.addPos(wK.getColor()+wK.getName(),Integer.toString(wK.getPos()));

        }else{
            //CONTINUEGAME
            //BLACKS
            Piece bp1= new Piece("PAWN1",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN1"));
            Piece bp2= new Piece("PAWN2",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN2"));
            Piece bp3= new Piece("PAWN3",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN3"));
            Piece bp4= new Piece("PAWN4",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN4"));
            Piece bp5= new Piece("PAWN5",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN5"));
            Piece bp6= new Piece("PAWN6",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN6"));
            Piece bp7= new Piece("PAWN7",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN7"));
            Piece bp8= new Piece("PAWN8",'B',"blackpawn"+selectedProfile.getSkinPieceName()+".png",getPositions("BPAWN8"));

            Piece bt2= new Piece("TOWER2",'B',"blacktower"+selectedProfile.getSkinPieceName()+".png",getPositions("BTOWER2"));

            Piece bk1= new Piece("KNIGHT1",'B',"blackknight"+selectedProfile.getSkinPieceName()+".png",getPositions("BKNIGHT1"));
            Piece bk2= new Piece("KNIGHT2",'B',"blackknight"+selectedProfile.getSkinPieceName()+".png",getPositions("BKNIGHT2"));

            Piece bb1= new Piece("BISHOP1",'B',"blackbishop"+selectedProfile.getSkinPieceName()+".png",getPositions("BBISHOP1"));
            Piece bb2= new Piece("BISHOP2",'B',"blackbishop"+selectedProfile.getSkinPieceName()+".png",getPositions("BBISHOP2"));

            Piece bq= new Piece("QUEEN",'B',"blackqueen"+selectedProfile.getSkinPieceName()+".png",getPositions("BQUEEN"));

            Piece bK= new Piece("KING",'B',"blackking"+selectedProfile.getSkinPieceName()+".png",getPositions("BKING"));

            //WHITES

            Piece wp1= new Piece("PAWN1",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN1"));
            Piece wp2= new Piece("PAWN2",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN2"));
            Piece wp3= new Piece("PAWN3",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN3"));
            Piece wp4= new Piece("PAWN4",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN4"));
            Piece wp5= new Piece("PAWN5",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN5"));
            Piece wp6= new Piece("PAWN6",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN6"));
            Piece wp7= new Piece("PAWN7",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN7"));
            Piece wp8= new Piece("PAWN8",'W',"whitepawn"+selectedProfile.getSkinPieceName()+".png",getPositions("WPAWN8"));

            Piece wt1= new Piece("TOWER1",'W',"whitetower"+selectedProfile.getSkinPieceName()+".png",getPositions("WTOWER1"));
            Piece wt2= new Piece("TOWER2",'W',"whitetower"+selectedProfile.getSkinPieceName()+".png",getPositions("WTOWER2"));

            Piece wk1= new Piece("KNIGHT1",'W',"whiteknight"+selectedProfile.getSkinPieceName()+".png",getPositions("WKNIGHT1"));
            Piece wk2= new Piece("KNIGHT2",'W',"whiteknight"+selectedProfile.getSkinPieceName()+".png",getPositions("WKNIGHT2"));

            Piece wb1= new Piece("BISHOP1",'W',"whitebishop"+selectedProfile.getSkinPieceName()+".png",getPositions("WBISHOP1"));
            Piece wb2= new Piece("BISHOP2",'W',"whitebishop"+selectedProfile.getSkinPieceName()+".png",getPositions("WBISHOP2"));

            Piece wq= new Piece("QUEEN",'W',"whitequeen"+selectedProfile.getSkinPieceName()+".png",getPositions("WQUEEN"));

            Piece wK= new Piece("KING",'W',"whiteking"+selectedProfile.getSkinPieceName()+".png",getPositions("WKING"));

            Piece bt1= new Piece("TOWER1",'B',"blacktower"+selectedProfile.getSkinPieceName()+".png",getPositions("BTOWER1"));

            boardboxes[bp1.getPos()].setPiece(bp1);
            boardboxes[bp2.getPos()].setPiece(bp2);
            boardboxes[bp3.getPos()].setPiece(bp3);
            boardboxes[bp4.getPos()].setPiece(bp4);
            boardboxes[bp5.getPos()].setPiece(bp5);
            boardboxes[bp6.getPos()].setPiece(bp6);
            boardboxes[bp7.getPos()].setPiece(bp7);
            boardboxes[bp8.getPos()].setPiece(bp8);

            boardboxes[bt2.getPos()].setPiece(bt2);

            boardboxes[bk1.getPos()].setPiece(bk1);
            boardboxes[bk2.getPos()].setPiece(bk2);

            boardboxes[bb1.getPos()].setPiece(bb1);
            boardboxes[bb2.getPos()].setPiece(bb2);

            boardboxes[bq.getPos()].setPiece(bq);
            boardboxes[bK.getPos()].setPiece(bK);

            boardboxes[wp1.getPos()].setPiece(wp1);
            boardboxes[wp2.getPos()].setPiece(wp2);
            boardboxes[wp3.getPos()].setPiece(wp3);
            boardboxes[wp4.getPos()].setPiece(wp4);
            boardboxes[wp5.getPos()].setPiece(wp5);
            boardboxes[wp6.getPos()].setPiece(wp6);
            boardboxes[wp7.getPos()].setPiece(wp7);
            boardboxes[wp8.getPos()].setPiece(wp8);

            boardboxes[wt1.getPos()].setPiece(wt1);
            boardboxes[wt2.getPos()].setPiece(wt2);
            boardboxes[wk1.getPos()].setPiece(wk1);
            boardboxes[wk2.getPos()].setPiece(wk2);
            boardboxes[wb1.getPos()].setPiece(wb1);
            boardboxes[wb2.getPos()].setPiece(wb2);
            boardboxes[wq.getPos()].setPiece(wq);
            boardboxes[wK.getPos()].setPiece(wK);

            boardboxes[bt1.getPos()].setPiece(bt1);
        }

    }

    /**
     * Obtiene la posicion registrada en el historial
     *
     * @param name  nombre de la pieza a colocar
     * @return      posicion en la que se coloca la pieza
     */
    public int getPositions(String name){
        String pos= history.getPosPieces().get(name);
        int value= Integer.parseInt(pos);

        if(value==-1){
            return 0;
        }

        return value;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(selectedBoardBox){
            //Logica de mover la pieza
            selectedBoardBox = false;
            //recuperamos casillas
            BoardBox anterior = (BoardBox)parent.getItemAtPosition(posSelectedBoardBox);
            BoardBox siguiente = (BoardBox)parent.getItemAtPosition(position);
            //Verificar que no se mueve una casilla vacia
            //Verificar que no se mueve a la misma casilla


            //Si hay una pieza
            if(!anterior.getDrawablePiece().equals("") && posSelectedBoardBox != position){
                    //Damos logros por posiciones
                    positionAchievementHandler(getProfileByPiece(anterior.getPiece()),anterior.getPiece(),position);
                    //Si hay una pieza, comprueba si es del mismo color
                    if(!siguiente.getPiece().getName().equals("EMPTY")){
                        //Coinciden colores
                        if(anterior.getPiece().getColor()==siguiente.getPiece().getColor()){
                            //añadimos el logro de comer tu propia pieza
                            Profile current = getProfileByPiece(anterior.getPiece());
                            if(!hasAchievement(current,"Insaciable"))
                                current.addAchievement(getApplicationContext(),new Achievement("Insaciable","Intenta comerte tu propia pieza"));
                        }else{
                            //añadimos puntos por comer pieza
                            addPointsEaten(anterior.getPiece(),siguiente.getPiece());
                            //logros por poner piezas en ciertas posiciones
                            //parte grafica------
                            //ponemos pieza de anterior en siguiente

                            //Guardamos la posicion de la pieza antes de ser eliminada
                            history.addPos(anterior.getPiece().getColor()+anterior.getPiece().getName(),Integer.toString(siguiente.getPiece().getPos()));
                            String eatenPiece= siguiente.getPiece().getColor()+siguiente.getPiece().getName();
                            siguiente.setPiece(anterior.getPiece());
                            //vaciamos anterior
                            history.addPos(eatenPiece,Integer.toString(-1));
                            anterior.setPiece(emptyPiece);
                            board.setAdapter(pieceAdapter);

                            //guardar movimiento---
                        }
                    }else{
                        //parte grafica------
                        //ponemos pieza de anterior en siguiente
                        history.addPos(anterior.getPiece().getColor()+anterior.getPiece().getName(),Integer.toString(siguiente.getPiece().getPos()));
                        siguiente.setPiece(anterior.getPiece());
                        //vaciamos anterior
                        history.addPos(anterior.getPiece().getColor()+anterior.getPiece().getName(),Integer.toString(siguiente.getPiece().getPos()));
                        anterior.setPiece(emptyPiece);
                        board.setAdapter(pieceAdapter);

                    }

                //guardar movimiento---
                String movimiento = translateCasilla(posSelectedBoardBox) + "-->" + translateCasilla(position);
                history.addMove(movimiento);
                TextView lastMove = (TextView) findViewById(R.id.historyLog);
                lastMove.setText(movimiento);

                saveProfile(selectedProfile);
                saveProfile(selectedRival);
            }

        }else{
            //Logica de selección
            //indicamos que hay casilla seleccionada
            selectedBoardBox = true;
            //guardamos posición de la casilla seleccionada
            posSelectedBoardBox = position;
        }
    }

    /**
     * Guarda los perfiles
     *
     * @param p perfil a guardar
     */
    public void saveProfile(Profile p){

        File temp = new File(p.getName()+".cfg");
        temp.delete();
        try (FileOutputStream f = this.openFileOutput( p.getName()+".cfg", Context.MODE_PRIVATE ) )
        {
            PrintStream cfg = new PrintStream( f );

            cfg.println( p.getName() ); //PROFILE NAME
            cfg.println( p.getImagePath()); //PROFILE IMAGE
            cfg.println( p.getSkinBoardName()); //PROFILE BOARD
            cfg.println( p.getSkinPieceName()); //PROFILE PIECE
            cfg.println( p.getPoints()); //PROFILE POINTS
            cfg.println( p.getAchievements().toString()); //PROFILE ACHIEVEMENTS
            cfg.println( p.getFriends().toString()); //PROFILE FRIENDS

            cfg.close();
        }
        catch(IOException exc) {
        }
    }

    /**
     * Concede los logros relacionados con posiciones en tablero
     *
     * @param profile   perfil del jugador que mueve la pieza
     * @param piece     pieza que se mueve
     * @param position  posicion a la que se mueve la pieza
     */
    private void positionAchievementHandler(Profile profile, Piece piece, int position) {
        //Si un Alfil esta en la diagonal
        if(piece.getName().equals("BISHOP") && (position == 0 || position ==  7 || position == 63|| position == 56)){
            if(!hasAchievement(profile,"Francotirador en posicion"))
                profile.addAchievement(getApplicationContext(),new Achievement("Francotirador en posicion","Coloca un alfil en una esquina del tablero"));
        }

        //Si un peon esta en la ultima linea
        if(piece.getName().equals("PAWN") && (position < 7 || position > 56)){
            if(!hasAchievement(profile,"Zona hostil"))
                profile.addAchievement(getApplicationContext(),new Achievement("Zona hostil","Lleva un peón a la ultima fila del tablero"));
        }
    }

    /**
     * Añade puntos al comer una pieza
     *
     * @param piece     pieza que come
     * @param comida    pieza comida
     */
    private void addPointsEaten(Piece piece, Piece comida) {
        Profile eater;
        deadPieces.add(comida);

        //comprobamos quien comio y se añaden los puntos al jugador correspondiente
        if(piece.getColor() == 'W'){
            selectedProfile.setPoints(100);
            eater = selectedProfile;
            //Guardamos los perfiles actualizados en el uploader
            updateProfiles();
        }
        else{
            selectedRival.setPoints(100);
            eater = selectedRival;
            //Guardamos los perfiles actualizados en el uploader
            updateProfiles();
        }

        eatenAchivementHandler(eater,piece,comida);
    }

    /**
     * Comprueba si el jugador consigue algun logro al comer una pieza
     *
     * @param eater perfil del jugador cuya pieza come a la del rival
     * @param piece pieza del jugador cuya pieza come a la del rival
     * @param eaten pieza comida del rival
     */
    private void eatenAchivementHandler(Profile eater,Piece piece,Piece eaten){

        //añadimos el logro de voraz si no lo tiene
        if(!hasAchievement(eater,"voraz"))
                eater.addAchievement(getApplicationContext(),new Achievement("Voraz","Come una pieza"));

        //Si es reyna añadimos el logro
        if(!hasAchievement(eater,"Al final si que era mortal") && eaten.getName().equals("QUEEN"))
            eater.addAchievement(getApplicationContext(),new Achievement("Al final si que era mortal","Come una reina"));

        //Si comio dos torres añadimos logro
        if(!hasAchievement(eater,"Un dia Oscuro")) {
            int contadorTorres = 0;
            for (Piece p : deadPieces) {
                if( piece.getColor() != p.getColor() && p.getName().equals("TOWER"))
                    contadorTorres++;
            }
            if(contadorTorres >=2)
                eater.addAchievement(getApplicationContext(),new Achievement("Un dia Oscuro","Come las dos torres"));
        }

        //Si se han comido un rey
        if(eaten.getName().contains("KING")) {
            //Si es un rey
            char loserColor=eaten.getColor();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Si J1 es selectedProfile y se han comido al rey negro GANA PROFILE
            //Si J1 es selectedProfile y se han comido al rey blanco PIERDE PROFILE
            //Si J1 es selectedRival y se han comido al rey negro GANA RIVAL
            //Si J1 es selectedRival y se han comido al rey blanco PIERDE RIVAL
            if((turn && loserColor=='B') || (!turn && loserColor=='W')){
                builder.setTitle("El jugador "+selectedProfile.getName()+" ha ganado");
            }else{
                builder.setTitle("El jugador "+selectedRival.getName()+" ha ganado");
            }

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //INFORMA Y DEJA LA PARTIDA
                    GameActivity.this.setResult( MainActivity.RESULT_CANCELED );
                    GameActivity.this.finish();
                }
            });
            builder.create().show();
        }
    }

    /**
     * Comprueba si el jugador especificado posee el logro especificado
     * @param player    jugador del que se quiere saber si posee el logro especificado
     * @param name      nombre del logro que se comrobar si el jugador especificado posee
     * @return          si el jugador especificado posee el logro especificado
     */
    public boolean hasAchievement(Profile player,String name){
        ArrayList<String> achivementsPlayer = player.getAchievements();
        for (String a : achivementsPlayer){
            if(a.equals(name))
                return true;
        }
        return false;
    }

    /**
     * Devuelve quien es el jugador de la pieza especificada
     *
     * @param piece piexa de la que se quiere conocer el jugador
     * @return      perfil del jugador dueño de la pieza especificada
     */
    public Profile getProfileByPiece(Piece piece){
        if(piece.getColor() == 'W')
            return selectedProfile;
        else
            return selectedRival;
    }

    /**
     * Traduce para los movimientos de ciertas casillas a letras
     *
     * @param pos   posicion casilla del tablero
     * @return      posicion en el tablero de ajedrez
     */
    public String translateCasilla(int pos){
        String[] letras = {"a","b","c","d","e","f","g","h"};
        int filaArray = pos/8+1;
        int fila = 9-filaArray;
        int columna = pos - ((filaArray -1) * 8);
        return letras[columna]+fila;
    }

    /**
     * Actualiza los perfiles
     */
    public void updateProfiles(){

        ArrayList<Profile> tempProfiles= profiles;
        Profile tempSP=null;
        Profile tempSR= null;

        for(Profile pr: profiles){
            if(selectedProfile.getName().equals(pr.getName())){
                //Quitamos el selected profile
                tempSP=pr;
            }else if(selectedRival.getName().equals(pr.getName())){
                //Quitamos el selected rival
                tempSR=pr;
            }
        }

        if(tempSP!=null){
            tempProfiles.remove(tempSP);
            tempProfiles.add(selectedProfile);
        }

        if(tempSR!=null){
            tempProfiles.remove(tempSR);
            tempProfiles.add(selectedRival);
        }

        Uploader.saveProfiles(getApplicationContext(),tempProfiles);
    }
}
