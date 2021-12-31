package com.example.testMgmtAndroid.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    // creating a constant variables for our database.
    private static final String DB_NAME = "ExamData.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "questionTable";
    private static final String TABLE_NAME2 = "cardDetailsTable";
    private static final String ID_COL = "id";
    private static final String EXAM_ID_COL = "exam_id";
    private static final String EXAM_ID_COL2 = "exam_code";
    private static final String QUESTION_TYPE_COL = "question_type";
    private static final String QUESTIONS_COL = "question";
    private static final String OPTIONS_COL = "options";
    private static final String EXPLANATION_COL = "explanation";
    private static final String IS_SKIP_COL = "is_skip";
    private static final String IS_FLAG_COL = "is_flag";
    private static final String ANSWER_COL = "answer";
    private static final String Duration_COL = "duration";
    private static final String QuestionCount_COL = "question_count";
    private static final String StartDate_COL = "start_date";
    private static final String StartTime_COL = "start_time";
    private static final String Tittle_COL = "tittle";
    private static final String QUESTION_SEQUENCE_COL = "question_sequence";
    private static final String Is_Finish_key = "is_finish_key";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase Db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXAM_ID_COL + " INTEGER,"
                + QUESTION_TYPE_COL + " VARCHAR(100),"
                + QUESTIONS_COL + " TEXT,"
                + OPTIONS_COL + " TEXT,"
                + EXPLANATION_COL + " TEXT,"
                + IS_SKIP_COL + " INTEGER DEFAULT 0 ,"
                + IS_FLAG_COL + " INTEGER DEFAULT 0 ,"
                + ANSWER_COL + " TEXT,"
                + QUESTION_SEQUENCE_COL + " INTEGER)";

        String query1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXAM_ID_COL2 + " INTEGER,"
                + Duration_COL + " INTEGER,"
                + QuestionCount_COL + " INTEGER,"
                + StartDate_COL + " TEXT,"
                + StartTime_COL + " TEXT,"
                + Tittle_COL + " TEXT,"
                + Is_Finish_key + " INTEGER DEFAULT 0 )";
        Db.execSQL(query);
        Db.execSQL(query1);
//        isTableExists(TABLE_NAME2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase Db, int oldVersion, int newVersion) {
        Db.execSQL(" Drop table if exists TABLE_NAME2");


    }
    public void deleteAnswer(String answer){
        SQLiteDatabase Db = this.getWritableDatabase();
        Db.delete(TABLE_NAME, ANSWER_COL + " = ?", new String[]{answer});

    }

    public void deleteAll() {
        SQLiteDatabase Db = this.getWritableDatabase();
        if (Db != null) {
            Db.execSQL("delete from "+ TABLE_NAME2);

            Db.close();
        }
    }

