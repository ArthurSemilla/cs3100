package rsck.chalkboard;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jacob.
 */
public class Course implements Parcelable {
    private int ID;
    private String courseName;
    private String schoolName;
    private ArrayList<WeightedGrades> grades;
    private int user_ID;
    private String token;

    public Course(){grades = new ArrayList<>();}

    public Course(String courseName, String schoolName, int user_ID, String token){
        this.courseName = courseName;
        this.schoolName = schoolName;
        this.user_ID = user_ID;
        this.token = token;
    };

    public Course(JSONObject courseJSON, int user_ID, String token){
        this.user_ID = user_ID;
        this.token = token;

        try {
            ID = courseJSON.getInt("id");
            courseName = courseJSON.getString("name");
            schoolName = courseJSON.getString("school");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        grades = new ArrayList<>();

        loadGrades();

    }

    //Getters.
    public int getID() {return ID;}

    public String getCourseName() {return courseName;}

    public String getSchoolName() {return schoolName;}

    public ArrayList<WeightedGrades> getGrades() {return grades;}

    //Loads all grades for this Course.
    public void loadGrades(){

        //TODO: add grades to course.
        //TODO: send JSON to query stuff.

        /*
        *  Calls: http://cs3100.brod.es:3100/get/grade/?token='token'&user='ID'
        *  sendString = "{ \"course_id\" = ID,}"
        *
        * */
        JSONObject JSONQuery = new JSONObject();

        try {
            JSONQuery.put("course_id", ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DjangoFunctions django = new DjangoFunctions();
        JSONObject response = django.access("grade", Integer.toString(user_ID), token, JSONQuery);

        
        //Now grab all unique Cat_IDs for the course...
        try {
            if(response.getBoolean("success")){
                JSONArray data = response.getJSONArray("data");

                ArrayList<Integer> uniqueIDs = new ArrayList<Integer>();

                for(int i = 0; i < data.length(); i++) {

                    JSONObject grade = data.getJSONObject(i);

                    /*
                    * Calls: http://cs3100.brod.es:3100/get/homework/?token='token'&user='ID'
                    * Where: {"id": 'grade.getInt("homework_id")'}
                    */
                    JSONObject query = new JSONObject();
                    query.put("id", grade.getInt("homework_id"));


                    JSONObject homework = django.access("homework", Integer.toString(user_ID), token, query);
                    JSONObject matchedHomework = homework.getJSONArray("data").getJSONObject(0);

                    int categoryID = matchedHomework.getInt("category_id");

                    //Category is unique, add to grades.
                    if(!uniqueIDs.contains(categoryID)){
                        WeightedGrades newWeightedCat = new WeightedGrades(categoryID, user_ID, token);
                        uniqueIDs.add(categoryID);

                        grades.add(newWeightedCat);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //returns grade as a decimal.
    //Multiply by 100 for percent.
    public float getCourseGrade(){
        float total = 0;
        
        for(WeightedGrades weightedGrades : grades)
            total += weightedGrades.weightedTotal();
            
        return total;
    }

    public boolean addWeightedCategory(String catName, String weight){
        Boolean success = false;
        DjangoFunctions django = new DjangoFunctions();
        JSONObject cat = new JSONObject();

        try {
            cat.put("name", catName);
            cat.put("weight", weight);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject response = django.add("category", Integer.toString(user_ID), token, cat);

        try{
            success = response.getBoolean("success");
            if(success){
                JSONObject data = response.getJSONObject("data");
                cat.put("id", data.getInt("id"));
                WeightedGrades newCat = new WeightedGrades(cat, user_ID, token);
                grades.add(newCat);
            }

        } catch(JSONException e){
            e.printStackTrace();
        }

        return success;
    }


    /*Needed Parcelable Declarations below here*/
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flag){
        out.writeInt(ID);
        out.writeString(courseName);
        out.writeString(schoolName);
        out.writeTypedList(grades);
        out.writeInt(user_ID);
        out.writeString(token);
    }

    public Course(Parcel in) {
        this();

        ID = in.readInt();
        courseName = in.readString();
        schoolName = in.readString();
        in.readTypedList(grades, WeightedGrades.CREATOR);
        user_ID = in.readInt();
        token = in.readString();
    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

}
