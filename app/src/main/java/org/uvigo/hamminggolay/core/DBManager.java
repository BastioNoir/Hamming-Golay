package org.uvigo.hamminggolay.core;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase para manejar el acceso a la base de datos.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class DBManager extends SQLiteOpenHelper {
    public static final String DB_NAME = "Chess";
    public static final int DB_VERSION = 5;

    public static final String HISTORY_TABLE = "HISTORY";
    public static final String ACHIEVEMENTS_TABLE = "ACHIEVEMENTS";

    public static final String HISTORY_NAME = "_id";
    public static final String HISTORY_LOG = "log";
    public static final String HISTORY_PIECEPOS = "pos";

    public static final String ACHIEVEMENT_NAME = "_id";
    public static final String ACHIEVEMENTS_CLUE = "clue";

    /**
     * Construye e inicializa un gestor de base de datos para ayudar en la creacion, apertura y
     * gestion de una base de datos
     *
     * @param context   contexto usado para localizar las rutas de la base de datos
     */
    public DBManager(Context context)
    {
        super( context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        onCreateAchievements(db);
        onCreateHistory(db);

    }

    /**
     * Crea la tabla de achievements
     *
     * @param db SQLiteDatabase usada para el manejo de comandos SQL
     */
    public void onCreateAchievements(SQLiteDatabase db)
    {

        try {
            db.beginTransaction();
            db.execSQL( "CREATE TABLE IF NOT EXISTS " + ACHIEVEMENTS_TABLE + "( "
                    + ACHIEVEMENT_NAME + " string(255) PRIMARY KEY NOT NULL, "
                    + ACHIEVEMENTS_CLUE + " string NOT NULL)");
            db.setTransactionSuccessful();
        }
        catch(SQLException exc)
        {
        }
        finally {
            db.endTransaction();
        }
    }

    /**
     * Crea la tabla de historiales de partidas
     *
     * @param db SQLiteDatabase usada para el manejo de comandos SQL
     */
    public void onCreateHistory(SQLiteDatabase db)
    {

        try {
            db.beginTransaction();
            db.execSQL( "CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE + "( "
                    + HISTORY_NAME + " string(255) PRIMARY KEY NOT NULL, "
                    + HISTORY_LOG + " string(255), "
                    + HISTORY_PIECEPOS + " string(255))");
            db.setTransactionSuccessful();
        }
        catch(SQLException exc)
        {
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgradeAchievements(db,oldVersion,newVersion);
        onUpgradeHistory(db,oldVersion,newVersion);

        this.onCreate( db );
    }

    /**
     * Actualiza la tabla de logros
     *
     * @param db            SQLiteDatabase usada para el manejo de comandos SQL
     * @param oldVersion    version de la base de datos a actualizar
     * @param newVersion    version de la nueva base de datos
     */
    public void onUpgradeAchievements(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        try {
            db.beginTransaction();
            db.execSQL( "DROP TABLE IF EXISTS " + ACHIEVEMENTS_TABLE );
            db.setTransactionSuccessful();

        }  catch(SQLException exc) {
        }
        finally {
            db.endTransaction();
        }

        this.onCreate( db );
    }

    /**
     * Actualiza los historiales
     *
     * @param db            SQLiteDatabase usada para el manejo de comandos SQL
     * @param oldVersion    version de la base de datos a actualizar
     * @param newVersion    version de la nueva base de datos
     */
    public void onUpgradeHistory(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        try {
            db.beginTransaction();
            db.execSQL( "DROP TABLE IF EXISTS " + HISTORY_TABLE );
            db.setTransactionSuccessful();

        }  catch(SQLException exc) {
        }
        finally {
            db.endTransaction();
        }

        this.onCreate( db );
    }

    /**
     * Añade un historial y devuelve si la operacion a tenido exito
     *
     * @param history   historial que vamos a añadir
     * @return          "true" si la operacion a tenido exito, "false" si no ha fracasado
     */
    public boolean addHistory(History history){
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( HISTORY_NAME, history.getName() );
        values.put( HISTORY_LOG, history.getPlainLog() );
        values.put( HISTORY_PIECEPOS, history.getPlainPos() );

        try {
            db.beginTransaction();

            cursor = db.query( HISTORY_TABLE,
                    null,
                    HISTORY_NAME + "=?",
                    new String[]{ history.getName() },
                    null, null, null, null );

            if ( cursor.getCount() > 0 ) {
                db.update( HISTORY_TABLE,
                        values, HISTORY_NAME + "= ?", new String[]{history.getName() } );
            } else {
                db.insert( HISTORY_TABLE, null, values );
            }

            db.setTransactionSuccessful();
            toret = true;

        } catch(SQLException exc)
        {
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    /**
     * Inserta un logro en la base de datos y devuelve si la operacion a tenido exito
     *
     * @param achievement   logro a insertar en la base de datos
     * @return              "true" si la operacion a tenido exito, "false" si no ha fracasado
     */
    public boolean addAchievement(Achievement achievement){
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( ACHIEVEMENT_NAME, achievement.getName() );
        values.put( ACHIEVEMENTS_CLUE, achievement.getDescription() );

        try {
            db.beginTransaction();

            cursor = db.query( ACHIEVEMENTS_TABLE,
                    null,
                    ACHIEVEMENT_NAME + "=?",
                    new String[]{ achievement.getName() },
                    null, null, null, null );

            if ( cursor.getCount() > 0 ) {
                db.update( ACHIEVEMENTS_TABLE,
                        values, ACHIEVEMENT_NAME + "= ?", new String[]{achievement.getName() } );
            } else {
                db.insert( ACHIEVEMENTS_TABLE, null, values );
            }

            db.setTransactionSuccessful();
            toret = true;

        } catch(SQLException exc)
        {
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }
    /**
     * Borra un historial por nombre y devuelve si la operacion a tenido exito
     *
     * @param name  nombre del historial a eliminar de la base de datos
     * @return      "true" si la operacion a tenido exito, "false" si no ha fracasado
     */
    public boolean deleteHistory(String name)
    {
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            db.delete( HISTORY_TABLE, HISTORY_NAME + "=?", new String[]{ name } );
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc) {
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Devuelve los historiales
     *
     * @return los historiales
     */
    public Cursor getHistories(){
        return this.getReadableDatabase().query(HISTORY_TABLE,null,null,null,null,null,null);
    }

    /**
     * Devuelve los historiales de las partidas en las que ha jugado el perfil especificado
     *
     * @param profileName   nombre del perfil del que se busca los historiales
     * @return              historiales del perfil especificado
     */
    public Cursor getHistoriesByName(String profileName){
        return this.getReadableDatabase().query(HISTORY_TABLE,null,HISTORY_NAME+" LIKE '%"+profileName+"%'",null,null,null,null);
    }

    /**
     * Devuelve los logros
     *
     * @return los logros
     */
    public Cursor getAchievements(){
        return this.getReadableDatabase().query(ACHIEVEMENTS_TABLE,null,null,null,null,null,null);
    }

    /**
     * Obtiene la descripcion de un logro para mostrarlo como pista
     *
     * @param name  nombre del logro a devolver
     * @return      pista para obtener el logro
     */
    public String getDescription(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns= new String[]{ACHIEVEMENTS_CLUE};
        Cursor cursor= db.query(ACHIEVEMENTS_TABLE,columns,ACHIEVEMENT_NAME+"=?",new String[]{name},null,null,null);;
        if (!cursor.moveToFirst())
            cursor.moveToFirst();
        return cursor.getString(0);
    }

    /**
     * Obtiene un historial por nombre
     *
     * @param name  nombre del historial a devolver
     * @return      historial especificado
     */
    public Cursor getHistory(String name){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns= new String[]{HISTORY_NAME,HISTORY_LOG,HISTORY_PIECEPOS};

        Cursor cursor= db.query(HISTORY_TABLE,columns,HISTORY_NAME+"=?",new String[]{name},null,null,null);;
        if (!cursor.moveToFirst())
            cursor.moveToFirst();
        return cursor;
    }
}