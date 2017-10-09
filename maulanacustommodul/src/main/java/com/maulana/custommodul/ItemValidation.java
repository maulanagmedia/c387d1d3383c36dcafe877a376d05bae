package com.maulana.custommodul;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by indra on 20/12/2016.
 * Custom Validation element
 */

public class ItemValidation {

    private final String TAG = "Item.Validation";

    //region custom date validation
    public void PreValidateCustomDate(final EditText edt, final TextInputLayout til, final String format){

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textDate = edt.getText().toString();

                if(!isValidFormat(format,textDate) && !textDate.isEmpty()){
                    if(til != null){
                        til.setErrorEnabled(true);
                        til.setError(format.toUpperCase());
                    }else{
                        edt.setError(format.toUpperCase(),null);
                    }
                }else{
                    if(til != null){
                        til.setError(null);
                        til.setErrorEnabled(false);
                    }else{
                        edt.setError(null,null);
                    }
                }
            }
        });
    }

    public String getToday(String format){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public boolean validateCustomDate(final EditText edt, final String format){
        String textDate = edt.getText().toString();
        if(!isValidFormat(format,textDate)){
            edt.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    public String getCurrentDate(String format){
        return new SimpleDateFormat(format).format(new Date());
    }

    public static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    public void CustomDateFormatCorrection(final EditText edt){

        edt.addTextChangedListener(new TextWatcher() {
            boolean changeChar = true;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edt.getText().length() == 5 || edt.getText().length() == 8){
                    if(edt.getText().toString().charAt(edt.getText().length() - 1) == '-' ){
                        changeChar = false;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if((edt.getText().length() == 4 || edt.getText().length() == 7) && changeChar){
                    edt.setText(edt.getText().toString() + "-");
                    edt.setSelection(edt.getText().length());
                }
                changeChar = true;
            }
        });
    }

    public String ChangeFormatDateString(String date, String formatDateFrom, String formatDateTo){

        String result = date;
        SimpleDateFormat sdf = new SimpleDateFormat(formatDateFrom);
        SimpleDateFormat sdfCustom = new SimpleDateFormat(formatDateTo);

        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (sdfCustom.format(date1) == null) ? "" : sdfCustom.format(date1);
    }

    public int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public int[] getScreenResolution(Context context){

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int[] sizeArray = new int[2];
        sizeArray[0] =  width;
        sizeArray[1] = height;
        return sizeArray;
    }

    public boolean isMoreThanCurrentDate(EditText edt, EditText edt2, String format){

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dateToCompare = null;
        Date dateCompare = null;

        try {
            dateToCompare = sdf.parse(edt.getText().toString());
            dateCompare = sdf.parse(edt2.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dateToCompare.after(dateCompare) || dateToCompare.equals(dateCompare)){
            return true;
        }else{
            return false;
        }
    }

    public String sumDate(String date1, int numberOfDay, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, numberOfDay);
        return sdf.format(c.getTime());
    }
    //endregion

    //region Datepicker
    public void datePickerEvent(final Context context, final EditText edt, final String drawablePosition, final String formatDate){
        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int prePosition;
                final Calendar customDate;

                switch (drawablePosition.toUpperCase()){
                    case "LEFT":
                        prePosition = 0;
                        break;
                    case "TOP":
                        prePosition = 1;
                        break;
                    case "RIGHT":
                        prePosition = 2;
                        break;
                    case "Bottom":
                        prePosition = 3;
                        break;
                    default:
                        prePosition = 2;
                }

                final int position = prePosition;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[position].getBounds().width())) {

                        /*Log.d(TAG, "onTouch: ");
                        // set format date
                        customDate = Calendar.getInstance();
                        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                customDate.set(Calendar.YEAR,year);
                                customDate.set(Calendar.MONTH,month);
                                customDate.set(Calendar.DATE,date);

                                SimpleDateFormat sdFormat = new SimpleDateFormat(formatDate, Locale.US);
                                edt.setText(sdFormat.format(customDate.getTime()));
                            }
                        };
                        
                        new DatePickerDialog(context,date,customDate.get(Calendar.YEAR),customDate.get(Calendar.MONTH),customDate.get(Calendar.DATE)).show();
                        return true;*/
                    }

                    customDate = Calendar.getInstance();
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            customDate.set(Calendar.YEAR,year);
                            customDate.set(Calendar.MONTH,month);
                            customDate.set(Calendar.DATE,date);

                            SimpleDateFormat sdFormat = new SimpleDateFormat(formatDate, Locale.US);
                            edt.setText(sdFormat.format(customDate.getTime()));
                        }
                    };

                    new DatePickerDialog(context,date,customDate.get(Calendar.YEAR),customDate.get(Calendar.MONTH),customDate.get(Calendar.DATE)).show();
                    return true;
                }
                return false;
            }
        });
    }

    public void datePickerEvent(final Context context, final EditText edt, final String drawablePosition, final String formatDate, final String value){
        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int prePosition;
                final Calendar customDate;

                switch (drawablePosition.toUpperCase()){
                    case "LEFT":
                        prePosition = 0;
                        break;
                    case "TOP":
                        prePosition = 1;
                        break;
                    case "RIGHT":
                        prePosition = 2;
                        break;
                    case "Bottom":
                        prePosition = 3;
                        break;
                    default:
                        prePosition = 2;
                }

                final int position = prePosition;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[position].getBounds().width())) {

                        /*Log.d(TAG, "onTouch: ");
                        // set format date
                        customDate = Calendar.getInstance();
                        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                customDate.set(Calendar.YEAR,year);
                                customDate.set(Calendar.MONTH,month);
                                customDate.set(Calendar.DATE,date);

                                SimpleDateFormat sdFormat = new SimpleDateFormat(formatDate, Locale.US);
                                edt.setText(sdFormat.format(customDate.getTime()));
                            }
                        };

                        new DatePickerDialog(context,date,customDate.get(Calendar.YEAR),customDate.get(Calendar.MONTH),customDate.get(Calendar.DATE)).show();
                        return true;*/
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat(formatDate);

                    Date dateValue = null;

                    try {
                        dateValue = sdf.parse(value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    customDate = Calendar.getInstance();
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            customDate.set(Calendar.YEAR,year);
                            customDate.set(Calendar.MONTH,month);
                            customDate.set(Calendar.DATE,date);

                            SimpleDateFormat sdFormat = new SimpleDateFormat(formatDate, Locale.US);
                            edt.setText(sdFormat.format(customDate.getTime()));
                        }
                    };


                    SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                    new DatePickerDialog(context,date, parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
                    return true;
                }
                return false;
            }
        });
    }
    //endregion

    //region Autocomplete
    /*Must pick from autocomplete*/
    public boolean validateAutocompleteMustPick(final AutoCompleteTextView actv, final TextInputLayout til, boolean selected, String errorMessage){
        if(!selected){
            if(til != null){
                til.setErrorEnabled(true);
                til.setError(errorMessage);
            }else{
                actv.setError(errorMessage, null);
            }
            actv.requestFocus();
            return false;
        }else{
            if(til != null){
                til.setError(null);
                til.setErrorEnabled(false);
            }else{
                actv.setError(null,null);
            }
            return true;
        }
    }

    //endregion

    //region Set limit (min / max)
    public void setLimitValue(final EditText edt, final TextInputLayout til, final String category, final int limitValue, final String errorMessage){

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int value;

                try{

                    value = Integer.parseInt(editable.toString());
                }catch(Exception e){

                    value = 0;
                }

                // MIN
                if(category.toUpperCase() == "MIN" && value < limitValue){
                    if(til != null){
                        til.setErrorEnabled(true);
                        til.setError(errorMessage);
                    }else{
                        edt.setError(errorMessage,null);
                    }
                }else if(category.toUpperCase() == "MIN" && value >= limitValue){
                    if(til != null){
                        til.setErrorEnabled(false);
                        til.setError(null);
                    }else{
                        edt.setError(null);
                    }
                }

                // MAX
                if(category.toUpperCase() == "MAX" && value > limitValue){
                    if(til != null){
                        til.setErrorEnabled(true);
                        til.setError(errorMessage);
                    }else{
                        edt.setError(errorMessage,null);
                    }
                }else if(category.toUpperCase() == "MAX" && value <= limitValue){
                    if(til != null){
                        til.setErrorEnabled(false);
                        til.setError(null);
                    }else{
                        edt.setError(null);
                    }
                }


            }
        });
    }
    //endregion

    //region Number
   /* Change Number to Rupiah*/
    public String ChangeToRupiahFormat(Float number){

        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        symbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(0);

        String hasil = String.valueOf(format.format(number));

        /*String stringConvert = "0";
        try {
            stringConvert = format.format(1000);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }


        if(!stringConvert.contains(",")){
            hasil += ",00";
        }*/

        return hasil;
    }

    public String ChangeToRupiahFormat(Double number){

        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        symbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(0);

        String hasil = String.valueOf(format.format(number));

        return hasil;
    }

    public String ChangeToRupiahFormat(Long number){

        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        symbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(0);

        String hasil = String.valueOf(format.format(number));

        return hasil;
    }
    //endregion

    //region Progressbar
    public void ProgressbarEvent(final LinearLayout llItem, final ProgressBar pbItem, final Button btnItem, String condition){

        if (condition.toUpperCase().equals("SHOW")){
            llItem.setVisibility(View.VISIBLE);
            pbItem.setVisibility(View.VISIBLE);
            btnItem.setVisibility(View.GONE);
        }else if(condition.toUpperCase().equals("ERROR")){
            llItem.setVisibility(View.VISIBLE);
            pbItem.setVisibility(View.GONE);
            btnItem.setVisibility(View.VISIBLE);
        }else if(condition.toUpperCase().equals("GONE")){
            llItem.setVisibility(View.GONE);
            pbItem.setVisibility(View.GONE);
            btnItem.setVisibility(View.GONE);
        }

    }
    //endregion

    //region Nullable value
    public int parseNullInteger(String s){
        int result = 0;
        if(s != null){
            try {
                result = Integer.parseInt(s);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return result;
    }

    //region Nullable value
    public long parseNullLong(String s){
        long result = 0;
        if(s != null){
            try {
                result = Long.parseLong(s);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return result;
    }

    //nullable value
    public float parseNullFloat(String s){
        float result = 0;
        if(s != null){
            try {
                result = Float.parseFloat(s);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return result;
    }

    public Double parseNullDouble(String s){
        double result = 0;
        if(s != null){
            try {
                result = Double.parseDouble(s);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return result;
    }

    public String doubleToStringFull(Double number){
        return String.format("%s", number).replace(",",".");
    }

    public String doubleToString(Double number){
        return String.format("%.1f", number).replace(",",".");
    }

    public String doubleToStringRound(Double number){
        return String.format("%.0f", number).replace(",",".");
    }

    public String parseNullString(String s){
        String result = "";
        if(s != null){
            result = s;
        }
        return result;
    }

    public int floatToInteger(float f){
        int result = 0;
        String temp = "0";
        temp = String.format("%.0f", f);
        if(temp != null){
            try {
                result = Integer.parseInt(temp);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return result;
    }

    public String toPercent(String value){
        return String.valueOf(Float.parseFloat(value) * 100) + " %";
    }
    //endregion

    //region EditText

    public Boolean mondatoryEdittext(TextInputLayout til, EditText edt, String errorMessage){

        Boolean result = false;

        if(edt.getText().length() <= 0){
            if(til != null){
                til.setErrorEnabled(true);
                til.setError(errorMessage);
            }else{
                edt.setError(errorMessage, null);
            }
            edt.requestFocus();
        }else{
            if(til != null){
                til.setErrorEnabled(false);
                til.setError(null);
            }else{
                edt.setError(null);
            }
            result = true;
        }

        return result;
    }
    //endregion

    //region Change Password
    public Boolean ValidateOldPassword(TextInputLayout til, EditText edt, String oldPassowrd){

        Boolean result = false;

        if(!edt.getText().toString().equals(oldPassowrd)){
            if(til != null){
                til.setErrorEnabled(true);
                til.setError("Password Lama Tidak Benar");
            }else{
                edt.setError("Password Lama Tidak Benar", null);
            }
            edt.requestFocus();
        }else{
            if(til != null){
                til.setErrorEnabled(false);
                til.setError(null);
            }else{
                edt.setError(null);
            }
            result = true;
        }

        return result;
    }

    public boolean syncPassword(EditText edtPass1, TextInputLayout tilPass2, EditText edtPass2){

        Boolean result = false;

        if(!edtPass1.getText().toString().equals(edtPass2.getText().toString())){
            if(tilPass2 != null){
                tilPass2.setErrorEnabled(true);
                tilPass2.setError("Password Baru Tidak Sama");
            }else{
                edtPass2.setError("Password Baru Tidak Sama", null);
            }
            edtPass2.requestFocus();
        }else{
            if(tilPass2 != null){
                tilPass2.setErrorEnabled(false);
                tilPass2.setError(null);
            }else{
                edtPass2.setError(null);
            }
            result = true;
        }

        return result;
    }

    public TextWatcher textChangeListenerCurrency(final EditText editText) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                editText.removeTextChangedListener(this);
                String originalString = s.toString();

                /*if(originalString.contains(",")){
                    originalString = originalString.replaceAll(",", "");
                }

                if(originalString.contains(".")){
                    originalString = originalString.replaceAll("\\.", "");
                }*/
                originalString = originalString.replace(",", "").replace(".","");

                DecimalFormat formatter = new DecimalFormat();
                String stringConvert = "0";
                try {
                    stringConvert = formatter.format(Double.parseDouble(originalString));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }

                editText.setText(stringConvert);
                editText.setSelection(editText.getText().length());
                editText.addTextChangedListener(this);
            }
        };
    }

    public void setCurrencyFormatOnFocus(final EditText edt){

        edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){

                    String originalString = edt.getText().toString();
                    originalString = originalString.replaceAll(",", "");
                    edt.setText(originalString);
                }else{

                    String originalString = edt.getText().toString();
                    if(originalString.length() > 0 && !originalString.trim().equals("0")){
                        String pattern = "#,##0.00";
                        DecimalFormat formatter = new DecimalFormat(pattern);
                        String formattedString = formatter.format(parseNullFloat(edt.getText().toString()));

                        //setting text after format to EditText
                        edt.setText(formattedString);
                    }
                }
            }
        });
    }
    //endregion

    public void hideSoftKey(Context context){
        InputMethodManager inputManager =
                (InputMethodManager) context.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                ((Activity) context).getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS); ((Activity) context).getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }


}