//
//    public boolean isTableExists(String tableName) {
//        boolean isExist = false;
//        SQLiteDatabase Db = this.getWritableDatabase();
//        Cursor cursor = Db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLE_NAME2 + "'", null);
//        if (cursor != null) {
//            if (cursor.getCount() > 0) {
//                isExist = true;
//            }
//            cursor.close();
//        }
//        return isExist;
//    }
//
//    public void deleteAll11() {
//        SQLiteDatabase Db = this.getWritableDatabase();
//        if (Db != null) {
//            Db.execSQL("delete from "+ TABLE_NAME);
//
//            Db.close();
//        }
//    }

    public void deleteById(String examId) {
        SQLiteDatabase Db = this.getWritableDatabase();
        if (Db != null) {
            Db.delete(TABLE_NAME, EXAM_ID_COL + " = ?", new String[]{examId});
            Db.close();
        }
    }

    public Boolean insertData2(String examId, String duration, String questioncCount, String startDate, String startTime, String tittle) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME2 +
                        " WHERE " + ID_COL + " = " + examId,
                null);
        if (cursorCourses.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Is_Finish_key, cursorCourses.getString(7));
                jsonArray.put(jsonObject);
                Log.e("insertdata", "nikk" + jsonArray);
            } while (cursorCourses.moveToNext());
        }
        cursorCourses.close();
        SQLiteDatabase Db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAM_ID_COL2, examId);
        contentValues.put(Duration_COL, duration);
        contentValues.put(QuestionCount_COL, questioncCount);
        contentValues.put(StartDate_COL, startDate);
        contentValues.put(StartTime_COL, startTime);
        contentValues.put(Tittle_COL, tittle);
        long result = Db.insert(TABLE_NAME2, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Boolean insertData(String examId, String questionType, String question, String options, String explanation, int questionSequ) {
        SQLiteDatabase Db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAM_ID_COL, examId);
        contentValues.put(QUESTION_TYPE_COL, questionType);
        contentValues.put(QUESTIONS_COL, question);
        contentValues.put(OPTIONS_COL, options);
        contentValues.put(EXPLANATION_COL, explanation);
        contentValues.put(QUESTION_SEQUENCE_COL, questionSequ);
        long result = Db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public long UpdateAnswer(String id, ArrayList<Object> answer) {
        SQLiteDatabase Db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ANSWER_COL, String.valueOf(answer));
        long result = Db.update("questionTable",
                contentValues, ID_COL + "=?",
                new String[]{id});

        return result;

    }
    public long UpdateAnswer2(String id, String answer1) {
        SQLiteDatabase Db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ANSWER_COL, (String) null);
        long result = Db.update("questionTable",
                contentValues, ID_COL + "=?",
                new String[]{id});

        return result;

    }
    public long setFlag(String id, int flag) {
        SQLiteDatabase Db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IS_FLAG_COL, flag);
        long result = Db.update("questionTable",
                contentValues, ID_COL + "= ? ",
                new String[]{id});
        return result;
    }
    public long setFinish(String id) {
        SQLiteDatabase Db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Is_Finish_key, 1);
        long result = Db.update("cardDetailsTable",
                contentValues, ID_COL + "= ? ",
                new String[]{id});
        return result;
    }
    public JSONArray questionCount(String examId) {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();
        try {
            Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                            " WHERE " + EXAM_ID_COL + " = " + examId,
                    null);

            if (cursorCourses.moveToFirst()) {
                do {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ID_COL, cursorCourses.getString(0));
                    jsonObject.put(EXAM_ID_COL, cursorCourses.getString(1));
                    jsonObject.put(QUESTION_TYPE_COL, cursorCourses.getString(2));
                    jsonObject.put(QUESTIONS_COL, cursorCourses.getString(3));
                    jsonObject.put(OPTIONS_COL, cursorCourses.getString(4));
                    jsonObject.put(QUESTION_SEQUENCE_COL, cursorCourses.getString(9));
                    jsonObject.put(EXPLANATION_COL, cursorCourses.getString(5));
                    jsonObject.put(ANSWER_COL, cursorCourses.getString(8));
                    jsonObject.put(IS_FLAG_COL, cursorCourses.getString(7));
                    jsonArray.put(jsonObject);
                    Log.e("atf", "nikk" + jsonArray);
                } while (cursorCourses.moveToNext());
            }
            cursorCourses.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getExamListCardData(int isFinishKey) {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();
        try {
            Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME2 +" where is_finish_key="+ isFinishKey,null);
            if (cursorCourses.moveToFirst()) {
                do {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ID_COL, cursorCourses.getString(0));
                    jsonObject.put(EXAM_ID_COL2, cursorCourses.getString(1));
                    jsonObject.put(Duration_COL, cursorCourses.getString(2));
                    jsonObject.put(QuestionCount_COL, cursorCourses.getString(3));
                    jsonObject.put(StartDate_COL, cursorCourses.getString(4));
                    jsonObject.put(StartTime_COL, cursorCourses.getString(5));
                    jsonObject.put(Tittle_COL, cursorCourses.getString(6));
                    jsonArray.put(jsonObject);
                    Log.e("atf", "nikk" + jsonArray);
                } while (cursorCourses.moveToNext());
            }

            cursorCourses.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getAllData(String examId, String queSequence) {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();
        String SequenceWhere = "";
        if (!queSequence.equals("0")) {
            SequenceWhere = " AND " + QUESTION_SEQUENCE_COL + " = " + queSequence;
        }
        try {
            Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                            " WHERE " + EXAM_ID_COL + " = " + examId + SequenceWhere,
                    null);

            if (cursorCourses.moveToFirst()) {
                do {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ID_COL, cursorCourses.getString(0));
                    jsonObject.put(EXAM_ID_COL, cursorCourses.getString(1));
                    jsonObject.put(QUESTION_TYPE_COL, cursorCourses.getString(2));
                    jsonObject.put(QUESTIONS_COL, cursorCourses.getString(3));
                    jsonObject.put(OPTIONS_COL, cursorCourses.getString(4));
                    jsonObject.put(QUESTION_SEQUENCE_COL, cursorCourses.getString(8));
                    jsonObject.put(EXPLANATION_COL, cursorCourses.getString(5));
                    jsonObject.put(ANSWER_COL, cursorCourses.getString(8));
                    jsonObject.put(IS_FLAG_COL, cursorCourses.getString(7));
                    jsonArray.put(jsonObject);
                    Log.e("atf", "nikk" + jsonArray);
                } while (cursorCourses.moveToNext());
            }

            cursorCourses.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
