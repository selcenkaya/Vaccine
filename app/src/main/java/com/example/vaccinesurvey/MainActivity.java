package com.example.vaccinesurvey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    Pattern p = Pattern.compile("[^A-Za-z ]");
    Button send;
    Button dateButton;
    CheckBox other;
    EditText specify;
    Boolean valid;
    EditText name;
    EditText city;

    TextView errorName;
    TextView errorBirth;
    TextView errorCity;
    TextView errorOther;

    Boolean validName;
    Boolean validBirth;
    Boolean validCity;
    Boolean validOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resetForm();
    }

    private void resetForm() {
        // reset
        validName = false;
        validBirth = false;
        validCity = false;
        validOther = false;
        valid = false;

        // button click activity
        send = findViewById(R.id.send);
        send.setVisibility(View.GONE); // reset
        send.setOnClickListener(v -> {
            Context context = getApplicationContext();
            CharSequence text = "Form Sent!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            View view = toast.getView();

            // change toast colors
            view.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.SRC_IN);
            TextView text2 = view.findViewById(android.R.id.message);
            text2.setTextColor(ContextCompat.getColor(context, R.color.white));

            // change toast text size
            ViewGroup group = (ViewGroup) toast.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(20);

            toast.show();
            send.setEnabled(false);
            int delay = 4200;
            new Handler().postDelayed(() -> send.setEnabled(true),delay);
            resetForm();
        });

        // date picker menu
        errorBirth = (TextView) findViewById(R.id.errorBirth);
        errorBirth.setVisibility(View.GONE);
        dateButton = findViewById(R.id.date);
        String birthdate = "Birth Date"; // reset
        dateButton.setText(birthdate); // reset
        initDatePicker();

        // other checkbox functionality
        other = (CheckBox) findViewById(R.id.other);
        other.setChecked(false); // reset
        specify = (EditText) findViewById(R.id.specify);
        specify.getText().clear(); // reset
        specify.setVisibility(View.GONE);
        errorOther = (TextView) findViewById(R.id.errorOther);
        errorOther.setVisibility(View.GONE);

        // initialize
        name = (EditText) findViewById(R.id.name);
        name.getText().clear(); // reset
        city = (EditText) findViewById(R.id.city);
        city.getText().clear(); // reset

        // reset
        errorName = (TextView) findViewById(R.id.errorName);
        errorCity = (TextView) findViewById(R.id.errorCity);
        errorName.setVisibility(View.GONE);
        errorCity.setVisibility(View.GONE);

        resetCheckBoxes(); // reset

        // name validation
        name.addTextChangedListener(new TextValidator(name) {
            @Override public void validate(TextView textView, String text) {
                Matcher m = p.matcher(text);
                boolean b = m.find();

                if( text.length() < 3 || b ) {
                    errorName.setVisibility(View.VISIBLE);
                    validName = false;
                }
                else {
                    errorName.setVisibility(View.GONE);
                    validName = true;
                }
                check();
            }
        });

        // city validation
        city.addTextChangedListener(new TextValidator(city) {
            @Override public void validate(TextView textView, String text) {
                Matcher m = p.matcher(text);
                boolean b = m.find();

                if( text.length() < 3 || b ) {
                    errorCity.setVisibility(View.VISIBLE);
                    validCity = false;
                }
                else {
                    errorCity.setVisibility(View.GONE);
                    validCity = true;
                }
                check();
            }
        });

    }

    private void check() {
        if( validName & validBirth & validCity ) {
            if( (other.isChecked() & validOther) | !other.isChecked() )
                send.setVisibility(View.VISIBLE);
        }
        else {
            send.setVisibility(View.GONE);
        }
    }

    private void resetCheckBoxes() {
        CheckBox fever = (CheckBox) findViewById(R.id.fever);
        fever.setChecked(false);
        CheckBox tiredness = (CheckBox) findViewById(R.id.tiredness);
        tiredness.setChecked(false);
        CheckBox headache = (CheckBox) findViewById(R.id.headache);
        headache.setChecked(false);
        CheckBox musclePain = (CheckBox) findViewById(R.id.musclePain);
        musclePain.setChecked(false);
        CheckBox chills = (CheckBox) findViewById(R.id.chills);
        chills.setChecked(false);
        CheckBox pcr = (CheckBox) findViewById(R.id.pcr);
        pcr.setChecked(false);
        CheckBox symptom = (CheckBox) findViewById(R.id.symptom);
        symptom.setChecked(false);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month += 1;
            String date = day + " " + getMonthFormat(month) + " " + year;

            Calendar cal = Calendar.getInstance();
            int thisYear = cal.get(Calendar.YEAR);
            int thisMonth = cal.get(Calendar.MONTH);
            int thisDay = cal.get(Calendar.DAY_OF_MONTH);

            // validation
            // faulty logic - TODO
            if( thisYear - year >= 18 ) {
                if( thisMonth - month < 0 ) {
                    if( thisDay - day <= 0 ) {
                        errorBirth.setVisibility(View.GONE);
                        validBirth = true;
                    }
                    else {
                        errorBirth.setVisibility(View.VISIBLE);
                        validBirth = false;
                    }
                }
                else {
                    errorBirth.setVisibility(View.VISIBLE);
                    validBirth = false;
                }
            }
            else {
                errorBirth.setVisibility(View.VISIBLE);
                validBirth = false;
            }
            check();
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.R.style.Theme_Holo_Light_Dialog;

        datePickerDialog = new DatePickerDialog( this, style, dateSetListener, year, month, day );
    }

    private String getMonthFormat(int month) {
        if( month == 1 ) {
            return "Jan";
        }
        if( month == 2 ) {
            return "Feb";
        }
        if( month == 3 ) {
            return "Mar";
        }
        if( month == 4 ) {
            return "Apr";
        }
        if( month == 5 ) {
            return "May";
        }
        if( month == 6 ) {
            return "Jun";
        }
        if( month == 7 ) {
            return "Jul";
        }
        if( month == 8 ) {
            return "Aug";
        }
        if( month == 9 ) {
            return "Sep";
        }
        if( month == 10 ) {
            return "Oct";
        }
        if( month == 11 ) {
            return "Nov";
        }
        if( month == 12 ) {
            return "Dec";
        }
        return "Jan";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void checkbox_clicked(View view) {
        if(other.isChecked()) {
            specify.setVisibility(View.VISIBLE);

            // specify validation
            specify.addTextChangedListener(new TextValidator(specify) {
                @Override public void validate(TextView textView, String text) {
                    Matcher m = p.matcher(text);
                    boolean b = m.find();

                    if( text.length() < 3 || b ) {
                        errorOther.setVisibility(View.VISIBLE);
                        validOther = false;
                    }
                    else {
                        errorOther.setVisibility(View.GONE);
                        validOther = true;
                    }
                    check();
                }
            });
        }
        else {
            specify.setVisibility(View.GONE);
            errorOther.setVisibility(View.GONE);
        }
    }
}